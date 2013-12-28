package cn.halen.data.pojo;

/**
 * User: hzhang
 * Date: 12/28/13
 * Time: 5:03 PM
 */
public class Configuration {
    private String key_space;
    private String key1;
    private String type;
    private String value;

    public String getKey_space() {
        return key_space;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setKey_space(String key_space) {
        this.key_space = key_space;
    }

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
