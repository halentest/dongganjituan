function autoScan(s) {
    var v = $(s).prev().val();
    var ix = $(s).prev().attr('data-index');
    var l = $(document).find('input.scan-input');
    console.log(l.size());
    $(l).each(function(index, item) {
        if($(item).attr('data-index') > ix) {
            v++;
            $(item).val(v);
        }
    })
}

$('#save-scan').click(function() {
    var delivery = $('#delivery-print').val();
    if(!delivery || delivery=="") {
        alert("请选择快递");
        return false;
    }
    var l = $(document).find('input.scan-input');
    var size = l.size()/2;
    var param = "";
    $(l).each(function(index, item) {
        if(index < size) {
            param += ",";
            param += $(item).attr('data-id') + ":" + $(item).val();
        }
    })

    $.ajax({
        type: "post",//使用get方法访问后台
        dataType: "json",//返回json格式的数据
        data: "param=" + param,
        url: "/trade/batch_add_tracking_number",//要访问的后台地址
        success: function(result){//msg为返回的数据，在这里做数据绑定
        if(result.errorInfo != "success") {
            alert(result.errorInfo);
            window.location.reload();
        } else {
            window.location.reload();
        }
    }});

})

$('#scan-delivery').click(function() {
    var delivery = $('#delivery-print').val();
    if(!delivery || delivery=="") {
        alert("请选择快递");
        return false;
    }
    window.location.href="/trade/trade_list?isCancel=0|-1&isSubmit=1&isSend=0&isFinish=0&status=WaitFind&scan=true&delivery=" + delivery;
})

$('#batch-out-goods').click(function() {
    var checked = $('#t-list').datagrid("getChecked");
    if(checked.length==0) {
        alert('至少选中一个订单!');
        return false;
    }
    var tids = "";
    $(checked).each(function(index, item) {
        var tid = item.id.trim();
        tids += tid;
        tids += ";";
    })
        $.ajax({
            type: "post",//使用get方法访问后台
            dataType: "json",//返回json格式的数据
            data: "tids=" + tids,
            url: "/trade/action/delivery_tracking_number",//要访问的后台地址
            success: function(result){//msg为返回的数据，在这里做数据绑定
            if(result.errorInfo != "success") {
                alert(result.errorInfo);
                window.location.reload();
            } else {
                window.location.reload();
            }
        }});
})

$('#batch-find-goods').click(function() {
    var checked = $('#t-list').datagrid("getChecked");
    if(checked.length==0) {
        alert('至少选中一个订单!');
        return false;
    }
    var tids = "";
    $(checked).each(function(index, item) {
        var tid = item.id.trim();
        tids += tid;
        tids += ";";
    })
    $.ajax({
    type: "post",//使用get方法访问后台
    dataType: "json",//返回json格式的数据
    data: "tids=" + tids + "&action=find-goods",
    url: "/trade/action/batch_change_status",//要访问的后台地址
    success: function(result){//msg为返回的数据，在这里做数据绑定
            if(result.errorInfo != "success") {
                alert(result.errorInfo);
                window.location.reload();
            } else {
                window.location.reload();
            }
    }});
})

$('#batch-submit').click(function() {
    var checked = $('#t-list').datagrid("getChecked");
    if(checked.length==0) {
        alert('至少选中一个订单!');
        return false;
    }
    var b = true;
    var tids = "";
    $(checked).each(function(index, item) {
        var tid = item.id.trim();
        tids += tid;
        tids += ";";
    })
    $.ajax({
    type: "post",//使用get方法访问后台
    dataType: "json",//返回json格式的数据
    data: "tids=" + tids + "&action=submit",
    url: "/trade/action/batch_change_status",//要访问的后台地址
    success: function(result){//msg为返回的数据，在这里做数据绑定
            if(result.errorInfo != "success") {
                alert(result.errorInfo);
                window.location.reload();
            } else {
                window.location.reload();
            }
    }});
})

$('.apply-refund').click(function() {
    $('#refund-curr-tid').val($(this).attr("data-tid"));
    $('#refund-curr-oid').val($(this).attr("data-oid"));
    $('#pop-up2').window('open');
})

function saveApplyRefund() {
    var tid = $('#refund-curr-tid').val();
    var oid = $('#refund-curr-oid').val();
    var refundReason = $('#refund-reason').val();
    $.ajax({
    type: "post",//使用get方法访问后台
    dataType: "json",//返回json格式的数据
    data: "tid=" + tid + "&oid=" + oid + "&refundReason=" + refundReason,
    url: "/trade/apply_refund",//要访问的后台地址
    success: function(result){//msg为返回的数据，在这里做数据绑定
        if(result.errorInfo != "success") {
            alert(result.errorInfo);
        } else {
            window.location.reload();
        }
    }});
}

function cancelApplyRefund() {
    $('#pop-up2').window('close');
}

function saveTrackingNumber() {
    var trackingNumber = $('#tracking-number').val();
    if(!trackingNumber || trackingNumber.length==0) {
        alert("单号不能为空！");
        return false;
    }
    var tid = $('#curr-tid').val();
    $.ajax({
        type: "post",//使用get方法访问后台
        dataType: "json",//返回json格式的数据
        data: "tid=" + tid + "&trackingNumber=" + trackingNumber,
        url: "/trade/add_tracking_number",//要访问的后台地址
        success: function(result){//msg为返回的数据，在这里做数据绑定
            if(result.errorInfo != "success") {
                alert(result.errorInfo);
            } else {
                window.location.reload();
            }
        }
    });
}

function cancelTrackingNumber() {
    $('#pop-up').window('close');
}

