<#import "/templates/root.ftl" as root >

<@root.html active=3 css=["jqpagination.css", "easyui.css", "icon.css", "trade_list.css"]
js=["trade_search.js", "jquery.jqpagination.min.js", "jquery.cookie.js", "jquery.easyui.min.js"]>
	<div style="width: 98%; height: 30px; background-color: #d6dff7; padding: 10px; padding-left: 20px;">

		<select id="criteria_type" style="width: 8%;">
			<option value="tid">网店单号</option>
            <option value="name">姓名</option>
            <option value="nick">昵称</option>
            <option value="mobile">手机</option>
            <option value="phone">电话</option>
            <option value="delivery_number">快递单号</option>
            <option value="id">系统单号</option>
		</select>
        <input type="text" id="criteria"/>

        <button id="search">搜索</button>
	</div>
<#if trade_list?? && trade_list?size gt 0>
    <div style="width:99%; margin-top:3px;">
        <table id="t-list" class="easyui-datagrid" style="height:auto;" data-options="scrollbarSize:'0', fitColumns:'true',checkOnSelect:false">
            <thead>
                <tr>
                    <th data-options="field:'ck',checkbox:true"></th>
                    <th data-options="field:'id',align:'center',width:$(this).width() * 0.3">订单号</th>
                    <th data-options="field:'tid',align:'center',width:$(this).width() * 0.3">网店单号</th>
                    <th data-options="field:'listprice',align:'center',width:$(this).width() * 0.3">顾客姓名</th>
                    <th data-options="field:'attr1',align:'center',width:$(this).width() * 0.3">订单状态</th>
                    <th data-options="field:'attr2',align:'center',width:$(this).width() * 0.3">成交时间</th>
                    <th data-options="field:'attr3',align:'center',width:$(this).width() * 0.4">操作</th>
                </tr>
            </thead>
            <tbody>
                <#list trade_list as trade>
                <tr>
                    <td style="width:50%"></td>
                    <td>${trade.id}</td>
                    <td>${trade.tid!''}</td>
                    <td>${trade.name}</td>
                    <td>
                    ${trade.tradeStatus.desc}<br>
                    <#if trade.is_cancel==-1>
                        <font color="red">(已申请取消)</font>
                    </#if>
                    <#if trade.is_refund==1>
                        <font color="red">(退换货)</font>
                    </#if>
                    <#if trade.is_pause==1>
                        <font color="red">(已暂停)</font>
                    </#if>
                    </td>
                    <td>${trade.created?string('yyyy-MM-dd HH:mm:ss')}</td>
                    <td>
                        <a href="${rc.contextPath}/trade/trade_detail?id=${trade.id}">订单详情</a> &nbsp;
                    </td>

                </tr>
                </#list>
            </tbody>
        </table>
    </div>
    <hr>
    <div class="pagination">
        <a href="#" class="first" data-action="first">&laquo;</a>
        <a href="#" class="previous" data-action="previous">&lsaquo;</a>
        <input id="page" type="text" readonly="readonly" data-current-page="${paging.page}" data-page-string="1 of 3" data-max-page="${paging.pageCount}" />
        <a href="#" class="next" data-action="next">&rsaquo;</a>
        <a href="#" class="last" data-action="last">&raquo;</a>
    </div>

<#else>
    <div class="alert" style="margin: 5px;">
        <a class="close" data-dismiss="alert">×</a>
        <strong>无内容！</strong>
    </div>
</#if>
</@root.html>

<script>
$(document).ready(function() {
    $('#criteria').val('${criteria!""}');
    $('#criteria_type').val('${criteria_type!"tid"}');
});
</script>
