/*
workAreaSector/workAreaSector.js
*/

var mapContainer=null;
var map = null;
var mapPolygonPath = null;
var mapPolygonPathParent = null;
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
		resetMapEvent(map);
		
		var html = " <select class='selectMapAction'><option value=''>동작선택</option><option value='point'>경계선</option></select>";
		$("#mapArea").parent().find("label").append(html);
		
		$("#areaType").on("change",function () {
			
			
			$("#areaMapImage").parent().parent().addClass("hidden");
//			$("#parentareaMapImage").parent().parent().addClass("hidden");
			$("#mapArea").parent().parent().addClass("hidden");
			
			if ($("#areaType").val() != "11") {
				$("#mapArea").parent().parent().addClass("hidden");
				if (isSetForm == false) {
					$("#areaPointLng").empty();
					$("#areaPointLat").empty();
					$("#areaPointLatLng").empty();
				}

				clearBorderPolygon();
				
			}
			
			if ($("#areaType").val() != "1") {
				$("#areaMapImage").parent().parent().addClass("hidden");
//				$("#parentareaMapImage").parent().parent().addClass("hidden");
			}
			
			if ($("#areaType").val() == "1") {
				$("#mapArea").parent().parent().removeClass("hidden");
			} else if ($("#areaType").val() == "11") {
				$("#areaMapImage").parent().parent().removeClass("hidden");
//				$("#parentareaMapImage").parent().parent().removeClass("hidden");
			}
			
		});
		
		
		if ($("#areaType").val() != "11") {
			$("#mapArea").parent().parent().addClass("hidden");
//			clearBorderPolygon();
		}
		
		if ($("#areaType").val() != "1") {
			$("#areaMapImage").parent().parent().addClass("hidden");
//			$("#parentareaMapImage").parent().parent().addClass("hidden");
			
		}
		
		if ($("#areaType").val() == "1") {
			$("#mapArea").parent().parent().removeClass("hidden");
		} else if ($("#areaType").val() == "11") {
			$("#areaMapImage").parent().parent().removeClass("hidden");
//			$("#parentareaMapImage").parent().parent().removeClass("hidden");
		}
		
		
		
/*		
		$("#areaCenterLng").on("change",function () {
			clearMarker();
			drawMarker();
		});
*/		
		
		
		
		$("#areaType").trigger("change");
		
	} else if (pos=="setFormDataBefore") {
		isSetForm = true;
		setTimeout(function() {isSetForm = false;},1000);
	} else if (pos=="setFormDataAfter") {
		
		isSetForm = true;
		
		var opts = $(".form-control[name=areaPointLatLng] option");
		for (var i=0;i<opts.length;i++) {
			var otpvals = $(opts[i]).val().split("/");
			$(opts[i]).attr("lng",otpvals[1]);
			$(opts[i]).attr("lat",otpvals[0]);
		}
		
		drawBorderPolygon();
//		clearBorderPolygonParent();
		drawBorderPolygonParent();

//		drawMarker();
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



function resetMapEvent(map) {
	
	var marker = new kakao.maps.Marker({ 
		// 지도 중심좌표에 마커를 생성합니다 
		position: map.getCenter() 
	}); 
	marker.setMap(map);
	
	kakao.maps.event.addListener(map, 'dblclick', function(mouseEvent) {
		var latlng = mouseEvent.latLng;
		marker.setPosition(latlng);
//		alert('double click! ' + latlng.toString());
		dataSetToTarget(latlng);
		
	});
}


function dataSetToTarget(latlng) {
	
	
	
//	var target = $(".selectMapAction").val();
	
	
	var lat = latlng.Ma;
	var lng = latlng.La;
	
/*	
	
	if (target == "center") {
		$(".form-control[name=areaCenterLat]").val(lat);
		$(".form-control[name=areaCenterLng]").val(lng);
		
		drawMarker();
		
	} else if (target == "point") {
		
*/		
		
		var optstr = "<option lat='"+lat+"' lng='"+lng+"' value='"+lat+"/"+lng+"'>"+lat+"/"+lng+"</option>";
		$(".form-control[name=areaPointLatLng]").append(optstr);
		var optstr = "<option value='"+lat+"'>"+lat+"</option>";
		$(".form-control[name=areaPointLat]").append(optstr);
		var optstr = "<option value='"+lng+"'>"+lng+"</option>";
		$(".form-control[name=areaPointLng]").append(optstr);
		
		drawBorderPolygon();
		
/*		
		
	}
	
*/	

}

function drawBorderPolygon() {
	clearBorderPolygon();
	var linePath = [];
	var opts = $(".form-control[name=areaPointLatLng] option");
	if (opts.length == 1) {
//		clearBorderPolygon();
		return;
	}
	for (var i=0;i<opts.length;i++) {
		var lng = $(opts[i]).attr("lng");
		var lat = $(opts[i]).attr("lat");
		linePath.push(new kakao.maps.LatLng(lat,lng));
	}
	if (opts.length > 0) {
		linePath.push(new kakao.maps.LatLng($(opts[0]).attr("lat"),$(opts[0]).attr("lng")));

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
	
}

function clearBorderPolygon(){
	
	if (mapPolygonPath == null) return;
	mapPolygonPath.setMap(null);
//	mapPolygonPath = null;
}





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





function drawBorderPolygonParent() {
	clearBorderPolygonParent();
	var linePath = [];
	
	var Lngopts = $(".form-control[name=parentareaPointLng] option");
	var Latopts = $(".form-control[name=parentareaPointLat] option");
	if (Lngopts.length == 1) {
		return;
	}
	for (var i=0;i<Lngopts.length;i++) {
		var lng = $(Lngopts[i]).val();
		var lat = $(Latopts[i]).val();
		linePath.push(new kakao.maps.LatLng(lat,lng));
	}
	if (Lngopts.length > 0) {
		linePath.push(new kakao.maps.LatLng($(Latopts[0]).val(),$(Lngopts[0]).val()));
		
		
		var moveLatLon = new kakao.maps.LatLng($(Latopts[0]).val(),$(Lngopts[0]).val());
		map.panTo(moveLatLon);
		
		
		var polyline = new kakao.maps.Polyline({
			path: linePath,
			strokeWeight: 5,
			strokeColor: '#F1D0CC',
			strokeOpacity: 0.7,
			strokeStyle: 'solid'
		})
		polyline.setMap(map);
		mapPolygonPathParent = polyline;
	}
	
}

function clearBorderPolygonParent(){
	
	if (mapPolygonPathParent == null) return;
	mapPolygonPathParent.setMap(null);
//	mapPolygonPathParent = null;
}


/*

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


