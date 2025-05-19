/*
sysSetDBInfo.js
*/

function private_function(pos, arg) {
	
	if (pos=="afterInit") {
		
		$(".search-panel .form-control[name=comCd]").on("change",function () {
			var comCd = $(".search-panel .form-control[name=comCd]").val();
			var data={};
			data.comCd = comCd;
			_utilForm.makeOptionString(".search-panel","gid","","선택",_utilAjax.sendSyncRet("/comSysOptMbGroup/list" ,_utilJson.make(_pid, data)));
		})
		$(".form-panel-sub .form-control[name=comCd]").on("change",function () {
			var comCd = $(".form-panel-sub .form-control[name=comCd]").val();
			var data={};
			data.comCd = comCd;
			_utilForm.makeOptionString(".form-panel-sub","gid","","선택",_utilAjax.sendSyncRet("/comSysOptMbGroup/list" ,_utilJson.make(_pid, data)));
		})
		
/*		
		
		_utilModal.setEventListner("#commonModal");
		
		$(".form-panel .btn-group").append("<button type=\"button\" class=\"btn btn-primary btn-createDB\" onclick=\"createNewDB();\">DB생성</button>");
		$(".form-panel .btn-group").append("<button type=\"button\" class=\"btn btn-primary btn-createDB\" onclick=\"dropDB();\">DB삭제</button>");
		
*/		
//		$(".form-panel .btn-group").append("<button type=\"button\" class=\"btn btn-primary btn-createPID\" onclick=\"createNewContentPID();\">신규 PID 등록</button>");
//		$(".form-panel .btn-group").append("<input type=\"text\" class=\"\">");
		
	} else if (pos=="setFormDataAfter") {
		$(".form-panel-sub .form-control[name=gid]").val(arg.gid);
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
