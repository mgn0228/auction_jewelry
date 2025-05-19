/*
searchUtil javascript
version 1.0
*/

var searchCls = null;

class cm_searchUtil {
	
	constructor () {
		this.searchCls = "";
		this.instanceDT = null;
	}
	
	init(cls, instanceDT) {
		
		var instance = this;
		searchCls = this;
		
		$(".search-panel .btn-search").off("click");
		$(".search-panel .btn-search").on("click",function () {
			var data = instance.getSearchData(instance.searchCls);
			var retdata = private_function("btnSearch",data);
			if (retdata != null || retdata != undefined) {
				data = retdata;
			}
			_utilAjax.sendAsync("/"+_pid+"/list", _utilJson.make(_pid, data), instance.searchSuccess, fail);
		});
		
		this.instanceDT = instanceDT;
		this.searchCls = cls;
	}
	
	
	getSearchData(cls) {
		var data={};
		
		$(cls+" .form-control").each(function () {
			var nm = $(this).prop("name");
//			var tag = $(this).prop("tagName");
			var tagType = $(this).prop("type");
			var val = $(this).val();
			if (tagType == "radio" || tagType == "checkbox") {
				val = "";
				if ($(this).prop("checked") == true) {
					val = $(this).val();
				}
			}
			data[nm] = val;
		});
		
		return data;
	}
	
	
	searchSuccess(data) {
		var len = data.length;
		var cls = searchCls.instanceDT.getTableClass();
		var fdlist = $(cls+" thead tr th");

		var new_data = private_function("searchSuccess",data);
		if (new_data != null || new_data != unidefined) data = new_data;
		
		var tblstr_thead = "";
		
		var tblstr_tbody = "";
		for (var i=0;i<data.length;i++) {
			tblstr_tbody += "<tr>";
			for (var j=0;j<fdlist.length;j++) {
				tblstr_tbody += "<td>"+data[i][$(fdlist[j]).attr("fn")] + "</td>";
			}
			tblstr_tbody += "</tr>";
		}
		tblstr_tbody += "";
		
		var tblstr_footer = "";
		
		_datatable = resetDataTable(_datatable, _dataTableCls, tblstr_tbody, _utilForm, _utilDatatable, _datatablePanelCls, _formPanelCls, "Y", _isFormMain, "N", true);
		_datatableSub = resetDataTable(_datatableSub, _dataTableSubCls, "", _utilFormSub, _utilDatatableSub, _datatableSubPanelCls, _formPanelSubCls, "Y", _isFormSub, "N", true);
		
	}
	
	
	
	
	initCustom(cls, instanceDT, callBackSuccess) {
		
		var instance = this;
		searchCls = this;
		$(".search-panel .btn-search").off("click");
		$(".search-panel .btn-search").on("click",function () {
			var data = instance.getSearchData(instance.searchCls);
			var retdata = private_function("btnSearchCustom",data);
			if (retdata != null && retdata != undefined) {
				data = retdata;
			} else {
//				return;
			}
			_utilAjax.sendAsync("/"+_pid+"/list", _utilJson.make(_pid, data), callBackSuccess, fail);
		});
		
		this.instanceDT = instanceDT;
		this.searchCls = cls;
	}
	
	
	
	initCustomType2(cls, instanceDT, callBackSuccess) {
		
		var instance = this;
		searchCls = this;
		
		$(".search-panel .btn-search").off("click");
		$(".search-panel .btn-search").on("click",function () {
			var data = instance.getSearchData(instance.searchCls);
			var retdata = private_function("btnSearchCustom",data);
			if (retdata != null && retdata != undefined) {
				data = retdata;
			} else {
				return;
			}
			_utilAjax.sendAsync("/"+_pid+"/list", _utilJson.make(_pid, data), callBackSuccess, fail);
		});
		
		this.instanceDT = instanceDT;
		this.searchCls = cls;
	}
	
	
}


