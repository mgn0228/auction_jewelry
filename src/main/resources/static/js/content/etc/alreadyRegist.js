window.addEventListener('DOMContentLoaded', () => {
    const allowedPath = "/agreeTerm";
    if (!document.referrer.includes(allowedPath)) {
        alert("잘못된 접근 경로입니다.");
        location.href = "/";
    }
});