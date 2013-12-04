<#import "/templates/root.ftl" as root >

<@root.html active=2 css=["all.css", "table.css", "jqpagination.css"] js=["jquery.jqpagination.min.js"]>
	<i class="icon-list-alt"></i>
    <#if action=="buy">
        进仓单列表
    <#elseif action="refund">退仓单列表
    <#elseif action="new">新建商品单列表
    <#elseif action="lock">锁定单列表
    </#if>
    <table class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>#</th>
            <th>名称</th>
            <th>添加时间</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <#if map??>
            <#list map?keys as key>
                <tr>
                    <td>${key_index + 1}</td>
                    <td>${key}</td>
                    <td>${map[key]?string('yyyy-MM-dd HH:mm:ss')}</td>
                    <td>
                        &nbsp;&nbsp;
                        <a href="${rc.contextPath}/goods/download?action=${action}&name=${key}">下载</a>
                        &nbsp;&nbsp;
                        <a href="${rc.contextPath}/goods/delete?action=${action}&name=${key}" onclick="return confirm('确定要删除这个文件吗？')">删除</a>
                    </td>
                </tr>
            </#list>
        </#if>
        </tbody>
    </table>

</@root.html>

