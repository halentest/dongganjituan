<#import "/templates/root.ftl" as root >
<@root.html css=["jdpicker.css", "trade_list.css"] js=["highcharts.js", "exporting.js"] >
<i class="icon-list-alt"></i>账户列表<br><br>
	<strong>系统管理员</strong>
	<table>
		<thead>
			<tr>
				
				<th>用户名</th>
				<th>密码</th>
				<th>状态</th>
				<th>创建时间</th>
				<th>修改时间</th>
				<th>操作</th>
			</tr>	
		</thead>
		<#if userMap["Admin"]??>
			<#assign admin = userMap["Admin"]>
			<tbody style="text-align: center;">
				<#list admin as user>
					<tr>
						<td>${user.username}</td>
						<td>${user.password}</td>
						<td><#if user.enabled==1>有效<#else>禁用</#if></td>
						<td>${user.created?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>${user.modified?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>
							<a href="${rc.contextPath}/admin/modify_password_form?username=${user.username}">修改密码</a>
						</td>
					</tr>
				</#list>
			</tbody>
		</#if>
	</table>
	<br>
	
	<strong>财务</strong>
	&nbsp;&nbsp;<a href="${rc.contextPath}/admin/add_user_form?type=Accounting">添加</a>
	<table>
		<thead>
			<tr>
				<th>用户名</th>
				<th>密码</th>
				<th>状态</th>
				<th>创建时间</th>
				<th>修改时间</th>
				<th>操作</th>
			</tr>	
		</thead>
		<#if userMap["Accounting"]??>
			<#assign accounting = userMap["Accounting"]>
			<tbody style="text-align: center;">
				<#list accounting as user>
					<tr>
						<td>${user.username}</td>
						<td>${user.password}</td>
						<td><#if user.enabled==1>有效<#else>禁用</#if></td>
						<td>${user.created?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>${user.modified?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>
							<a href="${rc.contextPath}/admin/modify_password_form?username=${user.username}">修改密码</a> &nbsp;&nbsp;
							<#if user.enabled==1>
								<a href="${rc.contextPath}/admin/change_user_status?username=${user.username}&enabled=0">禁用</a>
							<#else>
								<a href="${rc.contextPath}/admin/change_user_status?username=${user.username}&enabled=1">启用</a>
							</#if>
						</td>
					</tr>
				</#list>
			</tbody>
		</#if>
	</table>
	
	<br>
	<strong>货品专员</strong> 
	&nbsp;&nbsp;<a href="${rc.contextPath}/admin/add_user_form?type=GoodsManager">添加</a>
	<table>
		<thead>
			<tr>
				<th>用户名</th>
				<th>密码</th>
				<th>状态</th>
				<th>创建时间</th>
				<th>修改时间</th>
				<th>操作</th>
			</tr>	
		</thead>
		<#if userMap["GoodsManager"]??>
			<#assign goodsManager = userMap["GoodsManager"]>
			<tbody style="text-align: center;">
				<#list goodsManager as user>
					<tr>
						<td>${user.username}</td>
						<td>${user.password}</td>
						<td><#if user.enabled==1>有效<#else>禁用</#if></td>
						<td>${user.created?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>${user.modified?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>
							<a href="${rc.contextPath}/admin/modify_password_form?username=${user.username}">修改密码</a> &nbsp;&nbsp;
							<#if user.enabled==1>
								<a href="${rc.contextPath}/admin/change_user_status?username=${user.username}&enabled=0">禁用</a>
							<#else>
								<a href="${rc.contextPath}/admin/change_user_status?username=${user.username}&enabled=1">启用</a>
							</#if>
						</td>
					</tr>
				</#list>
			</tbody>
		</#if>
	</table>
	
	<br>
	<strong>仓库管理员</strong> 
	&nbsp;&nbsp;<a href="${rc.contextPath}/admin/add_user_form?type=WareHouse">添加</a>
	<table>
		<thead>
			<tr>
				<th>用户名</th>
				<th>密码</th>
				<th>状态</th>
				<th>创建时间</th>
				<th>修改时间</th>
				<th>操作</th>
			</tr>	
		</thead>
		<#if userMap["WareHouse"]??>
			<#assign wareHouse = userMap["WareHouse"]>
			<tbody style="text-align: center;">
				<#list wareHouse as user>
					<tr>
						<td>${user.username}</td>
						<td>${user.password}</td>
						<td><#if user.enabled==1>有效<#else>禁用</#if></td>
						<td>${user.created?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>${user.modified?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>
							<a href="${rc.contextPath}/admin/modify_password_form?username=${user.username}">修改密码</a> &nbsp;&nbsp;
							<#if user.enabled==1>
								<a href="${rc.contextPath}/admin/change_user_status?username=${user.username}&enabled=0">禁用</a>
							<#else>
								<a href="${rc.contextPath}/admin/change_user_status?username=${user.username}&enabled=1">启用</a>
							</#if>
						</td>
					</tr>
				</#list>
			</tbody>
		</#if>
	</table>
	
	<br>
	<strong>分销商管理员</strong> 
	&nbsp;&nbsp;<a href="${rc.contextPath}/admin/add_user_form?type=DistributorManager">添加</a>
	<table>
		<thead>
			<tr>
				<th>用户名</th>
				<th>密码</th>
				<th>状态</th>
				<th>创建时间</th>
				<th>修改时间</th>
				<th>操作</th>
			</tr>	
		</thead>
		<#if userMap["DistributorManager"]??>
			<#assign distributorManager = userMap["DistributorManager"]>
			<tbody style="text-align: center;">
				<#list distributorManager as user>
					<tr>
						<td>${user.username}</td>
						<td>${user.password}</td>
						<td><#if user.enabled==1>有效<#else>禁用</#if></td>
						<td>${user.created?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>${user.modified?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>
							<a href="${rc.contextPath}/admin/modify_password_form?username=${user.username}">修改密码</a> &nbsp;&nbsp;
							<#if user.enabled==1>
								<a href="${rc.contextPath}/admin/change_user_status?username=${user.username}&enabled=0">禁用</a>
							<#else>
								<a href="${rc.contextPath}/admin/change_user_status?username=${user.username}&enabled=1">启用</a>
							</#if>
						</td>
					</tr>
				</#list>
			</tbody>
		</#if>
	</table>
	
	<br>
	<strong>分销商</strong> 
	&nbsp;&nbsp;<a href="${rc.contextPath}/admin/add_distributor_form">添加</a>
	<#list dList as d>
	<table>
		<thead>
			<tr>
				<th>id</th>
				<th>姓名</th>
				<th>电话</th>
				<th>折扣</th>
				<th>余额(元)</th>
				<th>是否自营</th>
				<th>自动审核</th>
				<th>创建时间</th>
				<th>修改时间</th>
				<th>操作</th>
			</tr>	
		</thead>
		<tbody style="text-align: center;">
			<tr>
				<td>${d.id}</td>
				<td>${d.name}</td>
				<td>${d.phone}</td>
				<td>${d.discount}</td>
				<td>${d.deposit/100}</td>
				<td><#if d.self==1>是<#else>否</#if></td>
				<td><#if d.noCheck==1>
					<a href="${rc.contextPath}/admin/change_check?v=0&dId=${d.id}">是</a>
					<#else>
					<a href="${rc.contextPath}/admin/change_check?v=1&dId=${d.id}">否</a>
					</#if>
				</td>
				<td>${d.created?string('yyyy-MM-dd HH:mm:ss')}</td>
				<td>${d.modified?string('yyyy-MM-dd HH:mm:ss')}</td>
				<td>
				<a href="${rc.contextPath}/admin/add_shop_form?dId=${d.id}">添加店铺</a>
				<a href="${rc.contextPath}/admin/change_discount_form?dId=${d.id}">修改折扣</a>
				</td>
			</tr>
		</tbody>
	</table>
		<#list d.shopList as shop>
		<table style="width: 95%; float: right;">
			<thead>
				<tr>
					<th>id</th>
					<th>店铺名称</th>
					<th>店铺类型</th>
                    <th>上货比率</th>
					<th>自动同步订单</th>
					<th>自动同步库存</th>
					<th>创建时间</th>
					<th>修改时间</th>
					<th>操作</th>
				</tr>	
			</thead>
			<tbody style="text-align: center;">
				<tr>
					<td>${shop.id}</td>
					<td>${shop.sellerNick}</td>
					<td>${shop.type}</td>
                    <td>${shop.rate?string.percent}</td>
					<td><#if shop.autoSync==1>是<#else>否</#if></td>
					<td><#if shop.autoSyncStore==1>
						<a href="${rc.contextPath}/admin/change_sync_store?v=0&sId=${shop.id}">是</a>
						<#else>
						<a href="${rc.contextPath}/admin/change_sync_store?v=1&sId=${shop.id}">否</a>
						</#if>
					</td>
					<td>${shop.created?string('yyyy-MM-dd HH:mm:ss')}</td>
					<td>${shop.modified?string('yyyy-MM-dd HH:mm:ss')}</td>
					<td>
					<a href="${rc.contextPath}/admin/add_user_form?type=ServiceStaff&shopId=${shop.id}">添加客服</a> &nbsp;&nbsp;
					<a href="${rc.contextPath}/admin/add_user_form?type=Distributor&shopId=${shop.id}">添加客服经理</a>
                    <a href="${rc.contextPath}/admin/change_rate_form?sId=${shop.id}&oldRate=${shop.rate}">修改上货比率</a>
					</td>
				</tr>
			</tbody>
		</table>
			<#list shop.userList as user>
			<table style="width: 90%; float: right;">
				<thead>
					<tr>
						<th>id</th>
						<th>用户名</th>
						<th>密码</th>
						<th>是否有效</th>
						<th>类型</th>
						<th>创建时间</th>
						<th>修改时间</th>
						<th>操作</th>
					</tr>	
				</thead>
				<tbody style="text-align: center;">
					<tr>
						<td>${user.id}</td>
						<td>${user.username}</td>
						<td>${user.password}</td>
						<td><#if user.enabled==1>是<#else>否</#if></td>
						<td><#if user.type=="Distributor">客服经理<#else>客服</#if></td>
						<td>${user.created?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>${user.modified?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>
							<a href="${rc.contextPath}/admin/modify_password_form?username=${user.username}">修改密码</a> &nbsp;&nbsp;
							<#if user.enabled==1>
								<a href="${rc.contextPath}/admin/change_user_status?username=${user.username}&enabled=0">禁用</a>
							<#else>
								<a href="${rc.contextPath}/admin/change_user_status?username=${user.username}&enabled=1">启用</a>
							</#if>
						</td>
					</tr>
				</tbody>
			</table>
			</#list>
		</#list>
	</#list>	
</@root.html>
