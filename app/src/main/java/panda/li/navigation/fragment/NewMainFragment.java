package panda.li.navigation.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import panda.li.navigation.R;
import panda.li.navigation.activity.ActivityBusResult;
import panda.li.navigation.activity.ActivityQcode;
import panda.li.navigation.activity.BusLineActivity;
import panda.li.navigation.activity.BusLineInfoActivity;
import panda.li.navigation.adapter.HistoryAdapter;
import panda.li.navigation.adapter.SearchInfoAdapter;
import panda.li.navigation.base.MyApplication;
import panda.li.navigation.callback.BusLineCallBack;
import panda.li.navigation.callback.SearchInfoCallBack;
import panda.li.navigation.constant.Constant;
import panda.li.navigation.entity.BusLine;
import panda.li.navigation.entity.History;
import panda.li.navigation.entity.SearchInfo;
import panda.li.navigation.service.LocationService;
import panda.li.navigation.utils.AMapUtil;
import panda.li.navigation.utils.LogUtil;
import panda.li.navigation.utils.ToastUtil;

public class NewMainFragment extends Fragment {


    private static int QCODE = 1;
    @Bind(R.id.edt_search_station)
    AutoCompleteTextView tv_search_station;
    @Bind(R.id.new_start)
    AutoCompleteTextView newStart;
    @Bind(R.id.new_end)
    AutoCompleteTextView newEnd;
    @Bind(R.id.ll_clear)
    LinearLayout llClear;
    @Bind(R.id.list_history)
    ListView lv_history;


    private ProgressDialog progDialog = null;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private GeocodeSearch geocoderSearchStart, geocoderSearchEnd, geocoderLocation;
    private LatLonPoint startPoint;
    private LatLonPoint endPoint;
    private boolean isFirst = true;

    private RouteSearch mRouteSearch;
    private BusRouteResult mBusRouteResult;
    private List<SearchInfo> searchInfos = new ArrayList<SearchInfo>();
    ;

    private List<History> histories = new ArrayList<>();


    private Set<String> station = new HashSet<>();
    private Set<String> route = new HashSet<>();
    private Set<String> busLine = new HashSet<>();

    private HistoryAdapter adapter;

    private Constant constant;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TelephonyManager telephonyManager;

    //转换车站
    private String change;


    public static NewMainFragment newInstance() {
        return new NewMainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRouteSearch = new RouteSearch(getActivity());
        constant = Constant.getInstance();
        progDialog = new ProgressDialog(getActivity());
        geocoderSearchStart = new GeocodeSearch(getActivity());
        geocoderSearchEnd = new GeocodeSearch(getActivity());
        mRouteSearch = new RouteSearch(getActivity());
        geocoderLocation = new GeocodeSearch(getActivity());
        sharedPreferences = MyApplication.getInstance().getSharedPreferences(Constant
                .SHARED_HISTORY, Context.MODE_APPEND);
        editor = sharedPreferences.edit();
        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context
                .TELEPHONY_SERVICE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        //初始化定位
        mlocationClient = new AMapLocationClient(getActivity());
        //设置定位回调监听
        mlocationClient.setLocationListener(mLocationListener);
        initLocation();
        initStartAndEndPoint();
        initStationSearch();
        initHistoryList();
        return view;
    }

