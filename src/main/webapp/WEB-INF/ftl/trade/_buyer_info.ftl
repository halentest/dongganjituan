<div class="left">
    <strong>客户信息</strong>
    <br>
        客户姓名：${trade.name!''}  <br>
        手机：${trade.mobile!''}  <br>
        电话：${trade.phone!''}  <br>
        地址：${trade.state!''} ${trade.city!''} ${trade.district!''} ${trade.address!''}
        <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor">
            <#if (trade.status=="UnSubmit" || trade.status=="WaitSend") && trade.is_cancel==0>
            <a href="${rc.contextPath}/trade/action/modify_receiver_info_form?id=${trade.id}">修改</a>
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
                </#if>
                <a style="display: none;" data-id="${trade.id}" class="modify-delivery-submit">保存</a>
                <a style="display: none;" class="modify-delivery-cancel">取消</a>
            </#if>   <br>

        运费: ${trade.delivery_money/100!''}   <br>
        快递单号: ${trade.delivery_number!''}    <br>
        网店：${trade.seller_nick!''}        <br>
        买家留言: ${trade.buyer_message!''}   <br>
        店铺备注: ${trade.seller_memo!''}  <br>
        <#if trade.is_cancel==-1 || trade.is_cancel==1>
            取消原因：${trade.why_cancel!''}
        </#if>
</div>