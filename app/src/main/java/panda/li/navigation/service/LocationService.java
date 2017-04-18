package panda.li.navigation.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import panda.li.navigation.base.MyApplication;
import panda.li.navigation.constant.Constant;
import panda.li.navigation.utils.LogUtil;
import panda.li.navigation.utils.ToastUtil;

public class LocationService extends Service {

    private AMapLocationClientOption mLocationOption;
    private AMapLocationClient mlocationClient;
    private TelephonyManager telephonyManager;

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mlocationClient = new AMapLocationClient(getApplicationContext());
        mlocationClient.setLocationListener(mLocationListener);
        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context
                .TELEPHONY_SERVICE);
        initLocation();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void initLocation() {
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置定位时间间隔
        mLocationOption.setInterval(2000);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //给定位客户端对象设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        //设置每秒进行定位
        //启动定位
        mlocationClient.startLocation();
    }

    //定位
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    publishAppInfo(telephonyManager.getDeviceId(), amapLocation.getLongitude(),
                            amapLocation.getLatitude());
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
            ToastUtil.show(getApplicationContext(), "无法获取设备ID");
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
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if (!response.equals("")) {
                                if (Integer.parseInt(response.substring(1, response.length() - 1)
                                ) <= 0) {
                                    ToastUtil.show(getApplicationContext(), "上传失败");
                                }
                                Log.i("LocationService", response);
                            } else {
                                ToastUtil.show(getApplicationContext(), "上传失败");
                            }
                        }
                    });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mlocationClient.stopLocation();
        mlocationClient.onDestroy();
    }
}
