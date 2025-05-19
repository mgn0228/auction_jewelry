/*
formUtil javascript
version 1.0
*/


class cm_formUtil {
	
	constructor () {
		this.isSetData = false;
		this.frmCls = "";
		this.keyNm = "";
		this.instanceDT = null;
		this.actionMode = "";
		this.isProcess = false;
	}
	
	
	init(cls, instanceDT) {
		var instance = this;
		$(cls + " .box-header .btn-add").off("click");
		$(cls + " .box-header .btn-add").on("click",function () {
			
			if (instance.isProcess == true) return;
			instance.isProcess = true;
			var tmp = instance.getIsSetData();
			if (tmp == true) {
				if (!confirm("복제하시겠습니까?")) {
					instance.formClear(instance.frmCls);
				}
			} else {
				instance.formClear(instance.frmCls);
			}
			instance.setIsSetData(false);
			if (instance.keyNm != "" && instance.keyNm != undefined) {
				$(cls+" .form-control[name="+instance.keyNm+"]").val("");
			}
			instance.instanceDT.resetSelectedInfo();
			$(cls+" .form-control[defnoupdate='Y']").prop("disabled","");
			instance.isProcess = false;
		});
		
		$(cls + " .box-header .btn-save").off("click");
		$(cls + " .box-header .btn-save").on("click",function () {
			if (instance.isProcess == true) return;
			instance.isProcess = true;
			if (instance.keyNm == "" || instance.keyNm == undefined) {
				instance.isProcess = false;
				return;
			}
			if (!confirm("저장하시겠습니까?")) {
				instance.isProcess = false;
				return;
			}
			
			if (private_function("saveBefore") == false) {
				instance.isProcess = false;
				return;
			}
			
			var mode = "insert";
			if ($(cls+" .form-control[name="+instance.keyNm+"]").val() != "") {
				mode = "update";
			}
			instance.setActionMode(mode);
			
			_curMode = "insert";
			var data = instance.getMultipartFormData(instance.frmCls);
			_utilAjax.sendMultiPartAsync("/"+_pid+"/"+mode, data, success, fail);
			instance.isProcess = false;
		});
		$(cls + " .box-header .btn-delete").off("click");
		$(cls + " .box-header .btn-delete").on("click",function () {
			if (instance.isProcess == true) return;
			instance.isProcess = true;
			if (instance.keyNm == "" || instance.keyNm == undefined) {
				instance.isProcess = false;
				return;
			}
			if (!confirm("삭제하시겠습니까?")) {
				instance.isProcess = false;
				return;
			}
			
			if (private_function("deleteBefore") == false) {
				instance.isProcess = false;
				return;
			}
			
			var keyval = $(cls+" .form-control[name="+instance.keyNm+"]").val();
			if (keyval == "" || keyval == undefined || keyval == null) {
				alert("삭제할 정보를 선택해주세요");
				instance.isProcess = false;
				return;
			}
			instance.setActionMode("delete");
			_curMode = "delete";
			var data = {};
			data[instance.keyNm] = $(cls+" .form-control[name="+instance.keyNm+"]").val();
			_utilAjax.sendAsync("/"+_pid+"/delete", _utilJson.make(_pid, data), success, fail);
			instance.isProcess = false;
		});
		this.frmCls = cls;
		this.disable(cls, true);
		this.setKeyFieldNm($(cls+" .keyfdnm").attr("keyfd"));
		this.instanceDT = instanceDT;
		
		$(cls + " .form-control[defdisabled=true]").attr("disabled",true);
		
	}
	
	
	success(data) {
		console.log(data);
		
	}
	fail(e) {
		colsole.log(e);
	}
	
	setActionMode(mode) {
		this.actionMode = mode;
	}
	getActionMode() {
		return this.actionMode;
	}
	
