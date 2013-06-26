<#import "/templates/root.ftl" as root >

<@root.html active=3 css=["trade_list.css", "jqpagination.css"] js=["jquery.jqpagination.min.js"]>
	<i class="icon-list-alt"></i>退货列表
	<div style="width: 100%; height: 30px; background-color: #99CCCC; padding-top: 5px; padding-left: 20px;">
		<#if CURRENT_USER.type!="Distributor" && CURRENT_USER.type!="ServiceStaff">
		<strong>分销商</strong>
		<select id="distributor" style="width: 8%;">
			<option value="-1">所有分销商</option>
			<#list dList as d>
			<option value="${d.id}">${d.name}</option>
			</#list>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
		</#if>
		<#if CURRENT_USER.type!="ServiceStaff">
		<strong>店铺</strong>
		<select id="seller_nick" style="width: 8%;">
			<option value="">所有店铺</option>
			<#if shopList??>
				<#list shopList as shop>
				<option value="${shop.sellerNick}">${shop.sellerNick}</option>
				</#list>
			</#if>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
		</#if>
		<strong>状态</strong>
		<select id="status" style="width: 8%;">
			<option value="">所有状态</option>
			<#list statusList as status>
				<option value="${status.getValue()}">${status.getDesc()}</option>
			</#list>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<strong>订单号</strong>
		<input id="tid" type="input" value="" style="width: 16%; height: 20px;"/>
		<!-- &nbsp;&nbsp;&nbsp;&nbsp;
		<strong>开始时间</strong>
		<input id="name" type="input" value="" style="width: 8%; height: 20px;"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<strong>结束时间</strong>
		<input id="name" type="input" value="" style="width: 8%; height: 20px;"/> -->
		&nbsp;&nbsp;&nbsp;&nbsp;
		<button id="search">搜索</button>&nbsp;&nbsp;&nbsp;&nbsp;共${totalCount}个
	</div>
	<div class="pagination">
	    <a href="#" class="first" data-action="first">&laquo;</a>
	    <a href="#" class="previous" data-action="previous">&lsaquo;</a>
	    <input id="page" type="text" readonly="readonly" data-current-page="${paging.page}" data-page-string="1 of 3" data-max-page="${paging.pageCount}" />
	    <a href="#" class="next" data-action="next">&rsaquo;</a>
	    <a href="#" class="last" data-action="last">&raquo;</a>
	</div>
	<#list refund_list as refund>
		<#assign trade = refund.trade/>
		<#assign order = refund.order/>
		<table>
		    <tbody>
		      <tr class="trade">
		        <td colspan="7">
		        	${trade.come_from!}&nbsp;&nbsp;&nbsp;
		        	<strong>订单编号：</strong></strong>${trade.tid}  &nbsp;&nbsp;&nbsp;
		        	<strong>创建时间：</strong>${trade.created?string('yyyy-MM-dd HH:mm:ss')} &nbsp;&nbsp;&nbsp;
		        	<strong>卖家: </strong>${trade.seller_nick} &nbsp;&nbsp;&nbsp;
		        	<strong>买家: <strong>${trade.name} &nbsp;&nbsp;&nbsp;
		        	<span style="float: right; margin-right: 5px;"><strong>${order.oStatus.desc}</strong></span>
		        </td>
		      </tr>
		      	  <tr class="order_list">
		      	  		<td  style="width: 80px;">
		      	  			<img style="width: 80px; height: 80px;" src="${order.pic_path}_80x80.jpg" />
		      	  		</td>
				        <td style="width: 25%;">
				        	<p><strong>名称：</strong>${order.title}</p>
				        	<p><strong>编号：</strong>${order.goods_id}</p>
				        	<p><strong>属性：</strong>${order.sku.color} &nbsp;&nbsp; ${order.sku.size}</p>
				        </td>
				        <td style="width: 10%;">
				        	<p><strong>数量：</strong>${order.quantity}</p>
				        	<p><strong>价格：</strong>${order.payment/100}元</p>
				        	<#if CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="ServiceStaff">
				        		<#if trade.my_status==4 && order.status=="WAIT_SELLER_SEND_GOODS">
				        			<p>
				        			<a style="cursor: pointer;" data-tid="${trade.tid}" 
				        			 data-oid="${order.oid}" class="apply-refund">申请退货</a>
				        			</p>
				        		</#if>
				        	</#if> 
				        </td>
				        <td style="width: 25%;">
				        	<p><strong>地址：</strong>${trade.state}${trade.city}${trade.district}${trade.address}</p>
				        	<p>
				        		<strong>收货人：</strong>${trade.name} &nbsp;&nbsp;
				        		<strong>电话：</strong>${trade.phone!''} &nbsp;&nbsp;
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
				        <td style="width: 15%;">
				        	<p><strong>退货原因：</strong>${refund.refundReason!''}</p> &nbsp;&nbsp;
				        </td>
				        <td style="text-align: right; padding-right: 10px;">
				        	
				        </td>
			      </tr>
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
$(document).ready(function(){
		
	  $('#tid').val('${tid!""}');
      $('#distributor').val('${dId!-1}');
      $('#seller_nick').val('${seller_nick!""}');
      $('#status').val('${status!""}');
      
      $('.pagination').jqPagination({
		    paged: function(page) {
		    	var status = $('#status').val();
		    	var distributor = $('#distributor').val();
		    	if(!distributor) {
		    		distributor='';
		    	}
		    	var seller_nick = $('#seller_nick').val();
		    	var tid = $('#tid').val();
		        window.location.href="/trade/refund_list?page=" + page + "&status=" + status + "&seller_nick=" + seller_nick
		        	+ "&tid=" + tid + "&dId=" + distributor;
		    }
	   });
	   
	   $('#search').click(function() {
	   		var status = $('#status').val();
	   		var distributor = $('#distributor').val();
	   		if(!distributor) {
	    		distributor='';
	    	}
	    	var seller_nick = $('#seller_nick').val();
	    	var tid = $('#tid').val();
	    	var page = $('#page').attr('data-current-page');
	   		window.location.href="/trade/refund_list?page=" + page + "&status=" + status + "&seller_nick=" + seller_nick
		        	+ "&tid=" + tid + "&dId=" + distributor;
	   });
});
</script>
