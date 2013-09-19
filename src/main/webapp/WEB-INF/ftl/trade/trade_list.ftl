<#import "/templates/root.ftl" as root >

<@root.html active=3 css=["trade_list.css", "jqpagination.css", "easyui.css", "icon.css"]
js=["trade_list.js", "pagination.js", "jquery.jqpagination.min.js", "jquery.cookie.js", "jquery.easyui.min.js"]>
    <#if CURRENT_USER.type=="WareHouse">
    <script language="javascript" src="${rc.contextPath}/js/LodopFuncs.js"></script>
	<object  id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0>
	    <embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0></embed>
	</object>
    </#if>
	<div style="width: 98%; height: 60px; background-color: #d6dff7; padding-top: 5px; padding-left: 20px; <#if CURRENT_USER.type=="WareHouse">margin-top: -20px;</#if>">
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
		<input type="hidden" id="status">
		<strong>快递</strong>
		<select id="delivery" style="width: 8%;">
			<option value="">所有快递</option>
			<#list logistics as lo>
				<option value="${lo.name}">${lo.name}</option>
			</#list>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
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
        <div style="margin-top: 5px;">
            <strong>开始时间</strong>
            <input id="start" style="height: 15px;" type="input" placeholder="格式:2013-06-29 12:30:30">
            &nbsp;&nbsp;&nbsp;&nbsp;
            <strong>结束时间</strong>
            <input id="end" style="height: 15px;" type="input" placeholder="格式:2013-06-29 12:30:30">
            <span style="float: right; margin-right:15px;">
                <button id="search">搜索</button>&nbsp;&nbsp;&nbsp;&nbsp;共${totalCount}条交易
                &nbsp;&nbsp;&nbsp;&nbsp;
                <button id="search">查看报表</button>
            </span>
        </div>

	</div>
