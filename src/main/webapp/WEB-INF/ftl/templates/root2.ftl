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
    <link href="${rc.contextPath}/css/jdpicker.css" rel="stylesheet">
    <link href="${rc.contextPath}/css/theme.bootstrap.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/highslide.css" />
    <#list css as entry>
      <link href="${rc.contextPath}/css/${entry}" rel="stylesheet">
    </#list>
    <style type="text/css">
        ${custom_css!''}
    </style>
    <link rel="icon" href="img/favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" />
      
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
              <li><a href="#">尊敬的${CURRENT_USER.userType.getName()!''} ${CURRENT_USER.username!'无名氏'}，欢迎登录动感集团电子商务管理系统</a></li>
              <li><a href="/j_spring_security_logout">退出</a></li>
            </ul>
          </div><!-- /.nav-collapse -->
        </div>
      </div><!-- /navbar-inner -->
    </div>
    <div class="container" style="padding-bottom: 30px;">
        <ul class="nav nav-tabs">
	          <#if CURRENT_USER.type=="Admin" || CURRENT_USER.type=="SuperAdmin" || CURRENT_USER.type=="GoodsManager" || CURRENT_USER.type=="DistributorManager">
			          <li <#if active==1> class="active dropdown" <#else>  class="dropdown" </#if> >
			          		<a class="dropdown-toggle" data-toggle="dropdown" href="#">系统管理<b class="caret"></b></a>
			          		<ul class="dropdown-menu">
			          			<#if CURRENT_USER.type=="Admin" || CURRENT_USER.type=="SuperAdmin">
				                  <li><a href="${rc.contextPath}/admin/action/account_list">账户管理</a></li>
				                </#if>
				                <li><a href="${rc.contextPath}/admin/template_list">运费模板管理</a></li>
				                  <#if CURRENT_USER.type=="SuperAdmin">
				                  <li class="divider"></li>
				                  <li><a href="${rc.contextPath}/admin/action/system_init">系统初始化</a></li>
				                  </#if>
			              	</ul>
			          </li>
	          </#if>
	          <li <#if active==2> class="active dropdown" <#else>  class="dropdown" </#if> >
	              <a class="dropdown-toggle" data-toggle="dropdown" href="#">库存管理 <b class="caret"></b></a>
	              <ul class="dropdown-menu">
	                  <li><a href="${rc.contextPath}/goods/goods_list">商品列表</a></li>
	                  <li class="divider"></li>
	                  <li><a href="${rc.contextPath}/goods/action/upload?action=buy">进仓(excel批量导入)</a></li>
	                  <li><a href="${rc.contextPath}/goods/action/upload?action=refund">退仓(excel批量导入)</a></li>
                      <li><a href="${rc.contextPath}/goods/action/upload?action=new">新建商品(excel批量导入)</a></li>
                      <li class="divider"></li>
                      <li><a href="${rc.contextPath}/goods/upload_list?action=buy">进仓单列表</a></li>
                      <li><a href="${rc.contextPath}/goods/upload_list?action=refund">退仓单列表</a></li>
                      <li><a href="${rc.contextPath}/goods/upload_list?action=new">新建商品单列表</a></li>
	              </ul>
	          </li>
	          <#if CURRENT_USER.type=="SuperAdmin" || CURRENT_USER.type=="Admin" || CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="WareHouse" || CURRENT_USER.type=="DistributorManager">
	          <li <#if active==3> class="active dropdown" <#else>  class="dropdown" </#if>>
	              <a class="dropdown-toggle" data-toggle="dropdown" href="#">订单管理 <b class="caret"></b></a>
	              <ul class="dropdown-menu">
	                  <li><a href="${rc.contextPath}/trade/trade_list">订单列表</a></li>
	                  <li><a href="${rc.contextPath}/trade/refund_list">退货列表</a></li>
	                  <#if CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="ServiceStaff">
	                  <li class="divider"></li>
	                  <li><a href="${rc.contextPath}/trade/manual_sync_trade_form">手工同步订单</a></li>
	                  </#if>
	              </ul>
	          </li>
	          </#if>
	          <#if CURRENT_USER.type=="Accounting">
	          <li <#if active==4> class="active dropdown" <#else>  class="dropdown" </#if>">
	              <a class="dropdown-toggle" data-toggle="dropdown" href="#">财务管理<b class="caret bottom-up"></b></a>
	              <ul class="dropdown-menu bottom-up pull-right">
	                    <li><a href="${rc.contextPath}/accounting/distributor_list">分销商打款</a></li>
	              </ul>
	          </li>
	          </#if>
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


