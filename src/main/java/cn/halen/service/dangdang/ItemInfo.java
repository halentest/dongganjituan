package cn.halen.service.dangdang;

/**
 * User: hzhang
 * Date: 2/20/14
 * Time: 8:44 PM
 */
public class ItemInfo {
    private String itemID;
    private String outerItemID;
    private String itemName;
    private int itemType;        //0,商品  1，赠品
    private String specialAttribute;
    private float marketPrice;
    private float unitPrice;
    private int orderCount;

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getOuterItemID() {
        return outerItemID;
    }

    public void setOuterItemID(String outerItemID) {
        this.outerItemID = outerItemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getSpecialAttribute() {
        return specialAttribute;
    }

    public void setSpecialAttribute(String specialAttribute) {
        this.specialAttribute = specialAttribute;
    }

    public float getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(float marketPrice) {
        this.marketPrice = marketPrice;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }
}
