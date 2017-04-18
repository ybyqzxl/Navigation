package panda.li.navigation.entity;

/**
 * Created by xueli on 2016/8/13.
 */
public class BusInfo {
    /**
     * BusID : 4637
     * BusLine : 26
     * CurrentSpeed : 11.627000000000002
     * CurrentTime : /Date(1385000963000+0800)/
     * Distance : 0.1238
     * Latitude : 38.053663
     * Longitude : 114.496093
     * NextStopSerial : 7
     * Status : 成功
     * StopCount : 12
     * TimeMinute : 0.6388578309108109
     */

    private String BusID;
    private String BusLine;
    private double CurrentSpeed;
    private String CurrentTime;
    private double Distance;
    private double Latitude;
    private double Longitude;
    private String NextStopSerial;
    private String Status;
    private int StopCount;
    private double TimeMinute;
    private String BusFare;
    private String BusType;

    public String getBusFare() {
        return BusFare;
    }

    public void setBusFare(String busFare) {
        BusFare = busFare;
    }

    public String getBusType() {
        return BusType;
    }

    public void setBusType(String busType) {
        BusType = busType;
    }

    public String getBusID() {
        return BusID;
    }

    public void setBusID(String BusID) {
        this.BusID = BusID;
    }

    public String getBusLine() {
        return BusLine;
    }

    public void setBusLine(String BusLine) {
        this.BusLine = BusLine;
    }

    public double getCurrentSpeed() {
        return CurrentSpeed;
    }

    public void setCurrentSpeed(double CurrentSpeed) {
        this.CurrentSpeed = CurrentSpeed;
    }

    public String getCurrentTime() {
        return CurrentTime;
    }

    public void setCurrentTime(String CurrentTime) {
        this.CurrentTime = CurrentTime;
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

    public String getNextStopSerial() {
        return NextStopSerial;
    }

    public void setNextStopSerial(String NextStopSerial) {
        this.NextStopSerial = NextStopSerial;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public int getStopCount() {
        return StopCount;
    }

    public void setStopCount(int StopCount) {
        this.StopCount = StopCount;
    }

    public double getTimeMinute() {
        return TimeMinute;
    }

    public void setTimeMinute(double TimeMinute) {
        this.TimeMinute = TimeMinute;
    }
}
