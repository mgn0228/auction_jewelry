/*
stringUtil javascript
version 1.0
*/


class cm_stringUtil {
	
	makeDateTime4YMDHMSF(str) {
		var ret = str.substring(0,4)+"-"+str.substring(4,6)+"-"+str.substring(6,8)+" ";
		ret += str.substring(8,10)+":"+str.substring(10,12)+":"+str.substring(12,14)+" "+str.substring(14,17);
		return ret;
	}
	makeDateTime4YMDHMS(str) {
		var ret = str.substring(0,4)+"-"+str.substring(4,6)+"-"+str.substring(6,8)+" ";
		ret += str.substring(8,10)+":"+str.substring(10,12)+":"+str.substring(12,14);
		return ret;
	}
	makeDateTime4HMS(str) {
		var ret = str.substring(8,10)+":"+str.substring(10,12)+":"+str.substring(12,14);
		return ret;
	}
	
	
	makeCollapseItem(parentId, targetId,title,content) {
		
		var html = "";
		html += "<div class=\"panel box box-primary\">";
		
		html += "<div class=\"box-header with-border\">";
		html += "<div class=\"box-title\">";
		html += "<a data-toggle=\"collapse\" data-parent=\"#"+parentId+"\" href=\"#"+targetId+"\">";
		html += title;
		html += "</a>";
		html += "</div>";
		html += "</div>";
		
		html += "<div class=\""+targetId+" panel-collapse collapse\" id=\""+targetId+"\">";
		html += "<div class=\"box-body\">";
		html += content;
		html += "</div>";
		html += "</div>";

		html += "</div>";
		
		return html;
	}
	makePeriodDateFormat(str) {
		return str.replaceAll(" - ","~").replaceAll("/","-");
//		return str.replaceAll(" - ","~").replaceAll("/","").replaceAll("-","").replaceAll(":","").replaceAll(" ","");
	}
	getStartPeriodDateFormat(str) {
		var ret = str.split("~");
		return ret[0];
	}
	getEndPeriodDateFormat(str) {
		var ret = str.split("~");
		return ret[1];
	}
	existInAry(ary, key) {
		for(var i=0;i<ary.length;i++) {
			if (ary[i] == key) return true;
		}
		return false;
	}
	existInJsonAry(ary, jasonKey, key) {
		for(var i=0;i<ary.length;i++) {
			if (ary[i][jasonKey] == key) return true;
		}
		return false;
	}
	
	makeCryptoJSEncrypt(sid, str) {
		var key = CryptoJS.enc.Utf8.parse(sid.substring(0,32));
		var iv = CryptoJS.enc.Utf8.parse(sid.substring(0,16));
		var ct = CryptoJS.AES.encrypt(str, key, { iv: iv ,mode:CryptoJS.mode.CBC, padding:CryptoJS.pad.Pkcs7});
		return ct.toString();
	}
	
	makeCryptoJSDecrypt(sid, str) {
		var key = CryptoJS.enc.Utf8.parse(sid.substring(0,32));
		var iv = CryptoJS.enc.Utf8.parse(sid.substring(0,16));
		var ct = CryptoJS.AES.decrypt(str, key, { iv: iv ,mode: CryptoJS.mode.CBC ,padding: CryptoJS.pad.Pkcs7 });
		return ct.toString(CryptoJS.enc.Utf8);;
	}
	
	converDate2yyyymmdd (date) {
		return date.getFullYear() + ( (date.getMonth()+1).toString().padStart(2,'0') ) + ( date.getDate().toString().padStart(2,'0')) ;
	}
	converDate2yyyymm (date) {
		return date.getFullYear() + ( (date.getMonth()+1).toString().padStart(2,'0') ) ;
	}
	
	
		
		
	set_cookie(cookieName, value, exdays, domain, secureBool) {
		const exdate = new Date();
		exdate.setDate(exdate.getDate() + exdays);
		let cookieValue = encodeURIComponent(value) + "; path=/;";
		if (exdays) {
			cookieValue += " expires=" + exdate.toUTCString() + ";";
		}
//		if (domain) {
//			cookieValue += " domain=" + domain + ";";
//		}
		if (secureBool === true) {
			cookieValue += " secure;";
		}
		document.cookie = cookieName + "=" + cookieValue;
	}
	
	getCookie(cookieName) {
		cookieName = cookieName + '=';
		let cookieData = document.cookie;
		let start = cookieData.indexOf(cookieName);
		let cookieValue = '';
		if (start != -1) {
			start += cookieName.length;
			let end = cookieData.indexOf(';', start);
			if (end == -1) end = cookieData.length;
			cookieValue = cookieData.substring(start, end);
		}
		return decodeURIComponent(cookieValue);
	}

	
}

