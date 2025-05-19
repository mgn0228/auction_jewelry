/*
ajaxUtil javascript
version 1.0
*/


class cm_ajaxUtil {
	
	sendAsync (url, jsondata, success, fail) {
		if (success == undefined) success = this.success;
		if (fail == undefined) fail = this.fail;
		$.ajax({
			type : "POST",
			dataType : "json",
			url : url,
			data : jsondata,
			contentType : "application/json",
			accept : "application/json",
			success : function(data) {
				success(data);
			},
			error : function(e) {
				fail(e);
			}
		});
	}
	
	sendSync (url, jsondata, success, fail) {
		if (success == undefined) success = this.success;
		if (fail == undefined) fail = this.fail;
		$.ajax({
			type : "POST",
			dataType : "json",
			url : url,
			async : false,
			data : jsondata,
			contentType : "application/json",
			accept : "application/json",
			success : function(data) {
				success(data);
			},
			error : function(e) {
				fail(e);
			}
		});
	}
	
	sendSyncRet (url, jsondata) {
		var ret;
		$.ajax({
			type : "POST",
			dataType : "json",
			url : url,
			async : false,
			data : jsondata,
			contentType : "application/json",
			accept : "application/json",
			success : function(data) {
				ret = data;
			},
			error : function(e) {
				ret = e;
			}
		});
		return ret;
	}
	
	
	sendMultiPartAsync (url, jsondata, success, fail) {
		if (success == undefined) success = this.success;
		if (fail == undefined) fail = this.fail;
	
		$.ajax({
			type : "POST",
			dataType : "json",
			url : url,
			data : jsondata,
			processData : false,
			contentType : false,
			accept : "application/json",
			success : function(data) {
				success(data);
			},
			error : function(e) {
				fail(e);
			}
		});
	}
	
	
	getByteArrayImgAsync (url, jsondata, success, fail) {
		if (success == undefined)	success = this.success;
		if (fail == undefined)	fail = this.fail;
		
		$.ajax({
			type : "GET",
			url : url,
			beforeSend: function (xhr) {
				xhr.overrideMimeType('text/plain; charset=x-user-defined');
			},
			success: function (result, textStatus, jqXHR) {
				success(result, textStatus, jqXHR);
			},
			error: function(xhr, textStatus, errorThrown){
				fail(errorThrown);
			} 
		});
	}
	
	fail (e) {
		console.log("fail");
	}
	
	success (data) {
		console.log("success");
	}
	
	summernoteImgUplaod(url, data, success, fail) {
		if (success == undefined)	success = this.success;
		if (fail == undefined)	fail = this.fail;
		
		$.ajax({
			type : "POST",
			dataType : "json",
			url : url,
			data : data,
			processData : false,
			contentType : false,
			accept : "application/json",
			success : function(data) {
				success(data)
			},
			error : function(e) {
				fail(e);
			}
		});
	}

}

