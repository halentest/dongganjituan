	var LODOP; //声明为全局变量 
	function prn1_preview(sender, from, from_company, from_address, sender_mobile,
		receiver, to_company, to_address, receiver_mobile, to) {
		CreateOneFormPage();	
		LODOP.ADD_PRINT_SETUP_BKIMG("<img border='0' src='/img/kuaidi/shentong.jpg'>");
		LODOP.SET_SHOW_MODE("BKIMG_IN_PREVIEW",1); //注："BKIMG_IN_PREVIEW"-预览包含背景图 "BKIMG_IN_FIRSTPAGE"- 仅首页包含背景图
		LODOP.SET_PRINT_STYLE("FontSize",18);
		LODOP.SET_PRINT_STYLE("Bold",1);
		LODOP.ADD_PRINT_TEXT(110,86,100,25,sender);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(109,253,111,26,from);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(150,87,260,25,from_company);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(189,470,298,56,from_address);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(259,103,164,25,sender_mobile);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(112,484,111,25,receiver);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(147,479,249,27,to_company);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(189,470,309,56,to_address);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(261,506,157,24,receiver_mobile);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(112,649,125,26,to);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		//LODOP.PRINT_DESIGN();
		LODOP.PREVIEW();	
	};
	function prn1_print() {		
		CreateOneFormPage();
		LODOP.PRINT();	
	};
	function prn1_printA() {		
		CreateOneFormPage();
		LODOP.PRINTA(); 	
	};	
	function CreateOneFormPage(){
		LODOP=getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));  
		LODOP.PRINT_INIT("打印控件功能演示_Lodop功能_表单一");
		LODOP.SET_PRINT_STYLE("FontSize",18);
		LODOP.SET_PRINT_STYLE("Bold",1);
		//LODOP.ADD_PRINT_TEXT(50,231,260,39,"打印页面部分内容");
		//LODOP.ADD_PRINT_HTM(88,200,350,600,document.getElementById("form1").innerHTML);
	};	                     
	function prn2_preview() {	
		CreateTwoFormPage();	
		LODOP.PREVIEW();	
	};
	function prn2_manage() {	
		CreateTwoFormPage();
		LODOP.PRINT_SETUP();	
	};	
	function CreateTwoFormPage(){
		LODOP=getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));  
		LODOP.PRINT_INIT("打印控件功能演示_Lodop功能_表单二");
		LODOP.ADD_PRINT_RECT(70,27,634,242,0,1);
		LODOP.ADD_PRINT_TEXT(29,236,279,38,"页面内容改变布局打印");
		LODOP.SET_PRINT_STYLEA(2,"FontSize",18);
		LODOP.SET_PRINT_STYLEA(2,"Bold",1);
		LODOP.ADD_PRINT_HTM(88,40,321,185,document.getElementById("form1").innerHTML);
		LODOP.ADD_PRINT_HTM(87,355,285,187,document.getElementById("form2").innerHTML);
		LODOP.ADD_PRINT_TEXT(319,58,500,30,"注：其中《表单一》按显示大小，《表单二》在程序控制宽度(285px)内自适应调整");
	};              
	function prn3_preview(){
		LODOP=getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));  
		LODOP.PRINT_INIT("打印控件功能演示_Lodop功能_全页");
		LODOP.ADD_PRINT_HTM(20,40,700,900,document.documentElement.innerHTML);
		LODOP.PREVIEW();	
	};	