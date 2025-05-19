/*
dvcPublic/dvcPublic.js
*/

function private_function(pos, arg) {
	if (pos=="afterInit") {
		$("#dvcAreaCd").on("change",function () {
			var data = {};
			data.parentNo = $("#dvcAreaCd").val();
			_utilForm.makeOptionString(_formPanelCls,"dvcSectorCd","","선택",_utilAjax.sendSyncRet("/getWorkAreaSector/list" ,_utilJson.make(_pid, data)));
		});
	} else if (pos == "setFormDataAfter") {
		
		var formdata = arg;
		var data = {};
		data.parentNo = $("#dvcAreaCd").val();
		_utilForm.makeOptionString(_formPanelCls,"dvcSectorCd","","선택",_utilAjax.sendSyncRet("/getWorkAreaSector/list" ,_utilJson.make(_pid, data)));
		
		$("#dvcSectorCd").val(formdata.dvcSectorCd);
		
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
