/*
chart.chartJsUtil javascript
version 1.0
*/


class cm_charJSUtil {
	
	
	makePieChart(canvas,data,options) {
		var pieChartCanvas = $(canvas).get(0).getContext("2d");
		var pieChart = new Chart(pieChartCanvas);
		
		if (options == null || options == undefined) options = this.getDefaultOptions();
		pieChart.Doughnut(data, options);
		
		return pieChart;
	}
	
	
	getColorList() {
		return ["#f56954","#00a65a","#f39c12","#00c0ef","#3c8dbc","#d2d6de","#d2d6de"
				,"#3e95cd", "#8e5ea2","#3cba9f","#e8c3b9","#c45850"];
	}
	
	getDefaultOptions() {

		var pieOptions = {
			//Boolean - Whether we should show a stroke on each segment
			segmentShowStroke: true,
			//String - The colour of each segment stroke
			segmentStrokeColor: "#fff",
			//Number - The width of each segment stroke
			segmentStrokeWidth: 2,
			//Number - The percentage of the chart that we cut out of the middle
			percentageInnerCutout: 50, // This is 0 for Pie charts
			//Number - Amount of animation steps
			animationSteps: 100,
			//String - Animation easing effect
			animationEasing: "easeOutBounce",
			//Boolean - Whether we animate the rotation of the Doughnut
			animateRotate: true,
			//Boolean - Whether we animate scaling the Doughnut from the centre
			animateScale: false,
			//Boolean - whether to make the chart responsive to window resizing
			responsive: true,
			// Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
			maintainAspectRatio: true,
			//String - A legend template
			legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<segments.length; i++){%><li><span style=\"background-color:<%=segments[i].fillColor%>\"></span><%if(segments[i].label){%><%=segments[i].label%><%}%></li><%}%></ul>"
			
			,plugins: {
				datalabels: {
					color: 'white',
					display: function(value,context) {
						return context.dataset.data.labels[context.dataIndex] + "("+value+")";
					},
					font: {
						weight: 'bold'
					},
					formatter: Math.round
				}
			}




		};
		return pieOptions;

	}
	
	
		
	makeRadarChart_V2_9_4(canvas,data,label,options) {
		
		if (options == null || options == undefined) options = {};
		
		var chart = new Chart(document.getElementById(canvas), {
			type: "radar",
			data: data,
//			label: label,
			options:options
		});
		
		return chart;
	}
	
	
		
	makePieChart_V2_9_4(canvas,data,label,options) {
		
		if (options == null || options == undefined) options = {};
		
		var chart = new Chart(document.getElementById(canvas), {
			type: "pie",
			data: data,
//			label: label,
			options:options
		});
		
		return chart;
	}
		
		
	makeLineChart_V2_9_4(canvas,data,label,options) {
		
		if (options == null || options == undefined) options = {};
		
		var chart = new Chart(document.getElementById(canvas), {
			type: "line",
			data: data,
			options:options
		});
		return chart;
	}

		
	makeBarChart_V2_9_4(canvas,data,label,options) {
		
		if (options == null || options == undefined) options = {};
		
		var chart = new Chart(document.getElementById(canvas), {
			type: "bar",
			data: data,
			options:options
		});
		return chart;
	}



}


