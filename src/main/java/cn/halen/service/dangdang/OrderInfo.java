package cn.halen.service.dangdang;

/**
 * User: hzhang
 * Date: 2/20/14
 * Time: 8:37 PM
 */
public class OrderInfo {
    private int orderID;
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
}