<#if trade_list?size gt 0>

	<div class="pagination">
	    <a href="#" class="first" data-action="first">&laquo;</a>
	    <a href="#" class="previous" data-action="previous">&lsaquo;</a>
	    <input id="page" type="text" readonly="readonly" data-current-page="${paging.page}" data-page-string="1 of 3" data-max-page="${paging.pageCount}" />
	    <a href="#" class="next" data-action="next">&rsaquo;</a>
	    <a href="#" class="last" data-action="last">&raquo;</a>
	</div>
	<br>
	<#list trade_list as trade>
		<#assign tColor="#d6dff7"/>
        <#assign orderList = trade.myOrderList/>
        <!--准备打印在快递单上的商品信息-->
        <#assign goodsInfo=""/>
        <#list orderList as order>
            <#assign goodsInfo = order.goods_id + " " + order.sku.color + " " + order.sku.size + " " + order.quantity + "\r\n"/>
        </#list>
		<table>
		    <tbody>
		      <tr class="trade" style="background-color: ${tColor};">
		        <td colspan="7">
		        	<input class="wait-check" data-tid="${trade.tid}" data-status="${trade.myStatus.getStatus()}"
		        	data-delivery="${trade.delivery!''}"
		        	data-name="${trade.name}"
		        	data-address="${trade.state!''}${trade.city!''}${trade.district!''}${trade.address}"
		        	data-mobile="${trade.mobile!''}"
		        	data-state="${trade.state!''}"
                    data-goods-info="${goodsInfo!''}"
		        	type="checkbox"/> &nbsp;&nbsp;&nbsp;
		        	${trade.come_from!}&nbsp;&nbsp;&nbsp;
		        	<strong>订单编号：</strong></strong>${trade.tid}  &nbsp;&nbsp;&nbsp;
		        	<strong>创建时间：</strong>${trade.created?string('yyyy-MM-dd HH:mm:ss')} &nbsp;&nbsp;&nbsp;
		        	<strong>卖家: </strong>${trade.seller_nick} &nbsp;&nbsp;&nbsp;
		        	<strong>买家: <strong>${trade.name} &nbsp;&nbsp;&nbsp;
		        	<span style="float: right; margin-right: 5px;"><strong>${trade.myStatus.desc}<strong></span>
		        </td>
		      </tr>

		      <#list orderList as order>
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
				        	<p><strong>${order.oStatus.desc}</strong></p>
				        	<p><strong>数量：</strong>${order.quantity}</p>
				        	<p><strong>价格：</strong>${order.payment/100}元</p>
                            <#if CURRENT_USER.type=="WareHouse">
                                <#if trade.status=="WAIT_SELLER_SEND_GOODS" && trade.my_status==2 || trade.my_status==3>
                                    <p><a class="no-goods" data-tid="${trade.tid}" data-oid="${order.oid}" style="cursor: pointer">无货</a></p>
                                </#if>
                            </#if>
				        	<#if CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="ServiceStaff">
				        		<#if trade.my_status==4 && order.status=="WAIT_BUYER_CONFIRM_GOODS">
				        			<p>
				        			<a style="cursor: pointer;" data-tid="${trade.tid}"
				        			 data-oid="${order.oid}" class="apply-refund">申请退货</a>
				        			</p>
				        		</#if>
				        	</#if>
				        </td>
				        <#if order_index==0>
				        <td rowspan="${orderList?size}" style="width: 25%;">
				        	<p><strong>地址：</strong>${trade.state!''}${trade.city!''}${trade.district!''}${trade.address}</p>
				        	<p>
				        		<strong>收货人：</strong>${trade.name} &nbsp;&nbsp;
				        		<strong>电话：</strong>${trade.phone!''} &nbsp;&nbsp;
				        		<strong>手机：</strong>${trade.mobile} &nbsp;&nbsp;
				        	</p>
				        	<p><strong>邮编：</strong>${trade.postcode!''}</p>
				        	<#if trade.my_status==6 || trade.my_status==4>
				        		<p><strong>快递：</strong>${trade.delivery!''} &nbsp;&nbsp;<strong>单号：</strong>${trade.delivery_number!''}</p>
				        	</#if>
				        	<p>
				        		<strong>总数：</strong>${trade.goods_count} &nbsp;&nbsp;
				        		<strong>总额：</strong>${trade.payment/100}元 &nbsp;&nbsp;
				        		<strong>快递费：</strong>${trade.delivery_money/100}元
				        	</p>
				        </td>
				        <td rowspan="${orderList?size}" style="width: 15%;">
				        	<p><strong>买家留言：</strong>${trade.buyer_message!}</p> &nbsp;&nbsp;
				        	<p><strong>备注：</strong>${trade.seller_memo!}</p>
				        </td>
				        <td rowspan="${orderList?size}" style="text-align: right; padding-right: 10px;">
				        	<#if CURRENT_USER.type=="WareHouse">
				        		<p><strong>快递</strong>:
				        		<span>${trade.delivery}</span>
				        		<#if trade.status=="WAIT_SELLER_SEND_GOODS" && trade.my_status==2>
					        		<p><a class="find-goods" data-tid="${trade.tid}" style="cursor: pointer"></a></p>
					        		<p><a class="no-goods" data-tid="${trade.tid}" style="cursor: pointer">无货</a></p>
				        		</#if>
				        		<#if trade.my_status==3>
				        			<p>
				        				<#if trade.delivery=="申通E物流">
                                            <#assign prnFunc='prn_shentong'/>
                                        <#elseif trade.delivery=="圆通速递">
                                            <#assign prnFunc='prn_yuantong'/>
                                        <#elseif trade.delivery=="韵达快运" || trade.delivery=="韵达">
                                            <#assign prnFunc='prn_yunda'/>
                                        <#elseif trade.delivery=="EMS">
                                            <#assign prnFunc='prn_ems'/>
                                        <#elseif trade.delivery=="顺丰速运">
                                            <#assign prnFunc='prn_sf'/>
										</#if>
                                        <!--<a href="${rc.contextPath}/set_print?delivery=${trade.delivery}">打印调整</a>
					        			&nbsp;&nbsp;<a href="javascript:prn(${prnFunc}, '${trade.delivery}', '${sellerInfo.sender}', '${sellerInfo.from_state}', '${sellerInfo.from_company}',
					        			     '${sellerInfo.from_address}', '${sellerInfo.mobile}',
											'${trade.name}', '${trade.name}', '${trade.state!''}${trade.city!''}${trade.district!''}${trade.address}', '${trade.mobile!''}', '${trade.state!''}')">
											打印快递单
										</a> -->
									</p>
				        			<p><a class="send-goods" data-tid="${trade.tid}" data-delivery="${trade.delivery}"
				        				data-from="${trade.come_from}" data-sellernick="${trade.seller_nick}" style="cursor: pointer">扫描单号</a></p>
				        			<p><a class="no-goods" data-tid="${trade.tid}" style="cursor: pointer">无货</a></p>
				        		</#if>
				        	</#if>
				        	<#if CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="ServiceStaff">
				        		<p><strong>快递</strong>:
				        		<span>${trade.delivery!''}</span>
				        		<#if trade.my_status==0 || trade.my_status==1>
				        			<select style="display: none; width: 80px;">
				        				<#list logistics as lo>
				        					<option value="${lo.code}" <#if trade.delivery==lo.name>selected</#if>>${lo.name}</option>
				        				</#list>
									</select>
				        			<a style="cursor: pointer;" class="modify-delivery">修改</a>
				        			<a style="cursor: pointer; display: none;" data-tid="${trade.tid}" data-quantity="${trade.goods_count}"
				        				data-goods="${order.goods_id}" data-province="${trade.state!''}"
				        				class="modify-delivery-submit">保存</a>
				        			<a style="cursor: pointer; display: none;" class="modify-delivery-cancel">取消</a>
				        		</#if>
				        		</p>
				        		<#if trade.my_status==0>
				        			<p><a class="submit" data-tid="${trade.tid}" style="cursor: pointer">提交</a></p>
				        		</#if>
				        		<#if trade.my_status==0 || trade.my_status==1 || trade.my_status==2>
				        			<p><a class="cancel" data-tid="${trade.tid}" style="cursor: pointer">作废</a></p>
                                    <p><a href="${rc.contextPath}/trade/action/modify_receiver_info_form?tid=${trade.tid}" style="cursor: pointer">修改收货地址</a></p>
				        		</#if>
                                <#if trade.my_status==-6>
                                    <a class="cancel" data-tid="${trade.tid}" style="cursor: pointer">已处理</a>
                                </#if>
				        	</#if>
				        	<#if CURRENT_USER.type=="DistributorManager">
				        		<p><strong>快递</strong>:
					        		<span>${trade.delivery}</span>
					        		<#if trade.my_status==2>
					        			<select style="display: none;">
					        				<#list logistics as lo>
					        					<option value="${lo.code}" <#if trade.delivery==lo.name>selected</#if>>${lo.name}</option>
					        				</#list>
										</select>
					        			<a style="cursor: pointer;" class="modify-delivery">修改</a>
					        			<a style="cursor: pointer; display: none;" data-tid="${trade.tid}" data-quantity="${trade.goods_count}"
					        				data-goods="${order.goods_id}" data-province="${trade.state!''}"
					        				class="modify-delivery-submit">保存</a>
					        			<a style="cursor: pointer; display: none;" class="modify-delivery-cancel">取消</a>
					        		</#if>
				        		</p>
				        		<#if trade.my_status==1>
				        			<a class="cancel" data-tid="${trade.tid}" style="cursor: pointer">不通过</a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				        			<a class="approve1" data-tid="${trade.tid}" style="cursor: pointer">通过</a>
				        		</#if>
				        		<#if trade.my_status==-4>
				        			同意   不同意
				        		</#if>
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
    <div class="action-bar">
        <a id="check-all" style="cursor: pointer;">全选</a>&nbsp;&nbsp;&nbsp;&nbsp;
        <a id="not-check-all" style="cursor: pointer;">全不选</a>&nbsp;&nbsp;&nbsp;&nbsp;
        <#if CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="ServiceStaff">
            <#if status==0>
                <a id="batch-submit" style="cursor: pointer;">批量提交</a>
            </#if>
        </#if>
        <#if CURRENT_USER.type=="DistributorManager" && status==1>
            <a id="batch-approve1" style="cursor: pointer;">批量通过</a>
        </#if>
        <#if CURRENT_USER.type=="WareHouse">
            <#if status?? && status=2>
                <a id="batch-find-goods" style="cursor: pointer;">批量拣货</a>
            </#if>
            <#if status?? && status=6>
                <a id="batch-out-goods" style="cursor: pointer;">批量发货</a>
            </#if>
            <#if status?? && status=3>
                <select id="delivery-print" style="width: 8%;">
                    <option value="">选择快递</option>
                    <#list logistics as lo>
                        <option value="${lo.name}">${lo.name}</option>
                    </#list>
                </select>
                <a id="batch-prn-kdd" style="cursor: pointer;">打印快递单</a>
                <a id="print-setup" style="cursor: pointer;">打印调整</a>
                <a id="paper-setup" style="cursor: pointer;">纸张设置</a>
                <a href="${rc.contextPath}/trade/export_finding" style="cursor: pointer;">生成拣货单</a>
            </#if>
        </#if>
    </div>