    //初始化历史记录
    private void initHistoryList() {
        checkHistoryAndAdd();
        histories.clear();
        Set<String> stringSet = sharedPreferences.getStringSet("station", new HashSet<String>());
        Set<String> routeSet = sharedPreferences.getStringSet("route", new HashSet<String>());
        Set<String> busLineSet = sharedPreferences.getStringSet("busLine", new HashSet<String>
                ());
        if (stringSet.size() > 0) {
            for (String stationName : stringSet) {
                History history = new History();
                history.setType(1);
                history.setName(stationName);
                histories.add(history);
            }
        }
        if (routeSet.size() > 0) {
            for (String routeName : route) {
                History history = new History();
                history.setType(2);
                history.setName(routeName);
                String[] routeInfo = routeName.split("#");
                String startName = routeInfo[0];
                String startPoint = routeInfo[1];
                String endName = routeInfo[2];
                String endPoint = routeInfo[3];
                history.setStartName(startName);
                history.setStartPoint(startPoint);
                history.setEndName(endName);
                history.setEndPonit(endPoint);
                histories.add(history);
            }
        }
        if (busLineSet.size() > 0) {
            for (String busLineName : busLine) {
                History history = new History();
                history.setType(3);
                history.setName(busLineName);
                histories.add(history);
            }
        }
        if (histories.size() > 0 && histories != null) {
            llClear.setVisibility(View.VISIBLE);
            adapter = new HistoryAdapter(getActivity(), histories);
            lv_history.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            lv_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (histories.get(i).getType() == 1) {
                        String text = histories.get(i).getName();
                        //搜索车站公交
                        constant.setStationName(text);
                        searchBusLineByStationName(text);
                    } else if (histories.get(i).getType() == 2) {
                        String splitStart[] = histories.get(i).getStartPoint().split(",");
                        String splitEnd[] = histories.get(i).getEndPonit().split(",");
                        if (!constant.getCity().equals("")) {
                            showDialog("正在查询");
                            searchRouteResult(new LatLonPoint(Double.parseDouble(splitStart[0]),
                                            Double.parseDouble(splitStart[1])),
                                    new LatLonPoint(Double.parseDouble(splitEnd[0]),
                                            Double.parseDouble(splitEnd[1])));
                        } else {
                            ToastUtil.show(getActivity(), "请等待定位成功....");
                        }
                    } else if (histories.get(i).getType() == 3) {
                        Intent intent = new Intent(getActivity(), BusLineInfoActivity.class);
                        intent.putExtra("type", 3);
                        intent.putExtra("busLine", histories.get(i).getName());
                        startActivity(intent);
                    }
                }
            });
        }
        lv_history.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int
                    position, long l) {
                final History history = histories.get(position);
                String name = "";
                if (history.getType() == 1) {
                    name = history.getName();
                    station.remove(name);
                    editor.putStringSet("station", station);
                } else if (history.getType() == 2) {
                    name = history.getStartName() + "#" + history.getStartPoint() + "#" + history
                            .getEndName() + "#" + history.getEndPonit();
                    route.remove(name);
                    editor.putStringSet("route", route);
                } else if (history.getType() == 3) {
                    name = history.getName();
                    busLine.remove(name);
                    editor.putStringSet("busLine", route);
                }
                editor.commit();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle
                        ("确定要删除本条记录？").setPositiveButton("确定", new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        histories.remove(position);
                        adapter.remove(history);
                        adapter.notifyDataSetChanged();
                        if (histories.isEmpty()) {
                            llClear.setVisibility(View.GONE);
                        }
                    }
                }).setNegativeButton("取消", null);
                builder.show();
                return false;
            }
        });
    }

    private void initLocation() {
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置定位时间间隔
        // mLocationOption.setInterval(2000);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //给定位客户端对象设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        //设置每秒进行定位
        //启动定位
        if (isFirst) {
            showDialog("正在定位");
            newStart.setText("正在定位......");
        }
        mlocationClient.startLocation();
    }

    //搜素历史记录并显示
    private void checkHistoryAndAdd() {
        Set<String> stationHistory = sharedPreferences.getStringSet("station", new
                HashSet<String>());
        Set<String> routeHistory = sharedPreferences.getStringSet("route", new HashSet<String>());
        Set<String> busLineHistory = sharedPreferences.getStringSet("busLine", new
                HashSet<String>());
        if (stationHistory.size() > 0) {
            for (String mStation : stationHistory) {
                station.add(mStation);
            }
        }
        if (routeHistory.size() > 0) {
            for (String mRoute : routeHistory) {
                route.add(mRoute);
            }
        }
        if (busLineHistory.size() > 0) {
            for (String mBusLine : busLineHistory) {
                busLine.add(mBusLine);
            }
        }
    }

    //对车站搜索框进行设置监听
    private void initStationSearch() {
        tv_search_station.setThreshold(1);
        tv_search_station.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, final int i, int i1, int i2) {
                String newText = charSequence.toString().trim();
                if (!newText.isEmpty()) {
                    OkHttpUtils.get().url(Constant.LOAD_SEARCH)
                            .addParams
                                    ("LineOrName", newText).build().execute(new SearchInfoCallBack() {

                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(List<SearchInfo> response, int id) {
                            if (response != null && response.size() > 0) {
                                searchInfos = response;
                                SearchInfoAdapter aAdapter = new SearchInfoAdapter(
                                        getActivity(), searchInfos);
                                tv_search_station.setAdapter(aAdapter);
                                aAdapter.notifyDataSetChanged();
                                tv_search_station.setOnItemClickListener(new AdapterView
                                        .OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View
                                            view, int i, long l) {
                                        if (searchInfos.get(i).getIsName().equals(Integer
                                                .toString(1))) {
                                            tv_search_station.setText(searchInfos.get(i)
                                                    .getSearchName());
                                            constant.setStationName(searchInfos.get(i)
                                                    .getSearchName());
                                            searchBusLineByStationName(searchInfos.get(i)
                                                    .getSearchName());
                                            // checkHistoryAndAdd();
                                            station.add(searchInfos.get(i).getSearchName());
                                            editor.putStringSet("station", station);
                                            editor.commit();
                                        } else if (searchInfos.get(i).getIsName().equals(Integer
                                                .toString(0))) {
                                            if (searchInfos.get(i).getUpDown().equals(Integer
                                                    .toString(1))) {
                                                tv_search_station.setText(searchInfos.get(i)
                                                        .getSearchName() + "路上行");
                                            } else {
                                                tv_search_station.setText(searchInfos.get(i)
                                                        .getSearchName() + "路下行");
                                            }
                                            //checkHistoryAndAdd();
                                            busLine.add(searchInfos.get(i)
                                                    .getSearchName() + "#" + searchInfos.get(i)
                                                    .getUpDown() + "#" + searchInfos.get(i)
                                                    .getDestination());
                                            editor.putStringSet("busLine", busLine);
                                            editor.commit();
                                            Intent intent = new Intent(getActivity(),
                                                    BusLineInfoActivity.class);
                                            intent.putExtra("type", 1);
                                            intent.putExtra("searchInfo", searchInfos.get(i));
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //初始化起点和终点，对其设置监听
    private void initStartAndEndPoint() {
        newStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString().trim();
                if (!AMapUtil.IsEmptyOrNullString(newText)) {
                    InputtipsQuery inputquery = new InputtipsQuery(newText, constant.getCity());
                    Inputtips inputTips = new Inputtips(getActivity(), inputquery);
                    inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
                        @Override
                        public void onGetInputtips(List<Tip> tipList, int rCode) {
                            if (rCode == 1000) {// 正确返回
                                List<String> listString = new ArrayList<>();
                                for (int i = 0; i < tipList.size(); i++) {
                                    listString.add(tipList.get(i).getName());
                                }
                                ArrayAdapter<String> aAdapter = new ArrayAdapter<>(
                                        getActivity(),
                                        R.layout.item_for_autotext, listString);
                                newStart.setAdapter(aAdapter);
                                aAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.showerror(getActivity(), rCode);
                            }
                        }
                    });
                    inputTips.requestInputtipsAsyn();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        newEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString().trim();
                if (!AMapUtil.IsEmptyOrNullString(newText)) {
                    InputtipsQuery inputquery = new InputtipsQuery(newText, constant.getCity());
                    Inputtips inputTips = new Inputtips(getActivity(), inputquery);
                    inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
                        @Override
                        public void onGetInputtips(List<Tip> tipList, int rCode) {
                            if (rCode == 1000) {// 正确返回
                                List<String> listString = new ArrayList<>();
                                for (int i = 0; i < tipList.size(); i++) {
                                    listString.add(tipList.get(i).getName());
                                }
                                ArrayAdapter<String> aAdapter = new ArrayAdapter<>(
                                        getActivity(),
                                        R.layout.item_for_autotext, listString);
                                newEnd.setAdapter(aAdapter);
                                aAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.showerror(getActivity(), rCode);
                            }
                        }
                    });
                    inputTips.requestInputtipsAsyn();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        initLocation();
        initHistoryList();
        newEnd.setText("");
        tv_search_station.setText("");
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    //定位
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    if (isFirst) {
                        constant.setCity(amapLocation.getCity());//获得城市
                        constant.setLatLonPoint(new LatLonPoint(amapLocation.getLatitude(),
                                amapLocation.getLongitude()));
                        newStart.setText("我的位置");
                        isFirst = false;
                        dismissDialog();
                        Intent locationService = new Intent(getActivity(), LocationService.class);
                        getActivity().startService(locationService);
                    }


//                    publishAppInfo(telephonyManager.getDeviceId(), amapLocation.getLongitude(),
//                            amapLocation.getLatitude());
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation
                            .getErrorInfo();
                    LogUtil.e("AmapErr", errText);
                }
            }
        }
    };

    //上传信息
    private void publishAppInfo(String deviceId, double longitude, double latitude) {
        if (deviceId.equals("") && String.valueOf(longitude).isEmpty() && String.valueOf
                (latitude).isEmpty()) {
            ToastUtil.show(getActivity(), "无法获取设备ID");
        } else {
            OkHttpUtils.get().url(Constant.PUBLISH_USERINFO)
                    .addParams
                            ("APPID", deviceId).addParams("Longitude", String.valueOf(longitude))
                    .addParams("Latitude", String.valueOf(latitude))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ToastUtil.show(MyApplication.getInstance(), e.getMessage());
                            dismissDialog();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if (!response.equals("")) {
                                if (Integer.parseInt(response.substring(1, response.length() - 1)
                                ) <= 0) {
                                    ToastUtil.show(getActivity(), "上传失败");
                                }
                                Log.i("MAIN", response);
                            } else {
                                ToastUtil.show(getActivity(), "上传失败");
                            }
                            dismissDialog();
                        }
                    });
        }
    }


    //查询线路
    private void searchRouteResult(LatLonPoint start, LatLonPoint end) {

        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                start, end);

        RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, RouteSearch
                .BusDefault,
                constant.getCity(), 0);//
        // 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
        mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        mRouteSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult result, int errorCode) {
                if (errorCode == 1000) {
                    if (result != null && result.getPaths() != null) {
                        if (result.getPaths().size() > 0) {
                            //dismissDialog();
                            mBusRouteResult = result;
                            Intent intent = new Intent(getActivity(), ActivityBusResult.class);
                            intent.putExtra("busResult", mBusRouteResult);
                            startActivity(intent);
                        } else if (result != null && result.getPaths() == null) {
                            ToastUtil.show(getActivity(), "无查询结果");
                        }
                    } else {
                        ToastUtil.show(getActivity(), "无查询结果");
                    }
                } else {
                    ToastUtil.showerror(getActivity(), errorCode);
                }
                dismissDialog();
            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mlocationClient.stopLocation();
        mlocationClient.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QCODE) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    constant.setStationName(result);
                    searchBusLineByStationName(result);
                    // checkHistoryAndAdd();
                    station.add(result);
                    editor.putStringSet("station", station);
                    editor.commit();
                    ToastUtil.show(getActivity(), result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtil.show(getActivity(), "解析二维码失败");
                }
            }
        }
    }

    private void searchBusLineByStationName(String text) {
        OkHttpUtils.get().url(Constant.LOAD_BUS_LINE_BY_STATION)
                .addParams
                        ("BusStationName", text)
                .build()
                .execute(new BusLineCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show(MyApplication.getInstance(), e.getMessage());
                    }

                    @Override
                    public void onResponse(BusLine response, int id) {
                        if (response.getStatus().equals("成功")) {
                            if (response.getBusLines() != null) {
                                List<BusLine.BusLinesBean> list = response.getBusLines();
                                if (list.size() > 0) {
                                    Intent intent = new Intent(getActivity(), BusLineActivity
                                            .class);
                                    intent.putExtra("list", (Serializable) list);
                                    startActivity(intent);
                                }
                            }
                        } else {
                            ToastUtil.show(getActivity(), "无查询结果");
                        }
                    }
                });
    }


    private void startSearchRoute() {
        if (newStart.getText().toString().isEmpty()) {
            ToastUtil.show(getActivity(), "请输入起点");
        } else if (newEnd.getText().toString().isEmpty()) {
            ToastUtil.show(getActivity(), "请输入终点");
        } else {
            if (newStart.getText().toString().equals("我的位置")) {
                startPoint = constant.getLatLonPoint();
            } else {
                GeocodeQuery query = new GeocodeQuery(newStart.getText().toString(),
                        constant.getCity());//
                // 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
                geocoderSearchStart.setOnGeocodeSearchListener(new GeocodeSearch
                        .OnGeocodeSearchListener() {
                    @Override
                    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int
                            i) {

                    }

                    @Override
                    public void onGeocodeSearched(GeocodeResult geocodeResult, int
                            rCode) {
                        if (rCode == 1000) {
                            if (geocodeResult != null && geocodeResult
                                    .getGeocodeAddressList() != null
                                    && geocodeResult.getGeocodeAddressList().size() >
                                    0) {

                                GeocodeAddress address = geocodeResult
                                        .getGeocodeAddressList()
                                        .get(0);
                                startPoint = address.getLatLonPoint();
                                setStartAndEnd(startPoint, endPoint);
                                LogUtil.i("route", startPoint.toString());
                                //ToastUtil.show(getActivity(), addressName);
                            } else {
                                ToastUtil.show(getActivity(), "暂无结果");
                            }

                        } else {
                            ToastUtil.showerror(getActivity(), rCode);
                        }
                    }
                });
                geocoderSearchStart.getFromLocationNameAsyn(query);// 设置同步地理编码请求
            }
            if (newEnd.getText().toString().equals("我的位置")) {
                endPoint = constant.getLatLonPoint();
            } else {
                GeocodeQuery queryEnd = new GeocodeQuery(newEnd.getText().toString(), constant
                        .getCity());//
                // 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
                geocoderSearchEnd.setOnGeocodeSearchListener(new GeocodeSearch
                        .OnGeocodeSearchListener() {
                    @Override
                    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int
                            i) {

                    }

                    @Override
                    public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
                        if (rCode == 1000) {
                            if (geocodeResult != null && geocodeResult
                                    .getGeocodeAddressList() != null
                                    && geocodeResult.getGeocodeAddressList().size() > 0) {

                                GeocodeAddress address = geocodeResult
                                        .getGeocodeAddressList()
                                        .get(0);
                                endPoint = address.getLatLonPoint();
                                LogUtil.i("route", endPoint.toString());
                                setStartAndEnd(startPoint, endPoint);
                            } else {
                                ToastUtil.show(getActivity(), "暂无结果");
                            }

                        } else {
                            ToastUtil.showerror(getActivity(), rCode);
                        }
                    }
                });
                geocoderSearchEnd.getFromLocationNameAsyn(queryEnd);// 设置同步地理编码请求
            }
        }

    }

    private void setStartAndEnd(LatLonPoint start, LatLonPoint end) {
        if (!start.toString().isEmpty() && !end.toString().isEmpty()) {
            //  checkHistoryAndAdd();
            route.add(newStart.getText().toString() + "#" + start.toString() + "#" + newEnd
                    .getText()
                    .toString() + "#" + end.toString());
            editor.putStringSet("route", route);
            editor.commit();
            showDialog("正在查询");
            searchRouteResult(start, end);
        }
    }

    @OnClick({R.id.iv_new_saoyisao, R.id.iv_new_search, R.id
            .iv_new_trance, R.id.iv_route_search, R.id.tv_clear_history})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_new_saoyisao:
                Intent intent = new Intent(getActivity(), ActivityQcode.class);
                startActivityForResult(intent, QCODE);
                break;
            case R.id.iv_new_search:
                String text = tv_search_station.getText().toString();
                if ("".equals(text)) {
                    ToastUtil.show(getActivity(), "请输入搜索关键字");
                } else {
                    String a = text.substring(0, 1);
                    Pattern p = Pattern.compile("[0-9]*");
                    Matcher m = p.matcher(a);
                    if (m.matches()) {
                        return;
                    }
                    p = Pattern.compile("[\u4e00-\u9fa5]");
                    m = p.matcher(a);
                    if (m.matches()) {
                        //搜索车站公交
                        constant.setStationName(text);
                        searchBusLineByStationName(text);
                        // checkHistoryAndAdd();
                        station.add(text);
                        editor.putStringSet("station", station);
                        editor.commit();
                    }
                }
                break;
            case R.id.iv_new_trance:
                change = newStart.getText().toString();
                newStart.setText(newEnd.getText().toString());
                newEnd.setText(change);
                break;
            case R.id.iv_route_search:
                startSearchRoute();
                break;
            case R.id.tv_clear_history:
                editor.clear();
                editor.commit();
                histories.clear();
                llClear.setVisibility(View.GONE);
                break;
        }
    }


    /**
     * 显示进度条对话框
     */
    public void showDialog(String text) {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage(text);
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
