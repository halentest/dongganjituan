package cn.halen.service.excel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hzhang
 * Date: 7/6/13
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoodsRow {
    private String goodsId;

    private String name;

    private int price;

    private List<String> colors;

    private List<String> colorIds;

    private List<String> sizes;

    public GoodsRow(String goodsId, String name, int price, List<String> colorIds, List<String> colors, List<String> sizes) {
        this.goodsId = goodsId;
        this.name = name;
        this.price = price;
        this.colorIds = colorIds;
        this.colors = colors;
        this.sizes = sizes;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public List<String> getColorIds() {
        return colorIds;
    }

    public void setColorIds(List<String> colorIds) {
        this.colorIds = colorIds;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
