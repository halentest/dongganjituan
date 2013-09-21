$('.reject-refund, .receive-refund, .not-refund-money').click(function() {
    var action = $(this).attr("class");
    if(action=="reject-refund") {
        $('#action').html("拒绝退货");
    } else if(action=="not-refund-money") {
        $('#action').html("拒绝退款");
    }

    $('#curr-tid').val($(this).attr("data-tid"));
    $('#curr-oid').val($(this).attr("data-oid"));
    $('#curr-rid').val($(this).attr("data-rid"));
    $('#curr-action').val(action);
    $('#reason').val('');
    if(action=="receive-refund") {
        $('#pop-up2').window('open');
    } else {
        $('#pop-up').window('open');
    }
    $('#reason').focus();
})

function save() {
    var action = $('#curr-action').val();
    var tid = $('#curr-tid').val();
    var oid = $('#curr-oid').val();
    var rid = $('#curr-rid').val();
    var reason = $('#reason').val();
    $.ajax({
        type: "post",//使用get方法访问后台
        dataType: "json",//返回json格式的数据
        data: "rid=" + rid + "&tid=" + tid +
        "&oid=" + oid + "&action=" + action + "&comment=" + reason,
        url: "/trade/action/change_refund_status",//要访问的后台地址
        success: function(result){//msg为返回的数据，在这里做数据绑定
            if(result.success == false) {
                alert(result.errorInfo);
            } else {
                window.location.reload();
            }
        }});
}

function cancel() {
    $('#pop-up').window('close');
}

function save2() {
    var action = $('#curr-action').val();
    var tid = $('#curr-tid').val();
    var oid = $('#curr-oid').val();
    var rid = $('#curr-rid').val();
    var status = $('#refund-status').val();
    var isTwice = $('#is-twice').val();
    $.ajax({
        type: "post",//使用get方法访问后台
        dataType: "json",//返回json格式的数据
        data: "rid=" + rid + "&tid=" + tid +
        "&oid=" + oid + "&action=" + action + "&comment=" + status + "&isTwice=" + isTwice,
        url: "/trade/action/change_refund_status",//要访问的后台地址
        success: function(result){//msg为返回的数据，在这里做数据绑定
            if(result.success == false) {
                alert(result.errorInfo);
            } else {
                window.location.reload();
            }
        }});
}

function cancel2() {
    $('#pop-up2').window('close');
}