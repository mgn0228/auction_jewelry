package kr.co.automart.jewelry.controller;

import NiceID.Check.CPClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.automart.jewelry.common.CommonUtilAES256;
import kr.co.automart.jewelry.common.SessionConstant;
import kr.co.automart.jewelry.service.NiceCertService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cert")
public class NiceCertController {

    private final String SITE_CODE;
    private final String SITE_PASSWORD;
    private final String returnUrlBase;
    private final String errorUrl;

    private final NiceCertService niceCertService;
    private final CommonUtilAES256 commAESUtil;


    public NiceCertController(
            @Value("${nice.cert.site-code}") String SITE_CODE,
            @Value("${nice.cert.site-password}") String SITE_PASSWORD,
            @Value("${nice.cert.return-url}") String returnUrlBase,
            @Value("${nice.cert.error-url}") String errorUrl,
            NiceCertService niceCertService,
            CommonUtilAES256 commAESUtil
    ) {
        this.SITE_CODE = SITE_CODE;
        this.SITE_PASSWORD = SITE_PASSWORD;
        this.returnUrlBase = returnUrlBase;
        this.errorUrl = errorUrl;
        this.niceCertService = niceCertService;
        this.commAESUtil = commAESUtil;
    }

    @ResponseBody
    @PostMapping("/request")
    public Map<String, String> requestNiceCert(@RequestBody Map<String, Object> row, HttpSession session) {
        CPClient niceCheck = new CPClient();
        String requestNumber = niceCheck.getRequestNO(SITE_CODE);

        // REQ_SEQ를 세션에 저장 (위변조 방지용)
        session.setAttribute("REQ_SEQ", requestNumber);

        String returnUrl = returnUrlBase + row.get("url");

        // 선택 항목들
        String sAuthType = "M";     // M: 휴대폰, C: 카드, X: 공인인증서, ""(빈 값): 기본 선택 화면
        String popgubun = "N";      // Y: 취소버튼 있음 / N: 없음
        String customize = "";      // "Mobile"이면 모바일전용 UI
        String sGender = "";        // "0": 여성, "1": 남성, ""(빈 값): 선택 안함

        String sPlainData =
                "7:REQ_SEQ"      + requestNumber.getBytes().length + ":" + requestNumber +
                "8:SITECODE"     + SITE_CODE.getBytes().length + ":" + SITE_CODE +
                "9:AUTH_TYPE"    + sAuthType.getBytes().length + ":" + sAuthType +
                "7:RTN_URL"      + returnUrl.getBytes().length + ":" + returnUrl +
                "7:ERR_URL"      + errorUrl.getBytes().length + ":" + errorUrl +
                "11:POPUP_GUBUN" + popgubun.getBytes().length + ":" + popgubun +
                "9:CUSTOMIZE"    + customize.getBytes().length + ":" + customize +
                "6:GENDER"       + sGender.getBytes().length + ":" + sGender;

        int result = niceCheck.fnEncode(SITE_CODE, SITE_PASSWORD, sPlainData);
        String encData = (result == 0) ? niceCheck.getCipherData() : "";
        return Map.of("encData", encData);
    }

    @RequestMapping(value = "/success", method = {RequestMethod.GET, RequestMethod.POST})
    public String handleSuccess(HttpServletRequest request, @RequestParam("EncodeData") String encodeData, Model model, HttpSession session) {
        CPClient niceCheck = new CPClient();
        int result = niceCheck.fnDecode(SITE_CODE, SITE_PASSWORD, encodeData);

        if (result == 0) {
            String plainData = niceCheck.getPlainData();
            HashMap<String, String> dataMap = niceCheck.fnParse(plainData);

            String reqSeq = dataMap.get("REQ_SEQ");
            String name = dataMap.get("NAME");
            String birth = dataMap.get("BIRTHDATE");
            String gender = dataMap.get("GENDER");
            String ci = dataMap.get("CI");
            String di = dataMap.get("DI");
            String phone = dataMap.get("MOBILE_NO");

            // 위변조 방지
             String sessionReqSeq = (String) request.getSession().getAttribute("REQ_SEQ");
             if (!reqSeq.equals(sessionReqSeq)) {
                 model.addAttribute("status", "tamper");
                 return "jewelry/etc/certComplete";
             }

            String sessionId = request.getSession().getId();
            Map<String, Object> row = new HashMap<>();
            row.put("sessionId", sessionId);
            row.put("mb_nm", name);
            row.put("mb_birthdate", birth);
            row.put("mb_gender", gender);
            row.put("ci", ci);
            row.put("di", di);
            row.put("phone", phone);

            // 임시 저장 (CI 중복체크하고 없으면 INSERT)
            Map<String, String> existMember = niceCertService.selectMemberByCI(row);
            if (existMember != null) {
                if(existMember.get("IS_REGISTERED").equals("Y")) {
                    // 이미 가입된 회원
                    model.addAttribute("status", "already");
                    return "jewelry/etc/certComplete";
                } else {
                    // 임시 저장된 회원
                    // session Id가 같은지 확인
                    if(existMember.get("SESSION_ID").equals(sessionId)) {
                        // 같으면 성공
                        model.addAttribute("memberInfo", row);
                        model.addAttribute("status", "success");
                        return "jewelry/etc/certComplete";
                    } else {
                        // 다르면 session id update
                        niceCertService.delTempMember(existMember.get("SESSION_ID"));
                    }
                }
            }

            // 신규 가입이면 임시 저장
            niceCertService.addMemberInfo(row);

            model.addAttribute("memberInfo", row);
            model.addAttribute("status", "success");
            return "jewelry/etc/certComplete";

        } else {
            model.addAttribute("status", "fail");
            return "jewelry/etc/certComplete";
        }
    }

