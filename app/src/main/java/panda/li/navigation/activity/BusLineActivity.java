package panda.li.navigation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import panda.li.navigation.R;
import panda.li.navigation.adapter.BusLineAdapter;
import panda.li.navigation.base.ActivityCollector;
import panda.li.navigation.base.BaseActivity;
import panda.li.navigation.entity.BusLine;

/**
 * Created by xueli on 2016/8/12.
 */
public class BusLineActivity extends BaseActivity {
    @Bind(R.id.lv_busline)
    ListView lvBusline;

    private int upDown;
    private BusLineAdapter adapter;
    private List<BusLine.BusLinesBean> beanList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busline);
        ButterKnife.bind(this);
        beanList = (List<BusLine.BusLinesBean>) getIntent().getSerializableExtra("list");
        adapter = new BusLineAdapter(this, beanList);
        lvBusline.setAdapter(adapter);
        lvBusline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(BusLineActivity.this, BusLineInfoActivity.class);
                intent.putExtra("type", 2);
                intent.putExtra("busLine", beanList.get(i));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
