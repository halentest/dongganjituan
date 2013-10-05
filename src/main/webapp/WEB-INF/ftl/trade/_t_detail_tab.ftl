<div class="tab">
    网店单号：${trade.tid!''} 订单状态：
    <font color="red">
        ${trade.tradeStatus.desc!''},
        <#if trade.is_cancel==-1>
            申请取消
        <#elseif trade.is_cancel==1>
            已取消
        <#elseif trade.is_finish==1>
            已结束
        <#elseif trade.is_refund==1>
            退换货
        </#if>
    </font>
    <br>  <br>
    <a href="${rc.contextPath}/trade/trade_detail?id=${trade.id}">订单详情</a>
    <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor">
        <#if (trade.status=="UnSubmit" || trade.status=="WaitSend") && trade.is_cancel==0>
            <a href="${rc.contextPath}/trade/action/cancel_trade_form?id=${trade.id}">取消订单</a>
        <#elseif (trade.status=="WaitFind" || trade.status=="WaitOut") && trade.is_cancel==0>
            <a href="${rc.contextPath}/trade/action/cancel_trade_form?id=${trade.id}&isApply=true">申请取消</a>
        </#if>

    </#if>
    <#if trade.is_send==1>
        <a href="${rc.contextPath}/trade/action/apply_refund_form?id=${trade.id}">申请退换货</a>
    </#if>
    <#if CURRENT_USER.type=="WareHouse" && trade.is_cancel==-1>
        <a href="${rc.contextPath}/trade/action/cancel_trade?id=${trade.id}">同意取消</a>
    </#if>
</div>