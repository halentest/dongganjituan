package cn.halen.controller;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.ConfigurationMapper;
import cn.halen.data.mapper.MyTradeMapper;
import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.SellerInfo;
import cn.halen.data.pojo.TradeStatus;
import cn.halen.service.ResultInfo;
import cn.halen.util.Constants;
import com.sf.integration.expressservice.service.CommonServiceService;
import cn.halen.service.RequestXmlBuilder;
import com.sf.module.ewaybill.util.GenerationWaybillImage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 对接顺丰快递
 */
@Controller
public class SFController {

    @Autowired
    private MyTradeMapper tradeMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RequestXmlBuilder xmlBuilder;

    @Autowired
    private ConfigurationMapper configurationMapper;

    private FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value="/trade/sf/order")
    public @ResponseBody ResultInfo order(@RequestParam String ids) {
        ResultInfo ri = new ResultInfo();
        if(StringUtils.isBlank(ids)) {
            return ri;
        }
        String[] idArr = ids.split(",");
        StringBuilder errorBuilder = new StringBuilder();
        for(String id : idArr) {
            if(StringUtils.isBlank(id)) {
                continue;
            }
            MyTrade trade = tradeMapper.selectTradeMap(id);
            if(null == trade) {
                continue;
            }

            if(trade.getSf_status()!=0) {
                ri.setSuccess(false);
                errorBuilder.append(id).append(":").append("当前状态不能下单!").append("\r\n");
                continue;
            }

            if(!trade.getStatus().equals(TradeStatus.WaitFind.getStatus())) {
                ri.setSuccess(false);
                errorBuilder.append(id).append(":").append("当前状态不是待拣货, 不能下单!").append("\r\n");
                continue;
            }

            if(!"顺丰速运".equals(trade.getDelivery())) {
                ri.setSuccess(false);
                errorBuilder.append(id).append(":").append("非顺丰快递订单, 不能下单!").append("\r\n");
                continue;
            }

            CommonServiceService service = new CommonServiceService();

            String resp = null;
            try {
                resp = service.getCommonServicePort().sfexpressService(xmlBuilder.orderRequest(trade, adminMapper.selectSellerInfo()));
            } catch(Exception e) {
                log.error("error while make sf order, trade id is {}, {}", id, e);
                ri.setSuccess(false);
                errorBuilder.append(id).append(":").append("下单失败!").append("\r\n");
                continue;
            }
            log.debug("resp for id {} is {}", id, resp);
            try {
                DocumentBuilder builder = xmlBuilder.getBuilder();
                Document doc = builder.parse(new ByteArrayInputStream(resp.getBytes()));
                Node head = doc.getElementsByTagName("Head").item(0);
                String result = head.getTextContent();
                if("OK".equals(result)) {
                    Node orderResp = doc.getElementsByTagName("OrderResponse").item(0);
                    NamedNodeMap map = orderResp.getAttributes();
                    String filterResult = map.getNamedItem("filter_result").getTextContent();
                    if("3".equals(filterResult)) {
                        String remark = map.getNamedItem("remark").getTextContent();
                        String reason = null;
                        if("1".equals(remark)) {
                            reason = "收方超范围";
                        } else if("2".equals(remark)) {
                            reason = "派方超范围";
                        } else {
                            reason = "其他原因";
                        }
                        ri.setSuccess(false);
                        errorBuilder.append(id).append(":").append("下单失败, 失败原因: ").append(reason).append("\r\n");
                        trade.setSf_status(2);
                        tradeMapper.updateMyTrade(trade);
                        continue;
                    } else if("1".equals(filterResult)) {
                        ri.setSuccess(false);
                        errorBuilder.append(id).append(":").append("下单失败，不能派送!").append("\r\n");
                        trade.setSf_status(2);
                        tradeMapper.updateMyTrade(trade);
                        continue;
                    }
                    String mailno = map.getNamedItem("mailno").getTextContent();
                    String destcode = map.getNamedItem("destcode").getTextContent();
                    String origincode = map.getNamedItem("origincode").getTextContent();
                    trade.setDelivery_number(mailno);
                    trade.setDestcode(destcode);
                    trade.setOrigincode(origincode);
                    trade.setSf_status(1);
                    tradeMapper.updateMyTrade(trade);
                } else {
                    Node errorNode = doc.getElementsByTagName("ERROR").item(0);
                    String errorCode = errorNode.getAttributes().getNamedItem("code").getTextContent();
                    String errorMsg = errorNode.getTextContent();
                    log.error("error while order sf for id {}, error code is {}, error msg is {}", id, errorCode, errorMsg);
                    ri.setSuccess(false);
                    errorBuilder.append(id).append(":").append("下单失败, ").append(errorMsg).append("\r\n");
                }
            } catch (Exception e) {
                log.error("parse error,", e);
                ri.setSuccess(false);
                errorBuilder.append(id).append(":").append("顺丰服务器异常, 下单失败!").append("\r\n");
            }
        }
        ri.setErrorInfo(errorBuilder.toString());
        return ri;
    }

    @RequestMapping(value="/trade/sf/print")
    public @ResponseBody ResultInfo print(HttpServletRequest req, HttpServletResponse resp, @RequestParam String ids) {
        String[] idArr = ids.split(",");
        StringBuilder builder = new StringBuilder();
        for(String id : idArr) {
            if(StringUtils.isBlank(id)) {
                continue;
            }
            MyTrade trade = tradeMapper.selectTradeMap(id);
            if(null == trade) {
                continue;
            }

            if(trade.getSf_status()!=1 || StringUtils.isBlank(trade.getDelivery_number()) || !"顺丰速运".equals(trade.getDelivery())) {
                continue;
            }
            String root = req.getServletContext().getRealPath("img/sf");
            File rootDir = new File(root);
            if(!rootDir.exists()) {
                rootDir.mkdir();
            }
            String[] deliveryNumbers = trade.getDelivery_number().split(",");
            for(String deliveryNumber : deliveryNumbers) {
                if(StringUtils.isNotBlank(deliveryNumber)) {
                    String path = req.getServletContext().getRealPath("img/sf/" + id + "-" + deliveryNumber + ".png");
                    File f = new File(path);
                    if(!f.exists()) {
                        generagePic(adminMapper.selectSellerInfo(), trade, path, deliveryNumber);
                    }
                    builder.append("img/sf/" + id + "-" + deliveryNumber + ".png");
                    builder.append(",");
                }
            }
        }
        ResultInfo ri = new ResultInfo();
        ri.setErrorInfo(builder.toString());
        return ri;
    }

    private void generagePic(SellerInfo sellerInfo, MyTrade trade, String path, String deliveryNumber) {

        Map<String, Object> valueMap2 = new HashMap<String, Object>();
        // String number = WaybillNoValidator.getWaybillNo("12345678912");
        // A5参数
        valueMap2.put("waybillNo", deliveryNumber);
        valueMap2.put("sourceZoneCode", trade.getOrigincode());
     //   valueMap2.put("destZoneCode", trade.getDestcode());
    //    valueMap2.put("selfSend", "自寄");
    //    valueMap2.put("selfPickup", "自取");

        StringBuilder builder = new StringBuilder();
        // 合并寄件人信息
        builder.append(sellerInfo.getFrom_state()).append(" ").append(sellerInfo.getFrom_city()).append(" ")
                .append(sellerInfo.getFrom_address()).append(" ").append(sellerInfo.getSender())
                .append(" ").append(sellerInfo.getMobile());
        valueMap2.put("EXT_SHIPPER_INFO", builder.toString());
        // 合并收件人信息
        builder = new StringBuilder();
        builder.append(trade.getState()).append(" ").append(trade.getCity())
                .append(" ").append(trade.getDistrict()).append(" ").append(trade.getAddress())
                .append(" ").append(trade.getName())
                .append(" ").append(trade.getMobile())
                .append(" ").append(trade.getPhone());
        valueMap2.put("EXT_ADDRESSEE_INFO", builder.toString());

        // 付款方式
        String payMethod = "寄付";
        if(trade.getPay_method() == Constants.PAY_METHOD_RECEIVER) {
            payMethod = "到付";
        } else if(trade.getPay_method() == Constants.PAY_METHOD_OTHER) {
            payMethod = "第三方付";
        }
        valueMap2.put("EXT_PAY_INFO", payMethod);
        // 合并附加服务信息
        builder = new StringBuilder();
        boolean cod = trade.getPay_type() == Constants.PAY_TYPE_AFTER_RECEIVE;
        String customId = configurationMapper.selectByKey1("default", "custom_id", "5953106803").getValue();
        String yuejie = configurationMapper.selectByKey1("default", "yuejie", customId).getValue();
        if(cod) {
            int payment = (trade.getPayment() + trade.getDelivery_money())/100;
            builder.append("代收货款:").append(payment).append("元")
                    .append("     卡号:").append(yuejie)
                    .append("\n");
        }
        int insureValue = Integer.parseInt(configurationMapper.selectByKey1("default", "insure_value", "20000").getValue());
        int isInsure = Integer.parseInt(configurationMapper.selectByKey1("default", "is_insure", "0").getValue());
        boolean bIsInsure = trade.getIs_insure()==-1?(isInsure==1?true:false) : (trade.getIs_insure()==1?true:false);
        if(bIsInsure) {
            int value = trade.getInsure_value()==-1?insureValue/100 : trade.getInsure_value()/100;
            builder.append("申明价值:").append(value).append("元");
        }
        valueMap2.put("EXT_SRV_INFO", builder.toString());

//        valueMap2.put("cons_name", StringUtils.isBlank(trade.getCargo())?cargoBuilder.toString() : trade.getCargo());

        valueMap2.put("waybillCount", trade.getParcel_quantity());
        String expressType = configurationMapper.selectByKey1("default", "express_type", "3").getValue();
        valueMap2.put("expressType", expressType);
//        valueMap2.put("total_amount", "合计");
        valueMap2.put("custCode", yuejie);
//        valueMap2.put("total_amount2", "合计2");

        GenerationWaybillImage.generationImageA5(valueMap2, path + ".png");

        //添加电商特惠
        String s = "电商特惠";
        if("1".equals(expressType)) {
            s = "标准快递";
        } else if("2".equals(expressType)) {
            s = "顺丰特惠";
        }

        File file = new File(path + ".png");

        Font font = new Font("黑体", Font.BOLD, 40);

        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
        } catch (IOException e) {
            log.error("read pic error", e);
        }
        Graphics2D g2 = (Graphics2D)bi.getGraphics();
        g2.setFont(font);
