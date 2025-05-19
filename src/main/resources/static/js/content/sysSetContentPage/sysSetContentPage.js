/*
sysSetContentPage.js
*/

function private_function(pos, arg) {
	if (pos=="afterInit") {
		
		
		$(".search-panel .form-control[name=comCd]").on("change",function () {
			var comCd = $(".search-panel .form-control[name=comCd]").val();
			var data={};
			data.comCd = comCd;
			_utilForm.makeOptionString(".search-panel","gid","","선택",_utilAjax.sendSyncRet("/comSysOptMbGroup/list" ,_utilJson.make(_pid, data)));
//			$(".search-panel .form-control[name=gid]").trigger("change");
			_utilForm.makeOptionString(".search-panel","pidno","","선택", []);
		})
		$(".search-panel .form-control[name=gid]").on("change",function () {
			var comCd = $(".search-panel .form-control[name=comCd]").val();
			var gid = $(".search-panel .form-control[name=gid]").val()==null?"":$(".search-panel .form-control[name=gid]").val();
			var data={};
			data.comCd = comCd;
			data.gid = gid;
			_utilForm.makeOptionString(".search-panel","pidno","","선택",_utilAjax.sendSyncRet("/getComSysOptContenetPid/list" ,_utilJson.make(_pid, data)));
		})
		
		$(".form-panel .form-control[name=comCd]").on("change",function () {
			var comCd = $(".form-panel .form-control[name=comCd]").val();
			var data={};
			data.comCd = comCd;
			_utilForm.makeOptionString(".form-panel","gid","","선택",_utilAjax.sendSyncRet("/comSysOptMbGroup/list" ,_utilJson.make(_pid, data)));
//			$(".form-panel .form-control[name=gid]").trigger("change");
			_utilForm.makeOptionString(".form-panel","pidno","","선택",[]);
		})
		$(".form-panel .form-control[name=gid]").on("change",function () {
			var comCd = $(".form-panel .form-control[name=comCd]").val();
			var gid = $(".form-panel .form-control[name=gid]").val()==null?"":$(".form-panel .form-control[name=gid]").val();
			var data={};
			data.comCd = comCd;
			data.gid = gid;
			_utilForm.makeOptionString(".form-panel","pidno","","선택",_utilAjax.sendSyncRet("/getComSysOptContenetPid/list" ,_utilJson.make(_pid, data)));
		})
		
		
/*		
		
		
		$(".form-panel .btn-group").append("<button type=\"button\" class=\"btn btn-primary btn-createDB\" onclick=\"createNewDB();\">DB생성</button>");
		$(".form-panel .btn-group").append("<button type=\"button\" class=\"btn btn-primary btn-createDB\" onclick=\"dropDB();\">DB삭제</button>");
		
*/		
		$(".form-panel .btn-group").append("<button type=\"button\" class=\"btn btn-primary btn-createPID\" onclick=\"createNewContentPID();\">신규 PID 등록</button>");
//		$(".form-panel .btn-group").append("<input type=\"text\" class=\"\">");
		
	} else if (pos=="setFormDataAfter") {
		var data={};
		data.comCd = arg.comCd;
		_utilForm.makeOptionString(".form-panel","gid","","선택",_utilAjax.sendSyncRet("/comSysOptMbGroup/list" ,_utilJson.make(_pid, data)));
		$(".form-panel .form-control[name=gid]").val(arg.gid);
		
		data.gid = arg.gid;
		_utilForm.makeOptionString(".form-panel","pidno","","선택",_utilAjax.sendSyncRet("/getComSysOptContenetPid/list" ,_utilJson.make(_pid, data)));
		$(".form-panel .form-control[name=pidno]").val(arg.pidno);
		
	}
}




function createNewContentPID() {
	var newPid = prompt("생성할 신규 PID를 입력하세요.");
	newPid = newPid.trim();
	var data={};
	data.comCd = $(".form-panel .form-control[name=comCd]").val();
	data.newPid = newPid;
	data.gid = $(".form-panel .form-control[name=gid]").val();
	
	if ( newPid == "") {
		alert("신규 pid를 입력하세요.");
		return;
	}
	
	if ( data.comCd == "" || data.gid == "") {
		alert("상세정보에 적용회사와 상용자 그룹을 선택해주세요.");
		return;
	}
	
	_utilAjax.sendAsync("/sysSetContentPage/createNewContentPID" ,_utilJson.make(_pid, data),createNewContentPIDCallBack, fail);
	
}


function createNewContentPIDCallBack(ajax) {
	if (ajax == null || ajax.length == 0) {
		alert("처리에 오류가 있습니다.");
		return;
	}
	if (ajax.result == "OK") {
		alert(ajax.message);
		location.reload();
	} else {
		alert(ajax.message);
	}
}



function success(data) {
	if (data == null || data == undefined) return;
	if (data.result == "OK") {
		_utilForm.formClear(".form-panel");
		$(".btn-search").trigger("click");
	} else if (data.result == "Fail") {
		alert (data.message);
	}
}

function fail(e) {
	console.log(e);
}


