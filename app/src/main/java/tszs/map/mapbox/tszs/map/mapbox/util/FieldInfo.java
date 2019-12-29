package tszs.map.mapbox.tszs.map.mapbox.util;

public class FieldInfo {
    private String name;
    private String alias;
    private GlobalDataType type;
    private Integer length;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public GlobalDataType getType() {
        return type;
    }

    public void setType(GlobalDataType type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
