<#macro buyer_info trade conf from="">
<style>
    .column-name {
        color: rgb(153, 153, 153);
        font-family: 宋体, arial, tahoma, sans-serif;
    }
</style>
<div class="left">
    <strong>客户信息</strong>
    <br>
    <span class="column-name">订&nbsp;&nbsp;单&nbsp;号: </span><a href="${rc.contextPath}/trade/trade_detail?id=${trade.id}">${trade.id!''}</a>
    <br>
    <span class="column-name">网&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;店：</span>${trade.seller_nick!''}
    <br>
    <span class="column-name">网店单号: </span>${trade.tid!''}<br>
    <span class="column-name">客户姓名: </span>${trade.name!''}  <br>
    <#if trade.buyer_nick?? && trade.buyer_nick?length gt 0>
        <span class="column-name">买&nbsp;&nbsp;家&nbsp;ID: </span>${trade.buyer_nick!''}
        <br>
    </#if>
    <span class="column-name">手&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;机：</span>${trade.mobile!''}
    <br>
    <span class="column-name">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话：</span>${trade.phone!''}
    <br>
    <span class="column-name">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：</span>${trade.state!''} ${trade.city!''} ${trade.district!''} ${trade.address!''}
    <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="WareHouse">
        <#if (trade.status=="UnSubmit" || trade.status=="WaitSend" || trade.status=="WaitFind") && trade.is_cancel==0>
        <a href="${rc.contextPath}/trade/action/modify_receiver_info_form?id=${trade.id}&from=${from}">修改</a>
        </#if>
    </#if>
    <br>
    <#if trade.postcode?? && trade.postcode?length gt 0>
        <span class="column-name">邮&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;编:</span> ${trade.postcode!''}
    </#if>
    <br>
    <br>
    <strong>留言</strong>
    <br>
    <#if trade.buyer_message?? && trade.buyer_message?length gt 0>
        <span class="column-name">买家留言: </span>${trade.buyer_message!''}
        <br>
    </#if>
    <#if trade.seller_memo?? && trade.seller_memo?length gt 0>
        <span class="column-name">店铺备注: </span>${trade.seller_memo!''}
        <br>
    </#if>
    <span class="column-name">审单留言：</span>${trade.kefu_msg!''}
    <#if trade.is_submit==0 && (CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor")>
        <#if trade.kefu_msg??>
            <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=kefu_msg&from=${from}">修改</a>
        <#else>
            <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=kefu_msg&from=${from}">添加</a>
        </#if>
    </#if>
    <br>
    <span class="column-name">仓库留言：</span>${trade.cangku_msg!''}
    <#if trade.is_submit==1 && (CURRENT_USER.type=="WareHouse")>
        <#if trade.cangku_msg??>
            <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=kefu_msg&from=${from}">修改</a>
        <#else>
            <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=cangku_msg&from=${from}">添加</a>
        </#if>
    </#if>
    <br>
    <span class="column-name">备注(只有自己可以看到)：</span>
    <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor">
        ${trade.kefu_memo!''}
        <#if trade.kefu_memo??>
            <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=kefu_memo&from=${from}">修改</a>
        <#else>
            <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=kefu_memo&from=${from}">添加</a>
        </#if>
    <#elseif CURRENT_USER.type=="WareHouse">
        ${trade.cangku_memo!''}
        <#if trade.cangku_memo??>
            <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=cangku_memo&from=${from}">修改</a>
        <#else>
            <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=cangku_memo&from=${from}">添加</a>
        </#if>
    </#if>
    <br>
    <br>

    <strong>快递信息</strong><br>
    <span class="column-name">快&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;递:</span>
    <span>${trade.delivery!''}</span>
    <#if trade.is_send==0 && trade.is_cancel==0 && trade.is_finish==0>
        <select style="display: none; width: 80px;">
            <#list logistics as lo>
                <option value="${lo.code}" <#if trade.delivery==lo.name>selected</#if>>${lo.name}
            </#list>
        </select>
        <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor">
            <#if (trade.status=="UnSubmit" || trade.status=="WaitSend") && trade.is_cancel==0>
            <a class="modify-delivery">修改</a>
            </#if>
        <#elseif CURRENT_USER.type=="WareHouse">
            <a class="modify-delivery">修改</a>
        </#if>
        <a style="display: none;" data-id="${trade.id}" class="modify-delivery-submit">保存</a>
        <a style="display: none;" class="modify-delivery-cancel">取消</a>
    </#if>
    <br>
    <span class="column-name">单&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;号: </span><span>${trade.delivery_number!''}</span>
    <#if trade.is_send==0 && trade.is_cancel==0 && trade.is_finish==0>
        <input type="text" style="display: none; width: 80px;"/>
        <#if CURRENT_USER.type=="WareHouse" && trade.status=="WaitOut">
            <a class="modify-delivery-number">修改</a>
        </#if>
        <a style="display: none;" data-id="${trade.id}" class="modify-delivery-number-submit">保存</a>
        <a style="display: none;" class="modify-delivery-number-cancel">取消</a>
    </#if>
    <br>
    <span class="column-name">物&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;品: </span>
    <#if trade.cargo?? && trade.cargo?length gt 0>
        trade.cargo
    <#else>
        ${conf["cargo"]}
    </#if>
    <br>
    <#if trade.pay_type==1>
        <span class="column-name">货到付款: </span>${(trade.payment?int + trade.delivery_money?int)/100}元
        <br>
    </#if>
    <#if trade.is_insure==-1>
        <#if conf["is_insure"]=="1">
            <span class="column-name">保价金额: </span><#if trade.insure_value==-1>${(conf["insure_value"]?number)/100}<#else>${(trade.insure_value?number)/100}</#if>元
            <br>
        </#if>
    <#else>
        <#if trade.is_insure==1>
            <span class="column-name">保价金额: </span><#if trade.insure_value==-1>${(conf["insure_value"]?number)/100}<#else>${(trade.insure_value?number)/100}</#if>元
            <br>
        </#if>
    </#if>
    <#if CURRENT_USER.type=="WareHouse" && trade.delivery?? && trade.delivery=="顺丰速运">
        <span class="column-name">包裹数量：</span><span>${trade.parcel_quantity}</span>
        <#if trade.status!="WaitReceive" && trade.sf_status==0>
            <a id="modify-quantity" data-id="${trade.id}">修改</a>
        </#if>
    </#if>
    <br>
    <span class="column-name">运&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;费：</span> ${trade.delivery_money/100!''}
    <br>
    <span class="column-name">付款方式：</span>
    <#if trade.pay_method?? && trade.pay_method==1>
        寄方付
    <#elseif trade.pay_method==2>
        收方付
    <#elseif trade.pay_method==3>
        第三方付
    </#if>
    <br>
    <span class="column-name">下单状态：</span>
    <#if trade.delivery?? && trade.delivery=="顺丰速运" && CURRENT_USER.type=="WareHouse" && trade.is_submit==1 && trade.is_send==0 && trade.status=='WaitFind'>
        <#if trade.sf_status==1>
            下单成功
        <#elseif trade.sf_status==2>
            <font color="red">下单失败</font>
        <#else>
            未下单
        </#if>
    </#if>
    <br>
    <#if trade.is_cancel==-1 || trade.is_cancel==1>
        <span class="column-name">取消原因：</span>${trade.why_cancel!''}
    </#if>
    <br>
    <button onclick="submit('${trade.id}')">提交</button>
    <#if (CURRENT_USER.type=="WareHouse" && trade.status=="WaitOut") || ((CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor") && trade.status=="UnSubmit")>
        <#if trade.is_pause==1>
            <a onclick="pause('${trade.id?string}', 'cancel_pause')" title="取消暂停以后此订单将可以提交或者出库">取消暂停</a>
        <#else>
            <a onclick="pause('${trade.id?string}', 'pause')" title="暂停以后此订单将暂时无法提交或者出库">暂停</a>
        </#if>
    </#if>
</div>
</#macro>