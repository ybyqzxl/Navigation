package panda.li.navigation.callback;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.callback.Callback;

import java.util.List;

import okhttp3.Response;
import panda.li.navigation.entity.BusStation;

/**
 * Created by xueli on 2016/8/12.
 */
public abstract class BusStationJsonCallBack extends Callback<List<BusStation>> {

    @Override
    public List<BusStation> parseNetworkResponse(Response response, int id) throws Exception {

        String string = response.body().string();
        List<BusStation> json = new Gson().fromJson(string, new TypeToken<List<BusStation>>() {
        }.getType());
        return json;
    }
}
