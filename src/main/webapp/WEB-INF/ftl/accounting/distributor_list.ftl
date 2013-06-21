<#import "/templates/root.ftl" as root >
<@root.html css=["jdpicker.css", "trade_list.css"] js=["highcharts.js", "exporting.js"] >
<i class="icon-list-alt"></i>分销商列表<br><br>
	<table>
		<thead>
			<tr>
				<th>用户名</th>
				<th>姓名</th>
				<th>店铺名称</th>
				<th>折扣</th>
				<th>余额(元)</th>
				<th>操作</th>
			</tr>	
		</thead>
		<#if userList??>
		<tbody style="text-align: center;">
			<#list userList as user>
			<tr>
				<td>${user.username}</td>
				<td>${user.name}</td>
				<td>${user.distributor.seller_nick}</td>
				<td>${user.distributor.discount}</td>
				<td>${user.distributor.deposit/100}</td>
				<td>
					<a style="cursor: pointer;" data-username="${user.username}" id="recharge">打款</a>
				</td>
			</tr>
			</#list>
		</tbody>
		</#if>
	</table>
	<!-- start 提示框 -->
  <div class="modal hide" id="pop-up">
      <div class="modal-header">
        <a class="close" data-dismiss="modal">×</a>
        <h4 id="title"></h4>
      </div>
      <div class="modal-body">
	    <p>
	    	请输入金额 <input type="text" id="how-much"/>
	    	<input type="hidden" id="curr-user"/>
	    </p>
	  </div>
      <div class="modal-footer">
         <a href="#" class="btn" data-dismiss="modal">关闭</a>
    	 <a href="#" class="btn btn-primary" id="save">保存更新</a>
      </div>
  </div>
  <!-- end 提示框 -->
</@root.html>
<script type="text/javascript">
    //加载快递列表
    $(document).ready(function(){
    	$('#recharge').click(function() {
    		$('#title').val("为分销商 " + $(this).attr("data-username") + " 打款");
    		$('#curr-user').val($(this).attr("data-username"));
    		$('#pop-up').modal({
                keyboard: false
            })
    	})
    	
    	$('#save').click(function() {
	          $.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "how_much=" + $('#how-much').val() + "&username=" + $('#curr-user').val(),
	            url: "${rc.contextPath}/accounting/recharge",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
	                if(result.errorInfo != "success") {
	                	alert(result.errorInfo);
	                }
	                window.location.reload();
	            }}); 
    	})
    });
</script>
