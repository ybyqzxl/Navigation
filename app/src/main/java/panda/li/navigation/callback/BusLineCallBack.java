package panda.li.navigation.callback;

import android.util.Log;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;
import panda.li.navigation.entity.BusLine;

/**
 * Created by xueli on 2016/8/12.
 */
public abstract class BusLineCallBack extends Callback<BusLine> {
    @Override
    public BusLine parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        BusLine busLine = new Gson().fromJson(string, BusLine.class);
        return busLine;
    }
}
