/*
sysCompanyTableField.js
*/

function private_function(pos, arg) {
	if (pos=="afterInit") {
		
		$(".search-panel .form-control[name=comCd]").on("change",function() {
			var data={};
			data.comCd = $(".search-panel .form-control[name=comCd]").val();
			var ret = _utilAjax.sendSyncRet("/getComSysOptComTblCd/list" ,_utilJson.make(_pid, data));
			_utilForm.makeOptionString(".search-panel","comTableCd","","선택",ret);
		});
		$(".form-panel .form-control[name=comCd]").on("change",function() {
			var data={};
			data.comCd = $(".form-panel .form-control[name=comCd]").val();
			var ret = _utilAjax.sendSyncRet("/getComSysOptComTblCd/list" ,_utilJson.make(_pid, data));
			_utilForm.makeOptionString(".form-panel","comTableCd","","선택",ret);
		});
		
	} else if (pos=="setFormDataAfter") {
			var data={};
			data.comCd = $(".form-panel .form-control[name=comCd]").val();
			var ret = _utilAjax.sendSyncRet("/getComSysOptComTblCd/list" ,_utilJson.make(_pid, data));
			_utilForm.makeOptionString(".form-panel","comTableCd","","선택",ret);
			$(".form-panel .form-control[name=comTableCd]").val(arg.comTableCd);
			
			var tmp = $(".form-panel .form-control[name=colDefault]").val().trim();
			tmp = tmp.replace(/\'/g,"");
			$(".form-panel .form-control[name=colDefault]").val(tmp);
	}
}


function success(data) {
	if (data == null || data == undefined) return;
	if (data.result == "OK") {
		_utilForm.formClear(".form-panel");
		$(".btn-search").trigger("click");
//		location.reload();
	} else if (data.result == "Fail") {
		alert (data.message);
	}
}

function fail(e) {
	console.log(e);
}


/*

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
		$("#btn-search").trigger("click");
//		location.reload();
	} else {
		alert(ajax[0].message);
	}
}


*/




/*

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

*/


