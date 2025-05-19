/*
system/home.js
*/

var chver = "2.9.4";


$(document).ready(function () {
	var data={};
	_utilAjax.sendAsync("/todayEventInfo/list" ,_utilJson.make(_pid, data), getTotalEventInfoCallBack, fail);

});
	

function getLineChartDataEvent(data) {
	if (chver == "2.9.4") {
		
		var legend_item = []; // event
		var opt = $("#evtType option");
		for (var i=0;i<opt.length;i++) {
			var oneitem = {};
			if ($(opt[i]).val() == "" || $(opt[i]).val() == "9") continue;
			oneitem.cd = $(opt[i]).val();
			oneitem.nm = $(opt[i]).text();
			legend_item.push(oneitem);
		}
		var x_item = [];
		for (var i=0;i<legend_item.length;i++) {
			x_item.push(legend_item[i].nm);
		}
		var datasets = [];
		var dataitem = {};
		dataitem.label = "";
		dataitem.data = [];
		for (var i=0;i<legend_item.length;i++) {
			if (data == null || data.length == 0) {
				dataitem.data[i] = 0;
			} else {
				dataitem.data[i] = data[0]["evt_"+legend_item[i].cd];
			}
		}
		dataitem.backgroundColor=getRandomColor("0");
		dataitem.borderColor=getRandomColor("0.7");
		datasets.push(dataitem);
		
		var ChartData = {};
		ChartData.labels = x_item;
		ChartData.datasets = datasets;
		
		return ChartData;
		
		
	}
	
	return null;
	
}


function getTotalEventInfoCallBack(ajax) {
	var data = ajax;
	var chartData = getLineChartDataEvent(data);
	if (chartData != null) {
		
		var options = {responsive: true,animation:{duration:0},legend:{display:false},scales:{yAxes:[{ticks:{beginAtZero:true, min:0}}]} }
//		char_4 = _char_chartJS.makeBarChart_V2_9_4("todayEventInfo",chartData,null,options);
		char_4 = _char_chartJS.makeLineChart_V2_9_4("todayEventInfo",chartData,null,options);
	}
}

function fail(e) {
	console.log(e);
}



function getRandomColor (opacity) {
	var RGB_1 = Math.floor(Math.random() * (255 + 1));
	var RGB_2 = Math.floor(Math.random() * (255 + 1));
	var RGB_3 = Math.floor(Math.random() * (255 + 1));
	var strRGBA = "rgba(" + RGB_1 + "," + RGB_2 + "," + RGB_3 + ","+opacity+")";
	return strRGBA;
}
