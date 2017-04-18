package panda.li.navigation.callback;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.callback.Callback;

import java.util.List;

import okhttp3.Response;
import panda.li.navigation.entity.SearchInfo;

/**
 * Created by xueli on 2016/8/19.
 */
public abstract class SearchInfoCallBack extends Callback<List<SearchInfo>> {
    @Override
    public List<SearchInfo> parseNetworkResponse(Response response, int id) throws Exception {
        String json = response.body().string();
        List<SearchInfo> searchInfos = new Gson().fromJson(json, new TypeToken<List<SearchInfo>>() {
        }.getType());
        return searchInfos;
    }
}