	setIsSetData(mode) {
		this.isSetData = mode;
	}
	getIsSetData() {
		return this.isSetData;
	}
	
	
	getFormData(cls) {

		var data={};
		$(cls+" .form-control").each(function () {
			var nm = $(this).prop("name");
			var enc = $(this).attr("defenc")==undefined?"N":$(this).attr("defenc");
			var tag = $(this).prop("tagName");
			var tagType = $(this).prop("type")==undefined?"":$(this).prop("type");
			var val = $(this).val();
			if (tagType.toUpperCase() == "RADIO" || tagType.toUpperCase() == "CHECKBOX") {
				val = "";
				if ($(this).prop("checked") == true) {
					val = $(this).val();
				} else {
					return;
				}
			}
			if (enc == "Y") {
				val = _utilString.makeCryptoJSEncrypt(_sid,val);
			}
			
			if (tag.toUpperCase() == "SELECT" && $(this).hasClass("multiple")) {
				
				var defcombinedfds = $(this).attr("defcombinedfds");
				var combinedfds = null;
				if (defcombinedfds != undefined && defcombinedfds != null) {
					combinedfds = defcombinedfds.split(",");
				}
				if (combinedfds != null && combinedfds.length > 1) {
					var combinedval = new Array(combinedfds.length);
					for (var i=0;i<combinedval.length;i++) {
						combinedval[i]="";
					}
					
//					var multival = "";
					var opts = $(this).find("option");
					for (var i=0;i<opts.length;i++) {
						
						var optvals = $(opts[i]).prop("value");
						var optvalary = optvals.split("*_*");
						
						for (var j=0;j<optvalary.length;j++) {
							if (combinedval[j] != "") combinedval[j] += ",";
							if (enc == "Y") {
								combinedval[j] += _utilString.makeCryptoJSEncrypt(_sid,optvalary[j]);
							} else {
								combinedval[j] += optvalary[j];
							}
						}
					}
					
					
					for (var i=0;i<combinedfds.length;i++) {
						if (data[combinedfds[i]] == undefined) data[combinedfds[i]] = combinedval[i];
						else data[combinedfds[i]] = data[combinedfds[i]] + "," + combinedval[i];
					}
					
					return;
				}
				
				var multival = "";
				var opts = $(this).find("option");
				for (var i=0;i<opts.length;i++) {
					if (multival != "") multival += ",";
					if (enc == "Y") {
						multival += _utilString.makeCryptoJSEncrypt(_sid,$(opts[i]).prop("value"));
					} else {
						multival += $(opts[i]).prop("value");
					}
				}
				val = multival;
			}
			
			if (data[nm] == undefined) data[nm] = val;
			else data[nm] = data[nm] + "," + val;
		});
		
		return data;
	}
	
	getFormData_org(cls) {
		var data={};
		
		$(cls+" .form-control").each(function () {
			var nm = $(this).prop("name");
			var enc = $(this).attr("defenc")==undefined?"N":$(this).attr("defenc");
			var tag = $(this).prop("tagName");
			var tagType = $(this).prop("type")==undefined?"":$(this).prop("type");
			var val = $(this).val();
			if (tagType.toUpperCase() == "RADIO" || tagType.toUpperCase() == "CHECKBOX") {
				val = "";
				if ($(this).prop("checked") == true) {
					val = $(this).val();
				} else {
					return;
				}
			}
			if (tag.toUpperCase() == "SELECT" && $(this).hasClass("multiple")) {
				var multival = "";
				var opts = $(this).find("option");
				for (var i=0;i<opts.length;i++) {
					if (multival != "") multival += ",";
					multival += $(opts[i]).prop("value");
				}
				val = multival;
			}
			
			if (enc == "Y") {
				val = _utilString.makeCryptoJSEncrypt(_sid,val);
			}
			
			if (data[nm] == undefined) data[nm] = val;
			else data[nm] = data[nm] + "," + val;
		});
		
		return data;
	}
	
	
		
	getMultipartFormData(cls) {
		
		var frmId = $(cls).attr("frmid");
		
		var data=new FormData(document.getElementById(frmId));
		data.append('key', new Blob([ JSON.stringify(this.getFormData(cls)) ], {type : "application/json"}));
		return data;
	}

	
	
