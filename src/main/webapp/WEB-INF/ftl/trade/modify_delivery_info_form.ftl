<#import "/templates/root.ftl" as root >
<#import "/trade/_buyer_info.ftl" as buyer_info >
<#import "/trade/_t_detail_tab.ftl" as detail_tab >
<@root.html active=2 css=["trade_detail.css"] js=[]>
    <style>
        table tr td.column {
        text-align: right;
        color: rgb(153, 153, 153);
        font-family: 宋体, arial, tahoma, sans-serif;
        white-space:nowrap;
        }
    </style>
    <@detail_tab.detail_tab current_tab="detail" />
    <div style="border:1px solid gray; width: 100%; height: auto;">
        <div class="right">
            <strong>修改快递信息</strong>
            <form action="${rc.contextPath}/trade/action/modify_delivery_info" method="">
                <table>
                    <tr>
                        <td class="column">物品:</td>
                        <td>
                            <input type="text" name="cargo"
                            <#if trade.cargo?? && trade.cargo?length gt 0>
                                value="${trade.cargo}"
                                <#else>
                                    value="${conf['cargo']}"
                            </#if>
                            />
                        </td>
                        <td class="column">包裹数量:</td>
                        <td>
                            <input name="parcel_quantity" type="text" value="${trade.parcel_quantity}"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="column">保价:</td>
                        <td>
                            <input type="checkbox" name="is_insure"
                            <#if trade.is_insure==-1>
                                <#if conf["is_insure"]=="1">
                                    checked
                                </#if>
                                <#else>
                                    <#if trade.is_insure==1>
                                        checked
                                    </#if>
                            </#if>
                            >
                        </td>
                        <td class="column">保价金额:</td>
                        <td>
                            <#if trade.is_insure==-1>
                                    <input type="text" name="insure_value"
                                    <#if trade.insure_value==-1>
                                        value='${(conf["insure_value"]?number)/100}'
                                    <#else>
                                        value='${((trade.insure_value?number)/100)?c}'
                                    </#if>
                                    />元
                            <#else>
                                    <input type="text" name="insure_value"
                                    <#if trade.insure_value==-1>
                                        value='${(conf["insure_value"]?number)/100}'
                                    <#else>
                                        value='${(trade.insure_value/100)?c}'
                                    </#if>
                                    />元
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <td class="column">货到付款:</td>
                        <td>
                            <input type="checkbox" name="receiver_pay"
                                <#if trade.pay_type==1>checked</#if>
                            />
                        </td>
                        <td class="column">付款金额:</td>
                        <td>
                            ${(trade.payment?int + trade.delivery_money?int)/100}元
                        </td>
                    </tr>
                    <tr>
                        <td class="column">付款方式:</td>
                        <td>
                            <select name="pay_method">
                                <option value="1">寄方付</option>
                                <option value="2">收方付</option>
                                <option value="3">第三方付</option>
                            </select>
                        </td>
                    </tr>
                </table>

                <input type="hidden" name="id" value="${trade.id}"/>
                <input type="hidden" name="from" value="${from!''}"/>

                <div class="form-actions" >
                    <input type="submit" class="btn btn-primary" value="保存更改" >
                    <input onclick="history.go(-1)" type="reset"  value="取消"/>
                </div>
            </form>
        </div>
        <@buyer_info.buyer_info trade=trade conf=conf from=from!''/>
        <div style="clear: both;"></div>
    </div>
</@root.html>