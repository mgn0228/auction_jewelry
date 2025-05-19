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
	
	init(tblpanelcls) {
		var instanceme = this;
		
		this.datatablepanel = tblpanelcls;
		
		$(this.datatablepanel+" .dataTable tbody tr").off("click");
		$(this.datatablepanel+" .dataTable tbody tr").on("click",function (e) {
			if (instanceme.getFormEdit() == true) return;
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
	
	
	resetEvent() {
		
		var instanceme = this;
		
		$(this.datatablepanel + " .dataTable tbody tr").off("click");
		$(this.datatablepanel + " .dataTable tbody tr").on("click",function (e) {
			if (instanceme.getFormEdit() == true) return;
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


