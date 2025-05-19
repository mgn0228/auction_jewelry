const regex = /^[A-Za-z0-9]{6,20}$/;                        // 아이디 정규표현식
const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;   // 비밀번호 정규표현식
let isIdChecked = false;                                    // 아이디 중복확인 여부
let idFeedback;                                                     // 아이디 문구
let pwdFeedback;                                                    // 비밀번호 문구
let confirmFeedback;                                                // 비밀번호 확인 문구
let popupOpened = false;                                    // 팝업 open 여부

// 페이지 로드 시 적용
window.addEventListener('DOMContentLoaded', () => {
    // 진입 유효성 검사
    checkValidation();

    changeDomain();

    // UI 내용 초기화
    idFeedback = document.querySelector('#idFeedback');
    pwdFeedback = document.querySelector('#pwdFeedback');
    confirmFeedback = document.querySelector('#confirmFeedback');
    resetIdChecked();
    confirmPwd();
});

/**
 * 아이디 입력 감지 → 중복확인 상태 초기화
 */
function resetIdChecked() {
    const idInput = document.querySelector('input[name="memberId"]');
    idInput.addEventListener('input', () => {
        isIdChecked = false;
        idFeedback.textContent = "※ 중복확인을 진행해 주세요.";
        idFeedback.style.color = "#555";
    });
}

/**
 * 비밀번호 확인
 */
function confirmPwd() {
    const pwdInput = document.querySelector('input[name="confirmPwd"]');
    pwdInput.addEventListener('input', () => {
        const password = document.querySelector('input[name="memberPwd"]').value;
        const confirmPassword = document.querySelector('input[name="confirmPwd"]').value;

        if (!confirmPassword) {
            confirmFeedback.textContent = "";
            confirmFeedback.style.color = "#555";
        } else if (password !== confirmPassword) {
            confirmFeedback.textContent = "※ 비밀번호와 일치하지 않습니다.";
            confirmFeedback.style.color = "red";
        } else {
            confirmFeedback.textContent = "※ 비밀번호와 일치합니다.";
            confirmFeedback.style.color = "blue";
        }
    });
}

/**
 * 유효성 검사
 */
function checkValidation() {
    const name = document.querySelector('input[title="이름"]').value;
    const phone = document.querySelector('input[title="휴대폰번호 가운데자리"]').value;
    const regDtStr = document.querySelector('input[name="regDt"]').value;
    const isRegister = document.querySelector('input[name="isRegister"]').value;

    if (!name || !phone) {
        alert("잘못된 접근 경로입니다.");
        location.href = "/agreeTerm";
        return;
    }

    const regDt = new Date(regDtStr);
    const now = new Date();
    const diffMinutes = (now - regDt) / (1000 * 60);
    if (diffMinutes > 5) {
        alert("인증 유효 시간이 초과되었습니다.");
        deleteAndRedirect();
        location.href = "/agreeTerm";
        return;
    }

    if (isRegister === 'Y') {
        alert("이미 가입 완료된 회원입니다.");
        location.href = "/";
        return;
    }
}

/**
 * 아이디 중복확인 처리
 */
function checkDuplicateId() {
    const idInput = document.querySelector('input[name="memberId"]');
    const memberId = idInput.value;

    if (!regex.test(memberId)) {
        alert("아이디를 확인해주시기 바랍니다.");
        idFeedback.textContent = "※ 아이디는 6자리 이상의 영문 또는 숫자로 등록하여야 합니다.";
        idFeedback.style.color = "red";
        return;
    }

    const ajax = new cm_ajaxUtil();
    ajax.sendAsync("/cert/checkDuplicateId", JSON.stringify({memberId}), (res) => {
        if (res.isDuplicate) {
            // 중복 아이디
            idFeedback.textContent = "※ 이미 사용중인 아이디입니다.";
            idFeedback.style.color = "red";
            isIdChecked = false;
        } else {
            // 없는 아이디
            idFeedback.textContent = "※ 사용 가능한 아이디입니다.";
            idFeedback.style.color = "blue";
            isIdChecked = true;
        }
    }, (err) => {
        alert("중복확인 중 오류가 발생했습니다.");
        console.error(err);
        location.href = "/agreeTerm";
    });
}

/**
 *
 */
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

function joinMember() {
    // 유효성 검사
    const memberId = document.querySelector('input[name="memberId"]').value;
    const password = document.querySelector('input[name="memberPwd"]').value;
    const confirmPassword = document.querySelector('input[name="confirmPwd"]').value;

    if (!isIdChecked) {
        alert("중복확인을 진행해 주세요.");
        return;
    }

    if (!memberId || !password || !confirmPassword) {
        alert("필수 항목을 입력해주세요.");
        return;
    }

    if (password !== confirmPassword) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
    }

    if (!pwRegex.test(password)) {
        alert("8자리 이상의 영문 + 숫자 혼합으로 구성하여야 합니다.");
        return;
    }

    checkValidation();

    const emailId = document.querySelector('input[name="emailId"]').value;
    const emailDomain = document.querySelector('input[name="emailDomain"]').value;
    document.querySelector('input[name="email"]').value = emailId + '@' + emailDomain;

    let data = _utilForm.getMultipartFormData(".joinForm");
    data.append("register", "Y");

    const ajax = new cm_ajaxUtil();
    ajax.sendMultiPartAsync("/cert/join", data, success, (err) => {
        alert("가입 중 오류가 발생했습니다.");
        console.error(err);
        location.href = "/agreeTerm";
    });
}

function deleteAndRedirect() {
    const ajax = new cm_ajaxUtil();
    ajax.sendAsync("/cert/delTempMember", JSON.stringify({}), (data) => {
    }, (err) => {
        console.error(err);
        location.href = "/agreeTerm";
    });
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
            document.querySelector('input[name="address"]').value = addr;

            // 상세주소 input에 포커스
            document.querySelector('input[name="addressDetail"]').focus();

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

function success(data) {
    location.href = "/joinComplete";
}

function fail(e) {
    console.error(e);
    location.href = "/agreeTerm";
}