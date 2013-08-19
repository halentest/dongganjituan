<#import "/templates/root.ftl" as root >

<@root.html active=2 css=["trade_list.css", "jqpagination.css"] js=["jquery.cookie.js", "jquery.jqpagination.min.js"]>
    <style type="text/css">
        .file-box{ position:relative;width:340px}
        .txt{ height:22px; border:1px solid #cdcdcd; width:180px;}
        .file{ position:absolute; top:0; right:80px; height:24px; filter:alpha(opacity:0);opacity: 0;width:260px }
        .btn{margin-top: -10px;}
    </style>
	<i class="icon-pencil"></i>批量导入订单
    <br><br><br>
    <div class="file-box">
        <form action="${rc.contextPath}/trade/action/do_upload" method="post" enctype="multipart/form-data">
            <input type='text' name='textfield' id='textfield' class='txt' />
            <input type='button' class='btn' value='浏览...' />
            <input type="file" name="file" class="file" id="fileField" size="28" onchange="document.getElementById('textfield').value=this.value" />
            <input type="submit" name="submit" class="btn" value="上传" />
        </form>
    </div>
    <#if errorInfo??>
        <div class="alert alert-error">
            <a class="close" data-dismiss="alert">×</a>
            <strong>出错啦！</strong> ${errorInfo}
        </div>
    </#if>
    <#if successInfo??>
        <div class="alert alert-success">
            <a class="close" data-dismiss="alert">×</a>
            <strong>成功了！</strong> ${successInfo}
        </div>
        <div>
            <h4>成功订单列表</h4>
            <ul>
            <#list successed as s>
                <li>${s}</li>
            </#list>
            </ul>
            <h4>已存在订单列表</h4>
            <ul>
                <#list repeated as r>
                    <li>${r}</li>
                </#list>
            </ul>
            <h4>商品不存在订单列表</h4>
            <ul>
                <#list lost as l>
                    <li>${l}</li>
                </#list>
            </ul>
        </div>
    </#if>

</@root.html>
