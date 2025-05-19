/*
analysisWorkArea/analysisWorkArea.js
*/

/*

var mapContainer=null;
var map = null;
var mapPolygonPath = null;
var mapPolygonPathSector = null;

// var mapPolygonPathParent = null;
var propStep=-1;
//var marker_center = null;


//var isSetForm = false;


var getRealTimeInterval = null;
var intervalTime = 3000;

var marker_Worker = null;
*/

var evtTypeCnt = 8;



var char_1 = null;
var char_2 = null;
var char_3 = null;
var char_4 = null;
var chver = "2.9.4";

var initSearch = false;

function private_function(pos, arg) {
	
	if (pos=="afterInit") {
		$(".viewtitle.chartA1").text("작업자별 발생현황(건수)");
		$(".viewtitle.chartA2").text("유형별 발생현황(건수)");
		$(".viewtitle.chartA3").text("작업자(%)");
		$(".viewtitle.chartA4").text("유형별(%)");
	} else if (pos=="btnSearchCustom") {
		getAnalysisData()
		return null;
	}
	
	
}



function getAnalysisData() {
	var data={};
	data.wrkGrpCd = $(".search-panel .form-control[name=wrkGrpCd]").val();
	data.searchType = $(".search-panel .form-control[name=searchType]").val();
	if (data.searchType == "") {
		alert("분석단위를 선택해주세요.");
		return;
	}
	var searchType = $(".search-panel .form-control[name=searchType]").val();
	var str = _utilString.makePeriodDateFormat($(".search-panel .form-control[name=searchPeriod]").val());
	data.searchStartDt = _utilString.getStartPeriodDateFormat(str);
	data.searchEndDt = _utilString.getEndPeriodDateFormat(str);
	
	_utilAjax.sendAsync("/analsysWRK_worker_"+searchType+"/list" ,_utilJson.make(_pid, data), analysisSt1CallBack, fail);
	_utilAjax.sendAsync("/analsysWRK_event_"+searchType+"/list" ,_utilJson.make(_pid, data), analysisSt2CallBack, fail);
	_utilAjax.sendAsync("/analsysWRK_worker_percent_"+searchType+"/list" ,_utilJson.make(_pid, data), analysisSt3CallBack, fail);
	_utilAjax.sendAsync("/analsysWRK_event_percent_"+searchType+"/list" ,_utilJson.make(_pid, data), analysisSt4CallBack, fail);
	
}


function fail() {
	console.log("");
}

function analysisSt1CallBack(data) {
	
	if (char_1 != null) char_1.destroy();
	$("#chart_Area_1_info").text("");
	if (data.length < 1) {
		$("#chart_Area_1_info").text("자료가 없습니다.");
		return;
	}
	
	var chartData = getLineChartData(data);
	if (chartData != null) {
		var options = { }
//		var options = {responsive: true,animation:{duration:0},scales:{yAxes:[{ticks:{beginAtZero:false, min:0, stepSize:100,max:10000}}]} }
//		char_1 = _char_chartJS.makeBarChart_V2_9_4("chart_Area_1",ChartData,null,options);
		char_1 = _char_chartJS.makeLineChart_V2_9_4("chart_Area_1",chartData,null,options);
	}

}

function getLineChartData(data) {
	if (chver == "2.9.4") {
		
		var prevYMD = false;
		var x_item = []; // year/month
		var wk_item = []; // worker
		var datasets_item = [];

		for (var i=0;i<data.length;i++) {
			if (prevYMD != data[i].snsrDt_YMD) x_item.push(data[i].snsrDt_YMD);
			prevYMD = data[i].snsrDt_YMD;
			if (!_utilString.existInJsonAry(wk_item,"wrkCd",data[i].wrkCd)) {
				var item={};
				item.wrkCd = data[i].wrkCd;
				item.wrkNm = data[i].wrkNm;
				wk_item.push(item);
			}
		}
		for (var i=0;i<wk_item.length;i++) {
			var dataitem = {};
			dataitem.data = [];
			dataitem.label = wk_item[i].wrkNm;
			dataitem.backgroundColor=getRandomColor("0");
			dataitem.borderColor=getRandomColor("0.7");
			
			datasets_item[wk_item[i].wrkNm] = dataitem;
		}
		
		for (var j=0;j<wk_item.length;j++) {
			for (var i=0;i<x_item.length;i++) {
				var value = 0;
				for (var k=0;k<data.length;k++) {
					if (data[k].snsrDt_YMD==x_item[i] && data[k].wrkCd == wk_item[j].wrkCd) {
						value += data[k].cnt;
					}
				}
				datasets_item[wk_item[j].wrkNm].data.push(value);
			}
		}
		
		var datasets = [];
		for (var i=0;i<wk_item.length;i++) {
			datasets.push(datasets_item[wk_item[i].wrkNm]);
		}
		
		var ChartData = {};
		ChartData.labels = x_item;
		ChartData.datasets = datasets;
		
		return ChartData;
	}
	
	return null;
	
}




