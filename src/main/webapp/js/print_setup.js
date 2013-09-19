$('#print-setup').click(function() {
    var test = "var LODOP=getLodop(document.getElementById('LODOP2'),document.getElementById('LODOP_EM2'));"
            + 'LODOP.PRINT_INITA(0,0,760,321,"打印控件功能演示_Lodop功能_在线编辑获得程序代码");'
            + 'LODOP.ADD_PRINT_TEXT(10,50,175,30,"先加的内容");'
            + 'LODOP.SET_SHOW_MODE("DESIGN_IN_BROWSE",1);'
            + 'LODOP.PRINT_SETUP();';
    test = test + 'LODOP.SET_PRINT_PAGESIZE(1,2300,1270,"");';

    $('#w').window('open');
    eval(test);
})