<#import "/templates/root.ftl" as root >
    <#import "/trade/_buyer_info.ftl" as buyer_info >
        <#import "/trade/_t_detail_tab.ftl" as detail_tab >
<@root.html active=3 css=["easyui.css", "icon.css", "trade_detail.css"]
js=["jquery.cookie.js", "jquery.easyui.min.js", "trade_detail.js"]>
            <@detail_tab.detail_tab current_tab="apply_refund" />
       <div style="border:1px solid gray; width: 100%; height: auto;">
           <div class="right">
               <strong>请填写残次品信息</strong>

               <form action="${rc.contextPath}/trade/action/receive_refund" method="post">
                   <input type="hidden" name="id" value="${trade.id}"/>
               <table class="easyui-datagrid" style="height:auto;" data-options="scrollbarSize:'0', fitColumns:'true'">
                   <thead>
                   <tr>
                       <th data-options="field:'itemid',align:'center',width:$(this).width() * 0.3">商品名称</th>
                       <th data-options="field:'listprice',align:'center',width:$(this).width() * 0.3">购买数量</th>
                       <th data-options="field:'attr1',align:'center',width:$(this).width() * 0.3">颜色规格</th>
                       <th data-options="field:'action',align:'center',width:$(this).width() * 0.3">退换货数量</th>
                       <th data-options="field:'1',align:'center',width:$(this).width() * 0.3">残次品数量</th>
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
                           <td>${order.quantity}</td>
                           <td>颜色：${order.sku.color}, 规格：${order.sku.size}</td>
                           <td>
                               退货：${order.tui_quantity!0}<br>
                               换货：${order.huan_quantity!0}
                           </td>
                           <td>
                            <input type="text" value="0" name="bad${order.id?c}" style="width: 30px;"/>
                           </td>
                       </tr>
                   </#list>
                   </tbody>
               </table>
                   <br>
                   情况描述(描述退货情况或其他客服需要知道的情况)：<br>
                   <textarea name="comment" rows="6" cols="50"></textarea><br>
                   <br>

                   <input type="submit" value="确定"/>
                   <input type="button" value="取消"/>
                   </p>
               </form>

           </div>
           <@buyer_info.buyer_info trade=trade conf=conf/>
           <div style="clear: both;"></div>
       </div>
</@root.html>
