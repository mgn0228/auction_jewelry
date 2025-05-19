/*
workSchedule/workSchedule.js
*/

var mapContainer=null;
var map = null;
var mapPolygonPath = null;
var mapPolygonPathSector = null;

var propStep=-1;
var marker_center = null;

var isSetForm = false;


function private_function(pos, arg) {
	if (pos=="afterInit") {
		
		mapContainer = _utilKakaomap.makeContainer("mapArea");
		var options = { //지도를 생성할 때 필요한 기본 옵션
			center: new kakao.maps.LatLng("37.57894341917099","126.8906458680191"), //지도의 중심좌표.
			level: 1 //지도의 레벨(확대, 축소 정도)
		};
		map = new kakao.maps.Map(mapContainer, options);
		var mapTypeControl = new kakao.maps.MapTypeControl();
		map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);
		var zoomControl = new kakao.maps.ZoomControl();
		map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);

		$("#schdlArea").on("change",function () {
			clearBorderPolygon();
			clearBorderPolygonSector();
			var data = {};
			data.parentNo = $("#schdlArea").val();
			_utilForm.makeOptionString(_formPanelCls,"schdlSector","","선택",_utilAjax.sendSyncRet("/getWorkAreaSector/list" ,_utilJson.make(_pid, data)));
			
			data.no = $("#schdlArea").val();
			if (data.no != "") {
				var areainfo = _utilAjax.sendSyncRet("/getWorkAreaInfo/list" ,_utilJson.make(_pid, data));
				drawBorderPolygon(areainfo);
			}
		});
		
		$("#schdlSector").on("change",function () {
			clearBorderPolygonSector();
			var data = {};
			data.no = $("#schdlSector").val();
			if (data.no == "") return;
			var areainfo = _utilAjax.sendSyncRet("/getWorkAreaInfo/list" ,_utilJson.make(_pid, data));
			clearBorderPolygonSector();
			drawBorderPolygonSector(areainfo);
		});
		
	} else if (pos=="itemActionAdd") {
		$("#commonModal table").empty();
		var targetfd = $(arg).attr("acttarget");
		var data={};
		var thead = "";
		if (targetfd == "schdlWorkerGrp") {
			_utilModal.setModalTitleNTarget("#commonModal","작업그룹정보",targetfd);
//			$("#commonModalTitle").text("작업그룹정보");
			thead = "선택,작업그룹이름";
			var modalpid = $(arg).closest(".form-group").find("#schdlWorkerGrp").attr("defmodalpid");
			var memgrp = _utilAjax.sendSyncRet("/"+modalpid+"/list" ,_utilJson.make(_pid, data));
			_utilModal.setModalBody("#commonModal",memgrp,thead,true);
		} else if (targetfd == "schdlWorker") {
			_utilModal.setModalTitleNTarget("#commonModal","작업자정보",targetfd);
//			$("#commonModalTitle").text("작업자정보");
			thead = "선택,작업그룹이름,작업자이름";
			var modalpid = $(arg).closest(".form-group").find("#schdlWorker").attr("defmodalpid");
			
			var schdlWorkerGrp = "";
			var opts = $("#schdlWorkerGrp option");
			for(var i=0;i<opts.length;i++) {
				if (schdlWorkerGrp != "") schdlWorkerGrp += ",";
				schdlWorkerGrp += $(opts[i]).val();
			}
			
			data.memgrp = schdlWorkerGrp;
			var mem = _utilAjax.sendSyncRet("/"+modalpid+"/list" ,_utilJson.make(_pid, data));
			_utilModal.setModalBody("#commonModal",mem,thead,true);
		}
	
	} else if (pos == "modalOk") {
		var targetfd = $("#commonModal").attr("targetfd");
		if (targetfd == "schdlWorkerGrp") {
			var sellist = _utilModal.getCheckedList("#commonModal","workerGrpNm");
			for (var i=0;i<sellist.length;i++) {
				$("#schdlWorkerGrp").append("<option value='"+sellist[i].value+"'>"+sellist[i].label+"</option>");
			}
		} else if (targetfd == "schdlWorker") {
			var sellist = _utilModal.getCheckedList("#commonModal","workerGrpNm,workerNm");
			for (var i=0;i<sellist.length;i++) {
				$("#schdlWorker").append("<option value='"+sellist[i].value+"'>"+sellist[i].label+"</option>");
			}
		}
		$("#commonModal .close").trigger("click");
		
	} else if (pos == "itemActionDel") {
		var targetfd = $(arg).attr("acttarget");
		$("#"+targetfd).find("option:selected").remove();
	} else if (pos == "setFormDataAfter") {
		var wrkgrp = $("#schdlWorkerGrp option");
		var wrkgrpLbl = $("#workGrpNm option");
		for (var i=0;i<wrkgrpLbl.length;i++) {
			var str = $(wrkgrpLbl[i]).val().split("_");
			for (j=0;j<wrkgrp.length;j++) {
				var grpcd = $(wrkgrp[j]).val();
				if (str[0] == grpcd) {
					$(wrkgrp[j]).text(str[1]);
				}
			}
		}

		var wrk = $("#schdlWorker option");
		var wrkLbl = $("#workNm option");
		for (var i=0;i<wrkLbl.length;i++) {
			var strary = $(wrkLbl[i]).val().split("_");
			var wrkidx = strary[0];
			var str = "";
			for(var kk=1;kk<strary.length;kk++) {
				if (kk != 1) str += "_";
				str += strary[kk];
			}
			for (j=0;j<wrk.length;j++) {
				var grpcd = $(wrk[j]).val();
				if (wrkidx == grpcd) {
					$(wrk[j]).text(str);
				}
			}
		}
		
		
		var setorval = arg.schdlSector;
		$(document).ready(function () {
			$("#schdlSector").val(setorval);
			$("#schdlSector").trigger("change");
		});
		
		
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


/*

function resetMapEvent(map) {
	
	
	
	kakao.maps.event.addListener(map, 'dblclick', function(mouseEvent) {
		var latlng = mouseEvent.latLng;
		marker.setPosition(latlng);
		dataSetToTarget(latlng);
		
	});

	
}


function dataSetToTarget(latlng) {
	
	var target = $(".selectMapAction").val();
	var lat = latlng.Ma;
	var lng = latlng.La;
	if (target == "center") {
		$(".form-control[name=areaCenterLat]").val(lat);
		$(".form-control[name=areaCenterLng]").val(lng);
		
		drawMarker();
		
	} else if (target == "point") {
		var optstr = "<option lat='"+lat+"' lng='"+lng+"' value='"+lat+"/"+lng+"'>"+lat+"/"+lng+"</option>";
		$(".form-control[name=areaPointLatLng]").append(optstr);
		var optstr = "<option value='"+lat+"'>"+lat+"</option>";
		$(".form-control[name=areaPointLat]").append(optstr);
		var optstr = "<option value='"+lng+"'>"+lng+"</option>";
		$(".form-control[name=areaPointLng]").append(optstr);
		
		drawBorderPolygon();
		
	}
}

*/

function drawBorderPolygon(areainfo) {
	clearBorderPolygon();
	
	if (areainfo == null || areainfo == undefined || areainfo.length == undefined) return;
	if (areainfo.length < 1) {
		return;
	}
	
	if (areainfo.length < 1 || areainfo[0].areaType != "1") { // gps가 아니면
		mapPolygonPath = null;
		return;
	}
	
	var area = areainfo[0];
	
	if (area.areaPointLng.trim() == "" || area.areaPointLat.trim() == "") {
		console.log("error : 작업영역정보에 오류가 있습니다.");
		return;
	}
	
	var lng = area.areaPointLng.split(",");
	var lat = area.areaPointLat.split(",");
	
	if (lng.length != lat.length || lng.length < 2) {
		console.log("error : 위경도 정보가 정확하지 않습니다.");
		return;
	}
	
	
	var linePath = [];
	for (var i=0;i<lng.length;i++) {
		linePath.push(new kakao.maps.LatLng(lat[i],lng[i]));
	}
	linePath.push(new kakao.maps.LatLng(lat[0],lng[0]));
	
		
	if (area.areaCenterLat.trim() == "" || area.areaCenterLng.trim() == "") {
		var moveLatLon = new kakao.maps.LatLng(lat[0],lng[0]);
		map.panTo(moveLatLon);
	} else {
		var moveLatLon = new kakao.maps.LatLng(area.areaCenterLat,area.areaCenterLng);
		map.panTo(moveLatLon);
	}

	var polyline = new kakao.maps.Polyline({
		path: linePath,
		strokeWeight: 5,
		strokeColor: '#FFAE00',
		strokeOpacity: 0.7,
		strokeStyle: 'solid'
	})
	polyline.setMap(map);
	mapPolygonPath = polyline;

}

function clearBorderPolygon(){
	
	if (mapPolygonPath == null) return;
	mapPolygonPath.setMap(null);
//	mapPolygonPath = null;
}




function drawBorderPolygonSector(areainfo) {
	clearBorderPolygonSector();
	if (areainfo == null || areainfo == undefined || areainfo.length == undefined) return;
	
	if (areainfo.length < 1) {
		return;
	}
	
	if (areainfo.length < 1 || areainfo[0].areaType != "1") { // gps가 아니면
		mapPolygonPathSector = null;
		return;
	}
	
	var area = areainfo[0];
	
	if (area.areaPointLng.trim() == "" || area.areaPointLat.trim() == "") {
		console.log("error : 작업영역정보에 오류가 있습니다.");
		return;
	}
	
	var lng = area.areaPointLng.split(",");
	var lat = area.areaPointLat.split(",");
	
	if (lng.length != lat.length || lng.length < 2) {
		console.log("error : 위경도 정보가 정확하지 않습니다.");
		return;
	}
	
	
	var linePath = [];
	for (var i=0;i<lng.length;i++) {
		linePath.push(new kakao.maps.LatLng(lat[i],lng[i]));
	}
	linePath.push(new kakao.maps.LatLng(lat[0],lng[0]));
	
	var moveLatLon = new kakao.maps.LatLng(lat[0],lng[0]);
	map.panTo(moveLatLon);

	var polyline = new kakao.maps.Polyline({
		path: linePath,
		strokeWeight: 5,
		strokeColor: '#F1D0CC',
		strokeOpacity: 0.7,
		strokeStyle: 'solid'
	})
	polyline.setMap(map);
	mapPolygonPathSector = polyline;

}

function clearBorderPolygonSector(){
	
	if (mapPolygonPathSector == null) return;
	mapPolygonPathSector.setMap(null);

}



/*

function selListActionDel(obj) {
	var tarname = $(obj).attr("acttarget");
	var optidx = $(".form-control[name="+tarname+"] option").index($(".form-control[name="+tarname+"] option:selected"))

	if (tarname == "areaPointLatLng") {
		$(".form-control[name=areaPointLatLng] option:eq("+optidx+")").remove();
		$(".form-control[name=areaPointLng] option:eq("+optidx+")").remove();
		$(".form-control[name=areaPointLat] option:eq("+optidx+")").remove();
		drawBorderPolygon();
	}
	
}


function drawMarker() {
	clearMarker();
	var lat = $("#areaCenterLat").val().trim();
	var lng = $("#areaCenterLng").val().trim();
	
	if (lat == "" || lng == "") {
		return;
	}
	
	var imageSize = new kakao.maps.Size(20, 35);
	var markerImage = new kakao.maps.MarkerImage("/img/common/marker_1.png", imageSize); 
	
	var marker_position = new kakao.maps.LatLng(lat,lng);
	
	var marker = new kakao.maps.Marker({
		position: marker_position,
		title:"Center",
		image: markerImage
	});
	
	marker.setMap(map);
	marker_center = marker;
}


function clearMarker(){
	if (marker_center == null) return;
	marker_center.setMap(null);
	marker_center = null;
}


*/

