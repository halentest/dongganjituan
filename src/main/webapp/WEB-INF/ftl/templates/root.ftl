<#macro html title="欢迎登录动感集团电子商务管理系统" active=1 css=[] js=[] custom_css="" custom_js="">
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="${rc.contextPath}/css/bootstrap.css" rel="stylesheet">
    <link href="${rc.contextPath}/css/bootstrap-responsive.css" rel="stylesheet">
    <link href="${rc.contextPath}/css/ea.css" rel="stylesheet">
    <link href="${rc.contextPath}/css/jdpicker.css" rel="stylesheet">
    <link href="${rc.contextPath}/css/theme.bootstrap.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/highslide.css" />
    <#list css as entry>
      <link href="${rc.contextPath}/css/${entry}" rel="stylesheet">
    </#list>
    <style type="text/css">
        ${custom_css!''}
    </style>
      
  </head>
  <body>
    <div class="navbar">
      <div class="navbar-inner">
        <div class="container">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
        
          <div class="nav-collapse">
            <ul class="nav">
              <li><a href="#"></a></li>
              <li><a href="#">尊敬的${username!'无名氏'}，欢迎登录动感集团电子商务管理系统</a></li>
              <li><a href="/j_spring_security_logout">退出</a></li>
            </ul>
          </div><!-- /.nav-collapse -->
        </div>
      </div><!-- /navbar-inner -->
    </div>
    <div class="container">
        <ul class="nav nav-tabs">
          <li <#if active==1> class="active"</#if> ><a href="#">账户管理</a></li>
          <li <#if active==2> class="active dropdown" <#else>  class="dropdown" </#if> >
              <a class="dropdown-toggle" data-toggle="dropdown" href="#">库存管理 <b class="caret"></b></a>
              <ul class="dropdown-menu">
                  <li><a href="${rc.contextPath}/huopin/goods_list">库存列表</a></li>
                  <li><a href="#">添加商品</a></li>
                  <li class="divider"></li>
                  <li><a href="#">进货(excel批量导入)</a></li>
                  <li><a href="#">出货(excel批量导入)</a></li>
              </ul>
          </li>
          <li <#if active==3> class="active dropdown" <#else>  class="dropdown" </#if>>
              <a class="dropdown-toggle" data-toggle="dropdown" href="#">订单管理 <b class="caret"></b></a>
              <ul class="dropdown-menu">
                  <li><a href="${rc.contextPath}/trade_list">订单列表</a></li>
                  <li><a href="${rc.contextPath}/fenxiao/add_order_form">新建订单</a></li>
                  <li><a href="#">其他</a></li>
                  <li class="divider"></li>
                  <li><a href="#">被间隔的链接</a></li>
              </ul>
          </li>
          <li <#if active==4> class="active dropdown" <#else>  class="dropdown" </#if>">
              <a class="dropdown-toggle" data-toggle="dropdown" href="#">运费模板 <b class="caret bottom-up"></b></a>
              <ul class="dropdown-menu bottom-up pull-right">
                    <li><a href="#">动作</a></li>
                    <li><a href="#">其他动作</a></li>
                    <li><a href="#">其他</a></li>
                    <li class="divider"></li>
                    <li><a href="#">被间隔的链接</a></li>
              </ul>
          </li>
        </ul>

        <#nested>
    </div>
    

    <script src="${rc.contextPath}/js/jquery-min.js"></script>
    <script src="${rc.contextPath}/js/bootstrap-dropdown.js"></script>
    <script src="${rc.contextPath}/js/bootstrap-modal.js"></script>
    <script src="${rc.contextPath}/js/bootstrap-transition.js"></script>
    <script src="${rc.contextPath}/js/jquery.flot.js"></script>
    <script src="${rc.contextPath}/js/jquery.jdpicker.js"></script>
    <script src="${rc.contextPath}/js/jquery.tablesorter.min.js"></script>
    <script src="${rc.contextPath}/js/jquery.tablesorter.widgets.js"></script>
    <!-- Additional files for the Highslide popup effect -->
    <script type="text/javascript" src="${rc.contextPath}/js/highslide-full.min.js"></script>
    <script type="text/javascript" src="${rc.contextPath}/js/highslide.config.js" charset="utf-8"></script>
    

    <#list js as entry>
    <script src="${rc.contextPath}/js/${entry}"></script>
    </#list>
    <script type="text/javascript">
      ${custom_js!''}
    </script>
  </body>
</html>
</#macro>


