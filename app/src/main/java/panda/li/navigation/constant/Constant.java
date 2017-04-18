package panda.li.navigation.constant;

import com.amap.api.services.core.LatLonPoint;

/**
 * Created by xueli on 2016/8/12.
 */
public class Constant {

    private static Constant constant;


    public static synchronized Constant getInstance() {
        if (constant == null) {
            constant = new Constant();
        }
        return constant;
    }

    public final static String SHARED_HISTORY = "HISTORY";

    private final static String BASE_URL = "http://10.1.15.247:8080/Service1.svc/";

    //模糊搜索
    public final static String LOAD_SEARCH = BASE_URL + "GetSearchByLineOrName";

    //根据车站查找公交车线路
    public final static String LOAD_BUS_LINE_BY_STATION = BASE_URL + "GetBusLinesByStationName";
    //查找最近车站
    public final static String LOAD_NEAR_STATION = BASE_URL + "GetBusStations";
    //显示公交车线路
    public final static String LOAD_BUS_LOCATION = BASE_URL + "GetAllBusByBusLine";
    //显示公交车状态
    public final static String LOAD_BUS_STATE = BASE_URL + "GetLateBus";
    //获取公交车线路所有站台
    public final static String LOAD_AllBUSLINE_BY_LINE = BASE_URL + "GetAllBusStationByLine";
    //获取步行距离和时间
    public final static String LOAD_TIMEANDDISTANCE = BASE_URL + "GetWalkTime";
    //上传用户信息
    public final static String PUBLISH_USERINFO = BASE_URL + "GetAppLocationInfo";

    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private LatLonPoint latLonPoint;

    private String stationName;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public LatLonPoint getLatLonPoint() {
        return latLonPoint;
    }

    public void setLatLonPoint(LatLonPoint latLng) {
        this.latLonPoint = latLng;
    }
}