function analysisSt2CallBack(data) {
	
	if (char_2 != null) char_2.destroy();
	$("#chart_Area_2_info").text("");
	if (data.length < 1) {
		$("#chart_Area_2_info").text("자료가 없습니다.");
		return;
	}
	
	var chartData = getLineChartDataEvent(data);
	if (chartData != null) {
		var options = { }
//		var options = {responsive: true,animation:{duration:0},scales:{yAxes:[{ticks:{beginAtZero:false, min:0, stepSize:100,max:10000}}]} }
//		char_2 = _char_chartJS.makeBarChart_V2_9_4("chart_Area_2",ChartData,null,options);
		char_2 = _char_chartJS.makeLineChart_V2_9_4("chart_Area_2",chartData,null,options);
	}

}


function getLineChartDataEvent(data) {
	if (chver == "2.9.4") {
		
		var legend_item = []; // event
		var opt = $(".search-panel .form-control[name=evtType] option");
		for (var i=0;i<opt.length;i++) {
			var oneitem = {};
			if ($(opt[i]).val() == "" || $(opt[i]).val() == "9") continue;
			oneitem.cd = $(opt[i]).val();
			oneitem.nm = $(opt[i]).text();
			legend_item.push(oneitem);
		}
		
		var prevYMD = false;
		var x_item = []; // year/month
		
		var datasets_item = [];

		for (var i=0;i<data.length;i++) {
			if (prevYMD != data[i].snsrDt_YMD) x_item.push(data[i].snsrDt_YMD);
			prevYMD = data[i].snsrDt_YMD;
		}
	
		for (var i=0;i<legend_item.length;i++) {
			var dataitem = {};
			dataitem.data = [];
			dataitem.label = legend_item[i].nm;
			dataitem.backgroundColor=getRandomColor("0");
			dataitem.borderColor=getRandomColor("0.7");
			
			datasets_item[legend_item[i].nm] = dataitem;
		}
		
		for (var j=0;j<legend_item.length;j++) {
			for (var i=0;i<x_item.length;i++) {
				var value = 0;
				for (var k=0;k<data.length;k++) {
//					if (data[k].snsrDt_YMD==x_item[i] && data[k].wrkCd == wk_item[j].wrkCd) {
					if (data[k].snsrDt_YMD==x_item[i]) {
						value += data[k]["evt_"+legend_item[j].cd];
					}
				}
				datasets_item[legend_item[j].nm].data.push(value);
			}
		}
		
		var datasets = [];
		for (var i=0;i<legend_item.length;i++) {
			datasets.push(datasets_item[legend_item[i].nm]);
		}
		
		var ChartData = {};
		ChartData.labels = x_item;
		ChartData.datasets = datasets;
		
		return ChartData;
	}
	
	return null;
	
}



function analysisSt3CallBack(data) {
	
	if (char_3 != null) char_3.destroy();
	$("#chart_Area_3_info").text("");
	if (data.length < 1) {
		$("#chart_Area_3_info").text("자료가 없습니다.");
		return;
	}
	
	var chartData = getPieChartDataPercent(data);
	if (chartData != null) {
		var options = {
			plugins:{
				datalabels:{
					color: 'black',
					anchor: 'end',
					clamp: true,
					clip: true,
					formatter: function(value, context) {
						let result = value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
						return result + '%'
					},
					display: function(context) {
						if (!isShowDatalabel) {
							return false
						} else {
							return true
						}
					}

				}
			}
		}
//		var options = {responsive: true,animation:{duration:0},scales:{yAxes:[{ticks:{beginAtZero:false, min:0, stepSize:100,max:10000}}]} }
//		char_3 = _char_chartJS.makeBarChart_V2_9_4("chart_Area_3",ChartData,null,options);
		char_3 = _char_chartJS.makePieChart_V2_9_4("chart_Area_3",chartData,null,options);
	}

}



function getPieChartDataPercent(data) {
	if (chver == "2.9.4") {
		
		var x_item = []; // worker label
		var wk_item = []; // worker
		var datasets_item = [];
		
		var totalValue=0;
		for (var i=0;i<data.length;i++) {
			if (!_utilString.existInJsonAry(wk_item,"wrkCd",data[i].wrkCd)) {
				var item={};
				item.wrkCd = data[i].wrkCd;
				item.wrkNm = data[i].wrkNm;
				wk_item.push(item);
				x_item.push(data[i].wrkNm);
			}
			totalValue += data[i].cnt;
		}
		
		var onedata = {};
		onedata.data=[];
		onedata.backgroundColor=[];
		onedata.borderColor=[];
		onedata.borderWidth = 1;
		
			for (var i=0;i<x_item.length;i++) {
				var value = 0;
				for (var k=0;k<data.length;k++) {
					if (data[k].wrkCd == wk_item[i].wrkCd) {
						value += data[k].cnt;
					}
				}
				value = Math.round(value*1000 / totalValue)/10;
				onedata.data.push(value);
				var color = getRandomColor("0.7");
				onedata.backgroundColor.push(color);
				onedata.borderColor.push(color);
			}
		
		datasets_item.push(onedata);
		
		var ChartData = {};
		ChartData.labels = x_item;
		ChartData.datasets = datasets_item;
		
		return ChartData;
	}
	
	return null;
	
}




