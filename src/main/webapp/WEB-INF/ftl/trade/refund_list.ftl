<#import "/templates/root.ftl" as root >

<@root.html active=3 css=["trade_list.css", "jqpagination.css"] js=["jquery.jqpagination.min.js"]>
	<div style="width: 100%; height: 30px; background-color: #d6dff7; padding-top: 5px; padding-left: 20px;">
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
		<input type="hidden" id="status"/>
		<strong>订单号</strong>
		<input id="tid" type="input" value="" style="width: 10%; height: 15px;"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<strong>收货人</strong>
		<input id="name" type="input" value="" style="width: 6%; height: 15px;"/>
		<!-- &nbsp;&nbsp;&nbsp;&nbsp;
		<strong>开始时间</strong>
		<input id="name" type="input" value="" style="width: 8%; height: 20px;"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<strong>结束时间</strong>
		<input id="name" type="input" value="" style="width: 8%; height: 20px;"/> -->
		&nbsp;&nbsp;&nbsp;&nbsp;
		<button id="search">搜索</button>&nbsp;&nbsp;&nbsp;&nbsp;共${totalCount}个
	</div>
<#if refund_list?size gt 0>
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
		        	<span style="float: right; margin-right: 5px;"><strong>${refund.oStatus.desc}</strong></span>
		        </td>
		      </tr>
		      	  <tr class="order_list">
		      	  		<td  style="width: 80px;">
                            <#if order.pic_path??>
                                <#assign picPath = order.pic_path/>
                                <#else>
                                    <#assign picPath = 'http://img01.tbsandbox.com/bao/uploaded/i1/T1R1CzXeRiXXcckdZZ_032046.jpg'/>
                            </#if>
		      	  			<img style="width: 80px; height: 80px;" src="${picPath}_80x80.jpg" />
		      	  		</td>
				        <td style="width: 25%;">
				        	<p><strong>名称：</strong>${order.title}</p>
				        	<p><strong>编号：</strong>${order.goods_id}</p>
				        	<p><strong>属性：</strong>${order.sku.color} &nbsp;&nbsp; ${order.sku.size}</p>
				        </td>
				        <td style="width: 10%;">
				        	<p><strong>数量：</strong>${order.quantity}</p>
				        	<p><strong>价格：</strong>${order.payment/100}元</p>
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
				        	<p><strong>退货原因：</strong>${refund.refund_reason!''}</p> 
				        	<#if refund.status=="RejectRefund">
				        	<p><strong>拒绝退货原因：</strong>${refund.comment1!''}</p> 
				        	</#if>
				        	<#if refund.status=="ReceiveRefund" || refund.status=="Refund" || refund.status=="RefundSuccess">
				        	<p><strong>仓库评语：</strong>${refund.comment2!''}</p> 
				        	</#if>
				        	<#if refund.status=="Refund">
				        	<p><strong>拒绝退款原因：</strong>${refund.comment3!''}</p> 
				        	</#if>
				        </td>
				        <td style="text-align: right; padding-right: 10px;">
				        	<#if CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="ServiceStaff">
				        		<#if refund.status=="ApplyRefund" || refund.status=="Refunding">
				        			<p>
				        			<a style="cursor: pointer;" data-tid="${trade.tid}" data-sellernick="${trade.seller_nick}" data-rid="${refund.id}"
				        			 data-oid="${order.oid}" class="cancel-refund">取消退货</a>
				        			</p>
				        		</#if>
				        	</#if> 
				        	<#if CURRENT_USER.type=="DistributorManager">
				        		<#if refund.status=="ApplyRefund">
				        			<p>
				        			<a style="cursor: pointer;" data-tid="${trade.tid}" data-sellernick="${trade.seller_nick}" data-rid="${refund.id}"
				        			 data-oid="${order.oid}" class="approve-refund">同意退货</a>
				        			</p>
				        			<p>
				        			<a style="cursor: pointer;" data-tid="${trade.tid}" data-sellernick="${trade.seller_nick}" data-rid="${refund.id}"
				        			 data-oid="${order.oid}" class="reject-refund">拒绝退货</a>
				        			</p>
				        		</#if>
				        		<#if refund.status=="ReceiveRefund">
				        			<p>
				        			<a style="cursor: pointer;" data-tid="${trade.tid}" data-sellernick="${trade.seller_nick}" data-rid="${refund.id}"
				        			 data-oid="${order.oid}" class="refund-money">退款给分销商</a>
				        			</p>
				        			<p>
				        			<a style="cursor: pointer;" data-tid="${trade.tid}" data-sellernick="${trade.seller_nick}" data-rid="${refund.id}"
				        			 data-oid="${order.oid}" class="not-refund-money">分销商责任,不退款</a>
				        			</p>
				        		</#if>
				        	</#if> 
				        	<#if CURRENT_USER.type=="WareHouse">
				        		<#if refund.status=="Refunding">
				        			<p>
				        			<a style="cursor: pointer;" data-tid="${trade.tid}" data-sellernick="${trade.seller_nick}" data-rid="${refund.id}"
				        			 data-oid="${order.oid}" class="receive-refund">收到退货</a>
				        			</p>
				        		</#if>
				        	</#if> 
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
<#else>
        <div class="alert" style="margin: 5px;">
            <a class="close" data-dismiss="alert">×</a>
            <strong>无内容！</strong>
        </div>
</#if>
</@root.html>

