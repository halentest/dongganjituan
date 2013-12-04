<#import "/templates/root.ftl" as root >
<#import "/trade/_buyer_info.ftl" as buyer_info >

    <@root.html active=3 css=["jqpagination.css", "easyui.css", "icon.css", "trade_list.css", "all.css"]
    js=["pagination.js", "jquery.jqpagination.min.js", "jquery.cookie.js", "jquery.easyui.min.js", "trade_detail.js", "trade_list.js"]>
    <#if CURRENT_USER.type=="WareHouse">
        <script language="javascript" src="${rc.contextPath}/js/LodopFuncs.js"></script>
        <object  id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0>
            <embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0></embed>
        </object>
    </#if>
    <div style="width: 98%; height: 60px; background-color: #d6dff7; padding-top: 5px; padding-left: 20px; <#if CURRENT_USER.type=="WareHouse">margin-top: -20px;</#if>">
    <#if CURRENT_USER.type!="Distributor" && CURRENT_USER.type!="ServiceStaff">
        分销商
        <select id="distributor" style="width: 8%;">
            <option value="-1">所有分销商</option>
            <#list dList as d>
                <option value="${d.id}">${d.name}</option>
            </#list>
        </select>
        &nbsp;&nbsp;&nbsp;&nbsp;
    </#if>
    <#if CURRENT_USER.type!="ServiceStaff">
        店铺
        <select id="seller_nick" style="width: 8%;">
            <option value="">所有店铺</option>
            <#if shopList??>
                <#list shopList as shop>
                    <option value="${shop.seller_nick}">${shop.seller_nick}</option>
                </#list>
            </#if>
        </select>
        &nbsp;&nbsp;&nbsp;&nbsp;
    </#if>
    <input type="hidden" id="status">
    <input type="hidden" id="isSubmit">
    <input type="hidden" id="isCancel">
    <input type="hidden" id="isFinish">
    <input type="hidden" id="isSend">
    <input type="hidden" id="isRefund">
    <input type="hidden" id="map">
    快递
    <select id="delivery" style="width: 8%;">
        <option value="">所有快递</option>
        <#list logistics as lo>
            <option value="${lo.name}">${lo.name}</option>
        </#list>
    </select>
    &nbsp;&nbsp;&nbsp;&nbsp;
    订单号
    <input id="tid" type="input" value="" style="width: 10%; height: 15px;"/>
    &nbsp;&nbsp;&nbsp;&nbsp;
    收货人
    <input id="name" type="input" value="" style="width: 6%; height: 15px;"/>
    <!-- &nbsp;&nbsp;&nbsp;&nbsp;
    <strong>开始时间</strong>
    <input id="name" type="input" value="" style="width: 8%; height: 20px;"/>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <strong>结束时间</strong>
    <input id="name" type="input" value="" style="width: 8%; height: 20px;"/> -->
    &nbsp;&nbsp;&nbsp;&nbsp;
    <div style="margin-top: 5px;">
        开始时间
        <input id="start" class="easyui-datetimebox" type="input">
        &nbsp;&nbsp;&nbsp;&nbsp;
        结束时间
            <span style="float: right; margin-right:15px;">
                <button id="search">搜索</button>&nbsp;&nbsp;&nbsp;&nbsp;共${totalCount}条交易
                &nbsp;&nbsp;&nbsp;&nbsp;

                <#if CURRENT_USER.type=="WareHouse">
                    <form action="${rc.contextPath}/trade/report" method="get" style="display: inline;">
                        <input name="date" class="easyui-datebox" type="input" style="width:100px;">
                        <input type="submit" value="下载已发货订单"/>
                    </form>
                </#if>
            </span>
        <input id="end" class="easyui-datetimebox" type="input">
    </div>

    </div>
    <#if trade_list?size gt 0>
        <div style="width:99%; margin-top:3px;">
            <#list trade_list as trade>
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

                                <#if order.pic_path?? && order.pic_path?length &gt; 0>
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
                                    <#if trade.status=="UnSubmit" && trade.is_cancel==0>
                                        <a href="${rc.contextPath}/trade/action/del_goods?tid=${trade.id?string}&oid=${order.id?c}&from=list">删除</a>
                                    </#if>
                                </#if>
                            </td>
                        </tr>
                    </#list>

                    </tbody>
                </table>
                <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor">
                    <#if trade.status=="UnSubmit" && trade.is_cancel==0>
                        <a href="${rc.contextPath}/goods/goods_list?tid=${trade.id?string}&from=list">添加商品</a>
                    </#if>
                </#if>

                <div style="background-color: #FFFFCC; padding: 10px;">
                    <strong>订单状态：</strong>
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
                    <@buyer_info.buyer_info trade=trade from="list" />
                </div>
                <hr>
            </#list>
        </div>
        <div class="pagination">
            <a href="#" class="first" data-action="first">&laquo;</a>
            <a href="#" class="previous" data-action="previous">&lsaquo;</a>
            <input id="page" type="text" readonly="readonly" data-current-page="${paging.page}" data-page-string="1 of 3" data-max-page="${paging.pageCount}" />
            <a href="#" class="next" data-action="next">&rsaquo;</a>
            <a href="#" class="last" data-action="last">&raquo;</a>
        </div>
        <div class="action-bar">
            <#if CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="ServiceStaff">
                <#if isSubmit?? && isSubmit==0>
                    <button id="batch_submit" onclick="batchSubmit('${idList}')" style="cursor: pointer;">批量提交</button>
                    <a href="${rc.contextPath}/trade/trade_list?isCancel=0&isSubmit=0&isFinish=0&map=false">简单列表</a>
                </#if>
            </#if>
            <#if CURRENT_USER.type=="WareHouse">
                <#if isSubmit==1 && isSend==0 && status=='WaitSend'>
                    <a id="batch-find-goods" style="cursor: pointer;">批量导入待拣货</a>
                </#if>
                <#if isSubmit==1 && isSend==0 && status=='WaitOut'>
                    <a id="batch-out-goods" style="cursor: pointer;">批量出库</a>
                </#if>
                <#if isSubmit==1 && isSend==0 && status=='WaitFind'>
                    <a href="${rc.contextPath}/trade/export_finding" style="cursor: pointer;">生成拣货单</a>
                    <select id="delivery-print" style="width: 8%;">
                        <option value="">选择快递</option>
                        <#list logistics as lo>
                            <option value="${lo.name}">${lo.name}</option>
                        </#list>
                    </select>
                    <a id="batch-prn-kdd" style="cursor: pointer;">打印快递单</a>
                    <#if scan=="false">
                        <a id="scan-delivery">扫描单号</a>
                        <#else>
                            <a id="save-scan">保存单号</a>
                    </#if>
                    <a id="print-setup" style="cursor: pointer;">打印调整</a>
                    <a id="paper-setup" style="cursor: pointer;">纸张设置</a>
                </#if>
            </#if>
        </div>
        <#else>
            <#if CURRENT_USER.type=="WareHouse">
                <div class="alert" style="margin: 5px;">
                    <a class="close" data-dismiss="alert">×</a>
                    <strong>无内容！</strong>
                </div>
                <#if status?? && status='WaitFind'>
                    <select id="delivery-print" style="width: 8%;">
                        <option value="">选择快递</option>
                        <#list logistics as lo>
                            <option value="${lo.name}">${lo.name}</option>
                        </#list>
                    </select>
                    <a id="print-setup" style="cursor: pointer;">打印调整</a>
                    <a id="paper-setup" style="cursor: pointer;">纸张设置</a>
                </#if>
                <#else>
                    <div class="alert" style="margin: 5px;">
                        <a class="close" data-dismiss="alert">×</a>
                        <strong>无内容！</strong>
                    </div>
            </#if>
    </#if>
</@root.html>

<script>
    function initpage() {
        $('#name').val('${name!""}');
        $('#tid').val('${tid!""}');
        $('#distributor').val('${dId!-1}');
        $('#seller_nick').val('${seller_nick!""}');
        $('#status').val('${status!""}');
        $('#delivery').val('${delivery!""}');
        $('#start').val('${start!""}');
        $('#delivery-print').val('${delivery!""}');
        $('#end').val('${end!""}');
        $('#isSubmit').val('${isSubmit!""}');
        $('#isCancel').val('${isCancel!""}');
        $('#isRefund').val('${isRefund!""}');
        $('#isFinish').val('${isFinish!""}');
        $('#isSend').val('${isSend!""}');
        $('#map').val('${map!""}');
    }
</script>