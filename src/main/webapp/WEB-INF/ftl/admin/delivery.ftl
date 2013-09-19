<#import "/templates/root.ftl" as root >
<@root.html css=["trade_list.css", "easyui.css", "icon.css"] js=["print_setup.js", "jquery.easyui.min.js"] >
    <script language="javascript" src="${rc.contextPath}/js/LodopFuncs.js"></script>
    <object  id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0>
        <embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0></embed>
    </object>
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
                    <a id="print-setup" style="cursor: pointer;">打印设置</a>
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

    <div id="w" class="easyui-window" title="快递单模板设置" data-options="modal:true,collapsible:false,closed:true,
        resizable:false,shadow:false,minimizable:false, maximizable:false"
         style="width:915px;height:480px;padding:2px;">
        <object id="LODOP2" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=780 height=407>
            <param name="Caption" value="内嵌显示区域">
            <param name="Border" value="0">
            <param name="Color" value="#C0C0C0">
            <embed id="LODOP_EM2" TYPE="application/x-print-lodop" width=780 height=407 PLUGINSPAGE="install_lodop.exe">
        </object>
    </div>
</@root.html>
