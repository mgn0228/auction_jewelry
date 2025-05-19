/*
datatableUtil javascript
version 1.0
*/


class cm_datatableUtil {
	
	constructor () {
		this.formEdit = false;
		this.selData = null;
		this.selDataIndex = -1;
		this.datatable = null;
		this.datatablepanel = "";
		this.utilForm = null;
		this.isProcess = false;
	}
	
/*	
	
	getFormData(cls) {
		var data={};
		
		$(cls+" .form-control").each(function (el) {
			var nm = $(el).prop("name");
//			var tag = $(el).prop("tagName");
			var tagType = $(el).prop("type");
			var val = $(el).val();
			if (tagType == "radio" || tagType == "checkbox") {
				val = "";
				if ($(el).prop("checked") == true) {
					val = $(el).val();
				}
			}
			data[nm] = val;
		});
		
		return data;
	}
	
*/	
	
	init(tblpanelcls, datatbl, utilForm) {
		var instanceme = this;
		
		this.datatablepanel = tblpanelcls;
		this.datatable = datatbl;
		this.utilForm = utilForm;
		
		$(this.datatablepanel+" .btn-view").off("click");
		$(this.datatablepanel+" .btn-view").on("click",function (e) {
			if (utilForm.instanceDT.getSelectedIndex() == -1) {
				alert("선택한 정보가 없습니다.");
				return;
			}
			$(utilForm.frmCls).modal("show");
		});
		
		
		
		$(tblpanelcls + " .box-header .btn-add").off("click");
		$(tblpanelcls + " .box-header .btn-add").on("click",function () {
			
			if (instanceme.isProcess == true) return;
			instanceme.isProcess = true;
			var tmp = instanceme.utilForm.getIsSetData();
			if (tmp == true) {
				if (!confirm("복제하시겠습니까?")) {
					instanceme.utilForm.formClear(instanceme.utilForm.frmCls);
				}
			} else {
				instanceme.utilForm.formClear(instanceme.utilForm.frmCls);
			}
			
			instanceme.utilForm.setIsSetData(false);
			if (instanceme.utilForm.keyNm != "" && instanceme.utilForm.keyNm != undefined) {
				$(instanceme.utilForm.frmCls+" .form-control[name="+instanceme.utilForm.keyNm+"]").val("");
			}
			instanceme.utilForm.instanceDT.resetSelectedInfo();
			$(instanceme.utilForm.frmCls+" .form-control[defnoupdate='Y']").prop("disabled","");
			instanceme.isProcess = false;
			
			if (instanceme.utilForm.instanceDT.datatable != _datatableSub) {
				_datatableSub = resetDataTable(_datatableSub, _dataTableSubCls, "", _utilFormSub, _utilDatatableSub, _datatableSubPanelCls, _formPanelSubCls, "Y", _isFormSub, "N", true);
				_utilDatatableSub.setSelectData(null);
				_utilDatatableSub.setSelectedIndex(-1);
				_utilDatatableSub.utilForm.setIsSetData(false);
			}
			
			$(instanceme.utilForm.frmCls).modal("show");
		});
		
		
		
		$(tblpanelcls + " .box-header .btn-delete").off("click");
		$(tblpanelcls + " .box-header .btn-delete").on("click",function () {
			
			if (instanceme.isProcess == true) return;
			instanceme.isProcess = true;
			
			if (utilForm.keyNm == "" || utilForm.keyNm == undefined) {
				utilForm.instanceDT.isProcess = false;
				return;
			}
			if (!confirm("삭제하시겠습니까?")) {
				utilForm.instanceDT.isProcess = false;
				return;
			}
			
			if (private_function("deleteBefore", utilForm.instanceDT) == false) {
				utilForm.instanceDT.isProcess = false;
				return;
			}
			
			var keyval = $(utilForm.frmCls+" .form-control[name="+utilForm.keyNm+"]").val();
			if (keyval == "" || keyval == undefined || keyval == null) {
				alert("삭제할 정보를 선택해주세요");
				utilForm.instanceDT.isProcess = false;
				return;
			}
			
			var targetpid = $(utilForm.instanceDT.tableClass).attr("contentpid");
			if (targetpid == undefined || targetpid == "") {

				targetpid = $(utilForm.instanceDT.tableClass).attr("targetsubtablepid");
				if (targetpid == undefined || targetpid == "") {
					utilForm.instanceDT.isProcess = false;
					return;
				}
				targetpid = _pid;
				
			}
			
			_curutilDataTable = instanceme;
			_curMode = "delete";
			var data = {};
			data[utilForm.keyNm] = $(utilForm.frmCls+" .form-control[name="+utilForm.keyNm+"]").val();
			_utilAjax.sendAsync("/"+targetpid+"/delete", _utilJson.make(targetpid, data), success, fail);
			utilForm.instanceDT.isProcess = false;
			
		});
		
		
	}
	
	
		
	setFormEdit (mode){
		this.formEdit = mode;
	}
	getFormEdit (){
		return this.formEdit;
	}

	
	setDataTable(tbl, cls) {
		this.datatable = tbl;
		this.tableClass = cls
	}
	getDataTable () {
		return this.datatable;
	}
	getTableClass () {
		return this.tableClass
	}
	
	
	setSelectData (data) {
		this.selData = data;
	}
	getSelectData () {
		return this.selData;
	}
	
	setSelectedIndex (idx) {
		this.selDataIndex = idx;
	}	
	getSelectedIndex () {
		return this.selDataIndex;
	}	
	
	
	resetSelectedInfo() {
		this.selData = null;
		this.selDataIndex = -1;
		$(this.tableClass + " tr.selected").removeClass("selected");
	}
	
	
	resetEvent(utilDataTable) {
		
		var instanceme = utilDataTable;
		
		$(instanceme.datatablepanel + " .dataTable tbody tr").off("click");
		$(instanceme.datatablepanel + " .dataTable tbody tr").on("click",function (e) {
//			if (instanceme.getFormEdit() == true) return;
			var tblcls = instanceme.getTableClass();
			
			if ($(this).hasClass('selected')) {
				$(tblcls + " tr.selected").removeClass("selected");
				instanceme.setSelectData(null);
				instanceme.setSelectedIndex(-1);
			} else {
				$(tblcls + " tr.selected").removeClass("selected");
				$(this).addClass('selected');
				instanceme.setSelectData(instanceme.getDataTable().row(this).data());
				instanceme.setSelectedIndex(instanceme.getDataTable().row(this).index());
			}
			formSet(instanceme,false);
		});
		private_function("datatbleResetEvent",this);

	}
	
	
}


