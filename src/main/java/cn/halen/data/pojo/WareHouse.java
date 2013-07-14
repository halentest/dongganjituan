package cn.halen.data.pojo;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: hzhang
 * Date: 7/11/13
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class WareHouse {
    private int id;

    private String name;

    private String desc;

    private int type;

    private Date modified;

    private Date created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
