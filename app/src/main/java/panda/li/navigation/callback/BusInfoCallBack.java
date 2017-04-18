package panda.li.navigation.callback;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;
import panda.li.navigation.entity.BusInfo;

/**
 * Created by xueli on 2016/8/13.
 */
public abstract class BusInfoCallBack extends Callback<BusInfo> {
    @Override
    public BusInfo parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        BusInfo info = new Gson().fromJson(string, BusInfo.class);
        return info;
    }
}
