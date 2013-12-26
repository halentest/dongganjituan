package cn.halen.controller;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.MyTradeMapper;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.SellerInfo;
import cn.halen.data.pojo.TradeStatus;
import cn.halen.service.ResultInfo;
import com.sf.integration.expressservice.service.CommonServiceService;
import com.sf.integration.expressservice.service.RequestXmlBuilder;
import com.sf.module.ewaybill.util.GenerationWaybillImage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
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
                resp = service.getCommonServicePort().sfexpressService(RequestXmlBuilder.orderRequest(trade, adminMapper.selectSellerInfo()));
            } catch(Exception e) {
                log.error("error while make sf order, trade id is {}, {}", id, e);
                ri.setSuccess(false);
                errorBuilder.append(id).append(":").append("下单失败!").append("\r\n");
                continue;
            }
            log.debug("resp for id {} is {}", id, resp);
            try {
                DocumentBuilder builder = RequestXmlBuilder.getBuilder();
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

            String path = req.getServletContext().getRealPath("img/sf/" + id + ".png");
            File f = new File(path);
            if(!f.exists()) {
                generagePic(adminMapper.selectSellerInfo(), trade, path);
            }
            builder.append("img/sf/" + id + ".png");
            builder.append(",");
        }
        ResultInfo ri = new ResultInfo();
        ri.setErrorInfo(builder.toString());
        return ri;
    }

    private void generagePic(SellerInfo sellerInfo, MyTrade trade, String path) {

        Map<String, Object> valueMap2 = new HashMap<String, Object>();
        // String number = WaybillNoValidator.getWaybillNo("12345678912");
        // A5参数
        valueMap2.put("waybillNo", trade.getDelivery_number());
        valueMap2.put("sourceZoneCode", trade.getOrigincode());
        valueMap2.put("destZoneCode", trade.getDestcode());
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
                .append(" ").append(trade.getMobile());
        valueMap2.put("EXT_ADDRESSEE_INFO", builder.toString());
        // 付款方式
        valueMap2.put("EXT_PAY_INFO", "寄付");

        valueMap2.put("cons_name", "鞋子");

        valueMap2.put("waybillCount", "1");
        valueMap2.put("expressType", "3");
//        valueMap2.put("total_amount", "合计");
        valueMap2.put("custCode", RequestXmlBuilder.CUSTOMER_ID);
//        valueMap2.put("total_amount2", "合计2");

        GenerationWaybillImage.generationImageA5(valueMap2, path + ".png");

        //添加电商特惠
        String s = "电商特惠";

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

}