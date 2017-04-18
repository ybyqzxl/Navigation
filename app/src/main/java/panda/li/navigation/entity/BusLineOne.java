package panda.li.navigation.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xueli on 2016/8/12.
 */
public class BusLineOne implements Serializable {

    /**
     * Area : null
     * BusLines : [{"Area":null,"BusLineCode":"26","BusLineName":"26路","Destination":"棉六",
     * "UpDowns":"上行"},{"Area":null,"BusLineCode":"26","BusLineName":"26路","Destination":"南焦客运站",
     * "UpDowns":"下行"}]
     * BusStationCode : null
     * BusStationName : 省粮食局
     * Distance : 0
     * Latitude : 0
     * Longitude : 0
     * StantionToLine : null
     * Status : 成功
     */

    private Object Area;
    private Object BusStationCode;
    private String BusStationName;
    private int Distance;
    private int Latitude;
    private int Longitude;
    private Object StantionToLine;
    private String Status;
    /**
     * Area : null
     * BusLineCode : 26
     * BusLineName : 26路
     * Destination : 棉六
     * UpDowns : 上行
     */

    private List<BusLinesBean> BusLines;

    public Object getArea() {
        return Area;
    }

    public void setArea(Object Area) {
        this.Area = Area;
    }

    public Object getBusStationCode() {
        return BusStationCode;
    }

    public void setBusStationCode(Object BusStationCode) {
        this.BusStationCode = BusStationCode;
    }

    public String getBusStationName() {
        return BusStationName;
    }

    public void setBusStationName(String BusStationName) {
        this.BusStationName = BusStationName;
    }

    public int getDistance() {
        return Distance;
    }

    public void setDistance(int Distance) {
        this.Distance = Distance;
    }

    public int getLatitude() {
        return Latitude;
    }

    public void setLatitude(int Latitude) {
        this.Latitude = Latitude;
    }

    public int getLongitude() {
        return Longitude;
    }

    public void setLongitude(int Longitude) {
        this.Longitude = Longitude;
    }

    public Object getStantionToLine() {
        return StantionToLine;
    }

    public void setStantionToLine(Object StantionToLine) {
        this.StantionToLine = StantionToLine;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public List<BusLinesBean> getBusLines() {
        return BusLines;
    }

    public void setBusLines(List<BusLinesBean> BusLines) {
        this.BusLines = BusLines;
    }

    public static class BusLinesBean {
        private Object Area;
        private String BusLineCode;
        private String BusLineName;
        private String Destination;
        private String UpDowns;

        public Object getArea() {
            return Area;
        }

        public void setArea(Object Area) {
            this.Area = Area;
        }

        public String getBusLineCode() {
            return BusLineCode;
        }

        public void setBusLineCode(String BusLineCode) {
            this.BusLineCode = BusLineCode;
        }

        public String getBusLineName() {
            return BusLineName;
        }

        public void setBusLineName(String BusLineName) {
            this.BusLineName = BusLineName;
        }

        public String getDestination() {
            return Destination;
        }

        public void setDestination(String Destination) {
            this.Destination = Destination;
        }

        public String getUpDowns() {
            return UpDowns;
        }

        public void setUpDowns(String UpDowns) {
            this.UpDowns = UpDowns;
        }
    }
}
