package panda.li.navigation.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;

import panda.li.navigation.R;

public class AmapBusLineAdapter extends BaseAdapter {
    private List<BusLineItem> busLineItems;
    private LayoutInflater layoutInflater;

    public AmapBusLineAdapter(Context context, List<BusLineItem> busLineItems) {
        this.busLineItems = busLineItems;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return busLineItems.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.busline_item, null);
            holder = new ViewHolder();
            holder.busName = (TextView) convertView.findViewById(R.id.busname);
            holder.busId = (TextView) convertView.findViewById(R.id.busid);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.busName.setText("公交名 :"
                + busLineItems.get(position).getBusLineName());
        holder.busId.setText("公交ID:"
                + busLineItems.get(position).getBusLineId());
        return convertView;
    }

    class ViewHolder {
        public TextView busName;
        public TextView busId;
    }

}
