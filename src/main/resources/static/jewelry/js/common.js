// paging 공통함수
function setPageIndex() {

    totalPage = Math.ceil(totalCount / perPage);

    var pageGroup = Math.ceil(pageNum / 10);    //페이지 그룹 넘버링
    var next = pageGroup * 10;
    var prev = next - 9;
    var goNext = next + 1;

    if (prev - 1 <= 0) {
        var goPrev = 1;
    } else {
        var goPrev = prev - 1;
    }
    if (next > totalPage) {
        var goNext = totalPage;
        next = totalPage;
    } else {
        var goNext = next + 1;
    }

    var firstStep = " <a class=\"first\" href='javascript:getList(" + 1 + ");'>이전</a> ";
    var prevStep = " <a class=\"prev\" href='javascript:getList(" + goPrev + ");'>이전</a> ";
    var nextStep = " <a class=\"next\" href='javascript:getList(" + goNext + ");'>다음</a> ";
    var lastStep = " <a class=\"last\" href='javascript:getList(" + totalPage + ");'>이전</a> ";

    $("#pageIdx").empty();

    $("#pageIdx").append(firstStep);
    $("#pageIdx").append(prevStep);

    for (var i = prev; i <= next; i++) {
        var src = "";  
        if (i == pageNum) {
            src += "<strong>" + i + "</strong>";
        } else {
            src += "<a href=\"javascript:getList('" + i + "')\">" + i + "</a>";
        }
        $("#pageIdx").append(src);
    }
    $("#pageIdx").append(nextStep);
    $("#pageIdx").append(lastStep);
}

// 문자열을 찾아 바꿔준다. 
function replaceAll(str, searchStr, replaceStr) {
    return str.split(searchStr).join(replaceStr);
}

function setSearch() {
    $("#title").keydown(function (key) {
        if (key.keyCode == 13) {
            getList(1);
        }
    });
}

//콤마풀기
function uncomma(str) {
    str = String(str);
    return str.replace(/[^\d]+/g, '');
}

//콤마찍기
function comma(str) {
    str = String(str);
    return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
}

//콤마찍기 - 정규식이라네phj..
function commas(str) {
	return str.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

/*/////////////////////////////////////////////////////////////////////*/
/*
	// Cookie 설정 ( setCookie / getCookie / deleteCookie )
*/
/*/////////////////////////////////////////////////////////////////////*/
	//	setCookie
	function setCookie( cookiename, cookievalue, expiredays ){
		var todayDate = new Date();
		todayDate.setDate( todayDate.getDate() + expiredays );
		//	document.cookie.domain="automart.co.kr"
		document.cookie = cookiename + "=" + escape(cookievalue) + "; path=/; expires=" + todayDate.toGMTString() + ";";
		return;
	}
	//	getCookie
	function getCookie(cookiename) {
		var nameOfCookie = cookiename + "=";
		var x = 0;
		while(x <= document.cookie.length) {
			var y = (x + nameOfCookie.length);
			if(document.cookie.substring(x,y) == nameOfCookie) {
				if((endOfCookie = document.cookie.indexOf(";",y)) == -1)
					endOfCookie = document.cookie.length;
				return unescape(document.cookie.substring(y,endOfCookie));
			}
			x = document.cookie.indexOf(" ",x) + 1;
			if(x == 0)
				break;
		}
		return "";
	}
	//	오늘하루 스크립트
	function checkPoupCookie(cookieName){
		var cookie = document.cookie;
		if(cookie.length > 0){
			startIndex = cookie.indexOf(cookieName);
			if (startIndex != -1) {
				return true;
			} else {
				return false;
			};
		} else {
			return false;
		};
	}
