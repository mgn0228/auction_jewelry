const ajax = new cm_ajaxUtil();

function changePwd() {
    const currentPwd = document.getElementById("currentPwd").value;
    const newPwd = document.getElementById("newPwd").value;
    const newPwdConfirm = document.getElementById("newPwdConfirm").value;

    if(currentPwd === "" || newPwd === "" || newPwdConfirm === "") {
        alert("모든 칸에 정보를 입력해주세요.");
        return;
    }

    // 새 비밀번호와 새 비밀번호 확인 일치 체크
    if (newPwd !== newPwdConfirm) {
        alert("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        return;
    }

    const param = {
        currentPwd : _utilString.makeCryptoJSEncrypt(_sid, currentPwd),
        newPwd : _utilString.makeCryptoJSEncrypt(_sid, newPwd)
    }

    ajax.sendAsync("/account/changePassword", JSON.stringify(param), (res) => {
        if (res.result === "OK") {
            alert("비밀번호 변경이 완료되었습니다.");
            location.href = "/login";
        } else {
            alert(res.message);
        }
    }, (err) => {
        alert("비밀번호 변경 중 오류가 발생했습니다.");
        // location.href = "/home";
    });
}