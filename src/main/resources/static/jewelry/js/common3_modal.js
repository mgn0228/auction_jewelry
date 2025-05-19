/*!
 * https://github.com/CreativeDream/jquery.modal
 * jQuery Modal reUpdate
 * Requires: jQuery v1.7.1 or later
 */
 /*!
 * 기본. 
 * center: true, //Center Modal Box?
 * autoclose: false, //true:자동 닫힘, Auto Close Modal Box?
 * closeClick: true, //false:모달영역밖 클릭방지, Close Modal on click near the box
 * closable: true, //If Modal is closable
 * theme: 'atlant', //Modal Custom Theme (xenon | atlant | reseted)
 * animate: false, //Slide animation
 * background: 'rgba(0,0,0,0.35)', //Background Color, it can be null
 * zIndex: 1050, //z-index
 * callback: function(result){alert(result); return true;}
 */


//--------------------------------------------------
// test..
//--------------------------------------------------
function confirmBox2(txt, callbackMethod, jsonData){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'confirm',
        title: '알림',
        text: txt,
        callback: function(result) {
            if(result){
				callbackMethod(jsonData);
            }
            else {
				return;
        	}
        }
    });
}

//--------------------------------------------------
// Use define functino
//--------------------------------------------------
function confirmBidRegist(mtitle, mtxt, callbackMethod, callbackMethod2, jsonData){
	mtxt = mtxt.replace(/\n/gi,"<br>");
    modal({
        type: 'confirm',
        title: mtitle,
		closeClick: false,
        text: mtxt,

		buttonText: {
			ok: 'OK',
			yes: ' 입찰신청 ',
			cancel: ' 입찰 취소 '
		},
        callback: function(result) {
//	console.log(result);
			if(result){
				callbackMethod(jsonData, jsonData2);
			}
			else {
				callbackMethod2(jsonData, jsonData2);
				//return;
			}
        }
    });
}

function confirmBoxBidRegist(txt, callbackMethod, jsonData){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'confirm',
        title: '입찰신청 최종결정',
		closeClick: false,
        text: txt,
		buttonText: {
			ok: 'OK',
			yes: ' 입찰신청 합니다 ',
			cancel: ' 입찰 취소 '
		},
        callback: function(result) {
			if(result){
				callbackMethod(jsonData);
			}
			else {
				return;
			}
        }
    });
}

function warningBoxFocus(txt, obj){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'warning',
        title: '주의',
		closeClick: false,
        text: txt,
        callback: function(result){
            obj.focus();
        },
        center: false
    });
}

function errorBoxBank(txt){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'error',
        title: '오류 안내',
		closeClick: false,
        text: txt
    });
}


//--------------------------------------------------
// used.. 
//--------------------------------------------------
function primaryBox(txt){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'primary',
        title: '주의',
        text: txt
    });
}
function errorBox(txt){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'error',
        title: '오류 안내',
		closeClick: false,
        text: txt
    });
}

function alertBox(txt, callbackMethod, jsonData){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'alert',
        title: '알림',
		closeClick: false,
        text: txt,
        callback: function(result){
            if(callbackMethod){
                callbackMethod(jsonData);
            }
        }
    });
}

function alertBoxFocus(txt, obj){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'alert',
        title: '알림',
		closeClick: false,
        text: txt,
        callback: function(result){
            obj.focus();
        }
    });
}

function confirmBox(txt, callbackMethod, jsonData){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'confirm',
        title: '선택',
		closeClick: false,
        text: txt,
        callback: function(result) {
            if(result){
				callbackMethod(jsonData);
            }
        }
    });
}

function promptBox(txt, callbackMethod, jsonData){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'prompt',
        title: '입력',
		closeClick: false,
        text: txt,
        callback: function(result) {
            if(result){
                callbackMethod(jsonData);
            }
        }
    });
}

function successBox(txt){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'success',
        title: '안내사항',
		closeClick: false,
        text: txt
    });
}

function warningBox(txt){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'warning',
        title: '주의사항',
		closeClick: false,
        text: txt,
        center: false
    });
}

function infoBox(txt){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'info',
        title: '알림사항',
        text: txt,
        autoclose: true
    });
}

function invertedBox(txt){
	txt = txt.replace(/\n/gi,"<br>");
    modal({
        type: 'inverted',
        title: '알림',
        text: txt
    });
}

