package cn.halen.service.excel;

/**
 * Created with IntelliJ IDEA.
 * User: hzhang
 * Date: 8/15/13
 * Time: 7:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradeRow {

    private String shopName;

    private String tradeId;

    private String goodsId;

    private String color;

    private String size;

    private int num;

    private String name;

    private String mobile;

    private String phone;

    private String address;

    private String comment;

    private String delivery;

    private String title;

    public int getPrice() {
        return price;
    }

    private int price;

    public TradeRow(String shopName, String tradeId, String goodsId, String color, String size, int num, String name,
                    String mobile, String phone, String address, String comment, String delivery, String title, int price) {
        this.shopName = shopName;
        this.tradeId = tradeId;
        this.goodsId = goodsId;
        this.color = color;
        this.size = size;
        this.num = num;
        this.name = name;
        this.mobile = mobile;
        this.phone = phone;
        this.address = address;
        this.comment = comment;
        this.delivery = delivery;
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }
}
