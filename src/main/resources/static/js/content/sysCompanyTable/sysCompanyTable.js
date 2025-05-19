/*
sysCompanyTable.js
*/

function private_function(pos, arg) {
	if (pos=="afterInit") {
//		_datatable.column(0).visible(false);
/*	
		$("#dbCharsetNm").on("change",function() {
			var data={};
			data.dbCharsetNm = $(".form-panel .form-control[name=dbCharsetNm]").val();
			var ret = _utilAjax.sendSyncRet("/getComSysOptCharCollation/list" ,_utilJson.make(_pid, data));
			_utilForm.makeOptionString(".form-panel","dbCollationNm","","선택",ret);
		});
*/		
		$(".form-panel .btn-group").append("<button type=\"button\" class=\"btn btn-primary btn-createTable\" onclick=\"createNewTable();\">Table 추가(기본정보)</button>");
		$(".form-panel .btn-group").append("<button type=\"button\" class=\"btn btn-danger btn-createTable\" onclick=\"dropTable();\">Table 삭제</button>");
		
		
	} else if (pos=="setFormDataAfter") {
/*		
			var data={};
			data.dbCharsetNm = $(".form-panel .form-control[name=dbCharsetNm]").val();
			var ret = _utilAjax.sendSyncRet("/getComSysOptCharCollation/list" ,_utilJson.make(_pid, data));
			_utilForm.makeOptionString(".form-panel","dbCollationNm","","선택",ret);
			$(".form-panel .form-control[name=dbCollationNm]").val(arg.dbCollationNm);
*/		
			
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



function createNewTable() {
	
	if ( $(".form-panel .form-control[name=comCd]").val().trim() == "" || 
		$(".form-panel .form-control[name=comTableNm]").val().trim() == "") {
		alert("추가할 정보(회사코드, Table이름)을 선택하세요.");
		return;
	}
	
	var data={};
	data.comCd = $(".form-panel .form-control[name=comCd]").val();
	data.comTableNm = $(".form-panel .form-control[name=comTableNm]").val().trim();
	
	_utilAjax.sendAsync("/sysCompanyManage/createComTable" ,_utilJson.make(_pid, data),createTableCallBack, fail);
	
}


function createTableCallBack(ajax) {
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

function dropTable() {
	if ( $(".form-panel .form-control[name=comCd]").val().trim() == "" || 
		$(".form-panel .form-control[name=comTableNm]").val().trim() == "") {
		alert("목록에서 항목을 선택하세요.");
		return;
	}
	if ( !confirm("데이터가 손실됩니다. 필요시 백업을 먼저 하세요. 테이블을 삭제하시겠습니까?") ) {
		return;
	}
	
	var data={};
	data.comCd = $(".form-panel .form-control[name=comCd]").val();
	data.comTableNm = $(".form-panel .form-control[name=comTableNm]").val().trim();
	_utilAjax.sendAsync("/sysCompanyManage/dropComTable" ,_utilJson.make(_pid, data),dropTableCallBack, fail);
	
}


function dropTableCallBack(ajax) {
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




