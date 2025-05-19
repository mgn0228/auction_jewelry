/*
stringUtil javascript
version 1.0
*/


class cm_modalUtil {
	
	constructor() {
		this.dt = null;
		this.option = "";
	}
	
	
	setEventListner(selector) {
		$(selector).off('shown.bs.modal');
		$(selector).on('shown.bs.modal', function (event) {
			
			var panelCls = $(this).attr("panelCls");
			if (panelCls == undefined || panelCls == null) panelCls = "";
			if (panelCls == "") {
				panelCls = _formPanelCls;
			}
//			$(this).attr("panelCls","");
			
			var li = $(event.relatedTarget) ;
			var srcwhat = li.data('whatever');
			var obj = $(panelCls + " span[data-whatever="+srcwhat+"]");

			var target = $(obj).attr("acttarget").split(",");
			var params = $(obj).attr("actparams");
			if (params == undefined || params == null) {params = "";}
			params = params.split(",");
			var options = $(obj).attr("actoptions"); //.split(",");
			var pid = $(obj).attr("actpid");
			var modalTitle = $(obj).prev("span").text();
			$(selector+ " .modal-body .datatableModal").empty();
			_utilModal.setModalTitleNTarget(selector,modalTitle+" 정보",$(obj).attr("acttarget"),$(obj).attr("actresult"));
			
			private_function("afterShownModalEvent",li);
			
			var data = {};
			if ( params != "" ) {
				params.forEach(function (param , index) {
					var val = $(obj).closest(".row").find(".form-control[name="+param+"]").val();
					var label = $(obj).closest(".row").find(".form-control[name="+param+"]").prev().find("span").text();
					if ( val == "" ) {
						alert(label+" 항목을 선택해주세요.");
						$(selector).modal('hide');
						data = null;
						return;
					}
					data[param] = val;
				});
			}
			
			if (data == null) return;

			var newdata = private_function("beforeGetModalData",data);
			if ( newdata != undefined ) data = newdata;
			if (data == null) {
				$(selector).modal('hide');
				return;
			}

			var result = _utilAjax.sendSyncRet("/modal/"+pid ,_utilJson.make(_pid, data));
			private_function("afterGetModalData",data);

			_utilModal.setModalBody_v2(selector,result,options);

		});
		
		$(selector).off('hidden.bs.modal');
		$(selector).on('hidden.bs.modal', function (event) {
			
			var panelCls = $(this).attr("panelCls");
			if (panelCls == undefined || panelCls == null) panelCls = "";
			if (panelCls == "") {
				panelCls = _formPanelCls;
			}

			var modalTitle = $(this).find("#commonModalTitle").text("");
			$(selector+ " .modal-body .datatableModal").empty();
			
			private_function("afterHiddenModalEvent");
			
			if ( _utilModal.dt != null ) {
				$(selector+ " .modal-body ").empty();
				$(selector + " #modalContentTable " ).DataTable().destroy();
				
				_utilModal.dt = null;
			}

		});
		
		$(selector + " .btn-ok ").off("click");
		$(selector + " .btn-ok ").on("click",function (e) {
			if (private_function('modalOk') == false) {
				return;
			};

			// dbPid#dbPid
//			var resultFd = $(this).closest("#commonModal").attr("resultfd");
			var resultFd = $(this).closest(".modal").attr("resultfd");
			var resultFdary = resultFd.split(",");
			var resultFdidxary = [];
			var th = $(selector+" thead th");
			for (var i=0;i<resultFdary.length;i++) {
				var info = {};
				var tmpary = resultFdary[i].split("#");
				var cnt = 0;
				for (var j=1;j<th.length;j++) {
					var fdcd = $(th[j]).attr("fdcd");
					if ( fdcd == tmpary[0] ) {
						info.Val = j;
						cnt++;
					}
					if ( fdcd == tmpary[1] ) {
						info.Label = j;
						cnt++;
					}
					if (cnt == 2) {
						info.thIdx = -1;
						resultFdidxary.push(info);
						break;
					}
				}
			}

//			var targetFd = $(this).closest("#commonModal").attr("targetfd");
			var targetFd = $(this).closest(".modal").attr("targetfd");
			var targetFdary = targetFd.split(",");

			
//			var panelCls = $("#commonModal").attr("panelCls");
			var panelCls = $(this).closest(".modal").attr("panelCls");
			if (panelCls == undefined || panelCls == null) panelCls = "";
			if (panelCls == "") {
				panelCls = _formPanelCls;
			}
//			$(this).attr("panelCls","");
			
			var selitems = _utilModal.dt.rows(".selected")[0];
			var tabledata = _utilModal.dt.data();
			for (var i=0;i<selitems.length;i++) {

//				var resultfdinfo = resultFdary[]
				var item = tabledata[selitems[i]];
				
				var fdval = "";
				var fdLabel = "";
				
				for (var j=0; j<resultFdidxary.length; j++) {
					if (fdval != "") fdval += "#";
					fdval += item[resultFdidxary[j].Val];
					if (fdLabel != "") fdLabel += "/";
					fdLabel += item[resultFdidxary[j].Label];
				}
				
				for (var k=0;k<targetFdary.length;k++) {
					var tagName = $(panelCls).find(".form-control[name="+targetFdary[k]+"]").prop("tagName").toUpperCase();
					var multiple = $(panelCls).find(".form-control[name="+targetFdary[k]+"]").hasClass("multiple");
					if ( tagName == "SELECT") {
						if (multiple == true) {
							var optstr = "<option value=\""+fdval+"\">"+fdLabel+"</option>";
							$(panelCls).find(".form-control[name="+targetFdary[k]+"]").append(optstr);
						} else {
							if ($(panelCls).find(".form-control[name="+targetFdary[k]+"] option[value="+fdval+"]").length == 0) {
								var optstr = "<option value=\""+fdval+"\">"+fdLabel+"</option>";
								$(panelCls).find(".form-control[name="+targetFdary[k]+"]").append(optstr);
							}
							$(panelCls).find(".form-control[name="+targetFdary[k]+"]").val(fdval);
							$(panelCls).find(".form-control[name="+targetFdary[k]+"]").trigger("change");
						}
					} else {
						var val = $(panelCls).find(".form-control[name="+targetFdary[k]+"]").val().trim();
						if ( val != "" ) val += ",";
						val += fdval;
						$(panelCls).find(".form-control[name="+targetFdary[k]+"]").val(val);
					}
				}
				
			}
			
//			$("#commonmodal").modal('hide');
			$(this).closest(".modal").modal('hide');
		});

	};
	
	
	setItemCheck(obj,selector) {
		var opt = $(obj).find(".selecttr").attr("opt");
		if ( opt == "radio" ) {
			$(selector+ " .modal-body tr.selected").removeClass("selected");
			$(obj).addClass("selected");
			$(obj).find(" .selecttr ").attr("checked",true);
		} else {
			if ($(obj).hasClass("selected")) {
				$(obj).removeClass("selected");
				$(obj).find(" .selecttr ").prop("checked",false);
			} else {
				$(obj).addClass("selected");
				$(obj).find(" .selecttr ").prop("checked",true);
			}
		}
		return true;
	}
	
	
	setModalBody_v2 (selector, result, options) {
//		$(selector+ " .modal-body #datatableModal").empty();
		$(selector+ " .modal-body ").empty();
		var thead = "";
		var tbody = "";
		var html = "";
		if ( result == null || result.list == null || result.field == null || 
			result.field.length == 0 || result.list.length == 0) {
			html += "<table class=\"table table-bordered table-striped\" ><thead><tr>";
			html += "<tbody><tr><td>자료가 없습니다.</td></tr></tbody>";
			html += "</table>";
			$(selector+ " .modal-body table ").append(html);
			return;
		}
		
		var header = result.field;
		var list = result.list;
		
		html += "<table id=\"modalContentTable\" class=\"table table-bordered table-striped\" ><thead><tr>";
		if ( options == "radio" ) {
			html += "<th fdcd='selfd'>선택</th>";
		}
		if ( options == "checkbox" ) {
			html += "<th fdcd='selfd'><input class='selecttrall' type='checkbox' name='selecttrall' onclick='modal_selectAll(this);'>선택</th>";
		}
		for (var i = 0; i<header.length; i++) {
			html += "<th fdCd='"+header[i].fdCd+"'>"+header[i].fdLbl+"</th>";
		}
		html += "</tr></thead>";
		
		html += "<tbody>";
		for (var i = 0; i<list.length; i++ ) {
			html += "<tr class=\"datatr\" onclick=\"_utilModal.setItemCheck(this,'"+selector+"')\">";
			if ( options == "radio" ) {
				html += "<td><input type='radio' class='selecttr' opt='radio' name='selecttr'> </td>";
			} else {
				html += "<td><input type='checkbox' class='selecttr' opt='checkbox' name='selecttr'> </td>";
			}
			for (var j = 0; j<header.length; j++ ) {
				html += "<td fdCd='"+header[j].fdCd+"'>"+list[i][header[j].fdCd]+"</td>";
			}
			html += "</tr>";
		}
		html += "</tbody></table>";
		
		$(selector+ " .modal-body ").append(html);
		
		if ( this.dt != null ) {
			$(selector + " #modalContentTable " ).DataTable().destroy();
			this.dt = null;
		}
		
		this.dt = $(selector + " #modalContentTable " ).DataTable({
			"autoWidth": true
			,"paging": true
			,"lengthChange": true
			,"searching": true
			,"ordering": true
			,"info": true
			,"pageLength":_defaultPageLength
			,"language": {
				"info": "Showing page _PAGE_ of _PAGES_"
				, "infoEmpty": "No entries to show"
				, "emptyTable": "No data available in table"
				, "paginate": {
					"previous": "<"
					, "next": ">"
					, "last": ">>"
					, "first": "<<"
				}
				, "lengthMenu": 'Show <select>' +
					'<option value="10">10</option>' +
					'<option value="25">25</option>' +
					'<option value="50">50</option>' +
					'<option value="100">100</option>' +
					'<option value="-1">All</option>' +
					'</select> Entries'
			}
			,"drawCallback": function (settings) {
			}
		});
		
	}
	
	
	setModalTitleNTarget (selector, title, target, result) {
		$(selector+ " .modal-title").text(title);
		$(selector).attr("targetfd",target);
		$(selector).attr("resultfd",result);
	}
	
	
	getCheckedList(selector,lblfd) {
		var seltrs = $(selector + " input[type=checkbox]:checked").closest("tr");
		var getfd = lblfd.split(",");
		var ret = [];
		
		for(var idx=0;idx<seltrs.length;idx++) {
			var oneitem = {};
			var label = "";
			for (var i=0;i<getfd.length;i++) {
				if (label != "") label += "/";
				label += $(seltrs[idx]).find("td[fdnm="+getfd[i]+"]").text();
			}
			oneitem.label = label;
			oneitem.value = $(seltrs[idx]).find("input[type=checkbox]").val();
			ret.push(oneitem);
		}
		return ret;
	}
	
}

