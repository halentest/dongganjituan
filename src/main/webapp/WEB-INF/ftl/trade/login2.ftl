<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>欢迎登录动感集团电子商务管理系统</title>
    <link href="${rc.contextPath}/css/bootstrap.css" rel="stylesheet">
 </head>

 <body>
 	<div class="container" style="margin-top: 150px; width: 450px;">
		 	<form class="form-horizontal" action="j_spring_security_check" method="POST"> 
		 		<fieldset>
		 			<legend>欢迎登录动感集团电子商务管理系统</legend>
		 			<div class="control-group">
				      <label class="control-label" for="j_username">用户名</label>
				      <div class="controls">
				        <input type="text" class="input-medium" id="j_username" name="j_username">
				      </div>
				    </div>
				    <div class="control-group">
				      <label class="control-label" for="j_password">密码</label>
				      <div class="controls">
				        <input type="password" clolass="input-medium" id="j_password" name="j_password">
				      </div>
				    </div>
				    <div class="control-group">
				      <div class="controls">
				        <label class="checkbox">
          					<input type="checkbox" name="_spring_security_remember_me"> 记住我
        				</label>
				      </div>
				    </div>
				    <#if error??><div class="alert alert-error">登录失败！用户名或密码输入不正确，请重新输入</div></#if>
				    
				    <div class="form-actions">
				    	<button class="btn" type="reset">重置</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            			<button type="submit" class="btn btn-primary">登录</button>
          			</div>
			 	</fieldset>
		 	</form>
 	</div>
 </body>
 <script src="${rc.contextPath}/js/bootstrap.js"></script>

</html>