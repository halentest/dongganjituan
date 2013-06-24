<#import "/templates/root.ftl" as root >
<@root.html css=["jdpicker.css"] js=["highcharts.js", "exporting.js"] >
欢迎来到动感集团 ${CURRENT_USER.username!'无名氏'}
</@root.html>
