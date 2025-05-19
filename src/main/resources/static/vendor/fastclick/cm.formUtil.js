/*
formUtil javascript
version 1.0
*/


class cm_formUtil {
	
	getFormData(cls) {
		var data={};
		
		$(cls+" .form-control").each(function (index, el) {
			var nm = $(el).prop("name");
			var tag = $(el).prop("tagName");
			var tagType = $(el).prop("type");
			var val = $(el).val();
			if (tagType == "radio" || tagType == "checkbox") {
				val = "";
				if ($(el).prop("checked") == true) {
					val = $(el).val();
				}
			}
			data[nm] = val;
		});
		
		return data;
	}
	
	
}


