package panda.li.navigation.entity;

/**
 * Created by xueli on 2016/8/20.
 */
public class History {
    private int type;
    private String name;
    private String startPoint;
    private String endPonit;
    private String startName;
    private String endName;

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPonit() {
        return endPonit;
    }

    public void setEndPonit(String endPonit) {
        this.endPonit = endPonit;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
