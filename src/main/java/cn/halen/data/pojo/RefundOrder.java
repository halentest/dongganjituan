package cn.halen.data.pojo;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: apple
 * Date: 13-10-2
 * Time: 下午11:32
 * To change this template use File | Settings | File Templates.
 */
public class RefundOrder {
    private long id;
    private String tid;
    private String goods_id;
    private String title;
    private String pic_path;
    private long sku_id;
    private long quantity;
    private long tui_quantity;
    private long huan_quantity;
    private long bad_quantity;
    private Date modified;
    private Date created;

    private MySku sku;

    public MySku getSku() {
        return sku;
    }

    public long getBad_quantity() {
        return bad_quantity;
    }

    public void setBad_quantity(long bad_quantity) {
        this.bad_quantity = bad_quantity;
    }

    public void setSku(MySku sku) {
        this.sku = sku;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic_path() {
        return pic_path;
    }

    public void setPic_path(String pic_path) {
        this.pic_path = pic_path;
    }

    public long getSku_id() {
        return sku_id;
    }

    public void setSku_id(long sku_id) {
        this.sku_id = sku_id;
    }

    public long getTui_quantity() {
        return tui_quantity;
    }

    public void setTui_quantity(long tui_quantity) {
        this.tui_quantity = tui_quantity;
    }

    public long getHuan_quantity() {
        return huan_quantity;
    }

    public void setHuan_quantity(long huan_quantity) {
        this.huan_quantity = huan_quantity;
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
