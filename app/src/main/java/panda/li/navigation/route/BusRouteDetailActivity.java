package panda.li.navigation.route;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RouteBusWalkItem;

import java.util.List;

import panda.li.navigation.R;
import panda.li.navigation.activity.BasicWalkNaviActivityNavi;
import panda.li.navigation.adapter.BusSegmentListAdapter;
import panda.li.navigation.utils.AMapUtil;

public class BusRouteDetailActivity extends Activity {

	private BusPath mBuspath;
	private BusRouteResult mBusRouteResult;
	private TextView mTitle, mTitleBusRoute, mDesBusRoute;
	private ListView mBusSegmentList;
	private BusSegmentListAdapter mBusSegmentListAdapter;

	//测试步行导航是用
	//private Button btn_naviview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_detail);
		getIntentData();
		init();
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			mBuspath = intent.getParcelableExtra("bus_path");
			mBusRouteResult = intent.getParcelableExtra("bus_result");
		}
	}

	private void init() {
		mTitle = (TextView) findViewById(R.id.title_center);
		mTitle.setText("公交路线详情");
		mTitleBusRoute = (TextView) findViewById(R.id.firstline);
		mDesBusRoute = (TextView) findViewById(R.id.secondline);
		String dur = AMapUtil.getFriendlyTime((int) mBuspath.getDuration());
		String dis = AMapUtil.getFriendlyLength((int) mBuspath.getDistance());
		mTitleBusRoute.setText(dur + "(" + dis + ")");
		int taxiCost = (int) mBusRouteResult.getTaxiCost();
		mDesBusRoute.setText("打车约"+taxiCost+"元");
		mDesBusRoute.setVisibility(View.VISIBLE);
		configureListView();

		//测试步行导航是用

		List<BusStep> busSteps = mBuspath.getSteps();
		for(BusStep busStep:busSteps){
			RouteBusWalkItem walkPath=busStep.getWalk();

			LatLonPoint endlat=walkPath.getDestination();
			LatLonPoint startlat=walkPath.getOrigin();


		}


		//btn_naviview=(Button)findViewById(R.id.btn_naviview);
//		btn_naviview.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent it=new Intent();
//				it.setClass(BusRouteDetailActivity.this, BasicWalkNaviActivityNavi.class);
//				startActivity(it);
//			}
//		});

	}

	private void configureListView() {
		mBusSegmentList = (ListView) findViewById(R.id.bus_segment_list);
		mBusSegmentListAdapter = new BusSegmentListAdapter(
				this, mBuspath.getSteps());
		mBusSegmentList.setAdapter(mBusSegmentListAdapter);
		
	}
	
	public void onBackClick(View view) {
		this.finish();
	}
	

}
