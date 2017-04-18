package panda.li.navigation.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.overlay.BusLineOverlay;
import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.core.LatLonPoint;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import panda.li.navigation.R;
import panda.li.navigation.activity.BusLineActivity;
import panda.li.navigation.adapter.AmapBusLineAdapter;
import panda.li.navigation.base.MyApplication;
import panda.li.navigation.callback.BusLineCallBack;
import panda.li.navigation.callback.BusStationCallBack;
import panda.li.navigation.constant.Constant;
import panda.li.navigation.entity.BusLine;
import panda.li.navigation.entity.BusStation;
import panda.li.navigation.utils.ToastUtil;

public class MyMapFragment extends Fragment implements LocationSource,
        AMapLocationListener, AMap.OnMapClickListener, InfoWindowAdapter,
        AMap.OnMarkerClickListener, BusLineSearch
                .OnBusLineSearchListener {


    private MapView mapView;
    @Bind(R.id.edt_content)
    AutoCompleteTextView edtContent;
    private AMap aMap;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    //poi检索周边使用
    private int currentPage = 0;// 当前页面，从0开始计数
    private Marker locationMarker; // 选择的点
    private Marker mlastMarker;
    private Marker detailMarker;
    private String city = "天津市";
    private LatLng latLng;


    //线路搜索
    private BusLineQuery busLineQuery;// 公交线路查询的查询类
    private BusLineSearch busLineSearch;// 公交线路列表查询
    private int currentpage = 0;// 公交搜索当前页，第一页从0开始
    private BusLineResult busLineResult;// 公交线路搜索返回的结果
    private List<BusLineItem> lineItems = null;// 公交线路搜索返回的busline

    private BusStation busStation;

    private Constant constant;

    private boolean isFirst = true;     //标记


    public static MyMapFragment newInstance() {
        return new MyMapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        constant = Constant.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_map, container, false);
        ButterKnife.bind(this, view);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        return view;
    }

    /**
     * 初始化AMap对象
     */
    private void init() {

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        } else {
            aMap.clear();
            aMap = mapView.getMap();
            setUpMap();
        }
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        //poi相关
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setInfoWindowAdapter(this);
        //////////////////
        locationMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable
                                .point4)))
                .position(latLng));
        //定位相关
        aMap.setLocationSource(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        isFirst = true;
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                latLng = new LatLng(amapLocation
                        .getLatitude(), amapLocation.getLongitude());
                constant.setLatLonPoint(new LatLonPoint(amapLocation.getLatitude(),
                        amapLocation.getLongitude()));
                if (isFirst) {
                    isFirst = false;
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
                    OkHttpUtils.get().url(Constant.LOAD_NEAR_STATION)
                            .addParams
                                    ("Longitude", "114.49513").addParams("latitude", "38.054477")
                            .build()
                            .execute(new BusStationCallBack() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    ToastUtil.show(MyApplication.getInstance(), e
                                            .getMessage());
                                }

                                @Override
                                public void onResponse(BusStation response, int id) {
                                    busStation = response;
                                    aMap.addMarker(new MarkerOptions()
                                            .position(
                                                    new LatLng(response.getLatitude(), response
                                                            .getLongitude())).title(response
                                                    .getBusStationName())
                                            .snippet("公交车站"));
                                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng
                                            (response.getLatitude(), response
                                                    .getLongitude()), 19));
                                }
                            });
                }

            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation
                        .getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置高精度模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode
                    .Hight_Accuracy);
            mLocationOption.setNeedAddress(true);
            mLocationOption.setOnceLocation(false);
            mLocationOption.setInterval(2000);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


    //热点点击事件
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null) {
            try {
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetlastmarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.showInfoWindow();
            } catch (Exception e) {
                // TODO: handle exception
            }
        } else {
            resetlastmarker();
        }
        return true;
    }

    // 将之前被点击的marker置为原来的状态
    private void resetlastmarker() {
        mlastMarker.hideInfoWindow();
        mlastMarker = null;

    }

    @Override
    public View getInfoContents(Marker arg0) {
        return null;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.poikeywordsearch_uri, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(marker.getTitle());

        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        snippet.setText(marker.getSnippet());
        // 搜索路线
        final String name = title.getText().toString();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                constant.setStationName(busStation.getBusStationName());
                searchBusLineByStationName(name);
            }
        });
        return view;
    }


    @Override
    public void onMapClick(LatLng arg0) {
        if (mlastMarker != null) {
            resetlastmarker();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.iv_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                currentpage = 0;// 第一页默认从0开始
                String search = edtContent.getText().toString().trim();
                if ("".equals(search)) {
                    ToastUtil.show(getActivity(), "请输入公交线路");
                }
                busLineQuery = new BusLineQuery(search, BusLineQuery.SearchType.BY_LINE_NAME,
                        city);// 第一个参数表示公交线路名，第二个参数表示公交线路查询，第三个参数表示所在城市名或者城市区号
                busLineQuery.setPageSize(10);// 设置每页返回多少条数据
                busLineQuery.setPageNumber(currentpage);// 设置查询第几页，第一页从0开始算起
                busLineSearch = new BusLineSearch(getActivity(), busLineQuery);// 设置条件
                busLineSearch.setOnBusLineSearchListener(this);// 设置查询结果的监听
                busLineSearch.searchBusLineAsyn();// 异步查询公交线路名称
                break;
        }
    }

    /**
     * 公交线路搜索返回的结果显示在dialog中
     */
    public void showResultList(List<BusLineItem> busLineItems) {
        BusLineDialog busLineDialog = new BusLineDialog(getActivity(), busLineItems);
        busLineDialog.onListItemClicklistener(new OnListItemlistener() {
            @Override
            public void onListItemClick(BusLineDialog dialog,
                                        final BusLineItem item) {
                String lineId = item.getBusLineId();// 得到当前点击item公交线路id
                busLineQuery = new BusLineQuery(lineId, BusLineQuery.SearchType.BY_LINE_ID,
                        city);// 第一个参数表示公交线路id，第二个参数表示公交线路id查询，第三个参数表示所在城市名或者城市区号
                BusLineSearch busLineSearch = new BusLineSearch(
                        getActivity(), busLineQuery);
                busLineSearch.setOnBusLineSearchListener(MyMapFragment.this);
                busLineSearch.searchBusLineAsyn();// 异步查询公交线路id
            }
        });
        busLineDialog.show();
    }

    @Override
    public void onBusLineSearched(BusLineResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null
                    && result.getQuery().equals(busLineQuery)) {
                if (result.getQuery().getCategory() == BusLineQuery.SearchType
                        .BY_LINE_NAME) {
                    if (result.getPageCount() > 0
                            && result.getBusLines() != null
                            && result.getBusLines().size() > 0) {
                        busLineResult = result;
                        lineItems = result.getBusLines();
                        showResultList(lineItems);
                    }
                } else if (result.getQuery().getCategory() == BusLineQuery
                        .SearchType
                        .BY_LINE_ID) {
                    aMap.clear();// 清理地图上的marker
                    busLineResult = result;
                    lineItems = busLineResult.getBusLines();
                    BusLineOverlay busLineOverlay = new BusLineOverlay
                            (getActivity(),
                                    aMap, lineItems.get(0));
                    busLineOverlay.removeFromMap();
                    busLineOverlay.addToMap();
                    busLineOverlay.zoomToSpan();
                }
            } else {
                ToastUtil.show(getActivity(), "无结果");
            }
        } else {
            ToastUtil.showerror(getActivity(), rCode);
        }
    }

    interface OnListItemlistener {
        void onListItemClick(BusLineDialog dialog, BusLineItem item);
    }


    /**
     * 所有公交线路显示页面
     */
    class BusLineDialog extends Dialog implements View.OnClickListener {

        private List<BusLineItem> busLineItems;
        private AmapBusLineAdapter busLineAdapter;
        private Button preButton, nextButton;
        private ListView listView;
        protected OnListItemlistener onListItemlistener;

        public BusLineDialog(Context context, int theme) {
            super(context, theme);
        }

        public void onListItemClicklistener(
                OnListItemlistener onListItemlistener) {
            this.onListItemlistener = onListItemlistener;

        }

        public BusLineDialog(Context context, List<BusLineItem> busLineItems) {
            this(context, android.R.style.Theme_NoTitleBar);
            this.busLineItems = busLineItems;
            busLineAdapter = new AmapBusLineAdapter(context, busLineItems);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.busline_dialog);
            preButton = (Button) findViewById(R.id.preButton);
            nextButton = (Button) findViewById(R.id.nextButton);
            listView = (ListView) findViewById(R.id.listview);
            listView.setAdapter(busLineAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    onListItemlistener.onListItemClick(BusLineDialog.this,
                            busLineItems.get(arg2));
                    dismiss();

                }
            });
            preButton.setOnClickListener(this);
            nextButton.setOnClickListener(this);
            if (currentpage <= 0) {
                preButton.setEnabled(false);
            }
            if (currentpage >= busLineResult.getPageCount() - 1) {
                nextButton.setEnabled(false);
            }

        }

        @Override
        public void onClick(View v) {
            this.dismiss();
            if (v.equals(preButton)) {
                currentpage--;
            } else if (v.equals(nextButton)) {
                currentpage++;
            }
            busLineQuery.setPageNumber(currentpage);// 设置公交查询第几页
            busLineSearch.setOnBusLineSearchListener(MyMapFragment.this);
            busLineSearch.searchBusLineAsyn();// 异步查询公交线路名称
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
                                if (list.size() > 0 && list != null) {
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


}
