<#import "/templates/root.ftl" as root >
<@root.html css=["jdpicker.css", "trade_list.css"] js=["highcharts.js", "exporting.js"] >
	<style  type="text/css">
		input.my-input {
			height: 15px;
			width: 100px;
			margin-top: 2px;
			margin-bottom: 2px;
			margin-left: 48px;
			text-align: right;
		}
	</style>
	<form action="${rc.contextPath}/admin/add_template" method="post">
		<#if map??>模板名称: ${map["pt-hd"].name} 
			<input type="hidden" value="${map["pt-hd"].name}" name="template-name" />
		<#else>模板名称(不能为空) <input class="my-input" type="text" name="template-name" /> </#if> <br>
		<strong>普通快递</strong>
		<input type="hidden" name="action" value="<#if map??>modify<#else>add</#if>" />
		<table>
			<thead>
				<tr>
					<th>运送到</th>
					<th>首件(件)</th>
					<th>首费(元)</th>
					<th>续件(件)</th>
					<th>续费(元)</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>华东(上海、江苏、浙江、安徽、江西)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hd'].start_standard}<#else>1</#if>" name="pt-hd-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hd'].start_fee/100}<#else>0</#if>" name="pt-hd-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hd'].add_standard}<#else>1</#if>" name="pt-hd-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hd'].add_fee/100}<#else>0</#if>" name="pt-hd-add-fee"></td>
				</tr>
				<tr>
					<td>华北(北京、天津、山西、山东、河北、内蒙古)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hb'].start_standard}<#else>1</#if>" name="pt-hb-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hb'].start_fee/100}<#else>0</#if>" name="pt-hb-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hb'].add_standard}<#else>1</#if>" name="pt-hb-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hb'].add_fee/100}<#else>0</#if>" name="pt-hb-add-fee"></td>
				</tr>
				<tr>
					<td>华中(湖南、湖北、河南)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hz'].start_standard}<#else>1</#if>" name="pt-hz-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hz'].start_fee/100}<#else>0</#if>" name="pt-hz-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hz'].add_standard}<#else>1</#if>" name="pt-hz-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hz'].add_fee/100}<#else>0</#if>" name="pt-hz-add-fee"></td>
				</tr>
				<tr>
					<td>华南(广东、广西、福建、海南)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hn'].start_standard}<#else>1</#if>" name="pt-hn-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hn'].start_fee/100}<#else>0</#if>" name="pt-hn-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hn'].add_standard}<#else>1</#if>" name="pt-hn-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hn'].add_fee/100}<#else>0</#if>" name="pt-hn-add-fee"></td>
				</tr>
				<tr>
					<td>东北(辽宁、吉林、黑龙江)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-db'].start_standard}<#else>1</#if>" name="pt-db-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-db'].start_fee/100}<#else>0</#if>" name="pt-db-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-db'].add_standard}<#else>1</#if>" name="pt-db-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-db'].add_fee/100}<#else>0</#if>" name="pt-db-add-fee"></td>
				</tr>
				<tr>
					<td>西北(陕西、新疆、甘肃、宁夏、青海)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-xb'].start_standard}<#else>1</#if>" name="pt-xb-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-xb'].start_fee/100}<#else>0</#if>" name="pt-xb-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-xb'].add_standard}<#else>1</#if>" name="pt-xb-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-xb'].add_fee/100}<#else>0</#if>" name="pt-xb-add-fee"></td>
				</tr>
				<tr>
					<td>西南(重庆、云南、贵州、西藏、四川)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-xn'].start_standard}<#else>1</#if>" name="pt-xn-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-xn'].start_fee/100}<#else>0</#if>" name="pt-xn-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-xn'].add_standard}<#else>1</#if>" name="pt-xn-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-xn'].add_fee/100}<#else>0</#if>" name="pt-xn-add-fee"></td>
				</tr>
				<tr>
					<td>港澳台(香港、澳门、台湾)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-gat'].start_standard}<#else>1</#if>" name="pt-gat-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-gat'].start_fee/100}<#else>0</#if>" name="pt-gat-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-gat'].add_standard}<#else>1</#if>" name="pt-gat-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-gat'].add_fee/100}<#else>0</#if>" name="pt-gat-add-fee"></td>
				</tr>
				<tr>
					<td>海外</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hw'].start_standard}<#else>1</#if>" name="pt-hw-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hw'].start_fee/100}<#else>0</#if>" name="pt-hw-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hw'].add_standard}<#else>1</#if>" name="pt-hw-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['pt-hw'].add_fee/100}<#else>0</#if>" name="pt-hw-add-fee"></td>
				</tr>
			</tbody>
		</table>
		<strong>顺丰快递</strong>
		<table>
			<thead>
				<tr>
					<th>运送到</th>
					<th>首件(件)</th>
					<th>首费(元)</th>
					<th>续件(件)</th>
					<th>续费(元)</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>华东(上海、江苏、浙江、安徽、江西)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hd'].start_standard}<#else>1</#if>" name="sf-hd-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hd'].start_fee/100}<#else>0</#if>" name="sf-hd-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hd'].add_standard}<#else>1</#if>" name="sf-hd-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hd'].add_fee/100}<#else>0</#if>" name="sf-hd-add-fee"></td>
				</tr>
				<tr>
					<td>华北(北京、天津、山西、山东、河北、内蒙古)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hb'].start_standard}<#else>1</#if>" name="sf-hb-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hb'].start_fee/100}<#else>0</#if>" name="sf-hb-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hb'].add_standard}<#else>1</#if>" name="sf-hb-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hb'].add_fee/100}<#else>0</#if>" name="sf-hb-add-fee"></td>
				</tr>
				<tr>
					<td>华中(湖南、湖北、河南)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hz'].start_standard}<#else>1</#if>" name="sf-hz-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hz'].start_fee/100}<#else>0</#if>" name="sf-hz-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hz'].add_standard}<#else>1</#if>" name="sf-hz-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hz'].add_fee/100}<#else>0</#if>" name="sf-hz-add-fee"></td>
				</tr>
				<tr>
					<td>华南(广东、广西、福建、海南)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hn'].start_standard}<#else>1</#if>" name="sf-hn-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hn'].start_fee/100}<#else>0</#if>" name="sf-hn-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hn'].add_standard}<#else>1</#if>" name="sf-hn-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hn'].add_fee/100}<#else>0</#if>" name="sf-hn-add-fee"></td>
				</tr>
				<tr>
					<td>东北(辽宁、吉林、黑龙江)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-db'].start_standard}<#else>1</#if>" name="sf-db-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-db'].start_fee/100}<#else>0</#if>" name="sf-db-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-db'].add_standard}<#else>1</#if>" name="sf-db-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-db'].add_fee/100}<#else>0</#if>" name="sf-db-add-fee"></td>
				</tr>
				<tr>
					<td>西北(陕西、新疆、甘肃、宁夏、青海)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-xb'].start_standard}<#else>1</#if>" name="sf-xb-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-xb'].start_fee/100}<#else>0</#if>" name="sf-xb-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-xb'].add_standard}<#else>1</#if>" name="sf-xb-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-xb'].add_fee/100}<#else>0</#if>" name="sf-xb-add-fee"></td>
				</tr>
				<tr>
					<td>西南(重庆、云南、贵州、西藏、四川)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-xn'].start_standard}<#else>1</#if>" name="sf-xn-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-xn'].start_fee/100}<#else>0</#if>" name="sf-xn-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-xn'].add_standard}<#else>1</#if>" name="sf-xn-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-xn'].add_fee/100}<#else>0</#if>" name="sf-xn-add-fee"></td>
				</tr>
				<tr>
					<td>港澳台(香港、澳门、台湾)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-gat'].start_standard}<#else>1</#if>" name="sf-gat-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-gat'].start_fee/100}<#else>0</#if>" name="sf-gat-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-gat'].add_standard}<#else>1</#if>" name="sf-gat-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-gat'].add_fee/100}<#else>0</#if>" name="sf-gat-add-fee"></td>
				</tr>
				<tr>
					<td>海外</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hw'].start_standard}<#else>1</#if>" name="sf-hw-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hw'].start_fee/100}<#else>0</#if>" name="sf-hw-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hw'].add_standard}<#else>1</#if>" name="sf-hw-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['sf-hw'].add_fee/100}<#else>0</#if>" name="sf-hw-add-fee"></td>
				</tr>
			</tbody>
		</table>
		<strong>EMS</strong>
		<table>
			<thead>
				<tr>
					<th>运送到</th>
					<th>首件(件)</th>
					<th>首费(元)</th>
					<th>续件(件)</th>
					<th>续费(元)</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>华东(上海、江苏、浙江、安徽、江西)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hd'].start_standard}<#else>1</#if>" name="ems-hd-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hd'].start_fee/100}<#else>0</#if>" name="ems-hd-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hd'].add_standard}<#else>1</#if>" name="ems-hd-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hd'].add_fee/100}<#else>0</#if>" name="ems-hd-add-fee"></td>
				</tr>
				<tr>
					<td>华北(北京、天津、山西、山东、河北、内蒙古)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hb'].start_standard}<#else>1</#if>" name="ems-hb-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hb'].start_fee/100}<#else>0</#if>" name="ems-hb-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hb'].add_standard}<#else>1</#if>" name="ems-hb-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hb'].add_fee/100}<#else>0</#if>" name="ems-hb-add-fee"></td>
				</tr>
				<tr>
					<td>华中(湖南、湖北、河南)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hz'].start_standard}<#else>1</#if>" name="ems-hz-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hz'].start_fee/100}<#else>0</#if>" name="ems-hz-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hz'].add_standard}<#else>1</#if>" name="ems-hz-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hz'].add_fee/100}<#else>0</#if>" name="ems-hz-add-fee"></td>
				</tr>
				<tr>
					<td>华南(广东、广西、福建、海南)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hn'].start_standard}<#else>1</#if>" name="ems-hn-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hn'].start_fee/100}<#else>0</#if>" name="ems-hn-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hn'].add_standard}<#else>1</#if>" name="ems-hn-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hn'].add_fee/100}<#else>0</#if>" name="ems-hn-add-fee"></td>
				</tr>
				<tr>
					<td>东北(辽宁、吉林、黑龙江)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-db'].start_standard}<#else>1</#if>" name="ems-db-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-db'].start_fee/100}<#else>0</#if>" name="ems-db-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-db'].add_standard}<#else>1</#if>" name="ems-db-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-db'].add_fee/100}<#else>0</#if>" name="ems-db-add-fee"></td>
				</tr>
				<tr>
					<td>西北(陕西、新疆、甘肃、宁夏、青海)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-xb'].start_standard}<#else>1</#if>" name="ems-xb-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-xb'].start_fee/100}<#else>0</#if>" name="ems-xb-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-xb'].add_standard}<#else>1</#if>" name="ems-xb-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-xb'].add_fee/100}<#else>0</#if>" name="ems-xb-add-fee"></td>
				</tr>
				<tr>
					<td>西南(重庆、云南、贵州、西藏、四川)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-xn'].start_standard}<#else>1</#if>" name="ems-xn-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-xn'].start_fee/100}<#else>0</#if>" name="ems-xn-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-xn'].add_standard}<#else>1</#if>" name="ems-xn-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-xn'].add_fee/100}<#else>0</#if>" name="ems-xn-add-fee"></td>
				</tr>
				<tr>
					<td>港澳台(香港、澳门、台湾)</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-gat'].start_standard}<#else>1</#if>" name="ems-gat-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-gat'].start_fee/100}<#else>0</#if>" name="ems-gat-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-gat'].add_standard}<#else>1</#if>" name="ems-gat-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-gat'].add_fee/100}<#else>0</#if>" name="ems-gat-add-fee"></td>
				</tr>
				<tr>
					<td>海外</td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hw'].start_standard}<#else>1</#if>" name="ems-hw-start-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hw'].start_fee/100}<#else>0</#if>" name="ems-hw-start-fee"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hw'].add_standard}<#else>1</#if>" name="ems-hw-add-standard"></td>
					<td><input class="my-input" type="text" value="<#if map??>${map['ems-hw'].add_fee/100}<#else>0</#if>" name="ems-hw-add-fee"></td>
				</tr>
			</tbody>
		</table>
		<input style="margin-left: 900px; margin-top: 20px;" class="btn btn-primary" type="submit" value="保存">
		<input onclick="window.location.href='${rc.contextPath}/admin/template_list'" style="margin-left: 10px; margin-top: 20px;" type="reset" class="btn btn-inverse" value="取消"/>
	</form>
	
</@root.html>