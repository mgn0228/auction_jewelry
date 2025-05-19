/*
reqNewCom.js
*/

function requestProcess() {
	
	if (!confirm("신규업체 등록 요청하시겠습니까?")) return;
	
	var data = _utilForm.getFormData(".formCls");
	var result = _utilAjax.sendSyncRet("/company/newCompany", _utilJson.make(_pid,data));
	if (result == "OK") {
		alert("요청이 정상적으로 처리되었습니다. 등록하신 연락처로 연락드리겠습니다.");
		location.replace("/");
	} else {
		alert("요청이 처리가 안되었습니다.");		
	}
}


