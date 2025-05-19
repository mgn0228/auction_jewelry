/*
monitoring_echo/monitoring_echo.js
*/

var mapContainer=null;
var map = null;
var mapPolygonPath = null;
var mapPolygonPathSector = null;
var mapPolygonPathSectorInfo = null;

// var mapPolygonPathParent = null;
var propStep=-1;
//var marker_center = null;


//var isSetForm = false;


var getRealTimeInterval = null;
var intervalTime = 3000;

//var marker_Worker = null;
var marker_Worker = [];
var marker_Worker_Info = [];
var maxWkInfoChkCnt = 10; // event display max count

var emergencyLvl = 5;
var customOverlay = null;
var infowindow = null;

var handle_sectorMouseOver = null;
var handle_sectorMouseOut = null;
var handle_sectorMouseMove = null;
var handle_sectorClick = null;

function private_function(pos, arg) {
	if (pos=="afterInit") {
		$(_searchPanelCls+" .form-control[name=areaCd] option:eq(0)").remove();
		$(".layer_generalInfo ,.layer_workerInfo").draggable({
			handle: ".box-header"
		})
		
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
		
		customOverlay = new kakao.maps.CustomOverlay({})
		infowindow = new kakao.maps.InfoWindow({removable: true});
	}
	
}




function searchSuccess(data) {
	if (data.length > 0) {
		setWorkAreaInfo(data);
		if (data[0].areaType=="1") {
			$("#mapArea").removeClass("hidden");
			$("#imageMap").addClass("hidden");
			drawBorderPolygon(data);
		} else {
			$("#mapArea").addClass("hidden");
			$("#imageMap").removeClass("hidden");
			clearBorderPolygon();
			drawImageMap(data);
		}
		
		if (getRealTimeInterval != null) {
			clearInterval(getRealTimeInterval);
		}
		
		clearMarkerWorker();

		getRealTimeInfo();
		getRealTimeInterval = setInterval(function(){
			getRealTimeInfo();
		}, intervalTime);
		
	} else {
		clearInformation();
		clearBorderPolygon();
	}
	
}


function getRealTimeInfo() {
	var data={};
	data.areaCd = $(".search-panel .form-control[name=areaCd]").val();
	_utilAjax.sendAsync("/realtime/rt_"+_pid ,_utilJson.make(_pid, data), realTimeCallBack, fail)
//	_utilAjax.sendAsync("/realtime/rt_"+_pid+"_2" ,_utilJson.make(_pid, data), realTimeCallBack, fail)
}

function realTimeCallBack(data) {
	if (data == null || data == undefined || data.length == 0) {
		return;
	}
	drawMarkerWorker(data);
//	console.log("--");
}


function setWorkAreaInfo(data) {
	clearInformation();
	if (data.length > 0) {
		
		$(".layer_generalInfo .box-title").text(data[0].areaTitle);
		var sectorInfo = "";
		var prevSectorCd = "";
		var content = "";
		var wrkcnt = 0;
		var targetId="";
		var title="";
		for (var i=0;i<data.length;i++) {
			
			if (prevSectorCd != data[i].areaSectorCd) {
				if (i>0) {
					title += "(총 "+wrkcnt+"명)";
					content = "<ul>"+content+ "</ul>";
					sectorInfo += _utilString.makeCollapseItem("sectorInfo",targetId ,title ,content);
				}
				targetId = "sector_"+data[i].areaSectorCd;
				title = data[i].areaSectorTitle;
				if (data[i].schdlTitle == "") {
					content = "작업정보 없음";
					wrkcnt = 0;
				} else {
					content = "<li class='scheduleInfo'>";
					content += data[i].schdlTitle+"(총 "+ data[i].wrkcnt + "명)";
					content += "<ul class='scheduleInfo_Detail'>";
					content += "<li>작업시간<br>"+ data[i].schdlStartDt + "~" + data[i].schdlEndDt + "</li>";
					content += "<li>작업자<br>"+ data[i].wrkinfo + "</li>";
					content += "</ul>";
					content += "</li>";
					wrkcnt = parseInt(data[i].wrkcnt);
				}
			} else {
					content += "<li class='scheduleInfo'>";
					content += data[i].schdlTitle;
					content += "<ul class='scheduleInfo_Detail'>";
					content += "<li>작업시간 : "+ data[i].schdlStartDt + "~" + data[i].schdlEndDt + "</li>";
					content += "<li>작업자 : "+ data[i].wrkinfo + "</li>";
					content += "</ul>";
					content += "</li>";
					wrkcnt += parseInt(data[i].wrkcnt);
			}
			prevSectorCd = data[i].areaSectorCd;
		}
		title += "(총 "+wrkcnt+"명)";
		content = "<ul>"+content+ "</ul>";
		sectorInfo += _utilString.makeCollapseItem("sectorInfo",targetId ,title ,content);

		$(".layer_generalInfo .box-body .sectorInfo").append(sectorInfo);
	}
}


