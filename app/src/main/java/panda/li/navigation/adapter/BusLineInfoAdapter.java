package panda.li.navigation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.model.LatLng;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.Serializable;
import java.util.List;

import okhttp3.Call;
import panda.li.navigation.R;
import panda.li.navigation.activity.BusLineActivity;
import panda.li.navigation.activity.MapShowActivity;
import panda.li.navigation.base.MyApplication;
import panda.li.navigation.callback.BusLineCallBack;
import panda.li.navigation.constant.Constant;
import panda.li.navigation.entity.BusLine;
import panda.li.navigation.entity.BusStation;
import panda.li.navigation.utils.ToastUtil;

/**
 * Created by xueli on 2016/8/12.
 */
public class BusLineInfoAdapter extends ArrayAdapter<BusStation> {


    private Context context;
    private List<BusStation> busStations;
    private LayoutInflater layoutInflater;
    private int selectItem = -1;
    private int searchPosition = -1;

    public BusLineInfoAdapter(Context context, List<BusStation> busStations) {
        super(context, R.layout.item_for_busline_info, busStations);
        layoutInflater = LayoutInflater.from(context);
        this.busStations = busStations;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        searchPosition = position;
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_for_busline_info, null);
            holder = new ViewHolder();
            holder.stationName = (TextView) convertView.findViewById(R.id.tv_busStationName);
            holder.view_start = convertView.findViewById(R.id.view_start);
            holder.view_end = convertView.findViewById(R.id.view_end);
            holder.iv_state = (ImageView) convertView.findViewById(R.id.bus_state);
            holder.ll_station_around = (LinearLayout) convertView.findViewById(R.id
                    .ll_station_around);
            holder.tv_around = (TextView) convertView.findViewById(R.id.tv_around);
            holder.tv_route = (TextView) convertView.findViewById(R.id.tv_route);
            holder.view_around = convertView.findViewById(R.id.arount_view);
            holder.tv_map = (TextView) convertView.findViewById(R.id.tv_map);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (busStations.get(position).getStantionToLine().getBusLineSerial().equals("1")) {
            holder.view_start.setVisibility(View.INVISIBLE);
        } else if (busStations.get(position).getStantionToLine().getBusLineSerial().equals(Integer
                .toString(busStations.size()))) {
            holder.view_end.setVisibility(View.INVISIBLE);
        } else {
            holder.view_start.setVisibility(View.VISIBLE);
            holder.view_end.setVisibility(View.VISIBLE);
        }
        if (position == selectItem) {
            holder.iv_state.setImageResource(R.drawable.dot_red);
            holder.ll_station_around.setVisibility(View.VISIBLE);
            holder.view_around.setVisibility(View.VISIBLE);
        } else {
            holder.iv_state.setImageResource(R.drawable.dot);
            holder.ll_station_around.setVisibility(View.GONE);
            holder.view_around.setVisibility(View.GONE);
        }
        holder.stationName.setText(busStations.get(position).getBusStationName() + "  " +
                busStations.get(position).getStantionToLine().getBusLineSerial());

        holder.tv_around.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapShowActivity.class);
                LatLng latLng = new LatLng(busStations.get(searchPosition).getLatitude(),
                        busStations
                                .get(searchPosition).getLongitude());
                intent.putExtra("lal", latLng);
                context.startActivity(intent);
            }
        });
        holder.tv_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpUtils.get().url(Constant.LOAD_BUS_LINE_BY_STATION)
                        .addParams
                                ("BusStationName", busStations.get(searchPosition)
                                        .getBusStationName())
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
                                            Intent intent = new Intent(context,
                                                    BusLineActivity
                                                            .class);
                                            intent.putExtra("list", (Serializable) list);
                                            context.startActivity(intent);
                                        }
                                    }
                                } else {
                                    ToastUtil.show(context, "无查询结果");
                                }
                            }
                        });
            }
        });
        holder.tv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return convertView;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    static class ViewHolder {
        public TextView stationName;
        public View view_start;
        public View view_end;
        public ImageView iv_state;
        public LinearLayout ll_station_around;
        public TextView tv_around;
        public TextView tv_route;
        public View view_around;
        public TextView tv_map;
    }

}
