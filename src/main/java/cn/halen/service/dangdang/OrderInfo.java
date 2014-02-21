package cn.halen.service.dangdang;

import com.taobao.api.domain.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hzhang
 * Date: 2/20/14
 * Time: 8:37 PM
 */
public class OrderInfo {
    private String orderID;
    private int orderState;
    private String message;
    private String remark;
    private String paymentDate;
    private int orderMode;  //1:自发  2:代发
    private String buyerPayMode;
    private float goodsMoney;
    private float realPaidAmount;
    private float postage;
    private String dangdangAccountID;
    private String consigneeName;
    private String consigneeAddr;
    private String consigneeAddr_State;
    private String consigneeAddr_Province;
    private String consigneeAddr_City;
    private String consigneeAddr_Area;
    private String consigneePostcode;
    private String consigneeTel;
    private String consigneeMobileTel;
    private String sendGoodsMode;
    private String sendCompany;

    private List<ItemInfo> items = new ArrayList<ItemInfo>();

    public List<ItemInfo> getItems() {
        return items;
    }

    public void setItems(List<ItemInfo> items) {
        this.items = items;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(int orderMode) {
        this.orderMode = orderMode;
    }

    public String getBuyerPayMode() {
        return buyerPayMode;
    }

    public void setBuyerPayMode(String buyerPayMode) {
        this.buyerPayMode = buyerPayMode;
    }

    public float getGoodsMoney() {
        return goodsMoney;
    }

    public void setGoodsMoney(float goodsMoney) {
        this.goodsMoney = goodsMoney;
    }

    public float getRealPaidAmount() {
        return realPaidAmount;
    }

    public void setRealPaidAmount(float realPaidAmount) {
        this.realPaidAmount = realPaidAmount;
    }

    public float getPostage() {
        return postage;
    }

    public void setPostage(float postage) {
        this.postage = postage;
    }

    public String getDangdangAccountID() {
        return dangdangAccountID;
    }

    public void setDangdangAccountID(String dangdangAccountID) {
        this.dangdangAccountID = dangdangAccountID;
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

    public String getSendGoodsMode() {
        return sendGoodsMode;
    }

    public void setSendGoodsMode(String sendGoodsMode) {
        this.sendGoodsMode = sendGoodsMode;
    }

    public String getSendCompany() {
        return sendCompany;
    }

    public void setSendCompany(String sendCompany) {
        this.sendCompany = sendCompany;
    }
}