    @RequestMapping(value = "/fail", method = {RequestMethod.GET, RequestMethod.POST})
    public String handleFail(Model model) {
        model.addAttribute("status", "fail");
        return "jewelry/etc/certComplete";
    }

    @ResponseBody
    @RequestMapping(value = "/join", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> completeJoin(@RequestPart(value = "key") Map<String, Object> row, HttpServletRequest request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");

        try {
            // 비밀번호 복호화 후 재암호화
            String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
            String pwd = row.get("memberPwd").toString();
            pwd = commAESUtil.decrypt(pwd, sc);
            pwd = " HEX(AES_ENCRYPT('" + pwd + "','" + SessionConstant.HKEY + "')) ";
            row.put("memberPwd", pwd);

            // 세션 ID 및 CI로 update
            String sessionId = request.getSession().getId();
            row.put("sessionId", sessionId);

            String ci = niceCertService.getCI(sessionId);
            row.put("ci", ci);

            // 업데이트 실행
            niceCertService.updateMemberInfo(row);

            result.put("result", true);
        } catch (Exception e) {
            result.put("result", false);
            return new ResponseEntity<>(result, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/checkDuplicateId", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> checkDuplicateId(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");

        String memberId = (String) body.get("memberId");
        boolean isDuplicate = niceCertService.checkDuplicateId(memberId);
        result.put("isDuplicate", isDuplicate);

        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/delTempMember", method = {RequestMethod.GET, RequestMethod.POST})
    public void delTempMember(HttpServletRequest request, HttpSession session) {
        String sessionId =  request.getSession().getId();
        int result = niceCertService.delTempMember(sessionId);
    }

    @RequestMapping(value = "/changePhone", method = {RequestMethod.GET, RequestMethod.POST})
    public String changePhone(HttpServletRequest request, @RequestParam("EncodeData") String encodeData, Model model, HttpSession session) {
        CPClient niceCheck = new CPClient();
        int result = niceCheck.fnDecode(SITE_CODE, SITE_PASSWORD, encodeData);

        if (result == 0) {
            String plainData = niceCheck.getPlainData();
            HashMap<String, String> dataMap = niceCheck.fnParse(plainData);

            String reqSeq = dataMap.get("REQ_SEQ");
            String ci = dataMap.get("CI");
            String phone = dataMap.get("MOBILE_NO");

            // 위변조 방지
            String sessionReqSeq = (String) request.getSession().getAttribute("REQ_SEQ");
            if (!reqSeq.equals(sessionReqSeq)) {
                model.addAttribute("status", "tamper");
                return "jewelry/etc/certComplete";
            }

            Map<String, Object> row = new HashMap<>();
            row.put("ci", ci);
            row.put("phone", phone);

            // 기존 정보 조회
            Map<String, String> memberInfo = niceCertService.selectMemberByCI(row);

            if(memberInfo != null){
                // 업데이트
                int uResult = niceCertService.changeMemberInfo(row);
                if(uResult == 0){
                    // 실패
                    model.addAttribute("status", "error");
                    return "jewelry/etc/certComplete";
                }
            } else {
                // 본인 인증 실패
                model.addAttribute("status", "fail");
                return "jewelry/etc/certComplete";
            }

            model.addAttribute("memberInfo", row);
            model.addAttribute("status", "success");
            return "jewelry/etc/certComplete";

        } else {
            model.addAttribute("status", "error");
            return "jewelry/etc/certComplete";
        }
    }

}