function clearInformation(){
	$(".layer_generalInfo .maintitle").text("");
	$(".layer_generalInfo .mainbox .sectorInfo").empty();
}





function drawImageMap(data) {
	$("#imageMap").css("background-image", "url("+data[0].areaMapImage+")");
}

function clearImageMap(){
	$("#imageMap").css("background-image", "");
}


function clearBorderPolygon(){
	
	if (mapPolygonPath != null) {
		mapPolygonPath.setMap(null);
		mapPolygonPath = null;
	}
	if (mapPolygonPathSector != null) {
		removeSectorEvent();
		for (var i=0;i<mapPolygonPathSector.length;i++) {
			mapPolygonPathSector[i].setMap(null);
		}
		mapPolygonPathSector = null;
	}
	
}

function drawBorderPolygon(list) {
	clearBorderPolygon();
	
	if (list.length <= 0) return;
	
	var listLat = list[0].areaPointLat.split(",");
	var listLng = list[0].areaPointLng.split(",");
	var linePath = [];
	if (listLat.length > 2) {
		for (var i=0;i<listLat.length;i++) {
			linePath.push(new kakao.maps.LatLng(listLng[i],listLat[i]));
		}
	
		var polyline = new kakao.maps.Polygon({
			path: linePath,
			strokeWeight: 2,
			strokeColor: '#FFAE00',
			strokeOpacity: 0.7,
			strokeStyle: 'solid',
			fillColor: '#FFAE00', 
			fillOpacity: 0.4
		})
		polyline.setMap(map);
		mapPolygonPath = polyline;
	}
	
	mapPolygonPathSector = [];
	mapPolygonPathSectorInfo = [];
	var prevsector="";
	for (var j=0;j<list.length;j++) {
		if (list[j].areaSectorCd != "") {
			if (prevsector == list[j].areaSectorCd) continue;
			prevsector = list[j].areaSectorCd;
			var listLat = list[j].areaSectorPointLat.split(",");
			var listLng = list[j].areaSectorPointLng.split(",");
			var linePath = [];
			if (listLat.length > 2) {
				for (var i=0;i<listLat.length;i++) {
					linePath.push(new kakao.maps.LatLng(listLng[i],listLat[i]));
				}
				displayOneSector(linePath, list[j]);
			}
		}
	}
	
	var moveLatLon = new kakao.maps.LatLng(list[0].areaCenterLat,list[0].areaCenterLng);
	map.panTo(moveLatLon);
	
}


function setHandleMouseOver (polyline, title) {
	handle_sectorMouseOver = function(mouseEvent) {
		polyline.setOptions({ fillOpacity: '0.9' });
		polyline.setOptions({ strokeOpacity: '0.9' });
		customOverlay.setContent('<div class="sectorLayerInfo">' + title + '</div>');
		customOverlay.setPosition(mouseEvent.latLng);
		customOverlay.setMap(map);
	}
}

function setHandleMouseOut (polyline) {
	handle_sectorMouseOut = function(mouseEvent) {
		polyline.setOptions({ fillOpacity: '0.4' });
		polyline.setOptions({ strokeOpacity: '0.7' });
		customOverlay.setMap(null);
	}
}

function setHandleMouseMove () {
	handle_sectorMouseMove = function(mouseEvent) {
		customOverlay.setPosition(mouseEvent.latLng);
	}
}

