package panda.li.navigation.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import panda.li.navigation.R;
import panda.li.navigation.adapter.BusLineInfoAdapter;
import panda.li.navigation.base.ActivityCollector;
import panda.li.navigation.base.BaseActivity;
import panda.li.navigation.callback.BusInfoCallBack;
import panda.li.navigation.callback.BusStationJsonCallBack;
import panda.li.navigation.constant.Constant;
import panda.li.navigation.entity.BusInfo;
import panda.li.navigation.entity.BusLine;
import panda.li.navigation.entity.BusStation;
import panda.li.navigation.entity.SearchInfo;
import panda.li.navigation.utils.LogUtil;
import panda.li.navigation.utils.ToastUtil;

/**
 * Created by xueli on 2016/8/12.
 */
public class BusLineInfoActivity extends BaseActivity {


    @Bind(R.id.tv_bus_destination)
    TextView tv_bus_destination;
    @Bind(R.id.iv_alarm)
    ImageView iv_notice;
    @Bind(R.id.tv_bus_updown)
    TextView tv_bus_updown;
    @Bind(R.id.tv_show_busState)
    TextView tvShowBusState;
    @Bind(R.id.tv_show_walk)
    TextView tvShowWalk;
    @Bind(R.id.lv_busLine_info)
    ListView lvBusLineInfo;

    private int upDown;
    private List<BusStation> busStations;
    private List<BusStation.StantionToLineBean> stationToLineBeans = new ArrayList<>();
    private BusLineInfoAdapter adapter;
    private FreshBusInfoTask task;
    private Timer timer;
    private BusInfo info;
    private AlarmManager am;
    private int type;
    private int flag = -1;
    private boolean isFirst = true;
    private Constant constant;
    private int switchPosition;
    private boolean isChange = false;
    private String changeStationName;
    private boolean typeFirst;

