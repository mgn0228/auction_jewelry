/*
sysCompany.js
*/

function private_function(pos, arg) {
	if (pos=="afterInit") {
		
		$(".search-panel .form-control[name=comCd]").on("change",function () {
			var comCd = $(".search-panel .form-control[name=comCd]").val();
			var data={};
			data.comCd = comCd;
			_utilForm.makeOptionString(".search-panel","mbGrp","","선택",_utilAjax.sendSyncRet("/comSysOptMbGroup/list" ,_utilJson.make(_pid, data)));
		})
		$(".form-panel .form-control[name=comCd]").on("change",function () {
			var comCd = $(".form-panel .form-control[name=comCd]").val();
			var data={};
			data.comCd = comCd;
			_utilForm.makeOptionString(".form-panel","mbGrp","","선택",_utilAjax.sendSyncRet("/comSysOptMbGroup/list" ,_utilJson.make(_pid, data)));
		})
		
		
	} else if (pos=="setFormDataAfter") {
		var data={};
		data.comCd = arg.comCd;
		_utilForm.makeOptionString(".form-panel","mbGrp","","선택",_utilAjax.sendSyncRet("/comSysOptMbGroup/list" ,_utilJson.make(_pid, data)));
		$(".form-panel .form-control[name=mbGrp]").val(arg.mbGrp);
	}
}


function success(data) {
	if (data == null || data == undefined) return;
	if (data.result == "OK") {
		location.reload();
	} else if (data.result == "Fail") {
		alert (data.message);
	}
}

function fail(e) {
	console.log(e);
}


