<#macro html title="欢迎来到骆驼动感ERP管理系统" active=1 css=[] js=[] custom_css="" custom_js="">
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="${rc.contextPath}/css/bootstrap.css" rel="stylesheet">
    <link href="${rc.contextPath}/css/bootstrap-responsive.css" rel="stylesheet">
    <link href="${rc.contextPath}/css/ea.css" rel="stylesheet">
    <link href="${rc.contextPath}/css/theme.bootstrap.css" rel="stylesheet">
    <#list css as entry>
      <link href="${rc.contextPath}/css/${entry}" rel="stylesheet">
    </#list>
    <style type="text/css">
        ${custom_css!''}
        .container2 {
            margin-left:5px;
            margin-right:5px;
        }
    </style>
    <link rel="icon" href="img/favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" />
      
  </head>
  <body>
    <div class="container2" style="padding-bottom: 30px; padding-top: 5px;">
        <#nested>
    </div>


    <script src="${rc.contextPath}/js/jquery-min.js"></script>
    <script src="${rc.contextPath}/js/bootstrap-dropdown.js"></script>
    <script src="${rc.contextPath}/js/bootstrap-modal.js"></script>
    <script src="${rc.contextPath}/js/bootstrap-transition.js"></script>
    <script src="${rc.contextPath}/js/jquery.flot.js"></script>

    <#list js as entry>
        <script src="${rc.contextPath}/js/${entry}"></script>
    </#list>
    <script type="text/javascript">
      ${custom_js!''}
    </script>
  </body>
</html>
</#macro>


