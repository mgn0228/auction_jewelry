jQuery(function($){
	//gnb
	function  getGnbHeight(){
		var submenuA = new Array();
		var maxUlheight = 0;
		$(".submenu").each( function(rIdx, item) {
			var ulHeigh = 0;
			$( this ).find(">ul").each( function(index, item) {
				ulHeigh += $( this ).height();
			});
			submenuA[rIdx] = ulHeigh;
			if(maxUlheight < ulHeigh) maxUlheight = ulHeigh;
		});
		maxUlheight += 62;
		return maxUlheight;
	};
	
	function topmenu(){
		$("#gnb ul li .submenu").slideDown(100);
		var gnbHeight = getGnbHeight();
		$("#gnb_bg").css("height",gnbHeight+ 52 +"px");
		$("#gnb .depth1").css("height",gnbHeight + 52 + "px");
		$("#gnb_bg").slideDown(100);
		$("#gnb .link").slideDown(100);
	};
	function topmenuOff(){
		$("#gnb ul li .submenu").slideUp(150);
		$("#gnb_bg").slideUp(150);
		$("#gnb .link").slideUp(150);
		$("#gnb .depth1").css({"height":60 +"px"});
	};
	
	$('#gnb>ul>li').hover(function(){
		$('#gnb ul li').removeClass('ov');
		$(this).addClass('ov');
	});
	$('#gnb>ul').hover(function(){
		topmenu();
	});
	$('#header').mouseleave(function(){
		topmenuOff();
		$('#gnb ul li').removeClass('ov');
	});
	$('#gnb ul li a').focus(function(){
		topmenu();
		//$(this).parent().addClass('on');
	});
	$('#gnb ul li a').blur(function(){
		topmenuOff();
		$('#gnb ul li').removeClass('ov');
	});

	//상세 detial_img
	$(".detial_img").each(function() {
		$(".detial_img li").find('.effectPic').hide();
		$(".detial_img li:first").addClass("ov").find(".effectPic").show();
		$(".detial_img li a").mouseover(function(){
			$(".detial_img li").removeClass("ov");
			$(".detial_img li .effectPic").hide();
			$(this).parent('li').addClass("ov");
			$(this).parent('li').find(".effectPic").show();
		});
		$(".detial_img li a").focus(function(){
			$(".detial_img li").removeClass("ov");
			$(".detial_img li .effectPic").hide();
			$(this).parent('li').addClass("ov");
			$(this).parent('li').find(".effectPic").show();
		});
	});

	//qna_box
	var article = $('.qna_box li');
	article.addClass('off');
	article.find('.a').slideUp(100);
	
	$('.qna_box li a').click(function(){
		var myArticle = $(this).parents('li:first');
		if(myArticle.hasClass('off')){
			article.addClass('off').removeClass('on');
			article.find('.a').slideUp(100);
			myArticle.removeClass('off').addClass('on');
			myArticle.find('.txt_a').slideDown(100);
			return false;
		} else {
			myArticle.removeClass('on').addClass('off');
			myArticle.find('.txt_a').slideUp(100);
			return false;
		}
	});


	//data_list
	var folder = $('.data_list .folder');
	folder.addClass('off');
	folder.find('.a').slideUp(100);

	/*
	$('.data_list .folder a').click(function(){
		$('.data_list .list').removeClass('on');
		$(this).parents('.folder').next('.list').addClass('on');
		return false;
	});
	*/
	$('.data_list .folder a').click(function(){
		const $target = $(this).parents('.folder').next('.list');

		if ($target.hasClass('on')) {
			$target.removeClass('on'); // 접기
		} else {
			$('.data_list .list').removeClass('on'); // 다른 건 닫기
			$target.addClass('on'); // 펼치기
		}

		return false;
	});



	//btnPop
	$("#btnPop").click(function(e){
		e.preventDefault();
		$(".fullscreen").fadeIn(200);
		$("#layer_pop").fadeIn(200);
	});

	//btnClose
	$("#btnClose").click(function(e){
		e.preventDefault();
		$(this).parents(".layer_close").parents(".layer_pop").fadeOut(200);
		$(".fullscreen").fadeOut(200);
	});
});