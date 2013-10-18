<#import "/templates/root.ftl" as root >
    <#import "/trade/_buyer_info.ftl" as buyer_info >

<@root.html active=3 css=["easyui.css", "icon.css", "trade_detail.css"]
js=["jquery.cookie.js", "jquery.easyui.min.js", "trade_detail.js"]>
       <#include "/trade/_t_detail_tab.ftl">
       <div style="border:1px solid gray; width: 100%; height: auto;">
           <div class="right">
               <strong>退换货信息</strong>
               <p>责任方：
                   <#if refund.responsible_party=="customer">
                       顾客
                   <#elseif refund.responsible_party=="warehouse">
                       仓库
                   </#if>
               </p>
               退/换货原因：${refund.why_refund!''}
               <br>
               <br>
               快递信息：${refund.delivery!''}, ${refund.delivery_number}
               <br><br>
               <#if trade.status=="RefundFinish">
                   仓库留言：${refund.comment!''}
                   <br><br>
               </#if>

               <table class="easyui-datagrid" style="height:auto;" data-options="scrollbarSize:'0', fitColumns:'true'">
                   <thead>
                   <tr>
                       <th data-options="field:'itemid',align:'center',width:$(this).width() * 0.3">商品名称</th>
                       <th data-options="field:'listprice',align:'center',width:$(this).width() * 0.3">购买数量</th>
                       <th data-options="field:'attr1',align:'center',width:$(this).width() * 0.3">颜色规格</th>
                       <th data-options="field:'action',align:'center',width:$(this).width() * 0.3">退换货数量</th>
                       <#if trade.status=="RefundFinish">
                           <th data-options="field:'1',align:'center',width:$(this).width() * 0.3">残次品数量</th>
                       </#if>
                   </tr>
                   </thead>
                   <tbody>

                   <#list refund.refundOrderList as order>
                       <tr>
                           <td>

                               <#if order.pic_path??>
                                   <#assign picPath = order.pic_path/>
                               <#else>
                                   <#assign picPath = 'http://img01.tbsandbox.com/bao/uploaded/i1/T1R1CzXeRiXXcckdZZ_032046.jpg'/>
                               </#if>
                               <p><img style="width: 80px; height: 80px;" src="${picPath}_80x80.jpg" /></p>
                               <p>${order.title!''}</p>
                               <p>商品编号：${order.goods_id}</p>
                           </td>
                           <td>${order.quantity!0}</td>
                           <td>颜色：${order.sku.color}, 规格：${order.sku.size}</td>
                           <td>
                               <#if order.tui_quantity?? && order.tui_quantity &gt; 0>退货：${order.tui_quantity!0}</#if>   <br>
                               <#if order.huan_quantity?? && order.huan_quantity &gt; 0>换货：${order.huan_quantity!0}</#if>
                           </td>
                           <#if trade.status=="RefundFinish">
                            <td>
                                ${order.bad_quantity!0}
                            </td>
                           </#if>
                       </tr>
                   </#list>
                   </tbody>
               </table>
               <br>
               <br>
                   <#if refund.pic1??>图片1</#if>
                   <#if refund.pic2??>图片2</#if>
                   <#if refund.pic3??>图片3</#if>
               <br>
               <br>
               <#if CURRENT_USER.type=="WareHouse" && trade.status=="WaitWareHouseReceive">
               <p>
                <a href="${rc.contextPath}/trade/action/receive_refund_form?tid=${refund.tid}">收到退货</a>
               </p>
               </#if>
           </div>
           <@buyer_info.buyer_info trade=trade/>
           <div style="clear: both;"></div>
       </div>
</@root.html>