<#else>
    <#if CURRENT_USER.type=="WareHouse">
        <div class="alert" style="margin: 5px;">
            <a class="close" data-dismiss="alert">×</a>
            <strong>无内容！</strong>
        </div>
        <#if status?? && status=3>
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

<!-- start 提示框 -->
  <div class="modal hide" id="pop-up">
      <div class="modal-header">
        <a class="close" data-dismiss="modal">×</a>
        <h4 id="title"></h4>
      </div>
      <div class="modal-body">
	    <p>
	    	请输入单号 <input type="text" id="tracking-number" style="height: 30px; width: 350px;"/>
	    	<input type="hidden" id="curr-tid"/>
	    	<input type="hidden" id="curr-delivery"/>
	    	<input type="hidden" id="curr-from"/>
	    	<input type="hidden" id="curr-sellernick"/>
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
	    	请输入退货原因 <input type="text" id="refund-reason" style="height: 30px; width: 350px;"/>
	    	<input type="hidden" id="refund-curr-tid"/>
	    	<input type="hidden" id="refund-curr-oid"/>
	    </p>
	  </div>
      <div class="modal-footer">
         <a href="#" class="btn" data-dismiss="modal">取消</a>
    	 <a href="#" class="btn btn-primary" id="save2">确定</a>
      </div>
  </div>
  <!-- end 提示框 -->
