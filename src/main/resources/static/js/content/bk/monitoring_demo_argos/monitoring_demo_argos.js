/*
monitoring_demo_argos/monitoring_demo_argos.js
*/


var ___playDataList = null;
var ___playIdx = -1;


var ___playRealTime = null;

var __interval_Realtime = null;
var __interval_Img = null;


var isRealTimePlaying = false;
var realTimelastIdx = -1;
var realTimeCnt = 0;
var realTimeSubCnt = 0;


var last_event_idx=-1; // 마지막 이벤트 발생 idx
var last_event_level=1; // normal, warning ,emergency
var sendMsgIdx = -1; // SMS 보낸 시점 idx
var first_event_idx = -1; // 이벤트 발생 시작 idx; 이벤트가 연속적으로 발생하는지 확인하기 위한 용도


var audioContent=null;

//var chartDataTL=[];

function private_function(pos, arg) {
	
	if (pos == "btnSearchCustom") {
		
		ClearViewImage();
		if (__interval_Realtime != null) {
			clearInterval(__interval_Realtime);
			__interval_Realtime = null;
		}		
		var data = {};
		var str = arg.T_sndTime;
		str = str.replaceAll(" - ","~").replaceAll("-","").replaceAll(":","").replaceAll(" ","");
		data.T_sndTime = str;
//		return null;
		return data;
	} else if (pos="afterInit") {
//		var data={};
//		_utilAjax.sendAsync("/argosRealTimeSendMsgEMS", _utilJson.make(_pid, data), argosRealTimeSendEMSSuccess, fail);
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


var chart_Img=null;
var chart_Snd = null;
var chart_Timeline = null;

function searchSuccess(data) {
	
	
	if (chart_Img != null) chart_Img.destroy();
	if (chart_Snd != null) chart_Snd.destroy();

	var len = data.length;
	
	var stdata_img={};
	stdata_img["정상"]=0;
	var stdata_snd={};
	stdata_snd["정상"]=0;
	
	$(".panel_statistic").removeClass("hidden");
	
	$(".panel_monitoring .event-list ul").empty();
	data.forEach(function (item) {
		var tmpstr = item.T_kind+ "("+item.A_kind+")";
		if (tmpstr == "()") {
			stdata_img["정상"]=stdata_img["정상"]+1;
			stdata_snd["정상"] = stdata_snd["정상"]+1;
			return;
		}
		var str = "<li class=\"list-group-item\">";
		str += "<b class=\"predData\" T_no=\""+item.no+"\" A_no=\""+item.A_no+"\">"+_utilString.makeDateTime4YMDHMSF(item.T_sndTime);
+"</b>";
		str += "<b class=\"pull-right\">";
		str += tmpstr; //item.T_kind + "("+item.A_kind+")";
		str += "</b>";
		str += "</li>";
		$(".panel_monitoring .event-list ul").append(str);
		
		
		if (item.T_kind != "") {
			if (stdata_img[item.T_kind] == undefined) {
				stdata_img[item.T_kind] = 1;
			} else {
				var cnt = stdata_img[item.T_kind];
				stdata_img[item.T_kind] = cnt + 1;
			}
		} else {
			stdata_img["정상"]=stdata_img["정상"]+1; // .st_normal = stdata_img.st_normal+1;
		}
		
		if (item.A_kind != "") {
			if (stdata_snd[item.A_kind] == undefined) {
				stdata_snd[item.A_kind] = 1;
			} else {
				var cnt = stdata_snd[item.A_kind];
				stdata_snd[item.A_kind] = cnt + 1;
			}
		} else {
			stdata_snd["정상"] = stdata_snd["정상"]+1; // .st_normal = stdata_snd.st_normal+1;
		}
		
	});
	
	
	var colors =_char_chartJS.getColorList();
	
	var chver = "2.9.4";
if (chver == "2.9.4") { //
	
	var coloridx = 0;
	var imgkey = Object.keys(stdata_img);
	var labels = [];
	var PieData = [];
	var onedata ={};
	onedata.data = [];
	onedata.backgroundColor=[];
	onedata.label="영상";
	for (var i=0;i<imgkey.length;i++) {
		onedata.data.push(stdata_img[imgkey[i]]);
		if (coloridx >= colors.length) coloridx = 0;
		onedata.backgroundColor.push(colors[coloridx]);
		coloridx++;
		var pc = parseInt(100*stdata_img[imgkey[i]] / len);
		labels.push(imgkey[i]+"("+pc+"%)");
	}
	PieData.datasets = [];
	PieData.datasets.push(onedata);
	PieData.labels = labels;
	
	var options = {title: {display: true} }
	chart_Img = _char_chartJS.makePieChart_V2_9_4("pieChart_img",PieData,null,options);
	
	
	var coloridx = 0;
	var sndkey = Object.keys(stdata_snd);
	var labels = [];
	var PieData = [];
	var onedata ={};
	onedata.data = [];
	onedata.backgroundColor=[];
	onedata.label="소리";
	for (var i=0;i<sndkey.length;i++) {
		onedata.data.push(stdata_snd[sndkey[i]]);
		if (coloridx >= colors.length) coloridx = 0;
		onedata.backgroundColor.push(colors[coloridx]);
		coloridx++;
		var pc = parseInt(100*stdata_snd[sndkey[i]] / len);
		labels.push(sndkey[i]+"("+pc+"%)");
	}
	PieData.datasets = [];
	PieData.datasets.push(onedata);
	PieData.labels = labels;
	
	var options = {title: {display: true} }
	chart_Snd = _char_chartJS.makePieChart_V2_9_4("pieChart_snd",PieData,null,options);
	
} else if (chver == "1.1") {
	
	var PieData_dataset = [];
	var PieData_label = [];
	var imgkey = Object.keys(stdata_img);
	var coloridx = 0;
	for (var i=0;i<imgkey.length;i++) {
		var onedata = {};
		onedata.value = stdata_img[imgkey[i]];
		if (coloridx >= colors.length) coloridx = 0;
		onedata.color = colors[coloridx];
		onedata.highlight = colors[coloridx];
		if (imgkey[i] == "st_normal") onedata.label = "정상";
		else onedata.label = imgkey[i];
		PieData_dataset.push(onedata);
		PieData_label.push[onedata.label];
		coloridx++;
	}
	var PieData={};
	PieData.datasets = PieData_dataset;
	PieData.labels = PieData_label
	chart_Img = _char_chartJS.makePieChart("#pieChart_img",PieData_dataset,null);
	
	var PieData_snd = [];
	imgkey = Object.keys(stdata_snd);
	coloridx = 0;
	for (var i=0;i<imgkey.length;i++) {
		var onedata = {};
		onedata.value = stdata_snd[imgkey[i]]; //parseInt(100 * (stdata_snd[imgkey[i]] / len ));
		if (coloridx >= colors.length) coloridx = 0;
		onedata.color = colors[coloridx];
		onedata.highlight = colors[coloridx];
		if (imgkey[i] == "st_normal") onedata.label = "정상";
		else onedata.label = imgkey[i];
//		onedata.label = imgkey[i];
		PieData_snd.push(onedata);
		coloridx++;
	}
	
	var options = {
		
	}
	
	chart_Snd = _char_chartJS.makePieChart("#pieChart_snd",PieData_snd,null);

}
		
		
		$(document).ready(function () {
			$(".event-list li").off("click");
			$(".event-list li").on("click",function () {
				playImageData(this);
			});
		});
}
	


function playImageData(liobj) {
	
	
	if (__interval_Img != null) {
		clearInterval(__interval_Img);
		___playDataList = null;
		___playIdx = -1;
	}

	var a_no = $(liobj).find(".predData").attr("a_no");
	var data={};
	data.a_no=a_no;
	data.tm_gap = 10;
	_utilAjax.sendAsync("/argosPlayList", _utilJson.make(_pid, data), playDataSuccess, fail);
	
}


var buf;

function playDataSuccess(data) {

	if (data.length == 0) {
		alert("자료가 부족합니다.");
		return;
	}
	
	
	$(".realtime").addClass("hidden");
	$(".progressbar").removeClass("hidden");
	$(".progressbar").css("width","0%");
	$(".progressbar").text("");
	
	__interval_Realtime = null;
	
	___playDataList = data;
	___playIdx = -1;
	var datalen = ___playDataList.length-1;
	
	var snd = ___playDataList[___playDataList.length-1].A_snd;
	$("#sndaudio").prop("src",snd);
	
	var audiourl = null;
	
	
	console.log(___playDataList.length);
	__interval_Img = setInterval(function() {
		if (___playIdx == -1) {
			$(".view-image").removeClass("hidden");
		}
		___playIdx++;
		console.log("==>"+___playIdx);
		
		if (___playIdx >= ___playDataList.length-1) {
			clearInterval(__interval_Img);
			___playDataList = null;
			___playIdx = -1;
			$(".realtime").removeClass("hidden");
			window.URL.revokeObjectURL(audiourl);
			__interval_Img = null;
			return;
		}
		
		$(".view-image").attr("src", "data:image/png;base64," + ___playDataList[___playIdx].T_img);
		var percent = 100*(___playIdx+1) / datalen;
		$(".progressbar").css("width",percent+"%");
	},200);
}


function fail(e) {
	console.log(e);
}




$(document).ready(function () {
	$(".panel_statistic").addClass("hidden");
	$(".panel_realtime_timeline").addClass("hidden");
	
	$("input[name=chkplayRealTime]").on("click",function () {
		//$(".panel_statistic").removeClass("hidden");
		$(".event-list ul").empty();
		playRealTime();
	});
});


function playRealTime() {
	
	var checked = $("input[name=chkplayRealTime]").prop("checked");
	
	if (__interval_Img != null) {
		clearInterval(__interval_Img);
		__interval_Img = null;
		ClearViewImage();
	}
	
	if (checked == true) {
		$(".panel_statistic").addClass("hidden");
		$(".panel_realtime_timeline").removeClass("hidden");
		
		var str = "<li class=\"list-group-item object-status-signal\">";
		str += "<span class=\"glyphicon glyphicon-alert signal-icon normal\"></span>";
		str += "</li>";
		str += "<li class=\"list-group-item object-status-info\">";
		str += "정상";
		str += "</li>";
		$(".panel_monitoring .event-list ul").append(str);
		
		
		realTimelastIdx = -1;
		isRealTimePlaying = true;
		$(".view-image").removeClass("hidden");
		$(".progressbar").removeClass("hidden");
		$(".progressbar").css("width","100%");
		$(".progressbar").text("");
		
		realTimeCnt = 0;
		realTimeSubCnt = 0;

		last_event_idx=-1;
//		last_event_level="normal"; // normal, warning ,emergency
		last_event_level=1; // normal, warning ,emergency


		getRealTimeImage();
		getTimeLineChart();
		__interval_Realtime = setInterval(function() {
			if (isRealTimePlaying == false) {
				clearInterval(__interval_Realtime);
				__interval_Realtime = null;
				return;
			}
			getRealTimeImage();
		},200);
		
	} else {
		isRealTimePlaying = false;
	}

}


function getRealTimeImage() {
	var data={};
	data.idx = realTimelastIdx;
	_utilAjax.sendAsync("/argosRealTimePlay", _utilJson.make(_pid, data), playRealTimeDataSuccess, fail);	
}




function playRealTimeDataSuccess(data) {
	
	realTimeSubCnt++;
	if (realTimeSubCnt > 25) { // 현재 이미지는 250ms마다 저장, realtime interval은 200ms로 초당 5번 call, 25번은 5초, 5초마다 time chart 갱신ㄴ 
		realTimeSubCnt = 0;
		realTimeCnt++;
		getTimeLineChart();
	}
	
	if (data.length == 0) {
		console.log("자료가 없습니다.");
		return;
	}
	
	realTimelastIdx = data[0].idx;
	if (data[0].T_img != null && data[0].T_img != "") {
		$(".view-image").attr("src", "data:image/png;base64," + data[0].T_img);
	}
	$(".progressbar").text(data[0].T_sndTime);
	
	var change = "f";
	if (data[0].decision != "normal") {
		if (last_event_idx == -1) { // 마지막 발생 이벤트 idx
			last_event_idx = realTimelastIdx;
			last_event_level = data[0].decisionLvl;
			change = "t";
			first_event_idx = realTimelastIdx; // 첫 이벤트 발생 시점 저장
		} else {
//			if ((realTimelastIdx-last_event_idx) > 26) { //마지막 발생 이벤트 이후 5초이상 경과후 이고, 이벤트 발생한 경우 ==> 실제 없은 경우 
//				last_event_idx = realTimelastIdx;
//				last_event_level = data[0].decisionLvl;
//				change = "t";
//			} else { // 5초 이내 이벤트 발생한 경우 이벤트 정보 갱신
				last_event_idx = realTimelastIdx;
				if (data[0].decisionLvl > last_event_level) {
					last_event_level = data[0].decisionLvl;
					change = "t";
				}
//			}
		}
	} else {
		if ((realTimelastIdx-last_event_idx) > 25) {
			last_event_idx = -1;
			last_event_level = 1;
			change = "t";
			first_event_idx = -1;
		}
	}
	
	
//var sendMsgIdx = -1; // SMS 보낸 시점 idx
//var first_event_idx = -1; // 이벤트 발생 시작 idx; 이벤트가 연속적으로 발생하는지 확인하기 위한 용도
	
	
	if (change == "t") {
		$(".signal-icon").removeClass("normal");
		$(".signal-icon").removeClass("warning");
		$(".signal-icon").removeClass("emergency");
		$(".signal-icon").addClass(data[0].decision);
		var txt = data[0].T_predictName;
		if (data[0].A_Info != "") {
			txt += " ("+data[0].A_Info.A_predictName+")";
		}
		$(".object-status-info").text(txt);
		
	}
	if (data[0].decision != "normal") {
		if (first_event_idx != -1 && realTimelastIdx - first_event_idx > 75) { // 15초 이상이면 SMS 전송
			if (sendMsgIdx == -1 || (realTimelastIdx-sendMsgIdx > 5 * 60 * 10)) {
				var data={};
				_utilAjax.sendAsync("/argosRealTimeSendMsgEMS", _utilJson.make(_pid, data), argosRealTimeSendEMSSuccess, fail);
				sendMsgIdx = realTimelastIdx;
			}
		}
	}
	
	
}

function argosRealTimeSendEMSSuccess() {
	console.log("SMS send success");
}


function ClearViewImage() {
	$(".view-image").addClass("hidden");
	$(".progressbar").addClass("hidden");
	$(".progressbar").css("width","100%");
	$(".progressbar").text("");

	$(".panel_statistic").addClass("hidden");
	$(".panel_realtime_timeline").addClass("hidden");
	
	
	
	$(".event-list ul").empty();
	
	var checked = $("input[name=chkplayRealTime]").prop("checked");
	if(checked == true) $("input[name=chkplayRealTime]").prop("checked",false);
}



function getTimeLineChart() {
	
	var data={};
	data.idx = realTimelastIdx;
	_utilAjax.sendAsync("/argosRealTimeChart", _utilJson.make(_pid, data), playRealTimeChartDataSuccess, fail);
	
}


function playRealTimeChartDataSuccess(data) {
	
	if (chart_Timeline != null) chart_Timeline.destroy();
	var colors =_char_chartJS.getColorList();
	var chver = "2.9.4";
	if (chver == "2.9.4") { //
		
		var LineData = {};
		LineData.labels = [];
		LineData.datasets = [];
		var OneData = {};
		OneData.data = [];
		OneData.label = "Status";
		
		for(var i=0;i<data.length;i++) {
			OneData.data.push(data[i].DecisionLvl);
			LineData.labels.push(_utilString.makeDateTime4HMS(data[i].T_sndTime)); // x축 제목
		}
		LineData.datasets.push(OneData);
		var options = {responsive: true,animation:{duration:0},scales:{yAxes:[{ticks:{beginAtZero:false, min:1, stepSize:1,max:3}}]} }
		chart_Timeline = _char_chartJS.makeLineChart_V2_9_4("timelineChart_event",LineData,null,options);
		
	}

}

