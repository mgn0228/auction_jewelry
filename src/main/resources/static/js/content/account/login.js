/*
login.js
*/

function loginprocess () {
	$("button.submit").attr("disabled",true);
	var data_tmp = _utilForm.getFormData(".loginForm");
	var data={};
	data.comcd = _utilString.makeCryptoJSEncrypt(_sid,data_tmp.comcd);
	data.mbid = _utilString.makeCryptoJSEncrypt(_sid,data_tmp.mbid);
	data.mbpwd = _utilString.makeCryptoJSEncrypt(_sid,data_tmp.mbpwd);
	
	var ret = _utilAjax.sendSyncRet("/account/login", _utilJson.make(_pid,data));
	if (ret.result == "OK") {
		if (_pid=="login") _pid = "";

		const queryString = window.location.search;
		let redirectUrl = "/" + _pid;
		if (queryString) {
			redirectUrl += queryString;
		}
		location.replace(redirectUrl);
		//location.replace("/"+_pid);
	} else if (ret == null || ret == undefined) {
		alert("로그인 실패하였습니다.");
	} else {
		alert(ret.message);
	}
	$("button.submit").attr("disabled",false);
}

