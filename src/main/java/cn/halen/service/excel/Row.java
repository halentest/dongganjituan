package cn.halen.service.excel;

/**
 * Created with IntelliJ IDEA.
 * User: hzhang
 * Date: 7/6/13
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Row {
    private String goodsId;

    private String color;

    private String size;

    private int count;

    public Row(String goodsId, String color, String size, int count) {
        this.goodsId = goodsId;
        this.color = color;
        this.size = size;
        this.count = count;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
