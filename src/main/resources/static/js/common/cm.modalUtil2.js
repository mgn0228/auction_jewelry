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
		
		$(selector).on('shown.bs.modal', function (event) {
			
			var li = $(event.relatedTarget) ;
			var srcwhat = li.data('whatever');
			var obj = $("span[data-whatever="+srcwhat+"]");
			
			var target = $(obj).attr("acttarget").split(",");
			var params = $(obj).attr("actparams").split(",");
			var options = $(obj).attr("actoptions"); //.split(",");
			var pid = $(obj).attr("actpid");
			var modalTitle = $(obj).prev("span").text();
			$(selector+ " .modal-body .datatableModal").empty();
			_utilModal.setModalTitleNTarget(selector,modalTitle+" 정보",$(obj).attr("acttarget"),$(obj).attr("actresult"));
			
			
			var data=_utilForm.getFormData(_formPanelCls);
			
			params.forEach(function (param , index) {
				var val = $(obj).closest(".row").find(".form-control[name="+param+"]").val();
				var label = $(obj).closest(".row").find(".form-control[name="+param+"]").prev().find("span").text();
				if ( val == "" ) {
					alert(label+" 항목을 선택해주세요.");
					$(selector).modal('hide');
					data = null;
					return;
				}
			});
			
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
		$(selector + " .btn-ok ").off("click");
		$(selector + " .btn-ok ").on("click",function (e) {
			if (private_function('modalOk') == false) {
				return;
			};
			
			// dbPid#dbPid 
			var resultFd = $(this).closest("#commonModal").attr("resultfd");
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
			
			var targetFd = $(this).closest("#commonModal").attr("targetfd");
			var targetFdary = targetFd.split(",");
//			var thFdary
			
			
/*			
			var thary = $(this).closest("#commonModal").find("#modalContentTable thead th");
			
			var tharyidx = [];
			
			for (var i=0;i<resultFdidxary.length;i++) {
				for (var j=0;j<thary.length;j++) {
					if (resultFdidxary[i].Val == $(thary[j]).attr("fdcd") ) {
						resultFdidxary[i].thIdx = j;
						break;
					}
				}
			}
			
*/			
			
			
			var selitems = _utilModal.dt.rows(".selected")[0];
			var tabledata = _utilModal.dt.data();
			for (var i=0;i<selitems.length;i++) {
				
//				var resultfdinfo = resultFdary[]
				var item = tabledata[selitems[i]];
				
				for (var j=0; j<resultFdidxary.length; j++) {
					var fdval = item[resultFdidxary[j].Val];
					var fdLabel = item[resultFdidxary[j].Label];
					for (var k=0;k<targetFdary.length;k++) {
						var tagName = $(_formPanelCls).find(".form-control[name="+targetFdary[k]+"]").prop("tagName").toUpperCase();
						var multiple = $(_formPanelCls).find(".form-control[name="+targetFdary[k]+"]").hasClass("multiple");
						if ( tagName == "SELECT") {
							if (multiple == true) {
								var optstr = "<option value=\""+fdval+"\">"+fdLabel+"</option>";
								$(_formPanelCls).find(".form-control[name="+targetFdary[k]+"]").append(optstr);
							} else {
								$(_formPanelCls).find(".form-control[name="+targetFdary[k]+"]").val(fdval);
							}
						} else {
							var val = $(_formPanelCls).find(".form-control[name="+targetFdary[k]+"]").val().trim();
							if ( val != "" ) val += ",";
							val += fdval;
							$(_formPanelCls).find(".form-control[name="+targetFdary[k]+"]").val(val);
						}
						
					}
				}
			}
			
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
			html += "<th fdcd='selfd'><input class='selecttrall' type='checkbox' name='selecttrall'>선택</th>";
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
	
	
	
	
	
	
	
	
	
	
	
/*	
	
	
	setModalBody (selector, list, head, first_chkbox) {
		$(selector+ " .modal-body table ").empty();
		var thead = "";
		var tbody = "";
		
		if (head == null || head == "") {
			for (var i=0;i<keys.length;i++) {
				thead += "<th>"+keys[i]+"</th>";
			}
		} else {
			var heads = head.split(",");
			for (var i=0;i<heads.length;i++) {
				thead += "<th>"+heads[i]+"</th>";
			}
		}
		thead = "<thead>"+thead+"</thead>";
		
		var keys = "";
		if (list.length > 0) {
			keys = Object.keys(list[0]);
		}
		 
		for (var i in list) {
			
			tbody += "<tr>";
			for (var key in keys) {
				if (first_chkbox == true && key == 0) {
					tbody += "<td fdnm=\""+keys[key]+"\"><div class=\"checkbox\"><label><input type=\"checkbox\" value=\""+list[i][keys[key]]+"\"></label></div></td>";
				} else {
					tbody += "<td fdnm=\""+keys[key]+"\">"+list[i][keys[key]]+"</td>";
				}
			}
			tbody += "</tr>";
		}
		tbody = "<tbody>"+tbody+"</tbody>";
		
		$(selector+ " table ").append(thead+tbody);
	}
	
*/	


	
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

