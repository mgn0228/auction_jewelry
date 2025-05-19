/*
kakaoMapUtil javascript
version 1.0
*/


class cm_kakaoMapUtil {
	
	constructor () {
		this.container = null;
		this.markerGrp = [];
		this.markerGrpNm = [];
		this.markerGrpImg = [];
	}

	makeContainer(oid) {
		this.container = document.getElementById(oid);
		return this.container;
	}
	
}