    private ProgressDialog progDialog = null;


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_busline_info);
        ButterKnife.bind(this);
        progDialog = new ProgressDialog(this);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        constant = Constant.getInstance();
        type = getIntent().getIntExtra("type", 0);
        if (type == 1) {
            typeFirst = true;
            switchPosition = 0;
            final SearchInfo searchInfo = (SearchInfo) getIntent().getSerializableExtra
                    ("searchInfo");
            if (searchInfo != null) {
                String name;
                if (searchInfo.getUpDown().equals(Integer.toString(1))) {
                    name = "上行";
                } else name = "下行";
                showDialog();
                loadBusLineStation(searchInfo.getSearchName(), name);
                tv_bus_updown.setText("当前车辆为：" + searchInfo.getSearchName() + "路" + name);
            } else {
                ToastUtil.show(BusLineInfoActivity.this, "车站信息为空");
            }
        } else if (type == 2) {
            typeFirst = false;
            final BusLine.BusLinesBean busLinesBean = (BusLine.BusLinesBean) getIntent()
                    .getSerializableExtra("busLine");
            if (busLinesBean != null) {
                showDialog();
                loadBusLineStation(busLinesBean.getBusLineCode(), busLinesBean.getUpDowns());
                tv_bus_updown.setText("当前车辆为：" + busLinesBean.getBusLineName() + busLinesBean
                        .getUpDowns());
            }
        } else if (type == 3) {
            typeFirst = true;
            switchPosition = 0;
            String busLine = getIntent().getStringExtra("busLine");
            if (!busLine.isEmpty()) {
                String name;
                String[] busLineInfo = busLine.split("#");
                String busLineName = busLineInfo[0];
                if (busLineInfo[1].equals(Integer.toString(1))) {
                    name = "上行";
                } else name = "下行";
                showDialog();
                loadBusLineStation(busLineName, name);
                tv_bus_updown.setText("当前车辆为：" + busLineName + "路" + name);
            } else {
                ToastUtil.show(BusLineInfoActivity.this, "车站信息为空");
            }
        }

        lvBusLineInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isFirst) {
                    isFirst = true;
                }
                adapter.setSelectItem(i);
                adapter.notifyDataSetInvalidated();
                switchPosition = i;
                changeStationName = busStations.get(i).getBusStationName();
                showDialog();
                checkTimerTask();
                LoadWalkTime(busStations.get(i).getBusStationCode());
                task = new FreshBusInfoTask(busStations.get(i).getBusStationCode
                        ());
                timer.schedule(task, 0, 5000);
            }
        });
    }

    //解析车站
    private int checkStationName() {
        if (isChange) {
            for (int i = 0; i < busStations.size(); i++) {
                if (busStations.get(i).getBusStationName().equals(changeStationName)) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < busStations.size(); i++) {
                if (busStations.get(i).getBusStationName().equals(constant.getStationName())) {
                    return i;
                }
            }
        }
        isChange = false;
        return 0;
    }

    private void checkTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = new Timer();
    }

    @OnClick({R.id.iv_alarm, R.id.iv_switch})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_alarm:
                if (flag == -1) {
                    flag = 1;
                    iv_notice.setImageResource(R.drawable.alarm);
                    ToastUtil.show(this, "已开启提醒");
                } else if (flag == 1) {
                    flag = -1;
                    iv_notice.setImageResource(R.drawable.notice_gray);
                    ToastUtil.show(this, "已关闭提醒");
                }
                break;
            case R.id.iv_switch:
                if (!typeFirst) {
                    changeStationName = busStations.get(checkStationName()).getBusStationName();
                } else changeStationName = busStations.get(switchPosition).getBusStationName();
                isChange = true;
                String upDownName = "";
                if (busStations.get(switchPosition)
                        .getStantionToLine().getUpDown().equals(Integer.toString(1))) {
                    upDownName = "下行";
                } else {
                    upDownName = "上行";
                }
                showDialog();
                loadBusLineStation(busStations.get(switchPosition)
                        .getStantionToLine().getBusLineCode(), upDownName);
                tv_bus_updown.setText("当前车辆为：" + busStations.get(switchPosition)
                        .getStantionToLine().getBusLineCode() + "路" + upDownName);
                break;
        }

    }

    class FreshBusInfoTask extends TimerTask {

        private String busStationCode;

        public FreshBusInfoTask(String busStationCode) {
            this.busStationCode = busStationCode;
        }

        @Override
        public void run() {
            OkHttpUtils.get().url(Constant.LOAD_BUS_STATE)
                    .addParams
                            ("BusStationCode", busStationCode)
                    .build()
                    .execute(new BusInfoCallBack() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            checkTimerTask();
                            tvShowBusState.setText("暂无该站数据");
                            dismissDialog();
                        }

                        @Override
                        public void onResponse(BusInfo response, int id) {
                            String busStationName = "";
                            if (response.getStatus().equals("成功")) {
                                info = response;
                                if (searchBusStationName(info) != 0) {
                                    busStationName = busStations.get
                                            (searchBusStationName(info))
                                            .getBusStationName();
                                }
                                tvShowBusState.setText("当前时速：" + (int) info.getCurrentSpeed() +
                                        "KM/h" + "\n" + "距离本站还有：" + info.getStopCount() + "站\n" +
                                        "下一站：" + busStationName + "\n距本站到站时间：" + String.format
                                        ("%.2f", info.getTimeMinute()) + "分钟" + "\n" + "\n票价：" +
                                        info.getBusFare() + "元\n车型种类：" + info.getBusType());
                                dismissDialog();
                                if (Double.parseDouble(String.format
                                        ("%.2f", info.getTimeMinute())) < 1.00 && flag == 1 &&
                                        isFirst) {
                                    Intent intent = new Intent("BUS_CLOCK");
                                    intent.putExtra("msg", "车将要到站！！！");
                                    PendingIntent pi = PendingIntent.getBroadcast
                                            (BusLineInfoActivity.this, 0,
                                                    intent, 0);
                                    am.set(AlarmManager.RTC_WAKEUP, System
                                            .currentTimeMillis(), pi);
                                    isFirst = false;
                                    LogUtil.i("isFirst", isFirst + " ");
                                }
                            }
                        }
                    });
        }
    }

    private int searchBusStationName(BusInfo info) {
        for (int i = 0; i < stationToLineBeans.size(); i++) {
            if (stationToLineBeans.get(i).getBusLineSerial().equals(info
                    .getNextStopSerial())) {
                return i;
            }
        }
        return 0;
    }

    private void loadBusLineStation(String lineCode, String upDown) {
        if (upDown.equals("上行")) {
            this.upDown = 1;
        } else if (upDown.equals("下行")) {
            this.upDown = 2;
        }
        OkHttpUtils.get().url(Constant.LOAD_AllBUSLINE_BY_LINE)
                .addParams
                        ("busLineCode", lineCode).addParams("upDown", Integer.toString(this.upDown))
                .build()
                .execute(new BusStationJsonCallBack() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.e("error", e.getMessage());
                        ToastUtil.show(BusLineInfoActivity.this, "暂无信息");
                    }

                    @Override
                    public void onResponse(List<BusStation> response, int id) {
                        if (response.size() != 0) {
                            busStations = response;
                            for (BusStation busStation : busStations) {
                                stationToLineBeans.add(busStation.getStantionToLine());
                            }
                            adapter = new BusLineInfoAdapter(BusLineInfoActivity.this,
                                    busStations);
                            lvBusLineInfo.setAdapter(adapter);
                            lvBusLineInfo.setSelection(checkStationName());
                            adapter.setSelectItem(checkStationName());
                            adapter.notifyDataSetChanged();
                            tv_bus_destination.setText("目的地为：" + busStations.get(checkStationName
                                    ()).getStantionToLine().getDestination());
                            checkTimerTask();
                            LogUtil.i("AAAAAA", busStations.get(checkStationName())
                                    .getBusStationCode());
                            LoadWalkTime(busStations.get(checkStationName())
                                    .getBusStationCode());
                            task = new FreshBusInfoTask(busStations.get(checkStationName())
                                    .getBusStationCode
                                            ());
                            timer.schedule(task, 0, 5000);
                        }
                    }
                });
    }

    //加载步行时间
    private void LoadWalkTime(String busStationCode) {
        if (busStationCode.equals("")) {
            ToastUtil.show(this, "车站编码为空");
        } else {
            OkHttpUtils.get().url(Constant.LOAD_TIMEANDDISTANCE)
                    .addParams
                            ("stationCode", busStationCode).addParams("currentLng", String
                    .valueOf(constant.getLatLonPoint().getLongitude())).addParams("currentLat",
                    String.valueOf(constant
                            .getLatLonPoint()
                            .getLatitude()))
                    .build()
                    .execute(new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            LogUtil.e("error", e.getMessage());
                            ToastUtil.show(BusLineInfoActivity.this, "暂无信息");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if (response.equals("")) {
                                ToastUtil.show(BusLineInfoActivity.this, "暂无步行信息");
                            } else {
                                LogUtil.i("AAAAAA", response);
                                try {
                                    JSONArray array = new JSONArray(response);
                                    String walkDistance = array.getString(0);
                                    String walkTime = array.getString(1);
                                    LogUtil.i("AAAAAA", walkDistance + " " + walkTime);
                                    tvShowWalk.setText("距当前位置：" + walkDistance + "公里" + "\n步行时间："
                                            + String.format
                                            ("%.2f", Double.parseDouble(walkTime)) + "分钟");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        private MediaPlayer mediaPlayer;

        @Override
        public void onReceive(final Context context, Intent intent) {
            LogUtil.d("MyTag", "onclock......................");
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
// 如果为空，才构造，不为空，说明之前有构造过
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(context, uri);
                mediaPlayer.setLooping(true); //循环播放
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String msg = intent.getStringExtra("msg");
            ToastUtil.show(context, msg);
            final AlertDialog.Builder alBuilder = new AlertDialog.Builder(context, R.style
                    .myDialog);
            alBuilder.setTitle("通知");
            alBuilder.setMessage("请注意，车将要到站！");
            alBuilder.setCancelable(false);
            alBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mediaPlayer.stop();
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = alBuilder.create();
            alertDialog.getWindow().setType(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }


    /**
     * 显示进度条对话框
     */
    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在查询");
        progDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
}