<!-- start 提示框 -->
  <div class="modal hide" id="pop-up">
      <div class="modal-header">
        <a class="close" data-dismiss="modal">×</a>
        <h4 id="title"></h4>
      </div>
      <div class="modal-body">
	    <p>
	    	请输入<span id="action">退货</span>原因 <input type="text" id="reason" style="height: 30px; width: 350px;"/>
	    	<input type="hidden" id="curr-tid"/>
	    	<input type="hidden" id="curr-oid"/>
	    	<input type="hidden" id="curr-rid"/>
	    	<input type="hidden" id="curr-action"/>
	    </p>
	  </div>
      <div class="modal-footer">
         <a href="#" class="btn" data-dismiss="modal">取消</a>
    	 <a href="#" class="btn btn-primary" id="save">确定</a>
      </div>
  </div>
  <!-- end 提示框 -->
  
  <!-- start 提示框 -->
  <div class="modal hide" id="pop-up2">
      <div class="modal-header">
        <a class="close" data-dismiss="modal">×</a>
        <h4 id="title"></h4>
      </div>
      <div class="modal-body">
	    <p>
	    	请描述鞋子情况 <input type="text" id="refund-status" style="height: 30px; width: 350px;"/><br>
	    	&nbsp;&nbsp;可以再次销售&nbsp;&nbsp; <input type="checkbox" id="is-twice" checked/>
	    	<input type="hidden" id="curr-tid"/>
	    	<input type="hidden" id="curr-oid"/>
	    	<input type="hidden" id="curr-rid"/>
	    	<input type="hidden" id="curr-action"/>
	    </p>
	  </div>
      <div class="modal-footer">
         <a href="#" class="btn" data-dismiss="modal">取消</a>
    	 <a href="#" class="btn btn-primary" id="save2">确定</a>
      </div>
  </div>
  <!-- end 提示框 -->
  
<script>
$(document).ready(function(){
		
	  $('#tid').val('${tid!""}');
      $('#distributor').val('${dId!-1}');
      $('#seller_nick').val('${seller_nick!""}');
      $('#name').val('${name!""}');
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
		    	var name = $('#name').val();
                if(!page) {
                    page = 1;
                }
		        window.location.href="/trade/refund_list?page=" + page + "&status=" + status + "&seller_nick=" + seller_nick
		        	+ "&tid=" + tid + "&dId=" + distributor + "&name=" + name;
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
	    	var name = $('#name').val();
            if(!page) {
                page = 1;
            }
	   		window.location.href="/trade/refund_list?page=" + page + "&status=" + status + "&seller_nick=" + seller_nick
		        	+ "&tid=" + tid + "&dId=" + distributor + "&name=" + name;
	   });
	   
	   $('.cancel-refund, .approve-refund, .refund-money').click(function() {
			var action = $(this).attr("class");
			if(action=="cancel-refund") {
				var isTrue = confirm("确定要取消退货吗？");
				if(!isTrue) {
					return false;
				}
			} else if(action=="refund-money") {
				var isTrue = confirm("确定要退款给分销商吗？");
				if(!isTrue) {
					return false;
				}
			}
			$.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "rid=" + $(this).attr("data-rid") + "&tid=" + $(this).attr("data-tid") + 
	            "&oid=" + $(this).attr("data-oid") + "&action=" + action + "&sellerNick=" + $(this).attr("data-sellernick"),
	            url: "${rc.contextPath}/trade/action/change_refund_status",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
	                if(result.success == false) {
	                	alert(result.errorInfo);
	                } else {
	                	window.location.reload();
	                }
	            }}); 
		})
		
		$('.reject-refund, .receive-refund, .not-refund-money').click(function() {
			var action = $(this).attr("class");
			if(action=="reject-refund") {
				$('#action').html("拒绝退货");
			} else if(action=="not-refund-money") {
				$('#action').html("拒绝退款");
			}
			
			$('#curr-tid').val($(this).attr("data-tid"));
			$('#curr-oid').val($(this).attr("data-oid"));
			$('#curr-rid').val($(this).attr("data-rid"));
			$('#curr-action').val(action);
			$('#reason').val('');
			if(action=="receive-refund") {
				$('#pop-up2').modal({
	                keyboard: false
	            })
			} else {
				$('#pop-up').modal({
	                keyboard: false
	            })
			}
            $('#reason').focus();
		})
		
		$('#save').click(function() {
			var action = $('#curr-action').val();
			var tid = $('#curr-tid').val();
			var oid = $('#curr-oid').val();
			var rid = $('#curr-rid').val();
			var reason = $('#reason').val();
			$.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "rid=" + rid + "&tid=" + tid + 
	            "&oid=" + oid + "&action=" + action + "&comment=" + reason,
	            url: "${rc.contextPath}/trade/action/change_refund_status",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
	                if(result.success == false) {
	                	alert(result.errorInfo);
	                } else {
	                	window.location.reload();
	                }
	            }}); 
		})
		
		$('#save2').click(function() {
			var action = $('#curr-action').val();
			var tid = $('#curr-tid').val();
			var oid = $('#curr-oid').val();
			var rid = $('#curr-rid').val();
			var status = $('#refund-status').val();
			var isTwice = $('#is-twice').val();
			$.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "rid=" + rid + "&tid=" + tid + 
	            "&oid=" + oid + "&action=" + action + "&comment=" + status + "&isTwice=" + isTwice,
	            url: "${rc.contextPath}/trade/action/change_refund_status",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
	                if(result.success == false) {
	                	alert(result.errorInfo);
	                } else {
	                	window.location.reload();
	                }
	            }}); 
		})
	   
});
</script>
