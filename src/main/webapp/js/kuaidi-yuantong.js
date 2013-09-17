	var LODOP; //声明为全局变量 
	function prn_yuantong(x, y, sender, from, from_company, from_address, sender_mobile,
		receiver, to_company, to_address, receiver_mobile, to) {
		CreateOneFormPage();	
		LODOP.ADD_PRINT_SETUP_BKIMG("<img border='0' src='/img/kuaidi/yuantong-new.jpg'>");
		LODOP.SET_SHOW_MODE("BKIMG_IN_PREVIEW",1); 
		LODOP.SET_PRINT_STYLE("FontSize",18);
		LODOP.SET_PRINT_STYLE("Bold",1);
		console.log("x+101 is " + (101+x));
		LODOP.ADD_PRINT_TEXT(101+y,129+x,53,25,sender);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(101+y,275+x,111,26,from);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(130+y,121+x,260,25,from_company);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(165+y,114+x,270,56,from_address);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(236+y,162+x,109,25,sender_mobile);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(118+y,518+x,106,25,receiver);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(144+y,514+x,249,27,to_company);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(176+y,507+x,229,56,to_address);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(236+y,556+x,111,24,receiver_mobile);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(117+y,658+x,125,26,to);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		//LODOP.PRINT_DESIGN();
		//LODOP.PRINT();
		LODOP.PREVIEW();
	};
	
	function CreateYuantongPage(x, y, sender, from, from_company, from_address, sender_mobile,
		receiver, to_company, to_address, receiver_mobile, to, goodsInfo){
		LODOP.NewPage();
		LODOP.ADD_PRINT_SETUP_BKIMG("<img border='0' src='/img/kuaidi/shentong.jpg'>");
        LODOP.SET_SHOW_MODE("BKIMG_IN_PREVIEW",1);
		LODOP.ADD_PRINT_TEXTA("text1", 101+y,129+x,53,25,sender);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text2", 101+y,275+x,111,26,from);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text3", 130+y,121+x,260,25,from_company);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text4", 165+y,114+x,270,56,from_address);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text5", 236+y,162+x,109,25,sender_mobile);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text6", 118+y,518+x,106,25,receiver);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text7", 144+y,514+x,249,27,to_company);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text8", 176+y,507+x,229,56,to_address);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text9", 236+y,556+x,111,24,receiver_mobile);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text10", 117+y,658+x,125,26,to);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text9", 10+y,250+x,500,500,goodsInfo);
        LODOP.SET_PRINT_STYLEA(0,"FontSize",30);
	};                     