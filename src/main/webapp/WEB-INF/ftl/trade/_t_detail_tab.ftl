<div class="tab">
    网店单号：${trade.tid!''} 订单状态：
    <font color="red">
        ${trade.tradeStatus.desc!''},
        <#if trade.is_cancel==1>
            已取消
        <#elseif trade.is_finish==1>
            已结束
        <#elseif trade.is_refund==1>
            退换货
        </#if>
    </font>
    <br>  <br>
    <a href="${rc.contextPath}/trade/trade_detail?id=${trade.id}">订单详情</a>
    <a href="${rc.contextPath}/trade/action/cancel_trade_form?id=${trade.id}">取消订单</a>
    <a href="${rc.contextPath}/trade/cancel_trade?id=${trade.id}">申请取消</a>
</div>