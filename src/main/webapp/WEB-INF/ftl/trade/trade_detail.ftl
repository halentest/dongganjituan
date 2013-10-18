<#import "/templates/root.ftl" as root >
<#import "/trade/_buyer_info.ftl" as buyer_info >

<@root.html active=3 css=["easyui.css", "icon.css", "trade_detail.css"]
js=["jquery.cookie.js", "jquery.easyui.min.js", "trade_detail.js"]>
       <#include "/trade/_t_detail_tab.ftl">
       <div style="border:1px solid gray; width: 100%; height: auto;">
           <div class="right">
               <strong>商品列表</strong>
               <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor">
                   <#if (trade.status=="UnSubmit" || trade.status=="WaitSend") && trade.is_cancel==0>
                    <a href="${rc.contextPath}/goods/goods_list?tid=${trade.id?string}">添加商品</a>
                   </#if>
               </#if>
               <table class="easyui-datagrid" style="height:auto;" data-options="scrollbarSize:'0', fitColumns:'true'">
                   <thead>
                   <tr>
                       <th data-options="field:'itemid',align:'center',width:$(this).width() * 0.3">商品名称</th>
                       <th data-options="field:'productid',align:'center',width:$(this).width() * 0.3">单价</th>
                       <th data-options="field:'listprice',align:'center',width:$(this).width() * 0.3">数量</th>
                       <th data-options="field:'attr1',align:'center',width:$(this).width() * 0.3">颜色规格</th>
                       <th data-options="field:'action',align:'center',width:$(this).width() * 0.3">操作</th>
                   </tr>
                   </thead>
                   <tbody>
                   <#list trade.myOrderList as order>
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
                           <td>${order.price/1000}</td>
                           <td>${order.quantity}</td>
                           <td>颜色：${order.sku.color}, 规格：${order.sku.size}</td>
                           <td>
                               <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor">
                                   <#if (trade.status=="UnSubmit" || trade.status=="WaitSend") && trade.is_cancel==0>
                                    <a href="${rc.contextPath}/trade/action/del_goods?tid=${trade.id?string}&oid=${order.id}">删除</a>
                                   </#if>
                               </#if>
                           </td>
                       </tr>
                   </#list>
                   </tbody>
               </table>
           </div>
           <@buyer_info.buyer_info trade=trade/>
           <div style="clear: both;"></div>
       </div>
</@root.html>
