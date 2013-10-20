<#macro buyer_info trade from="">
<div class="left">
    <strong>客户信息</strong>
    <br>
    <br>
        客户姓名：${trade.name!''}  <br>
        买家ID：${trade.buyer_nick!''} <br>
        手机：${trade.mobile!''}  <br>
        电话：${trade.phone!''}  <br>
        地址：${trade.state!''} ${trade.city!''} ${trade.district!''} ${trade.address!''}
        <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor">
            <#if (trade.status=="UnSubmit" || trade.status=="WaitSend") && trade.is_cancel==0>
            <a href="${rc.contextPath}/trade/action/modify_receiver_info_form?id=${trade.id}&from=${from}">修改</a>
            </#if>
        </#if>
        <br>
        邮编: ${trade.postcode!''} <br>
        快递:
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
            </#if>   <br>

        <div>运费: ${trade.delivery_money/100!''}</div>
        单号: <span>${trade.delivery_number!''}</span>
        <#if trade.is_send==0 && trade.is_cancel==0 && trade.is_finish==0>
            <input type="text" style="display: none; width: 80px;"/>
            <#if CURRENT_USER.type=="WareHouse" && trade.status=="WaitOut">
                <a class="modify-delivery-number">修改</a>
            </#if>
            <a style="display: none;" data-id="${trade.id}" class="modify-delivery-number-submit">保存</a>
            <a style="display: none;" class="modify-delivery-number-cancel">取消</a>
        </#if>
        <br>
        网店：${trade.seller_nick!''}        <br>
        买家留言: ${trade.buyer_message!''}   <br>
        店铺备注: ${trade.seller_memo!''}  <br>

        <#if trade.is_cancel==-1 || trade.is_cancel==1>
            取消原因：${trade.why_cancel!''}
        </#if>
        <br>
        <br>
        审单留言：${trade.kefu_msg!''}
        <#if trade.is_submit==0 && (CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor")>
            <#if trade.kefu_msg??>
                <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=kefu_msg&from=${from}">修改</a>
            <#else>
                <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=kefu_msg&from=${from}">添加</a>
            </#if>
        </#if>
        <br>
        仓库留言：${trade.cangku_msg!''}
        <#if trade.is_submit==1 && (CURRENT_USER.type=="WareHouse")>
            <#if trade.cangku_msg??>
                <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=kefu_msg&from=${from}">修改</a>
            <#else>
                <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=cangku_msg&from=${from}">添加</a>
            </#if>
        </#if>
        <br>
        备注(只有您自己可以看到)：
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
</div>
</#macro>