<div id="w" class="easyui-window" title="纸张设置" data-options="modal:true,collapsible:false,closed:true,
        resizable:false,shadow:false,minimizable:false, maximizable:false" style="width:300px;height:180px;padding:2px;">
    <form id="ff" method="post">
        <table>
            <tr>
                <td>宽度:</td>
                <td><input id="paper-width" class="easyui-validatebox" type="text" data-options="required:true,validType:'length[1, 10]',missingMessage:'不能为空,请输入大于0的数字'"></input></td>
            </tr>
            <tr>
                <td>高度:</td>
                <td><input id="paper-height" class="easyui-validatebox" type="text" data-options="required:true,validType:'length[1, 10]',missingMessage:'不能为空,请输入大于0的数字'"></input></td>
            </tr>
        </table>
        <div style="text-align:center;padding:5px">
            <a class="easyui-linkbutton" onclick="savePaperChange()">确定</a>
            <a class="easyui-linkbutton" onclick="cancelPaperChange()">取消</a>
        </div>
    </form>
</div>

<script>

	$(document).ready(function() {
		$('.cancel, .approve1, .submit, .no-goods, .find-goods').click(function() {
			var action = $(this).attr("class");
			if(action=="cancel") {
				var isTrue = confirm("确定作废这个订单吗？");
				if(!isTrue) {
					return false;
				}
			} else if(action=="no-goods") {
				var isTrue = confirm("确定没有货吗？");
				if(!isTrue) {
					return false;
				}
			}
			$.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "tid=" + $(this).attr("data-tid") + "&action=" + action + "&oid=" + $(this).attr("data-oid"),
	            url: "${rc.contextPath}/trade/action/change_status",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
	                if(result.success == false) {
	                	alert(result.errorInfo);
	                } else {
	                	window.location.reload();
	                }
	            }});
		})

		$('.modify-delivery').click(function() {
			$(this).css("display", "none");
			$(this).next().css("display", "inline");
			$(this).next().next().css("display", "inline");
			$(this).prev().prev("span").css("display", "none");
			$(this).prev("select").css("display", "inline");
		})

		$('.modify-delivery-cancel').click(function() {
			$(this).css("display", "none");
			$(this).prev().css("display", "none");
			$(this).prev().prev().css("display", "inline");
			$(this).prev().prev().prev().prev("span").css("display", "inline");
			$(this).prev().prev().prev("select").css("display", "none");
		})

		$('.modify-delivery-submit').click(function() {
			var selected = $(this).prev().prev().val();
			var curr = $(this);
			$.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "delivery=" + selected + "&tid=" + $(this).attr("data-tid") + "&goods=" + $(this).attr("data-goods") +
	            	"&quantity=" + $(this).attr("data-quantity") + "&province=" + $(this).attr("data-province"),
	            url: "${rc.contextPath}/trade/action/change_delivery",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
	                if(result.success == false) {
	                	alert(result.errorInfo);
	                } else {
	                	$(curr).css("display", "none");
	                	$(curr).next().css("display", "none");
	                	$(curr).prev().css("display", "inline");
	                	$(curr).prev().prev().css("display", "none");
	                	$(curr).prev().prev().prev().html(result.errorInfo);
	                	$(curr).prev().prev().prev().css("display", "inline");
	                }
	            }});
		})

		$('#distributor').change(function() {
			$('#seller_nick').empty();
	        $('#seller_nick').append('<option value="">所有店铺</option>');
	        $.ajax({
	            type: "get",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "dId=" + $(this).val(),
	            url: "${rc.contextPath}/trade/list_shop",//要访问的后台地址
	            success: function(shopList){//msg为返回的数据，在这里做数据绑定
	                $.each(shopList, function(index, shop) {
	                    $('#seller_nick').append('<option value="' + shop.sellerNick + '">' + shop.sellerNick + '</option>');
	                });
	            }});
		})

		$('.send-goods').click(function() {
			$('#curr-tid').val($(this).attr("data-tid"));
			$('#curr-delivery').val($(this).attr("data-delivery"));
			$('#curr-from').val($(this).attr("data-from"));
			$('#curr-sellernick').val($(this).attr("data-sellernick"));
			$('#tracking-number').val('');
    		$('#pop-up').modal({
                keyboard: false
            })
            $('#tracking-number').focus();
		})

		$('#save').click(function() {
			  var trackingNumber = $('#tracking-number').val();
              if(!trackingNumber || trackingNumber.length==0) {
                    alert("单号不能为空！");
                    return false;
              }
			  var tid = $('#curr-tid').val();
			  var delivery = $('#curr-delivery').val();
			  var from = $('#curr-from').val();
			  var sellerNick = $('#curr-sellernick').val();
	          $.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "tid=" + tid + "&delivery=" + delivery + "&trackingNumber=" + trackingNumber,
	            url: "${rc.contextPath}/trade/add_tracking_number",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
	                if(result.errorInfo != "success") {
	                	alert(result.errorInfo);
	                } else {
		                window.location.reload();
	                }
	            }});
    	})

    	$('.apply-refund').click(function() {
    		$('#refund-curr-tid').val($(this).attr("data-tid"));
			$('#refund-curr-oid').val($(this).attr("data-oid"));
    		$('#pop-up2').modal({
                keyboard: false
            })
    	})

    	$('#save2').click(function() {
			  var tid = $('#refund-curr-tid').val();
			  var oid = $('#refund-curr-oid').val();
			  var refundReason = $('#refund-reason').val();
	          $.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "tid=" + tid + "&oid=" + oid + "&refundReason=" + refundReason,
	            url: "${rc.contextPath}/trade/apply_refund",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
	                if(result.errorInfo != "success") {
	                	alert(result.errorInfo);
	                } else {
		                window.location.reload();
	                }
	            }});
    	})

    	$('#check-all').click(function() {
    		$('.wait-check').attr("checked", true);
    	})

    	$('#not-check-all').click(function() {
    		$('.wait-check').attr("checked", false);
    	})

    	$('#batch-submit').click(function() {
    		var checked = $('.wait-check:checked');
    		if(checked.length==0) {
    			alert('至少选中一个订单!');
    			return false;
    		}
    		var b = true;
    		var tids = "";
    		$(checked).each(function(index, item) {
    			var status = $(item).attr("data-status");
    			if(status != 0) {
    				b = false;
    				return false;
    			}
    			var tid = $(item).attr("data-tid");
    			tids += tid;
    			tids += ";";
    		})
    		if(!b) {
    			alert('不能提交除"新建"以外的订单!');
    		} else {
    			$.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "tids=" + tids + "&action=submit",
	            url: "${rc.contextPath}/trade/action/batch_change_status",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
		                if(result.errorInfo != "success") {
		                	alert(result.errorInfo);
		                	window.location.reload();
		                } else {
			                window.location.reload();
		                }
	            }});
    		}
    	})

    	$('#batch-approve1').click(function() {
    		var checked = $('.wait-check:checked');
    		if(checked.length==0) {
    			alert('至少选中一个订单!');
    			return false;
    		}
    		var b = true;
    		var tids = "";
    		$(checked).each(function(index, item) {
    			var status = $(item).attr("data-status");
    			if(status != 1) {
    				b = false;
    				return false;
    			}
    			var tid = $(item).attr("data-tid");
    			tids += tid;
    			tids += ";";
    		})
    		if(!b) {
    			alert('不能审核除"待审核"以外的订单!');
    		} else {
    			$.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "tids=" + tids + "&action=approve1",
	            url: "${rc.contextPath}/trade/action/batch_change_status",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
		                if(result.errorInfo != "success") {
		                	alert(result.errorInfo);
		                	window.location.reload();
		                } else {
			                window.location.reload();
		                }
	            }});
    		}
    	})

        $('#batch-out-goods').click(function() {
            var checked = $('.wait-check:checked');
            if(checked.length==0) {
                alert('至少选中一个订单!');
                return false;
            }
            var b = true;
            var tids = "";
            $(checked).each(function(index, item) {
                var status = $(item).attr("data-status");
                if(status != 6) {
                    b = false;
                    return false;
                }
                var tid = $(item).attr("data-tid");
                tids += tid;
                tids += ";";
            })
            if(!b) {
                alert('不能发货除"已扫描"以外的订单!');
            } else {
                $.ajax({
                    type: "post",//使用get方法访问后台
                    dataType: "json",//返回json格式的数据
                    data: "tids=" + tids,
                    url: "${rc.contextPath}/trade/action/delivery_tracking_number",//要访问的后台地址
                    success: function(result){//msg为返回的数据，在这里做数据绑定
                    if(result.errorInfo != "success") {
                        alert(result.errorInfo);
                        window.location.reload();
                    } else {
                        window.location.reload();
                    }
                }});
            }
        })

    	$('#batch-find-goods').click(function() {
    		var checked = $('.wait-check:checked');
    		if(checked.length==0) {
    			alert('至少选中一个订单!');
    			return false;
    		}
    		var b = true;
    		var tids = "";
    		$(checked).each(function(index, item) {
    			var status = $(item).attr("data-status");
    			if(status != 2) {
    				b = false;
    				return false;
    			}
    			var tid = $(item).attr("data-tid");
    			tids += tid;
    			tids += ";";
    		})
    		if(!b) {
    			alert('不能拣货除"待发货"以外的订单!');
    		} else {
    			$.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "tids=" + tids + "&action=find-goods",
	            url: "${rc.contextPath}/trade/action/batch_change_status",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
		                if(result.errorInfo != "success") {
		                	alert(result.errorInfo);
		                	window.location.reload();
		                } else {
			                window.location.reload();
		                }
	            }});
    		}
    	})

    	$('#batch-prn-kdd').click(function() {
            var delivery = $('#delivery-print').val();
            if(!delivery || delivery=="") {
                alert("请选择快递");
                return false;
            }
    		var checked = $('.wait-check:checked');
    		if(checked.length==0) {
    			alert('至少选中一个订单!');
    			return false;
    		}

            if(delivery=="韵达快运" || delivery=="韵达") {
                bg = "/img/kuaidi/yunda.jpg";
            } else if(delivery=="申通E物流") {
                bg = "/img/kuaidi/shentong.jpg";
            } else if(delivery=="顺丰速运") {
                bg = "/img/kuaidi/sf.jpg";
            } else if(delivery=="EMS") {
                bg = "/img/kuaidi/ems_jingji2.jpg";
            } else if(delivery=="圆通速递") {
                bg = "/img/kuaidi/yuantong-new.jpg";
            }

            LODOP=getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
            LODOP.PRINT_INIT(delivery);

            var width = $.cookie(delivery + "width");
            var height = $.cookie(delivery + "height");
            if(!width) {
                width = 2300;
            } else {
                width = parseInt(width);
            }
            if(!height) {
                height = 1270;
            } else {
                height = parseInt(height);
            }
            LODOP.SET_PRINT_PAGESIZE(1,width,height,"");
            LODOP.SET_PRINT_STYLE("FontSize",16);
            LODOP.SET_PRINT_STYLE("Bold",1);
            $(checked).each(function(index, item) {
                var name = $(item).attr("data-name");
                var address = $(item).attr("data-address");
                var mobile = $(item).attr("data-mobile");
                var state = $(item).attr("data-state");
                var goodsInfo = $(item).attr("data-goods-info");

                CreatePrintPage('${sellerInfo.sender}', '${sellerInfo.from_state}', '${sellerInfo.from_company}',
                            '${sellerInfo.from_address}', '${sellerInfo.mobile}',
                            name, name, address, mobile, state, goodsInfo,bg);

    	    })
            LODOP.SET_PREVIEW_WINDOW(1,1,0,900,600,"");
            LODOP.PREVIEW();
	    })
    })

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
    }

	function CreateOneFormPage(){
		LODOP=getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
		LODOP.PRINT_INIT("1");
		LODOP.SET_PRINT_STYLE("FontSize",10);
		LODOP.SET_PRINT_STYLE("Bold",1);
		//LODOP.ADD_PRINT_TEXT(50,231,260,39,"打印页面部分内容");
		//LODOP.ADD_PRINT_HTM(88,200,350,600,document.getElementById("form1").innerHTML);
	};

    function prn(func, delivery, sender, from, from_company, from_address, sender_mobile,
        receiver, to_company, to_address, receiver_mobile, to) {
        var x = $.cookie(delivery + "x");
        var y = $.cookie(delivery + "y");
        if(!x) {
            x = 0;
        } else {
            x = parseInt(x);
        }
        if(!y) {
            y = 0;
        } else {
            y = parseInt(y);
        }
        console.log("x=" + x + " y=" + y);
        func(x, y, sender, from, from_company, from_address, sender_mobile,
            receiver, to_company, to_address, receiver_mobile, to);
    }
</script>
