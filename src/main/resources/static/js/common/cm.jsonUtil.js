/*
jsonUtil javascript
version 1.0
*/


class cm_jsonUtil {
	
	make (pid, saveval){
		saveval.pid = pid;
		var jsonData = JSON.stringify(saveval);
		return jsonData;
	}
	
	parse (pid, saveval){
		saveval.pid = pid;
		var jsonData = JSON.parse(saveval);
		return jsonData;
	}
	
	defJson (){
		var def = {};
	}
	
}


