package panda.li.navigation.entity;

import java.io.Serializable;

/**
 * Created by xueli on 2016/8/19.
 */
public class SearchInfo implements Serializable {


    /**
     * IsName : 1
     * SearchName : 中基礼域
     * Status : null
     * UpDown : 1
     */

    private String IsName;
    private String SearchName;
    private Object Status;
    private String UpDown;

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    private String Destination;

    public String getIsName() {
        return IsName;
    }

    public void setIsName(String IsName) {
        this.IsName = IsName;
    }

    public String getSearchName() {
        return SearchName;
    }

    public void setSearchName(String SearchName) {
        this.SearchName = SearchName;
    }

    public Object getStatus() {
        return Status;
    }

    public void setStatus(Object Status) {
        this.Status = Status;
    }

    public String getUpDown() {
        return UpDown;
    }

    public void setUpDown(String UpDown) {
        this.UpDown = UpDown;
    }
}
