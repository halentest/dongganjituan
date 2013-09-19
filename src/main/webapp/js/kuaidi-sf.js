	var LODOP; //声明为全局变量 
	function prn_sf(x, y, sender, from, from_company, from_address, sender_mobile,
		receiver, to_company, to_address, receiver_mobile, to) {
		CreateOneFormPage();	
		LODOP.ADD_PRINT_SETUP_BKIMG("<img border='0' src='/img/kuaidi/sf.jpg'>");
		LODOP.SET_SHOW_MODE("BKIMG_IN_PREVIEW",1); 
		LODOP.SET_PRINT_STYLE("FontSize",18);
		LODOP.SET_PRINT_STYLE("Bold",1);
		
		LODOP.ADD_PRINT_TEXT(148+y,318+x,53,25,sender);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(146+y,108+x,152,25,from_company);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(174+y,98+x,270,56,from_address);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(229+y,188+x,109,25,sender_mobile);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(272+y,109+x,106,25,receiver);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(272+y,314+x,58,27,to_company);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(302+y,104+x,229,56,to_address);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		LODOP.ADD_PRINT_TEXT(377+y,202+x,111,24,receiver_mobile);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
		//LODOP.PRINT_DESIGN();
		//LODOP.PRINT();
		LODOP.PREVIEW();
	};
	function CreateSfPage(x, y, sender, from, from_company, from_address, sender_mobile,
		receiver, to_company, to_address, receiver_mobile, to, goodsInfo){
		LODOP.NewPage();

//		LODOP.ADD_PRINT_SETUP_BKIMG("<img border='0' src='/img/kuaidi/sf.jpg'>");
//		LODOP.SET_SHOW_MODE("BKIMG_IN_PREVIEW",1);
//		LODOP.SET_PRINT_STYLE("FontSize",18);
//        LODOP.SET_PRINT_STYLE("Bold",1);
//		LODOP.ADD_PRINT_TEXTA("text1", 148+y,318+x,53,25,sender);
//		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
//		LODOP.ADD_PRINT_TEXTA("text2", 146+y,108+x,152,25,from_company);
//		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
//		LODOP.ADD_PRINT_TEXTA("text3", 174+y,98+x,270,56,from_address);
//		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
//		LODOP.ADD_PRINT_TEXTA("text4", 229+y,188+x,109,25,sender_mobile);
//		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
//		LODOP.ADD_PRINT_TEXTA("text5", 272+y,109+x,106,25,receiver);
//		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
//		LODOP.ADD_PRINT_TEXTA("text6", 272+y,314+x,58,27,to_company);
//		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
//		LODOP.ADD_PRINT_TEXTA("text7", 302+y,104+x,229,56,to_address);
//		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
//		LODOP.ADD_PRINT_TEXTA("text8", 377+y,202+x,111,24,receiver_mobile);
//		LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
//		LODOP.ADD_PRINT_TEXTA("text9", 10+y,250+x,500,500,goodsInfo);
//        LODOP.SET_PRINT_STYLEA(0,"FontSize",30);


            LODOP.ADD_PRINT_SETUP_BKIMG("<img border='0' src='/img/kuaidi/yunda.jpg'>");
            LODOP.SET_SHOW_MODE("BKIMG_IN_PREVIEW",1);
            LODOP.ADD_PRINT_TEXTA("text1", 82,114,100,25,"发件人");
            LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
            LODOP.ADD_PRINT_TEXTA("text2", 81,258,111,26,"始发地");
            LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
            LODOP.ADD_PRINT_TEXTA("text3", 107,115,260,25,"发件公司");
            LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
            LODOP.ADD_PRINT_TEXTA("text4", 136,108,270,56,"发件地址");
            LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
            LODOP.ADD_PRINT_TEXTA("text5", 227,155,89,25,"发件电话");
            LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
            LODOP.ADD_PRINT_TEXTA("text6", 106,497,111,25,"收件人");
            LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
            LODOP.ADD_PRINT_TEXTA("text7", 229,479,142,27,"收件公司");
            LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
            LODOP.ADD_PRINT_TEXTA("text8", 135,470,260,56,"收件地址");
            LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
            LODOP.ADD_PRINT_TEXTA("text9", 82,673,105,24,"收件电话");
            LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
            LODOP.ADD_PRINT_TEXTA("text10", 84,472,125,26,"目的地");
            LODOP.SET_PRINT_STYLEA(0,"FontSize",10);
            LODOP.ADD_PRINT_TEXTA("text11", 10,250,500,500,"商品信息");
            LODOP.SET_PRINT_STYLEA(0,"FontSize",30);
	};