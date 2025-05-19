const ajax = new cm_ajaxUtil();

function checkPWDExist() {
    const id = document.getElementById("id").value;
    const name = document.getElementById("name").value;
    const phone1 = document.getElementById("phone1").value;
    const phone2 = document.getElementById("phone2").value;
    const phone3 = document.getElementById("phone3").value;

    if(id === "" || name === "" || phone1 === "" || phone2 === "" || phone3 === "") {
        alert("모든 칸에 정보를 입력해주세요.");
        return;
    }

    const param = {
        mbId : id,
        name : name,
        phone : phone1 + phone2 + phone3
    }

    ajax.sendAsync("/account/findPwd", JSON.stringify(param), (res) => {
        if (res.result === "OK") {
            alert(res.message);
            location.href = "/login";
        } else {
            alert(res.message);
        }
    }, (err) => {
        alert("비밀번호 확인 중 오류가 발생했습니다.");
        location.href = "/home";
    });
}