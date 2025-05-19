const ajax = new cm_ajaxUtil();
const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;   // 비밀번호 정규표현식
let pwdFeedback;                                                    // 비밀번호 문구
let confirmFeedback;                                                // 비밀번호 확인 문구
let popupOpened = false;                                    // 팝업 open 여부

// 페이지 로드 시 적용
window.addEventListener('DOMContentLoaded', () => {
    // 진입 유효성 검사
    const name = document.querySelector('input[title="이름"]').value;
    const phone = document.querySelector('input[title="휴대폰번호 가운데자리"]').value;

    if (!name || !phone) {
        alert("잘못된 접근 경로입니다.");
        window.location.href = "/";
        return;
    }

    changeDomain();

    // UI 내용 초기화
    // pwdFeedback = document.querySelector('#pwdFeedback');
    // confirmFeedback = document.querySelector('#confirmFeedback');
    // confirmPwd();
});

window.addEventListener("message", function(event) {
    const status = event.data.status;
    const memberInfo = event.data.memberInfo;

    switch (status) {
        case "success":
            alert("휴대폰번호 변경이 완료되었습니다.");

            // 변경된 전화번호 입력
            const newPhone = memberInfo.phone;
            const phone1 = document.getElementById("phone1");
            const phone2 = document.getElementById("phone2");
            const phone3 = document.getElementById("phone3");
            phone1.value = newPhone.substring(0, 3);
            phone2.value = newPhone.substring(3, 7);
            phone3.value = newPhone.substring(7, 11);

            break;
        case "fail":
            // 인증 실패 또는 취소
            alert("본인 확인 실패.");
            break;
        case "tamper":
            // 위변조 요청
            alert("잘못된 접근 경로입니다.");
            break;
        default:
            // 기타 오류
            alert("휴대폰 번호 인증 과정에서 오류가 발생했습니다.");
    }
});

function openPass() {
    ajax.sendAsync("/cert/request", JSON.stringify({url: "changePhone"}), function(encData) {
        if (!encData) {
            return;
        }

        const form = document.form_chk;
        form.EncodeData.value = encData.encData;

        window.open('', 'popupChk',
            'width=500,height=600,top=100,left=100,fullscreen=no,menubar=no,' +
            'status=no,toolbar=no,titlebar=yes,location=no,scrollbars=no');

        form.action = "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb";
        form.target = "popupChk";
        form.submit();
    });
}

/**
 * 비밀번호 확인
 */
function confirmPwd() {
    // const pwdInput = document.querySelector('input[name="confirmPwd"]');
    // pwdInput.addEventListener('input', () => {
    //     const password = document.querySelector('input[name="newPwd"]').value;
    //     const confirmPassword = document.querySelector('input[name="confirmPwd"]').value;
    //
    //     if (!confirmPassword) {
    //         confirmFeedback.textContent = "";
    //         confirmFeedback.style.color = "#555";
    //     } else if (password !== confirmPassword) {
    //         confirmFeedback.textContent = "※ 비밀번호와 일치하지 않습니다.";
    //         confirmFeedback.style.color = "red";
    //     } else {
    //         confirmFeedback.textContent = "※ 비밀번호와 일치합니다.";
    //         confirmFeedback.style.color = "blue";
    //     }
    // });
}

function changeDomain() {
    const domainSelect = document.querySelector('select[name="domainSelect"]');
    const domainInput = document.querySelector('input[name="emailDomain"]');

    domainSelect.addEventListener('change', function () {
        const selectedValue = this.value;

        if (selectedValue === '') {
            domainInput.value = ''; // 직접입력이면 비우기
        } else {
            domainInput.value = selectedValue;
        }
    });
}

function changeMemberInfo() {
    // 유효성 검사
    // const password = document.querySelector('input[name="newPwd"]').value;
    // const confirmPassword = document.querySelector('input[name="confirmPwd"]').value;
    //
    // if (!password || !confirmPassword) {
    //     alert("필수 항목을 입력해주세요.");
    //     return;
    // }
    //
    // if (password !== confirmPassword) {
    //     alert("비밀번호가 일치하지 않습니다.");
    //     return;
    // }
    //
    // if (!pwRegex.test(password)) {
    //     alert("8자리 이상의 영문 + 숫자 혼합으로 구성하여야 합니다.");
    //     return;
    // }

    const emailId = document.querySelector('input[name="emailId"]').value;
    const emailDomain = document.querySelector('input[name="emailDomain"]').value;
    document.querySelector('input[name="email"]').value = emailId + '@' + emailDomain;

    let data = _utilForm.getMultipartFormData(".updateForm");
    ajax.sendMultiPartAsync("/account/changeMemberInfo", data, success, fail);
}

/**
 * 카카오 주소 api
 */
function searchAddress() {
    // 이미 열려있는 팝업이 있으면 return
    if (popupOpened) return;

    popupOpened = true;

    new daum.Postcode({
        oncomplete: function(data) {
            // 기본값 설정
            let addr = ''; // 최종 주소
            let extraAddr = ''; // 참고항목 (건물명 등)

            // 사용자가 도로명 주소 선택
            if (data.userSelectedType === 'R') {
                addr = data.roadAddress;

                // 참고항목 추가
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraAddr += data.bname;
                }

                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }

                if (extraAddr !== '') {
                    addr += ' (' + extraAddr + ')';
                }

            } else { // 지번 주소 선택
                addr = data.jibunAddress;
            }

            // 값 채워넣기
            document.querySelector('input[name="zipCode"]').value = data.zonecode;
            document.querySelector('input[name="addr"]').value = addr;

            // 상세주소 input에 포커스
            document.querySelector('input[name="addrDetail"]').focus();

            popupOpened = false;
        },
        onclose: function() {
            popupOpened = false;
        },
        width: 500,
        height: 600,
        top: 100,
        left: 100,
        // top: (window.screen.height / 2) - 230,
        // left: (window.screen.width / 2) - 250,
    }).open();
}

function cancelMember () {
    const id = document.querySelector('input[name="memberId"]').value;

    const param = {
        mbId : id,
    }

    ajax.sendAsync("/account/cancelMember", JSON.stringify(param), (res) => {
        if (res.result === "OK") {
            alert(res.message);
            location.href = "/";
        } else {
            alert(res.message);
        }
    }, (err) => {
        alert("회원 탈퇴 중 오류가 발생했습니다. 고객센터에 문의해주세요.");
    });
}

function success(res) {
    if (res.result === "OK") {
        alert("회원정보 변경이 완료되었습니다.");
        window.location.href = "/myinfo";
    } else {
        alert(res.message);
    }
}

function fail(e) {
    console.error(e);
    alert("정보 변경 중 오류가 발생했습니다.");
}