function analysisSt4CallBack(data) {
	
	if (char_4 != null) char_4.destroy();
	$("#chart_Area_4_info").text("");
	if (data.length < 1) {
		$("#chart_Area_4_info").text("자료가 없습니다.");
		return;
	}
	
	var chartData = getPieChartDataEventPercent(data);
	if (chartData != null) {
		var options = {
			plugins:{
				datalabels:{
					color: 'black',
					anchor: 'end',
					clamp: true,
					clip: true,
					formatter: function(value, context) {
						let result = value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
						return result + '%'
					},
					display: function(context) {
						if (!isShowDatalabel) {
							return false
						} else {
							return true
						}
					}

				}
			}
		}
//		var options = {responsive: true,animation:{duration:0},scales:{yAxes:[{ticks:{beginAtZero:false, min:0, stepSize:100,max:10000}}]} }
//		char_4 = _char_chartJS.makeBarChart_V2_9_4("chart_Area_4",ChartData,null,options);
		char_4 = _char_chartJS.makePieChart_V2_9_4("chart_Area_4",chartData,null,options);
	}

}


function getPieChartDataEventPercent(data) {
	
	if (chver == "2.9.4") {
		
		var legend_item = []; // event
		var x_item = []; // year/month
		var opt = $(".search-panel .form-control[name=evtType] option");
		for (var i=0;i<opt.length;i++) {
			var oneitem = {};
			if ($(opt[i]).val() == "" || $(opt[i]).val() == "9") continue;
			oneitem.cd = $(opt[i]).val();
			oneitem.nm = $(opt[i]).text();
			legend_item.push(oneitem);
			x_item.push($(opt[i]).text());
		}
		
//		var prevYMD = false;
		
		var datasets_item = [];
		
		var totalValue=0;
		for (var i=0;i<data.length;i++) {
			for (var j=0;j<legend_item.length;j++) {
				totalValue += data[i]["evt_"+legend_item[j].cd];
			}
		}

		var onedata = {};
		onedata.data=[];
		onedata.backgroundColor=[];
		onedata.borderColor=[];
		onedata.borderWidth = 1;

		for (var j=0;j<legend_item.length;j++) {
				var value = 0;
				for (var k=0;k<data.length;k++) {
					value += data[k]["evt_"+legend_item[j].cd];
				}
				value = Math.round(value*1000 / totalValue)/10;
				onedata.data.push(value)
				var color = getRandomColor("0.7");
				onedata.backgroundColor.push(color);
				onedata.borderColor.push(color);
		}
		
		
		datasets_item.push(onedata);
		
		var ChartData = {};
		ChartData.labels = x_item;
		ChartData.datasets = datasets_item;
		
		return ChartData;
	}
	
	return null;
	
/*	
	
	if (chver == "2.9.4") {
		
		var x_item = []; // worker label
		var wk_item = []; // worker
		var datasets_item = [];
		
		
		var totalValue=0;
		for (var i=0;i<data.length;i++) {
			if (!_utilString.existInJsonAry(wk_item,"wrkCd",data[i].wrkCd)) {
				var item={};
				item.wrkCd = data[i].wrkCd;
				item.wrkNm = data[i].wrkNm;
				wk_item.push(item);
				x_item.push(data[i].wrkNm);
			}
			totalValue += data[i].cnt;
		}
		
		var onedata = {};
		onedata.data=[];
		onedata.backgroundColor=[];
		onedata.borderColor=[];
		onedata.borderWidth = 1;
		
//		for (var j=0;j<wk_item.length;j++) {
			for (var i=0;i<x_item.length;i++) {
				var value = 0;
				for (var k=0;k<data.length;k++) {
					if (data[k].wrkCd == wk_item[i].wrkCd) {
						value += data[k].cnt;
					}
				}
				value = parseInt(value*100 / totalValue);
				onedata.data.push(value);
				var color = getRandomColor("0.7");
				onedata.backgroundColor.push(color);
				onedata.borderColor.push(color);
			}
//		}
		
		datasets_item.push(onedata);
		var ChartData = {};
		ChartData.labels = x_item;
		ChartData.datasets = datasets_item;
		
		return ChartData;
	}
	
	return null;
	
*/	
	
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
		
		getRealTimeInfo();
		getRealTimeInterval = setInterval(function(){
			getRealTimeInfo();
		}, intervalTime);
		
	} else {
		clearInformation();
		clearBorderPolygon();
	}
	
}


function getRandomColor (opacity) {
	var RGB_1 = Math.floor(Math.random() * (255 + 1));
	var RGB_2 = Math.floor(Math.random() * (255 + 1));
	var RGB_3 = Math.floor(Math.random() * (255 + 1));
	var strRGBA = "rgba(" + RGB_1 + "," + RGB_2 + "," + RGB_3 + ","+opacity+")";
	return strRGBA;
}






