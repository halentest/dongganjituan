<#macro detail_tab current_tab="detail">
<style>
    .tabbtn {
        -webkit-background-clip: border-box;
        -webkit-background-origin: padding-box;
        -webkit-background-size: auto;
        background-attachment: scroll;
        background-clip: border-box;
        background-color: rgba(0, 0, 0, 0);
        background-image: url(http://www.lanrenzhijia.com/demo/1206/tab/images/tabbg.gif);
        background-origin: padding-box;
        background-size: auto;
        border-left-color: rgb(221, 221, 221);
        border-left-style: solid;
        border-left-width: 1px;
        border-right-color: rgb(221, 221, 221);
        border-right-style: solid;
        border-right-width: 1px;
        display: block;
        font-family: Arial, Helvetica, sans-serif, 宋体;
        font-size: 12px;
        font-style: normal;
        font-variant: normal;
        font-weight: normal;
        height: 30px;
        line-height: 21px;
        list-style-type: none;
        margin-bottom: 0px;
        margin-left: 0px;
        margin-right: 0px;
        margin-top: 0px;
        padding-bottom: 0px;
        padding-left: 0px;
        padding-right: 0px;
        padding-top: 0px;
    }

    .tabbtn li {
        display: list-item;
        float: left;
        font-family: Arial, Helvetica, sans-serif, 宋体;
        font-size: 12px;
        font-style: normal;
        font-variant: normal;
        font-weight: normal;
        height: 30px;
        line-height: 21px;
        list-style-type: none;
        margin-bottom: 0px;
        margin-left: -1px;
        margin-right: 0px;
        margin-top: 0px;
        padding-bottom: 0px;
        padding-left: 0px;
        padding-right: 0px;
        padding-top: 0px;
        position: relative;
        text-align: left;
        width: 108px;
    }

    .tabbtn li.current {
        border-left-color: rgb(213, 213, 213);
        border-left-style: solid;
        border-left-width: 1px;
        border-right-color: rgb(213, 213, 213);
        border-right-style: solid;
        border-right-width: 1px;
        border-top-color: rgb(197, 197, 197);
        border-top-style: solid;
        border-top-width: 1px;
        display: list-item;
        float: left;
        font-family: Arial, Helvetica, sans-serif, 宋体;
        font-size: 12px;
        font-style: normal;
        font-variant: normal;
        font-weight: normal;
        height: 29px;
        line-height: 21px;
        list-style-type: none;
        margin-bottom: 0px;
        margin-left: -1px;
        margin-right: 0px;
        margin-top: 0px;
        padding-bottom: 0px;
        padding-left: 0px;
        padding-right: 0px;
        padding-top: 0px;
        position: relative;
        text-align: left;
        width: 108px;
    }

    .tabbtn li.current a {
        -webkit-background-clip: border-box;
        -webkit-background-origin: padding-box;
        -webkit-background-size: auto;
        background-attachment: scroll;
        background-clip: border-box;
        background-color: rgb(255, 255, 255);
        background-image: none;
        background-origin: padding-box;
        background-size: auto;
        border-bottom-color: rgb(51, 102, 204);
        border-bottom-style: none;
        border-bottom-width: 0px;
        border-image-outset: 0px;
        border-image-repeat: stretch;
        border-image-slice: 100%;
        border-image-source: none;
        border-image-width: 1;
        border-left-color: rgb(51, 102, 204);
        border-left-style: none;
        border-left-width: 0px;
        border-right-color: rgb(51, 102, 204);
        border-right-style: none;
        border-right-width: 0px;
        border-top-color: rgb(255, 102, 0);
        border-top-style: solid;
        border-top-width: 2px;
        color: rgb(51, 102, 204);
        cursor: pointer;
        display: block;
        float: left;
        font-family: Arial, Helvetica, sans-serif, 宋体;
        font-size: 12px;
        font-style: normal;
        font-variant: normal;
        font-weight: 800;
        height: 27px;
        line-height: 27px;
        list-style-type: none;
        margin-bottom: 0px;
        margin-left: 0px;
        margin-right: 0px;
        margin-top: 0px;
        overflow-x: hidden;
        overflow-y: hidden;
        padding-bottom: 0px;
        padding-left: 0px;
        padding-right: 0px;
        padding-top: 0px;
        text-align: center;
        text-decoration: none;
        width: 108px;
    }

    .tabbtn li a {
        border-bottom-color: rgb(51, 51, 51);
        border-bottom-style: none;
        border-bottom-width: 0px;
        border-image-outset: 0px;
        border-image-repeat: stretch;
        border-image-slice: 100%;
        border-image-source: none;
        border-image-width: 1;
        border-left-color: rgb(51, 51, 51);
        border-left-style: none;
        border-left-width: 0px;
        border-right-color: rgb(51, 51, 51);
        border-right-style: none;
        border-right-width: 0px;
        border-top-color: rgb(51, 51, 51);
        border-top-style: none;
        border-top-width: 0px;
        color: rgb(51, 51, 51);
        cursor: pointer;
        display: block;
        float: left;
        font-family: Arial, Helvetica, sans-serif, 宋体;
        font-size: 12px;
        font-style: normal;
        font-variant: normal;
        font-weight: normal;
        height: 30px;
        line-height: 30px;
        list-style-type: none;
        margin-bottom: 0px;
        margin-left: 0px;
        margin-right: 0px;
        margin-top: 0px;
        overflow-x: hidden;
        overflow-y: hidden;
        padding-bottom: 0px;
        padding-left: 0px;
        padding-right: 0px;
        padding-top: 0px;
        text-align: center;
        text-decoration: none;
        width: 108px;
    }
</style>
<div class="tab">
    网店单号：${trade.tid!''} 订单状态：
    <font color="red">
        ${trade.tradeStatus.desc!''}
        <#if trade.is_cancel==-1>
            ，申请取消
        <#elseif trade.is_cancel==1>
            ，已取消
        <#elseif trade.is_finish==1>
            ，已结束
        <#elseif trade.is_refund==1>
            ，退换货
        </#if>
        <#if trade.is_pause==1>
            ，已暂停
        </#if>
        <#if trade.is_apply_refund==1>
            ，已申请退款
        </#if>
    </font>
    <br>  <br>
    <ul class="tabbtn">
    <li <#if current_tab=="detail">class="current"</#if> ><a href="${rc.contextPath}/trade/trade_detail?id=${trade.id}">订单详情</a></li>
    <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor">
        <#if (trade.status=="UnSubmit" || trade.status=="WaitSend") && trade.is_cancel==0>
            <li <#if current_tab=="cancel">class="current"</#if> ><a href="${rc.contextPath}/trade/action/cancel_trade_form?id=${trade.id}">取消订单</a></li>
        <#elseif (trade.status=="WaitFind" || trade.status=="WaitOut") && trade.is_cancel==0>
            <li <#if current_tab=="cancel">class="current"</#if> ><a href="${rc.contextPath}/trade/action/cancel_trade_form?id=${trade.id}&isApply=true">申请取消</a></li>
        </#if>

    </#if>
    <#if trade.is_send==1>
        <li <#if current_tab=="apply_refund">class="current"</#if> ><a href="${rc.contextPath}/trade/action/apply_refund_form?id=${trade.id}">申请退换货</a></li>
    </#if>
    <#if CURRENT_USER.type=="WareHouse" && trade.is_cancel==-1>
        <li <#if current_tab=="approve_cancel">class="current"</#if> ><a href="${rc.contextPath}/trade/action/cancel_trade?id=${trade.id}">同意取消</a></li>
    </#if>
    </ul>
</div>
</#macro>