	disable(cls, mode) {
//		$(cls + " .form-control").attr("disabled",mode);
	}
	
	
	formClear(cls) {
		$(cls+" input[type=hidden].form-control").val("");
		$(cls+" input[type=text].form-control").val("");
		$(cls+" input[type=password].form-control").val("");
		$(cls+" input[type=file].form-control").val("");
		$(cls+" textarea.form-control").val("");
		$(cls+" input[type=checkbox].form-control").prop("checked",false);
		$(cls+" input[type=checkbox].form-control").parent().removeClass("checked");
		$(cls+" input[type=radio].form-control").prop("checked",false);
		$(cls+" input[type=radio].form-control").parent().removeClass("checked");
		$(cls+" select.form-control").each(function () {
			$(this).val($(this).find("option:first").val());
			$(this).trigger("change");
		});
		
		$(cls+" select.multiple.form-control").empty();
		
		$(cls+" .form-control[defnoupdate='Y']").prop("disabled","");
		$(cls+" .form-control[defdisabled='true']").prop("disabled",true);
		
		$("#summernote").summernote("code", "");
/*		
		
		$(cls+" button.fileAttach").attr("value","");
		$(cls+" button.fileAttach").attr("linksrc","");
		
		$(cls+" select.form-control.multiselect").each(function (obj) {
			$(this).empty();
			$(this).closest(".form-group").find(".listcnt").attr("cnt",0);
			$(this).closest(".form-group").find(".listcnt").html("0개");
		});
		
		$(cls+" .image-uploader").remove();
		
		$(cls+" .fileImg").attr("fnkeys","");
		
		$(cls+" .fileImg").each(function (idx, obj) {
			var key = $(obj).attr("id");
			var maxfile = parseInt($(obj).attr("frmmax"));
				$("#"+key).imageUploader({
					imagesInputName:key,
					maxFiles: maxfile,
					extensions: ['.jpg', '.jpeg', '.png'],
					mimes: ['image/jpeg', 'image/png']
				});
		});
		
*/		
		
		this.setIsSetData(false);
		
		private_function("formCelearAfter");
		
	}
	
	
	setKeyFieldNm(keyNm) {
		this.keyNm = keyNm;
	}
	
	getKeyFieldNm() {
		return this.keyNm;
	}
	