function setHandlesectorClick (sector) {
	handle_sectorClick = function(mouseEvent) {
		displaySectorPopupInfo(sector);
	}
}




function displayOneSector(linepath, listinfo) {
				
	var polyline = new kakao.maps.Polygon({
		path: linepath,
		strokeWeight: 2,
		strokeColor: listinfo.areaSectorColor, //'#3c8dbc',
		strokeOpacity: 0.7,
		strokeStyle: 'solid',
		fillColor: listinfo.areaSectorColor, // '#a7c5fb', 
		fillOpacity: 0.4
	})
	polyline.setMap(map);
	mapPolygonPathSector.push(polyline);
	var oneinfo = {};
	oneinfo.areaSectorTitle = listinfo.areaSectorTitle;
	oneinfo.listLatLng = linepath[0];
	mapPolygonPathSectorInfo.push(oneinfo);
	
	var title = listinfo.areaSectorTitle;
	
	setHandleMouseOver (polyline, title);
	setHandleMouseOut (polyline);
	setHandleMouseMove ();
	setHandlesectorClick (listinfo.areaSectorCd);
	
	kakao.maps.event.addListener(polyline, 'mouseover', handle_sectorMouseOver);
	kakao.maps.event.addListener(polyline, 'mouseout', handle_sectorMouseOut);
	kakao.maps.event.addListener(polyline, 'mousemove', handle_sectorMouseMove);
	kakao.maps.event.addListener(polyline, 'click', handle_sectorClick);
	
}



function removeSectorEvent () {
	
	for (var i=0;i<mapPolygonPathSector.length;i++) {
		var polyline = mapPolygonPathSector[i];
		kakao.maps.event.addListener(polyline, 'mouseover', handle_sectorMouseOver);
		kakao.maps.event.addListener(polyline, 'mouseout', handle_sectorMouseOut);
		kakao.maps.event.addListener(polyline, 'mousemove', handle_sectorMouseMove);
		kakao.maps.event.addListener(polyline, 'click', handle_sectorClick);
	}
	
}


function fail(e) {
	console.log(e);
}



