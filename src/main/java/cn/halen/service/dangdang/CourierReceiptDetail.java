package cn.halen.service.dangdang;

/**
 * User: hzhang
 * Date: 2/25/14
 * Time: 10:26 AM
 * 快递单信息
 */
public class CourierReceiptDetail {
    private String receiptTitle;
    private String shopWarehouse;
    private String sendGoodsWarehouse;
    private String rejectWarehouse;
    private String orderID;
    private String orderCreateTime;
    private String consigneeName;
    private String consigneeAddr;
    private String consigneeAddr_State;
    private String consigneeAddr_Province;
    private String consigneeAddr_City;
    private String consigneeAddr_Area;
    private String consigneePostcode;
    private String consigneeTel;
    private String consigneeMobileTel;
    private String shopName;
    private String shopID;
    private String consignerName;
    private String consignerTel;
    private String consignerAddr;
    private String totalBarginPrice;
    private String sendGoodsTime;
    private String expressCompany;

    private String operCode;
    private String operation;
    private boolean success = true;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOperCode() {
        return operCode;
    }

    public void setOperCode(String operCode) {
        this.operCode = operCode;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getReceiptTitle() {
        return receiptTitle;
    }

    public void setReceiptTitle(String receiptTitle) {
        this.receiptTitle = receiptTitle;
    }

    public String getShopWarehouse() {
        return shopWarehouse;
    }

    public void setShopWarehouse(String shopWarehouse) {
        this.shopWarehouse = shopWarehouse;
    }

    public String getSendGoodsWarehouse() {
        return sendGoodsWarehouse;
    }

    public void setSendGoodsWarehouse(String sendGoodsWarehouse) {
        this.sendGoodsWarehouse = sendGoodsWarehouse;
    }

    public String getRejectWarehouse() {
        return rejectWarehouse;
    }

    public void setRejectWarehouse(String rejectWarehouse) {
        this.rejectWarehouse = rejectWarehouse;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneeAddr() {
        return consigneeAddr;
    }

    public void setConsigneeAddr(String consigneeAddr) {
        this.consigneeAddr = consigneeAddr;
    }

    public String getConsigneeAddr_State() {
        return consigneeAddr_State;
    }

    public void setConsigneeAddr_State(String consigneeAddr_State) {
        this.consigneeAddr_State = consigneeAddr_State;
    }

    public String getConsigneeAddr_Province() {
        return consigneeAddr_Province;
    }

    public void setConsigneeAddr_Province(String consigneeAddr_Province) {
        this.consigneeAddr_Province = consigneeAddr_Province;
    }

    public String getConsigneeAddr_City() {
        return consigneeAddr_City;
    }

    public void setConsigneeAddr_City(String consigneeAddr_City) {
        this.consigneeAddr_City = consigneeAddr_City;
    }

    public String getConsigneeAddr_Area() {
        return consigneeAddr_Area;
    }

    public void setConsigneeAddr_Area(String consigneeAddr_Area) {
        this.consigneeAddr_Area = consigneeAddr_Area;
    }

    public String getConsigneePostcode() {
        return consigneePostcode;
    }

    public void setConsigneePostcode(String consigneePostcode) {
        this.consigneePostcode = consigneePostcode;
    }

    public String getConsigneeTel() {
        return consigneeTel;
    }

    public void setConsigneeTel(String consigneeTel) {
        this.consigneeTel = consigneeTel;
    }

    public String getConsigneeMobileTel() {
        return consigneeMobileTel;
    }

    public void setConsigneeMobileTel(String consigneeMobileTel) {
        this.consigneeMobileTel = consigneeMobileTel;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getConsignerName() {
        return consignerName;
    }

    public void setConsignerName(String consignerName) {
        this.consignerName = consignerName;
    }

    public String getConsignerTel() {
        return consignerTel;
    }

    public void setConsignerTel(String consignerTel) {
        this.consignerTel = consignerTel;
    }

    public String getConsignerAddr() {
        return consignerAddr;
    }

    public void setConsignerAddr(String consignerAddr) {
        this.consignerAddr = consignerAddr;
    }

    public String getTotalBarginPrice() {
        return totalBarginPrice;
    }

    public void setTotalBarginPrice(String totalBarginPrice) {
        this.totalBarginPrice = totalBarginPrice;
    }

    public String getSendGoodsTime() {
        return sendGoodsTime;
    }

    public void setSendGoodsTime(String sendGoodsTime) {
        this.sendGoodsTime = sendGoodsTime;
    }

    public String getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }
}
