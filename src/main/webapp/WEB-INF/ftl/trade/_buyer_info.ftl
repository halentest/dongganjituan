<#macro buyer_info trade conf from="">
<style>
    .column-name {
        color: rgb(153, 153, 153);
        font-family: 宋体, arial, tahoma, sans-serif;
    }
    table tr td.column {
        text-align: right;
        color: rgb(153, 153, 153);
        font-family: 宋体, arial, tahoma, sans-serif;
        white-space:nowrap;
    }
</style>
<div class="left">
    <strong>订单信息</strong>
    <table>
        <tr>
            <td class="column">订单号:</td>
            <td><a href="${rc.contextPath}/trade/trade_detail?id=${trade.id}">${trade.id!''}</a></td>
        </tr>
        <tr>
            <td class="column">网店:</td>
            <td>${trade.seller_nick!''}</td>
        </tr>
        <tr>
            <td class="column">网店单号:</td>
            <td>${trade.tid!''}</td>
        </tr>
        <tr>
            <td class="column">客户姓名:</td>
            <td>${trade.name!''}</td>
        </tr>
        <#if trade.buyer_nick?? && trade.buyer_nick?length gt 0>
            <tr>
                <td class="column">买家ID:</td>
                <td>${trade.buyer_nick!''}</td>
            </tr>
        </#if>
        <tr>
            <td class="column">手机:</td>
            <td>${trade.mobile!''}</td>
        </tr>
        <tr>
            <td class="column">电话:</td>
            <td>${trade.phone!''}</td>
        </tr>
        <tr>
            <td class="column">地址:</td>
            <td>
                ${trade.state!''} ${trade.city!''} ${trade.district!''} ${trade.address!''}
                <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="WareHouse">
                    <#if (trade.status=="UnSubmit" || trade.status=="WaitSend" || trade.status=="WaitFind") && trade.is_cancel==0>
                        <br>
                        <a href="${rc.contextPath}/trade/action/modify_receiver_info_form?id=${trade.id}&from=${from}">修改</a>
                    </#if>
                </#if>
            </td>
        </tr>
        <#if trade.postcode?? && trade.postcode?length gt 0>
            <tr>
                <td class="column">邮编:</td>
                <td>${trade.postcode!''}</td>
            </tr>
        </#if>
        <#if trade.is_cancel==-1 || trade.is_cancel==1>
            <tr>
                <td class="column">取消原因:</td>
                <td>${trade.why_cancel!''}</td>
            </tr>
        </#if>
    </table>
    <strong>留言</strong>
    <table>
        <#if trade.buyer_message?? && trade.buyer_message?length gt 0>
            <tr>
                <td class="column">买家留言:</td>
                <td>${trade.buyer_message!''}</td>
            </tr>
        </#if>
        <#if trade.seller_memo?? && trade.seller_memo?length gt 0>
            <tr>
                <td class="column">店铺备注:</td>
                <td>${trade.seller_memo!''}</td>
            </tr>
        </#if>
        <tr>
            <td class="column">审单留言:</td>
            <td>
                ${trade.kefu_msg!''}
                <#if trade.is_submit==0 && (CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor")>
                    <#if trade.kefu_msg??>
                        <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=kefu_msg&from=${from}">修改</a>
                    <#else>
                        <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=kefu_msg&from=${from}">添加</a>
                    </#if>
                </#if>
            </td>
        </tr>
        <tr>
            <td class="column">仓库留言:</td>
            <td>
                ${trade.cangku_msg!''}
                <#if trade.is_submit==1 && (CURRENT_USER.type=="WareHouse")>
                    <#if trade.cangku_msg??>
                        <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=kefu_msg&from=${from}">修改</a>
                    <#else>
                        <a href="${rc.contextPath}/trade/action/add_comment_form?id=${trade.id}&type=cangku_msg&from=${from}">添加</a>
                    </#if>
                </#if>
            </td>
        </tr>
        <tr>
            <td class="column">
                <span title="只有自己可以看到">备注</span>
            </td>
            <td>
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
            </td>
        </tr>
    </table>

    <strong>快递信息</strong>
    <#if CURRENT_USER.type=="WareHouse" && trade.delivery?? && trade.delivery=="顺丰速运">
        <a>修改</a>
    </#if>
    <br>
    <table>
        <tr>
            <td class="column">快递:</td>
            <td>
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
            </td>
        </tr>
        <tr>
            <td class="column">单号:</td>
            <td>
                <span>${trade.delivery_number!''}</span>
                <#if trade.is_send==0 && trade.is_cancel==0 && trade.is_finish==0>
                    <input type="text" style="display: none; width: 80px;"/>
                    <#if CURRENT_USER.type=="WareHouse" && trade.status=="WaitOut">
                        <a class="modify-delivery-number">修改</a>
                    </#if>
                    <a style="display: none;" data-id="${trade.id}" class="modify-delivery-number-submit">保存</a>
                    <a style="display: none;" class="modify-delivery-number-cancel">取消</a>
                </#if>
            </td>
        </tr>
        <#if CURRENT_USER.type=="WareHouse" && trade.delivery?? && trade.delivery=="顺丰速运">
            <tr>
                <td class="column">物品:</td>
                <td>
                    <#if trade.cargo?? && trade.cargo?length gt 0>
                        trade.cargo
                    <#else>
                        ${conf["cargo"]}
                    </#if>
                </td>
            </tr>
        </#if>
        <#if trade.pay_type==1>
            <tr>
                <td class="column">货到付款:</td>
                <td>
                    ${(trade.payment?int + trade.delivery_money?int)/100}元
                </td>
            </tr>
        </#if>
        <#if CURRENT_USER.type=="WareHouse" && trade.delivery?? && trade.delivery=="顺丰速运">
            <#if trade.is_insure==-1>
                <#if conf["is_insure"]=="1">
                    <tr>
                        <td class="column">保价金额:</td>
                        <td><#if trade.insure_value==-1>${(conf["insure_value"]?number)/100}<#else>${(trade.insure_value?number)/100}</#if>元</td>
                    </tr>
                </#if>
            <#else>
                <#if trade.is_insure==1>
                    <tr>
                        <td class="column">保价金额:</td>
                        <td><#if trade.insure_value==-1>${(conf["insure_value"]?number)/100}<#else>${(trade.insure_value?number)/100}</#if>元</td>
                    </tr>
                </#if>
            </#if>
        </#if>
        <#if CURRENT_USER.type=="WareHouse" && trade.delivery?? && trade.delivery=="顺丰速运">
            <tr>
                <td class="column">包裹数量:</td>
                <td>
                    <span>${trade.parcel_quantity}</span>
                    <#if trade.status!="WaitReceive" && trade.sf_status==0>
                        <a id="modify-quantity" data-id="${trade.id}">修改</a>
                    </#if>
                </td>
            </tr>
        </#if>
        <tr>
            <td class="column">运费:</td>
            <td>${trade.delivery_money/100!''}</td>
        </tr>
        <#if CURRENT_USER.type=="WareHouse" && trade.delivery?? && trade.delivery=="顺丰速运">
            <tr>
                <td class="column">付款方式:</td>
                <td>
                    <#if trade.pay_method?? && trade.pay_method==1>
                        寄方付
                    <#elseif trade.pay_method==2>
                        收方付
                    <#elseif trade.pay_method==3>
                        第三方付
                    </#if>
                </td>
            </tr>
        </#if>
        <#if CURRENT_USER.type=="WareHouse" && trade.delivery?? && trade.delivery=="顺丰速运">
            <tr>
                <td class="column">下单状态:</td>
                <td>
                    <#if trade.delivery?? && trade.delivery=="顺丰速运" && CURRENT_USER.type=="WareHouse" && trade.is_submit==1 && trade.is_send==0 && trade.status=='WaitFind'>
                        <#if trade.sf_status==1>
                            下单成功
                        <#elseif trade.sf_status==2>
                            <font color="red">下单失败</font>
                        <#else>
                            未下单
                        </#if>
                    </#if>
                </td>
            </tr>
        </#if>
        <tr>
            <td>
                <#if (CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor") && trade.status=="UnSubmit" && trade.is_cancel==0>
                    <button onclick="submit('${trade.id}')">提交</button>
                </#if>
            </td>
            <td>
                <#if (CURRENT_USER.type=="WareHouse" && trade.status=="WaitOut") || ((CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor") && trade.status=="UnSubmit")>
                    <#if trade.is_pause==1>
                        <a onclick="pause('${trade.id?string}', 'cancel_pause')" title="取消暂停以后此订单将可以提交或者出库">取消暂停</a>
                    <#else>
                        <a onclick="pause('${trade.id?string}', 'pause')" title="暂停以后此订单将暂时无法提交或者出库">暂停</a>
                    </#if>
                </#if>
            </td>
        </tr>
    </table>
</div>
</#macro>