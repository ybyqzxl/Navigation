package panda.li.navigation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import panda.li.navigation.R;
import panda.li.navigation.entity.SearchInfo;

/**
 * Created by xueli on 2016/8/20.
 */
public class SearchInfoAdapter extends ArrayAdapter<SearchInfo> {
    private List<SearchInfo> searchInfos;
    private LayoutInflater layoutInflater;

    public SearchInfoAdapter(Context context, List<SearchInfo> objects) {
        super(context, R.layout.item_for_autotext, objects);
        this.searchInfos = objects;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_for_autotext, null);
            viewHolder = new ViewHolder();
            viewHolder.item_tv = (TextView) convertView.findViewById(R.id.item_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (searchInfos.get(position).getIsName() != null) {
            if (searchInfos.get(position).getIsName().equals(Integer
                    .toString(1))) {
                viewHolder.item_tv.setText(searchInfos.get(position).getSearchName());
            } else if (searchInfos.get(position).getIsName().equals(Integer
                    .toString(0))) {
                if (searchInfos.get(position).getUpDown().equals(Integer
                        .toString(1))) {
                    viewHolder.item_tv.setText(searchInfos.get(position).getSearchName() + "路上行");
                } else if (searchInfos.get(position).getUpDown().equals(Integer
                        .toString(2))) {
                    viewHolder.item_tv.setText(searchInfos.get(position).getSearchName() + "路下行");
                }
            }
        }

        return convertView;
    }

    class ViewHolder {
        public TextView item_tv;
    }
}
