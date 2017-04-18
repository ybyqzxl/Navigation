package panda.li.navigation.entity;

import java.io.Serializable;

/**
 * Created by xueli on 2016/8/12.
 */
public class BusStation implements Serializable {


    private Object Area;
    private Object BusLines;
    private String BusStationCode;
    private String BusStationName;
    private double Distance;
    private double Latitude;
    private double Longitude;
    private String Status;

    private StantionToLineBean StantionToLine;

    public Object getArea() {
        return Area;
    }

    public void setArea(Object Area) {
        this.Area = Area;
    }

    public Object getBusLines() {
        return BusLines;
    }

    public void setBusLines(Object BusLines) {
        this.BusLines = BusLines;
    }

    public String getBusStationCode() {
        return BusStationCode;
    }

    public void setBusStationCode(String BusStationCode) {
        this.BusStationCode = BusStationCode;
    }

    public String getBusStationName() {
        return BusStationName;
    }

    public void setBusStationName(String BusStationName) {
        this.BusStationName = BusStationName;
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double Distance) {
        this.Distance = Distance;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double Latitude) {
        this.Latitude = Latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double Longitude) {
        this.Longitude = Longitude;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public StantionToLineBean getStantionToLine() {
        return StantionToLine;
    }

    public void setStantionToLine(StantionToLineBean StantionToLine) {
        this.StantionToLine = StantionToLine;
    }

    public static class StantionToLineBean implements Serializable {
        private String BusLineCode;
        private String BusLineSerial;
        private String Destination;

        public String getDestination() {
            return Destination;
        }

        public void setDestination(String destination) {
            Destination = destination;
        }

        private String UpDown;

        public String getBusLineCode() {
            return BusLineCode;
        }

        public void setBusLineCode(String BusLineCode) {
            this.BusLineCode = BusLineCode;
        }

        public String getBusLineSerial() {
            return BusLineSerial;
        }

        public void setBusLineSerial(String BusLineSerial) {
            this.BusLineSerial = BusLineSerial;
        }

        public String getUpDown() {
            return UpDown;
        }

        public void setUpDown(String UpDown) {
            this.UpDown = UpDown;
        }
    }
}
