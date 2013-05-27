<#import "/templates/root.ftl" as root >

<@root.html active=3 css=["trade_list.css", "jqpagination.css"] js=["pagination.js", "jquery.jqpagination.min.js"]>
	<div style="width: 100%; height: 30px; background-color: #99CCCC; padding-top: 5px; padding-left: 20px;">
		<strong>状态</strong>
		<select id="status" style="width: 8%;">
			<option value="">所有状态</option>
			<option value="WAIT_SELLER_SEND_GOODS">已付款</option>
			<option value="WAIT_BUYER_CONFIRM_GOODS">已发货</option>
			<option value="TRADE_FINISHED">交易成功</option>
			<option value="TRADE_CLOSED">交易关闭</option>
			<option value="SELLER_CONSIGNED_PART">部分发货</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<strong>分销商</strong>
		<select id="seller_nick" style="width: 8%;">
			<option value="">所有分销商</option>
			<option value="志东张">志东张</option>
			<option value="小花生米">小花生米</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<strong>收货人姓名</strong>
		<input id="name" type="input" value="" style="width: 6%; height: 20px;"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<button id="search">搜索</button>
	</div>
	<div class="pagination">
	    <a href="#" class="first" data-action="first">&laquo;</a>
	    <a href="#" class="previous" data-action="previous">&lsaquo;</a>
	    <input id="page" type="text" readonly="readonly" data-current-page="${paging.page}" data-page-string="1 of 3" data-max-page="${paging.pageCount}" />
	    <a href="#" class="next" data-action="next">&rsaquo;</a>
	    <a href="#" class="last" data-action="last">&raquo;</a>
	</div>
	<#list trade_list as trade>
		<table>
		    <tbody>
		      <tr class="trade">
		        <td colspan="7">
		        	${trade.come_from!}&nbsp;&nbsp;&nbsp;
		        	<strong>订单编号：</strong></strong>${trade.tid?c}  &nbsp;&nbsp;&nbsp;
		        	<strong>创建时间：</strong>${trade.created?string('yyyy-MM-dd HH:mm:ss')} &nbsp;&nbsp;&nbsp;
		        	<strong>卖家: </strong>${trade.seller_nick} &nbsp;&nbsp;&nbsp;
		        	<strong>买家: <strong>${trade.name} &nbsp;&nbsp;&nbsp;
		        	<strong>${trade.oStatus.desc}<strong>
		        </td>
		      </tr>
		      <#assign orderList = trade.myOrderList>
		      <#list orderList as order>
		      	  <tr class="order_list">
		      	  		<td  style="width: 80px;">
		      	  			<img style="width: 80px; height: 80px;" src="${order.pic_path}" />
		      	  		</td>
				        <td style="width: 25%;">
				        	<p><strong>名称：</strong>${order.title}</p>
				        	<p><strong>编号：</strong>${order.goods_id}</p>
				        	<p><strong>属性：</strong>${order.sku.color} &nbsp;&nbsp; ${order.sku.size}</p>
				        </td>
				        <td style="width: 10%;">
				        	<p><strong>${order.oStatus.desc}</strong></p>
				        	<p><strong>数量：</strong>${order.quantity}</p>
				        	<p><strong>价格：</strong>${order.payment/100}元</p>
				        </td>
				        <#if order_index==0>
				        <td rowspan="${orderList?size}" style="width: 25%;">
				        	<p><strong>地址：</strong>${trade.state}${trade.city}${trade.district}${trade.address}</p>
				        	<p>
				        		<strong>收货人：</strong>${trade.name} &nbsp;&nbsp;
				        		<strong>电话：</strong>${trade.phone} &nbsp;&nbsp;
				        		<strong>手机：</strong>${trade.mobile} &nbsp;&nbsp;
				        	</p>
				        	<p><strong>邮编：</strong>${trade.postcode}</p>
				        	<#if trade.status=="WAIT_BUYER_CONFIRM_GOODS">
				        		<p><strong>快递：</strong>${order.logistics_company} &nbsp;&nbsp;<strong>单号：</strong>${order.invoice_no}</p>
				        	</#if>
				        	<p>
				        		<strong>总数：</strong>${trade.goods_count} &nbsp;&nbsp;
				        		<strong>总额：</strong>${trade.payment/100}元 &nbsp;&nbsp;
				        		<strong>快递费：</strong>${trade.delivery_money/100}元
				        	</p>
				        </td>
				        <td rowspan="${orderList?size}" style="width: 22%;">
				        	<p><strong>买家留言：</strong>${trade.buyer_message!}</p> &nbsp;&nbsp;
				        	<p><strong>备注：</strong>${trade.seller_memo!}</p>
				        </td>
				        <td rowspan="${orderList?size}">
				        	<#if trade.status=="WAIT_SELLER_SEND_GOODS">
				        		发货
				        	</#if>
				        </td>
				        </#if>
			      </tr>
		      </#list>
		    </tbody>
		</table>
	</#list>
	<div class="pagination" style="float: right;">
	    <a href="#" class="first" data-action="first">&laquo;</a>
	    <a href="#" class="previous" data-action="previous">&lsaquo;</a>
	    <input type="text" readonly="readonly" data-current-page="${paging.page}" data-page-string="1 of 3" data-max-page="${paging.pageCount}" />
	    <a href="#" class="next" data-action="next">&rsaquo;</a>
	    <a href="#" class="last" data-action="last">&raquo;</a>
	</div>
	
</@root.html>


<script>
	function initpage() {
	      $('#name').val('${name!""}');
	      $('#seller_nick').val('${seller_nick!""}');
	      $('#status').val('${status!""}');
	}
</script>
