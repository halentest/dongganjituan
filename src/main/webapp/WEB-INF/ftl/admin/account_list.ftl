<#import "/templates/root.ftl" as root >
<@root.html css=["jdpicker.css", "trade_list.css"] js=["highcharts.js", "exporting.js"] >
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
						<td>修改密码</td>
					</tr>
				</#list>
			</tbody>
		</#if>
	</table>
	<br>
	
	<strong>财务</strong>
	&nbsp;&nbsp;<a>添加</a>
	<table>
		<thead>
			<tr>
				<th>用户名</th>
				<th>密码</th>
				<th>姓名</th>
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
						<td>${user.name}</td>
						<td><#if user.enabled==1>有效<#else>禁用</#if></td>
						<td>${user.created?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>${user.modified?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>
							修改密码 &nbsp;&nbsp;
							禁用
						</td>
					</tr>
				</#list>
			</tbody>
		</#if>
	</table>
	
	<br>
	<strong>货品专员</strong> 
	&nbsp;&nbsp;<a>添加</a>
	<table>
		<thead>
			<tr>
				<th>用户名</th>
				<th>密码</th>
				<th>姓名</th>
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
						<td>${user.name}</td>
						<td><#if user.enabled==1>有效<#else>禁用</#if></td>
						<td>${user.created?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>${user.modified?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>
							修改密码 &nbsp;&nbsp;
							禁用
						</td>
					</tr>
				</#list>
			</tbody>
		</#if>
	</table>
	
	<br>
	<strong>仓库管理员</strong> 
	&nbsp;&nbsp;<a>添加</a>
	<table>
		<thead>
			<tr>
				<th>用户名</th>
				<th>密码</th>
				<th>姓名</th>
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
						<td>${user.name}</td>
						<td><#if user.enabled==1>有效<#else>禁用</#if></td>
						<td>${user.created?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>${user.modified?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>
							修改密码 &nbsp;&nbsp;
							禁用
						</td>
					</tr>
				</#list>
			</tbody>
		</#if>
	</table>
	
	<br>
	<strong>客服</strong> 
	&nbsp;&nbsp;<a>添加</a>
	<table>
		<thead>
			<tr>
				<th>用户名</th>
				<th>密码</th>
				<th>姓名</th>
				<th>店铺</th>
				<th>状态</th>
				<th>创建时间</th>
				<th>修改时间</th>
				<th>操作</th>
			</tr>	
		</thead>
		<#if userMap["ServiceStaff"]??>
			<#assign serviceStaff = userMap["ServiceStaff"]>
			<tbody style="text-align: center;">
				<#list serviceStaff as user>
					<tr>
						<td>${user.username}</td>
						<td>${user.password}</td>
						<td>${user.name}</td>
						<td>${user.seller_nick}</td>
						<td><#if user.enabled==1>有效<#else>禁用</#if></td>
						<td>${user.created?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>${user.modified?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>
							修改密码 &nbsp;&nbsp;
							禁用
						</td>
					</tr>
				</#list>
			</tbody>
		</#if>
	</table>
	
	<br>
	<strong>分销商</strong> 
	&nbsp;&nbsp;<a>添加</a>
	<table>
		<thead>
			<tr>
				<th>用户名</th>
				<th>密码</th>
				<th>姓名</th>
				<th>店铺</th>
				<th>折扣</th>
				<th>余额(元)</th>
				<th>状态</th>
				<th>创建时间</th>
				<th>修改时间</th>
				<th>操作</th>
			</tr>	
		</thead>
		<#if userMap["Distributor"]??>
			<#assign distributor = userMap["Distributor"]>
			<tbody style="text-align: center;">
				<#list distributor as user>
					<tr>
						<td>${user.username}</td>
						<td>${user.password}</td>
						<td>${user.name}</td>
						<td>${user.seller_nick}</td>
						<td>${user.distributor.discount}</td>
						<td>${user.distributor.deposit/100}</td>
						<td><#if user.enabled==1>有效<#else>禁用</#if></td>
						<td>${user.created?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>${user.modified?string('yyyy-MM-dd HH:mm:ss')}</td>
						<td>
							修改密码 &nbsp;&nbsp;
							禁用
						</td>
					</tr>
				</#list>
			</tbody>
		</#if>
	</table>
</@root.html>
