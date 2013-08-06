<#import "/templates/root.ftl" as root >
<@root.html css=["jdpicker.css", "trade_list.css"] js=["highcharts.js", "exporting.js"] >
    <#if  CURRENT_USER.type=="Distributor" ||  CURRENT_USER.type=="ServiceStaff">
        您的余额：${deposit/100} 元
    <#else>
        <div class="alert" style="margin: 5px;">
            <a class="close" data-dismiss="alert">×</a>
            <strong>无内容！</strong>
        </div>
    </#if>
</@root.html>