function addTrackingNumber(id) {
    $('#curr-tid').val(id);
    $('#tracking-number').val('');
    $('#pop-up').window('open');
    $('#tracking-number').focus();
}

$('#paper-setup').click(function() {
    var delivery = $('#delivery-print').val();
    if(!delivery || delivery=="") {
        alert("请选择快递");
        return false;
    }
    var width = $.cookie(delivery + "width");
    var height = $.cookie(delivery + "height");
    if(!width) {
        width = 2300;
    } else {
        width = parseInt(width);
    }
    if(!height) {
        height = 1270;
    } else {
        height = parseInt(height);
    }
    $('#paper-width').val(width);
    $('#paper-height').val(height);
    $('#w').window('open');
})

function cancelPaperChange() {
    $('#w').window('close');
}

function savePaperChange() {
    var width = $('#paper-width').val();
    var height = $('#paper-height').val();
    var delivery = $('#delivery-print').val();
    $.cookie(delivery+'width', width, { expires: 1024, path: '/' });
    $.cookie(delivery+'height', height, { expires: 1024, path: '/' });
    $('#w').window('close');
}

$('#delivery-print').change(function() {
    var delivery = $('#delivery-print').val();
    window.location.href="/trade/trade_list?status=WaitFind&isCancel=0&isFinish=0&isSend=0&isSubmit=1&delivery=" + delivery;
})

function CreatePrintPage(sender, from, from_company, from_address, sender_mobile,
		receiver, to_company, to_address, receiver_mobile, to, goodsInfo, bg){
    LODOP.NewPage();
    LODOP.ADD_PRINT_SETUP_BKIMG("<img border='0' src='" + bg + "'>");
    LODOP.SET_SHOW_MODE("BKIMG_IN_PREVIEW",1);
    LODOP.ADD_PRINT_TEXTA("text1", 82,114,100,25,sender);
    LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
    LODOP.ADD_PRINT_TEXTA("text2", 81,258,111,26,from);
    LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
    LODOP.ADD_PRINT_TEXTA("text3", 107,115,260,25,from_company);
    LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
    LODOP.ADD_PRINT_TEXTA("text4", 136,108,270,56,from_address);
    LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
    LODOP.ADD_PRINT_TEXTA("text5", 227,155,89,25,sender_mobile);
    LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
    LODOP.ADD_PRINT_TEXTA("text6", 106,497,111,25,receiver);
    LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
    LODOP.ADD_PRINT_TEXTA("text7", 229,479,142,27,to_company);
    LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
    LODOP.ADD_PRINT_TEXTA("text8", 135,470,260,56,to_address);
    LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
    LODOP.ADD_PRINT_TEXTA("text9", 82,673,105,24,receiver_mobile);
    LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
    LODOP.ADD_PRINT_TEXTA("text10", 84,472,125,26,to);
    LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
    LODOP.ADD_PRINT_TEXTA("text11", 10,250,500,200,goodsInfo);
    LODOP.SET_PRINT_STYLEA(0,"FontSize",25);
};

$('#print-setup').click(function() {
    var delivery = $('#delivery-print').val();
    if(!delivery || delivery=="") {
        alert("请选择快递");
        return false;
    }
    if(delivery=="韵达快运" || delivery=="韵达") {
        bg = "/img/kuaidi/yunda.jpg";
    } else if(delivery=="申通E物流") {
        bg = "/img/kuaidi/shentong.jpg";
    } else if(delivery=="顺丰速运") {
        bg = "/img/kuaidi/sf.jpg";
    } else if(delivery=="EMS") {
        bg = "/img/kuaidi/ems_jingji2.jpg";
    } else if(delivery=="圆通速递") {
        bg = "/img/kuaidi/yuantong-new.jpg";
    }

    LODOP=getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
    LODOP.PRINT_INIT(delivery);
    LODOP.SET_PRINT_PAGESIZE(1,2300,1270,"");
    LODOP.SET_PRINT_STYLE("FontSize",16);
    LODOP.SET_PRINT_STYLE("Bold",1);
    CreatePrintPage("发件人","始发地","发件公司","发件地址","发件电话","收件人","收件公司","收件地址","收件电话","目的地","商品信息",bg);
    LODOP.SET_PREVIEW_WINDOW(1,1,0,900,600,"");
    LODOP.PRINT_SETUP();
})

$('#search').click(function() {
    var status = $('#status').val();
    var distributor = $('#distributor').val();
    if(!distributor) {
        distributor='';
    }
    var start = $('#start').datetimebox('getValue');
    var end = $('#end').datetimebox('getValue');
    if(start.length==0 || end.length==0) {
        start = "";
        end = "";
    }
    var seller_nick = $('#seller_nick').val();
    var name = $('#name').val();
    var tid = $('#tid').val();
    var page = $('#page').attr('data-current-page');
    var delivery = $('#delivery').val();
    var isSubmit = $('#isSubmit').val();
    var isCancel = $('#isCancel').val();
    var isFinish = $('#isFinish').val();
    var isRefund = $('#isRefund').val();
    var isSend = $('#isSend').val();
    if(!page) {
        page = 1;
    }
    window.location.href="/trade/trade_list?page=" + page + "&status=" + status + "&seller_nick=" + seller_nick
            + "&name=" + name + "&tid=" + tid + "&dId=" + distributor + "&delivery=" + delivery + "&start=" + start
            + "&end=" + end + "&isSubmit=" + isSubmit + "&isCancel=" + isCancel + "&isFinish=" + isFinish + "&isRefund=" + isRefund
                                                + "&isSend=" + isSend;;
});