	setFormData (dt, formData) {
		private_function("setFormDataBefore",formData);
		this.formClear(dt);
		if (formData == undefined || formData == null) {
			return;
		}
		
		var items = Object.keys(formData);
		
		for (var i=0;i<items.length;i++) {
			var value = formData[items[i]];
			var obj = $(dt+" .form-control[name="+items[i]+"]");
			if ($(obj).prop("tagName") == undefined) continue;
			var tagName = $(obj).prop("tagName").toUpperCase();
			var tagType = $(obj).prop("type")==undefined? "" :$(obj).prop("type").toUpperCase();
			if (tagName == "INPUT") {
				if (tagType == "RADIO" || tagType == "CHECKBOX") {
					$(dt+" .form-control[name="+items[i]+"][value="+value+"]").prop("checked",true);
					$(dt+" .form-control[name="+items[i]+"][value="+value+"]").parent().find(".iCheck-helper").trigger("click");
				} else if (tagType == "FILE") {
					$(dt+" .form-control[name="+items[i]+"]").attr("fileInfo",value);
//					$(dt+" .imageView[targetname="+items[i]+"]").attr("fileInfo",value);
				} else {
					$(dt+" .form-control[name="+items[i]+"]").val(value);
				}
			} else if (tagName == "SELECT"  && $(obj).hasClass("multiple")) {
				var opts = value.split(",");
				for (var j=0;j<opts.length;j++) {
					if (opts[j] != "") {
						var val_label = opts[j].split("#_#");
						if (val_label.length == 2) {
							$(dt+" .form-control[name="+items[i]+"]").append("<option value='"+val_label[0]+"'>"+val_label[1]+"</option>");
						} else {
							$(dt+" .form-control[name="+items[i]+"]").append("<option value='"+opts[j]+"'>"+opts[j]+"</option>");
						}
					}
				}
			} else if (tagName == "SELECT") {
				var chkitemobj = $(dt+" .form-control[name="+items[i]+"]").prev().find(".selListAdd");
				if ($(chkitemobj).length > 0) {
					var optfdinfo = $(chkitemobj).attr("actresult");
					var vallabel = optfdinfo.split("#");
					var val=formData[vallabel[0]];
					var label=val;
					if (vallabel.length > 1) label = formData[vallabel[1]];
					if ($(dt+" .form-control[name="+items[i]+"] option[value="+val+"]").length == 0) {
						var optstr = "<option value=\""+val+"\">"+label+"</option>";
						$(dt+" .form-control[name="+items[i]+"]").append(optstr);
					}
				}
				$(dt+" .form-control[name="+items[i]+"]").val(value);
				$(dt+" .form-control[name="+items[i]+"]").trigger("change");
			} else if (tagName == "TEXTAREA") {
				$(dt+" .form-control[name="+items[i]+"]").val(value);
				if ($(dt+" .form-control[name="+items[i]+"]").prop("id") == "summernote") {
					$("#summernote").summernote("code", value);
				}
			} else if (tagName == "IMG") {
				$(dt+" .form-control[name="+items[i]+"]").prop("src",value);				
			} else {
				$(dt+" .form-control[name="+items[i]+"]").val(value);
			}
		}
		this.setIsSetData(true);
		$(dt+" .form-control[defnoupdate='Y']").prop("disabled","disabled");
		
		private_function("setFormDataAfter",formData);
		
		
		
/*		
		if (data == undefined || data == null) {
			cm_makeForm.prototype.formClear(formType ,cls);
		}
		var items = this.formItem;
		for (var i=0;i<items.length;i++) {
			$(cls+" .form-control[name="+items[i].frmColNm+"]").val(data[items[i].frmColNm]);
		}
		var props = data["props"];
		if (props==null) return data;
		
		var imgfile_id = [];
		
		for (var i=0;i<props.length;i++) {
			var prop = props[i];
			var tag = prop.propTag;
			var propcd = prop.propCd;
			var propVal = prop.propVal;
			var propLbl = prop.propLbl;
			
			if (tag=="checkbox" || tag=="radio") {
				if (propVal == undefined || propVal == null || propVal.trim() == "") continue;
				$(cls+" .form-control[name="+propcd+"][value='"+propVal+"']").prop("checked",true);
			} else if (tag=="text_selectMulti" || tag=="twotext_selectMulti" || tag=="threetext_selectMulti" || tag=="threetextselect_selectMulti" || tag=="textselect_selectMulti" || tag=="texttextselect_selectMulti" || tag=="selectMulti") {
				
				var opt = "<option value=\""+propVal+"\" lbl=\""+propLbl+"\">"+propLbl+"</option>";
				$(cls+" .form-control[name="+propcd+"]").append(opt);
				var cnt = parseInt($(cls+" .multiformgroup .form-control[name="+propcd+"]").closest(".multiformgroup").find(".listcnt").attr("cnt"));
				cnt++;
				$(cls+" .multiformgroup  .form-control[name="+propcd+"]").closest(".multiformgroup").find(".listcnt").attr("cnt",cnt);
				$(cls+" .multiformgroup  .form-control[name="+propcd+"]").closest(".multiformgroup").find(".listcnt").html(cnt+"개");			
				
				
			} else if (tag=="search_selectMulti") {
				
				var opt = "<option value=\""+propVal+"\" lbl=\""+propLbl+"\">"+propLbl+"</option>";
				$(cls+" .form-control[name="+propcd+"]").append(opt);
				var cnt = parseInt($(cls+" .multiformgroup .form-control[name="+propcd+"]").closest(".multiformgroup").find(".listcnt").attr("cnt"));
				cnt++;
				$(cls+" .multiformgroup  .form-control[name="+propcd+"]").closest(".multiformgroup").find(".listcnt").attr("cnt",cnt);
				$(cls+" .multiformgroup  .form-control[name="+propcd+"]").closest(".multiformgroup").find(".listcnt").html(cnt+"개");			
			} else if (tag=="file_img") {
				var isChecked=false;
				for (var kk=imgfile_id.length-1;kk>=0;kk--) {
					if (imgfile_id[kk] == propcd) isChecked = true;
				}
				if (isChecked==false) imgfile_id.push(propcd);
			} else if (tag=="file_attach") {
				$(cls+" .form-control[name="+propcd+"]").attr("linksrc",propVal);
				$(cls+" .form-control[name="+propcd+"]").attr("value",propVal);
			} else {
				$(cls+" .form-control[name="+propcd+"]").val(propVal);
			}
		}
		
		if (imgfile_id.length > 0) {
			
				for (var j=0;j<imgfile_id.length;j++) {
					var key = imgfile_id[j];
					var preloaddata = [];
					for (var i=0,idx=1;i<props.length;i++) {
						if (props[i].propCd == key) {
							
							var onedata = {};
							onedata.id=idx;
							onedata.src=props[i].propVal;
							idx++;
							preloaddata.push(onedata);
						}
					}
					var maxfile = parseInt($("#"+key).attr("frmmax"));
					$("#"+key).empty();
					if (preloaddata.length > 0) {
						$("#"+key).imageUploader({
							preloaded: preloaddata,
							imagesInputName:key,
							maxFiles: maxfile,
							extensions: ['.jpg', '.jpeg', '.png'],
							mimes: ['image/jpeg', 'image/png']
						});
					} else {
						$("#"+key).imageUploader({
							imagesInputName:key,
							maxFiles: maxfile,
							extensions: ['.jpg', '.jpeg', '.png'],
							mimes: ['image/jpeg', 'image/png']
						});
					}
				}
				
				for (var j=0;j<imgfile_id.length;j++) {
					var fnkeys = "";
					$("#"+imgfile_id[j]+" .uploaded-image img").each(function () {
						var src = $(this).attr("src");
						src = src.replace(/\//gi,"");
						src = src.replace(/\./gi,"");
						$(this).attr("fnkey",src);
						if (fnkeys != "") fnkeys += ",";
						fnkeys += src;
	//					var src = $(this).prop("src");
	//					var srcattr = $(this).attr("src");
	//					_ajaxUtil.getByteArrayImgSync(src ,_jsonUtil.make(_pid, {}), "#"+imgfile_id[j], "#"+imgfile_id[j]+" .uploaded-image img[src='"+srcattr+"']");
					});
					$("#"+imgfile_id[j]).attr("fnkeys",fnkeys);
				}
				
		}
		
		
		
		$(_formId + " .form-control.multiselect.props").each( function(index, item) {
			var type = $(item).attr("multiselecttype");
			if (type == "tt" || type == "ts" || type == "tts" || type == "ttt" || type == "ttts") {
				var tarfd = $(item).attr("targetfd");
				var tarfds = tarfd.split(",");
				for(var i=0;i<tarfds.length;i++) {
					$(_formId + " .form-control.multiselect.props[name="+tarfds[i]+"]").empty();
				}
				
			}
		});
		
		return data;
		
*/		
		
	}
	
	
	makeOptionString (cls, controlname, defval, deflabel, optlst){
		var ret = "";
		if (defval != null && defval != undefined) {
			ret += "<option value=\""+defval+"\" lbl=\""+deflabel+"\">"+deflabel+"</option>";
		}
		for(var i=0;i<optlst.length;i++) {
			ret += "<option value=\""+optlst[i].value+"\" lbl=\""+optlst[i].label+"\">"+optlst[i].label+"</option>";
		}
		
		$(cls+" .form-control[name="+controlname+"]").html(ret);
	
	}

	
}


