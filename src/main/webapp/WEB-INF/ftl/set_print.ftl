<#import "/templates/root.ftl" as root >
<@root.html active=3 css=[] js=["jquery.cookie.js"]>
    <legend><i class="icon-pencil"></i><font size="3">调整快递单:${delivery}</font></legend>
    <p>左边距: <input type="text" id="x"></p>
    <p>右边距: <input type="text" id="y"></p>
    <button id="save">保存</button>
    <button id="cancel">取消</button>
</@root.html>

<script type="text/javascript">
    $(document).ready(function() {
        var x = $.cookie("${delivery}x");
        var y = $.cookie("${delivery}y");
        if(!x) {
            x = 0;
        }
        if(!y) {
            y = 0;
        }
        $('#x').val(x);
        $('#y').val(y);

        $('#cancel').click(function() {
            history.back();
        });

        $('#save').click(function() {
            var newX = $('#x').val();
            var newY = $('#y').val();
            $.cookie('${delivery}x', newX, { expires: 1024, path: '/' });
            $.cookie('${delivery}y', newY, { expires: 1024, path: '/' });
            alert("修改成功，确定后返回!");
            history.back();
        });
    });
</script>