//        g2.setBackground(Color.WHITE);
//        g2.clearRect(0, 0, width, height);
        g2.setPaint(Color.black);

        g2.drawString(s, 800, 640);

        g2.drawString("电子秤 集团客户", 760, 1170);

        //添加打印时间
        g2.setFont(new Font("黑体", Font.ITALIC, 30));
        String printDate = format.format(new Date());
        g2.drawString(printDate, 300, 640);

//        g2.setFont(new Font("黑体", Font.BOLD, 280));
//        g2.drawString("E", 330, 200);

        g2.setFont(new Font("黑体", Font.BOLD, 180));
        int left = 800;
        if(StringUtils.isNotBlank(trade.getDestcode()) && trade.getDestcode().length()>3) {
            left = 650;
        }
        g2.drawString(trade.getDestcode(), left, 500);

        g2.setFont(new Font("黑体", Font.BOLD, 30));

        int origin = 1005;
        int interval = 30;
        for(MyOrder order : trade.getMyOrderList()) {
            StringBuilder cargoBuilder = new StringBuilder();
            cargoBuilder.append(order.getGoods_id()).append(" ")
                    .append(order.getSku().getColor()).append(" ")
                    .append(order.getSku().getSize()).append(" ")
                    .append(order.getQuantity());
            g2.drawString(cargoBuilder.toString(), 30, origin);
            origin += interval;
        }

        try {
            ImageIO.write(bi, "png", new File(path));
        } catch (IOException e) {
            log.error("write pic error", e);
        }
        //删除临时文件
        if(file.exists()) {
            file.delete();
        }
    }

    @RequestMapping(value="/trade/sf/export")
    public @ResponseBody ResultInfo export(HttpServletRequest req, HttpServletResponse resp, @RequestParam String ids) {
        String[] idArr = ids.split(",");
        for(String id : idArr) {
            if(StringUtils.isBlank(id)) {
                continue;
            }
            MyTrade trade = tradeMapper.selectTradeMap(id);
            if(null == trade) {
                continue;
            }

            if(trade.getSf_status()!=1 || StringUtils.isBlank(trade.getDelivery_number()) || !"顺丰速运".equals(trade.getDelivery())
                    || !trade.getStatus().equals(TradeStatus.WaitFind.getStatus())) {
                continue;
            }

            trade.setStatus(TradeStatus.WaitOut.getStatus());
            trade.setScan_time(new Date());
            tradeMapper.updateMyTrade(trade);
        }
        ResultInfo ri = new ResultInfo();
        return ri;
    }

    @RequestMapping(value="/trade/action/sf_modify_parcel_quantity")
    public @ResponseBody ResultInfo modifyParcelQuantity(@RequestParam String id, @RequestParam int v) {
        ResultInfo ri = new ResultInfo();
        MyTrade trade = tradeMapper.selectById(id);
        if(trade == null) {
            ri.setSuccess(false);
            ri.setErrorInfo("订单不存在!");
            return ri;
        }
        if(!trade.getDelivery().equals("顺丰速运") || trade.getSf_status()!=0 || trade.getStatus().equals(TradeStatus.WaitReceive.getStatus())) {
            ri.setSuccess(false);
            ri.setErrorInfo("当前状态不能修改包裹数量!");
            return ri;
        }
        if(v < 1) {
            ri.setSuccess(false);
            ri.setErrorInfo("包裹数量必须大于或者等于1");
            return ri;
        }
        trade.setParcel_quantity(v);
        tradeMapper.updateMyTrade(trade);
        return ri;
    }

}