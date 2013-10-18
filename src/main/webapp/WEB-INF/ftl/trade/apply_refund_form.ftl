<#import "/templates/root.ftl" as root >
    <#import "/trade/_buyer_info.ftl" as buyer_info >

<@root.html active=3 css=["easyui.css", "icon.css", "trade_detail.css"]
js=["jquery.cookie.js", "jquery.easyui.min.js", "trade_detail.js"]>
       <#include "/trade/_t_detail_tab.ftl">
       <div style="border:1px solid gray; width: 100%; height: auto;">
           <div class="right">
               <strong>请选择退换货的商品</strong>
               <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor">
                   <#if (trade.status=="UnSubmit" || trade.status=="WaitSend") && trade.is_cancel==0>
                    <a href="${rc.contextPath}/goods/goods_list?tid=${trade.id?string}">添加商品</a>
                   </#if>
               </#if>
               <form action="${rc.contextPath}/trade/apply_refund" method="post" enctype="multipart/form-data">
                   <input type="hidden" name="id" value="${trade.id}"/>
               <table class="easyui-datagrid" style="height:auto;" data-options="scrollbarSize:'0', fitColumns:'true'">
                   <thead>
                   <tr>
                       <th data-options="field:'itemid',align:'center',width:$(this).width() * 0.3">商品名称</th>
                       <th data-options="field:'listprice',align:'center',width:$(this).width() * 0.3">购买数量</th>
                       <th data-options="field:'attr1',align:'center',width:$(this).width() * 0.3">颜色规格</th>
                       <th data-options="field:'action',align:'center',width:$(this).width() * 0.3">退换货数量</th>
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
                           <td>${order.quantity}</td>
                           <td>颜色：${order.sku.color}, 规格：${order.sku.size}</td>
                           <td>
                               退货：<input type="text" name="tui${order.id}" style="width: 30px;"/><br>
                               换货：<input type="text" name="huan${order.id}" style="width: 30px;"/>
                           </td>
                       </tr>
                   </#list>
                   </tbody>
               </table>
                   <p>请选择您认为的责任方：
                   <label><input name="responsible_party" type="radio" value="customer"/>顾客</label>
                   <label><input name="responsible_party" type="radio" value="warehouse"/>仓库</label>
                   </p>
                   退/换货原因：<br>
                   <textarea name="why_refund" rows="6" cols="50"></textarea><br>
                   <br>
                   快递信息：
                   <select name="delivery" style="width: 80px;">
                       <option value="">选择快递</option>
                       <#list logistics as lo>
                           <option value="${lo.name}">${lo.name}
                       </#list>
                   </select>
                   <input type="text" name="delivery_number"/>
                   <br><br>
                   添加图片：<br><br>
                   图片1，<input type="file" name="pic1"> <br>
                   图片2，<input type="file" name="pic2"> <br>
                   图片3，<input type="file" name="pic3"> <br>
                   <p>
                   <input type="submit" value="确定"/>
                   <input type="button" value="取消"/>
                   </p>
               </form>

           </div>
           <@buyer_info.buyer_info trade=trade/>
           <div style="clear: both;"></div>
       </div>
</@root.html>
