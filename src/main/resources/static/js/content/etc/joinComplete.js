window.addEventListener('DOMContentLoaded', () => {
    const allowedPath = "/memberInfo";
    if (!document.referrer.includes(allowedPath)) {
        alert("잘못된 접근 경로입니다.");
        location.href = "/";
        return;
    }

    const memberNm = document.getElementById("memberNm").value;
    const memberPwd = document.getElementById("memberPwd").value;
    if (!memberNm || !memberPwd) {
        alert("잘못된 접근 경로입니다.");
        location.href = "/agreeTerm";
    }
});