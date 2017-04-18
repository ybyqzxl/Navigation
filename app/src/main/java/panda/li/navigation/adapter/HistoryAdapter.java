package panda.li.navigation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import panda.li.navigation.R;
import panda.li.navigation.entity.History;

/**
 * Created by xueli on 2016/8/20.
 */
public class HistoryAdapter extends ArrayAdapter<History> {

    private List<History> histories;
    private LayoutInflater layoutInflater;

    public HistoryAdapter(Context context, List<History> histories) {
        super(context, R.layout.item_for_history, histories);
        layoutInflater = LayoutInflater.from(context);
        this.histories = histories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_for_history, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_station = (TextView) convertView.findViewById(R.id.item_station);
            viewHolder.tv_route = (TextView) convertView.findViewById(R.id.item_route);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (histories.get(position).getType() == 1) {
            viewHolder.tv_station.setVisibility(View.VISIBLE);
            viewHolder.tv_route.setVisibility(View.GONE);
            viewHolder.tv_station.setText(histories.get(position).getName());
        } else if (histories.get(position).getType() == 2) {
            viewHolder.tv_station.setVisibility(View.GONE);
            viewHolder.tv_route.setVisibility(View.VISIBLE);
            viewHolder.tv_route.setText(histories.get(position).getStartName() + "至" + histories
                    .get(position).getEndName());
        } else if (histories.get(position).getType() == 3) {
            String name;
            viewHolder.tv_station.setVisibility(View.VISIBLE);
            viewHolder.tv_route.setVisibility(View.GONE);
            String[] busLine = histories.get(position).getName().split("#");
            String busLineName = busLine[0];
            if (busLine[1].equals(Integer.toString(1))) {
                name = "上行";
            } else name = "下行";
            viewHolder.tv_station.setText(busLineName + "路" + name);
        }
        return convertView;
    }

    class ViewHolder {
        public TextView tv_station;
        public TextView tv_route;
    }
}
