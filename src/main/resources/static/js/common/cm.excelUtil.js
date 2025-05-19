/*
excelUtil javascript
version 1.0
*/


class cm_excelUtil {
	
	downLoadCSVByDataTables(tbl, fn) {
		
		var csvstr = "";
		
//		var header  [];
		var tmp = tbl.columns().header();
		for (var i=0;i<tmp.length;i++) {
//			headers.push($(tmp[i]).text());
			var hd = $(tmp[i]).text();
			hd = hd.replaceAll(",","_");
			if (csvstr != "") csvstr += ",";
			csvstr += hd;
		}
		csvstr += "\n";
		var rows = tbl.rows().data();
		for (var i=0;i<rows.length;i++) {
			
			var rec = rows[i];
			var rowstr = "";
			for (var j=0;j<rec.length;j++) {
				if (rowstr != "") rowstr += ",";
				var rd = rec[j];
				rd = rd.replaceAll(",","_");
				rowstr += rd;
			}
			csvstr += rowstr + "\n";
		}
		
		var link = document.createElement("a");
		var blob = new Blob(["\uFEFF"+csvstr], {type: 'text/csv; charset=utf-8'});
		var url = URL.createObjectURL(blob);
		$(link).attr({"download" : fn , "href" : url});
		link.click();

	}
	
}

