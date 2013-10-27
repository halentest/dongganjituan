<#import "/templates/root.ftl" as root >
<@root.html css=["table.css"] js=[] >
    <table>
        <tr>
            <th>快递编码</th>
            <th>快递名称</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        <#list deliveryList as delivery>
            <tr>
                <td>${delivery.code}</td>
                <td>${delivery.name}</td>
                <td>
                    <#if delivery.status==-1>
                        已停止使用
                    <#else>
                        <#if delivery.status==0>
                            使用中
                        <#else>
                            默认快递
                        </#if>
                    </#if>
                </td>
                <td>
                    <#if delivery.status==-1>
                        <a href="${rc.contextPath}/admin/update_delivery?status=0&id=${delivery.id}">启用</a>  &nbsp;&nbsp;&nbsp;&nbsp;
                        <a href="${rc.contextPath}/admin/update_delivery?status=1&id=${delivery.id}">设为默认</a>
                        <#else>
                            <#if delivery.status==0>
                                <a href="${rc.contextPath}/admin/update_delivery?status=-1&id=${delivery.id}">暂停使用</a>
                            </#if>
                    </#if>
                </td>
            </tr>
        </#list>
    </table>

</@root.html>
