/*
sysCompanyDB.js
*/

function private_function(pos, arg) {
	if (pos=="afterInit") {
		$("#dbCharsetNm").on("change",function() {
			var data={};
			data.dbCharsetNm = $(".form-panel .form-control[name=dbCharsetNm]").val();
			var ret = _utilAjax.sendSyncRet("/getComSysOptCharCollation/list" ,_utilJson.make(_pid, data));
			_utilForm.makeOptionString(".form-panel","dbCollationNm","","선택",ret);
		});
		$(".form-panel .btn-group").append("<button type=\"button\" class=\"btn btn-primary btn-createDB\" onclick=\"createNewDB();\">DB생성</button>");
		$(".form-panel .btn-group").append("<button type=\"button\" class=\"btn btn-primary btn-createDB\" onclick=\"dropDB();\">DB삭제</button>");
		
		
	} else if (pos=="setFormDataAfter") {
			var data={};
			data.dbCharsetNm = $(".form-panel .form-control[name=dbCharsetNm]").val();
			var ret = _utilAjax.sendSyncRet("/getComSysOptCharCollation/list" ,_utilJson.make(_pid, data));
			_utilForm.makeOptionString(".form-panel","dbCollationNm","","선택",ret);
			$(".form-panel .form-control[name=dbCollationNm]").val(arg.dbCollationNm);
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

function createNewDB() {
	
	if ( $(".form-panel .form-control[name=comCd]").val().trim() == "") {
		alert("목록에서 항목을 선택하세요.");
		return;
	}
	
	var data={};
	data.comCd = $(".form-panel .form-control[name=comCd]").val();
	_utilAjax.sendAsync("/sysCompanyManage/createComDB" ,_utilJson.make(_pid, data),createDBCallBack, fail);
	
}


function createDBCallBack(ajax) {
	if (ajax == null || ajax.length == 0) {
		alert("처리에 오류가 있습니다.");
		return;
	}
	if (ajax[0].result == "OK") {
		alert(ajax[0].message);
		location.reload();
	} else {
		alert(ajax[0].message);
	}
}

function dropDB() {
	if ( $(".form-panel .form-control[name=comCd]").val().trim() == "") {
		alert("목록에서 항목을 선택하세요.");
		return;
	}
	if ( !confirm("데이터가 손실됩니다. 필요시 백업을 먼저 하세요. 데이터베이스를 삭제하시겠습니까?") ) {
		return;
	}
	
	var data={};
	data.comCd = $(".form-panel .form-control[name=comCd]").val();
	_utilAjax.sendAsync("/sysCompanyManage/dropComDB" ,_utilJson.make(_pid, data),dropDBCallBack, fail);
	
}


function dropDBCallBack(ajax) {
	if (ajax == null || ajax.length == 0) {
		alert("처리에 오류가 있습니다.");
		return;
	}
	if (ajax[0].result == "OK") {
		alert(ajax[0].message);
		location.reload();
	} else {
		alert(ajax[0].message);
	}
}