function drawMarkerWorker(data) {
	
	var infoContentTemplate = '<div style="padding:5px;">#{wrkNm}<br><span style="color:red">#{evtNm}</span></div>';
	
	for (var j=0;j<marker_Worker_Info.length;j++) {
		marker_Worker_Info[j].chked = false;
		if (marker_Worker_Info[j].chkcnt > 0) marker_Worker_Info[j].chkcnt--;
		if (marker_Worker_Info[j].infoWindow != null) {
			marker_Worker_Info[j].infoWindow.close();
			marker_Worker_Info[j].infoWindow = null;
		}
//console.log("A ==> "+marker_Worker_Info[j].dvcSN + " :: "+marker_Worker_Info[j].chkcnt);
	}
	
	for (var i=0; i<data.length; i++) {
		
		if (data[i].dvcSN == "") continue;
		
		var infoPosition = null;
		var infoWindow = null;
		var infoContent = null;
		
		var lat = data[i].lstGPSLat;
		var lng = data[i].lstGPSLng;
		var evtLvl = data[i].evtLvl;
		var evtNm = data[i].evtNm;
//		var evtCd = data[i].evtCd;
		
		var imgfn = "/img/common/human_blue.png";
		if (lat == "" || lng == "") {
			imgfn = "/img/common/human_black.png";
		} else {
			if (evtNm != "") {
				if (evtLvl >= emergencyLvl) {
					imgfn = "/img/common/human_red.png";
					infoPosition = new kakao.maps.LatLng(lat, lng);
					infoContent = infoContentTemplate.replace("#{wrkNm}",data[i].wrkNm).replace("#{evtNm}",evtNm);
					infoWindow = new kakao.maps.InfoWindow({
						position : infoPosition, 
						content : infoContent 
					});
				}
			}
		}
		
		var fidx = -1;
		for (var j=0;j<marker_Worker_Info.length;j++) {
			if (marker_Worker_Info[j].dvcSN == data[i].dvcSN) {
				fidx = j;
			}
		}
		
		var wrkinfo = "["+data[i].wrkNm+"]["+data[i].dvcSN+"]["+data[i].lstGPSDt+"]";
		var imageSize = new kakao.maps.Size(28, 28);
		var markerImage = new kakao.maps.MarkerImage(imgfn, imageSize); 
		var marker_position = new kakao.maps.LatLng(lat,lng);
		var marker = new kakao.maps.Marker({
			position: marker_position,
			title:wrkinfo,
			image: markerImage
		});
		marker.setMap(map);
		if (infoWindow != null) {
			infoWindow.open(map, marker); 
		}
		
		if (fidx != -1) {
			marker_Worker[fidx].setMap(null);
			
			marker_Worker[fidx] = marker;
			marker_Worker_Info[fidx].dvcSN = data[i].dvcSN;
			marker_Worker_Info[fidx].wrkNm = data[i].wrkNm;
			marker_Worker_Info[fidx].lstGPSLat = data[i].lstGPSLat;
			marker_Worker_Info[fidx].lstGPSLng = data[i].lstGPSLng;
			marker_Worker_Info[fidx].evtLvl = data[i].evtLvl;
			marker_Worker_Info[fidx].evtNm = data[i].evtNm;
			marker_Worker_Info[fidx].infoWindow = infoWindow;
			marker_Worker_Info[fidx].chked = true;
			
			if (evtNm != "") {
				marker_Worker_Info[fidx].chkcnt = maxWkInfoChkCnt;
			}
//console.log("B ==> "+marker_Worker_Info[fidx].dvcSN + " :: "+marker_Worker_Info[fidx].chkcnt);
			
		} else {
			marker_Worker.push(marker);
			var mkwkinfo = {};
			mkwkinfo.dvcSN = data[i].dvcSN;
			mkwkinfo.wrkNm = data[i].wrkNm;
			mkwkinfo.lstGPSLat = data[i].lstGPSLat;
			mkwkinfo.lstGPSLng = data[i].lstGPSLng;
			mkwkinfo.evtLvl = data[i].evtLvl;
			mkwkinfo.evtNm = data[i].evtNm;
			mkwkinfo.chked = true;
			mkwkinfo.chkcnt = maxWkInfoChkCnt;
			mkwkinfo.infoWindow = infoWindow;
			marker_Worker_Info.push(mkwkinfo);
//console.log("C ==> "+mkwkinfo.dvcSN + " :: "+mkwkinfo.chkcnt);
	
		}
	}

	for (var j=marker_Worker_Info.length-1;j>=0;j--) {
		var dvcSN = marker_Worker_Info[j].dvcSN;
		if ( (!marker_Worker_Info[j].chked && marker_Worker_Info[j].chkcnt < 1) || (marker_Worker_Info[j].chkcnt < 1)) {
//console.log("----"+marker_Worker_Info[j].chkcnt);
			
//			if (marker_Worker_Info[j].chked == false) {
//				marker_Worker[j].setMap(null);
//			}
			marker_Worker_Info.slice(j,1);
			
			$("#workerInfo #eventDvcSN_"+dvcSN).parent().remove();
			continue;
		}
		var obj = $("#workerInfo #eventDvcSN_"+dvcSN);
		if (marker_Worker_Info[j].evtNm != "" && (obj == null || obj.length == 0) ) {
			var title = "";
			title += marker_Worker_Info[j].wrkNm + " [" + marker_Worker_Info[j].evtNm + "]";
			$("#workerInfo").append(_utilString.makeCollapseItem("workerInfo","eventDvcSN_"+marker_Worker_Info[j].dvcSN ,title ,""));
		}
	}
	
}


function clearMarkerWorker(){
	if (marker_Worker == null || marker_Worker.length == 0) return;
	
	for (var i=-0;i<marker_Worker.length;i++) {
		marker_Worker[i].setMap(null);
	}
	maker_worker = null;
	maker_worker = [];
	marker_Worker_Info = null;
	marker_Worker_Info = [];
}


function displaySectorPopupInfo(sector) {
	$("#modalSectorInfo .infoContent").empty();
	$("#sector_"+sector+" .box-body ul").clone().appendTo("#modalSectorInfo .infoContent");
	$("#modalSectorInfo .modal").modal('show');
}




