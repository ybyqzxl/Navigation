package panda.li.navigation.callback;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;
import panda.li.navigation.entity.BusStation;

/**
 * Created by xueli on 2016/8/12.
 */
public abstract class BusStationCallBack extends Callback<BusStation> {
    @Override
    public BusStation parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        BusStation busStation = new Gson().fromJson(string, BusStation.class);
        return busStation;
    }
}
