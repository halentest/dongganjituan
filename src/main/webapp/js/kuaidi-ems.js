	var LODOP; //声明为全局变量 
	function prn_ems(x, y, sender, from, from_company, from_address, sender_mobile,
		receiver, to_company, to_address, receiver_mobile, to) {
		CreateOneFormPage();	
		LODOP.ADD_PRINT_SETUP_BKIMG("<img border='0' src='/img/kuaidi/ems_jingji2.jpg'>");
		LODOP.SET_SHOW_MODE("BKIMG_IN_PREVIEW",1); 
		LODOP.SET_PRINT_STYLE("FontSize",18);
		LODOP.SET_PRINT_STYLE("Bold",1);
		
		
		LODOP.ADD_PRINT_TEXT(67,83,74,25,sender);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(84+y,93+x,192,25,from_company);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(104+y,78+x,270,30,from_address);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(66+y,201+x,83,25,sender_mobile);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(152+y,83+x,86,27,receiver);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(191+y,81+x,230,43,to_address);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(151+y,204+x,105,24,receiver_mobile);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(227+y,90+x,123,26,to);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		//LODOP.PRINT_DESIGN();
		//LODOP.PRINT();
		LODOP.PREVIEW();
	};
	
	function CreateEmsPage(x, y, sender, from, from_company, from_address, sender_mobile,
		receiver, to_company, to_address, receiver_mobile, to, goodsInfo){
		LODOP.NewPage();
		LODOP.ADD_PRINT_SETUP_BKIMG("<img border='0' src='/img/kuaidi/ems_jingji2.jpg'>");
        LODOP.SET_SHOW_MODE("BKIMG_IN_PREVIEW",1);
		LODOP.ADD_PRINT_TEXTA("text1", 67,83,74,25,sender);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text2", 84+y,93+x,192,25,from_company);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text3", 104+y,78+x,270,30,from_address);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text4", 66+y,201+x,83,25,sender_mobile);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text5", 152+y,83+x,86,27,receiver);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text6", 191+y,81+x,230,43,to_address);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text7", 151+y,204+x,105,24,receiver_mobile);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text8", 227+y,90+x,123,26,to);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXTA("text9", 10+y,250+x,500,500,goodsInfo);
        LODOP.SET_PRINT_STYLEA(0,"FontSize",30);
	};                     