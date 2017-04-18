package panda.li.navigation.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.amap.api.services.route.BusRouteResult;

import butterknife.Bind;
import butterknife.ButterKnife;
import panda.li.navigation.R;
import panda.li.navigation.adapter.BusResultListAdapter;
import panda.li.navigation.base.BaseActivity;

/**
 * Created by xueli on 2016/8/19.
 */
public class ActivityBusResult extends BaseActivity {

    @Bind(R.id.route_detail)
    ListView routeDetail;
    private BusRouteResult mBusRouteResult;
    private BusResultListAdapter mBusResultListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_busresult);
        ButterKnife.bind(this);
        mBusRouteResult = getIntent().getParcelableExtra("busResult");
        mBusResultListAdapter = new BusResultListAdapter
                (this, mBusRouteResult);
        routeDetail.setAdapter(mBusResultListAdapter);

    }
}
