package kr.co.automart.jewelry.controller;

import java.util.HashMap;
import java.util.Map;

//import javax.servlet.http.HttpSession;

import org.mybatis.spring.SqlSessionTemplate;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
//import lombok.extern.slf4j.Slf4j;
import kr.co.automart.jewelry.common.CommonUtilAES256;
import kr.co.automart.jewelry.common.SessionConstant;
import kr.co.automart.jewelry.service.CommonService;

//@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private CommonService commService;

    @Autowired
    @Qualifier("sqlSession")
    private SqlSessionTemplate sqlSession;

//	private	String			_pid = "";

//	private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CommonUtilAES256 commAESUtil;


    @ResponseBody
    @RequestMapping(value = "/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> row
            , HttpServletRequest request, Model model, HttpServletResponse response, HttpSession session) {

        Map<String, String> ret = new HashMap<String, String>();

        String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
        String mbpwd = "";
        String comcd = "";
        String mbid = "";
        try {
            mbpwd = commAESUtil.decrypt(row.get("mbpwd"), sc);
            comcd = commAESUtil.decrypt(row.get("comcd"), sc);
            mbid = commAESUtil.decrypt(row.get("mbid"), sc);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret.put("result", "FAIL");
            ret.put("message", "정보가 정확하지 않습니다.");

            HttpHeaders headers = new HttpHeaders();
            headers.add("content-type", "application/json; charset=UTF-8");

            return new ResponseEntity<Map<String, String>>(ret, headers, HttpStatus.OK);
        }

        row.put("comcd", comcd);
        row.put("mbid", mbid);
        row.put("mbpwd", mbpwd);

        row.put("dbNm", SessionConstant.SP_DBNM);
        row.put("hkey", SessionConstant.HKEY);
        row.put("defaultComCd", SessionConstant.DEFAULT_COMCD);

        Map<String, String> meminfo = sqlSession.selectOne("account.login", row);

        if (meminfo == null) {
            ret.put("result", "FAIL");
            ret.put("message", "정보가 정확하지 않습니다.");

        } else {
            ret.put("result", "OK");

            row.put("no", String.valueOf(meminfo.get("no")));
            sqlSession.update("account.updateMemberInfo4Login", row);

//			HttpSession session = (HttpSession) request.getSession();
            commService.SetSessionMemberInfo(session, meminfo);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json; charset=UTF-8");

        return new ResponseEntity<Map<String, String>>(ret, headers, HttpStatus.OK);


    }


    @RequestMapping(value = "/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> row
            , HttpServletRequest request, Model model, HttpServletResponse response) {

//		_pid = "logout";
        Map<String, String> ret = new HashMap<String, String>();

        ret.put("result", "OK");

        HttpSession session = (HttpSession) request.getSession();
        commService.ResetSessionMemberInfo(session);

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json; charset=UTF-8");

        return new ResponseEntity<Map<String, String>>(ret, headers, HttpStatus.OK);

    }


    /**
     * 아이디 찾기 메서드
     */
    @ResponseBody
    @RequestMapping(value = "/findID")
    public ResponseEntity<Map<String, String>> findID(@RequestBody Map<String, String> row
            , HttpServletRequest request, Model model, HttpServletResponse response, HttpSession session) {
        Map<String, String> ret = new HashMap<>();

        try {
            Map<String, String> memberInfo = sqlSession.selectOne("account.findID", row);

            if (memberInfo == null) {
                ret.put("result", "FAIL");
                ret.put("message", "가입하신 회원 정보를 찾을 수 없습니다.");
            } else {
                String mbId = memberInfo.get("mbId");
                String mbNm = memberInfo.get("mbNm");
                String phone = memberInfo.get("phone");

                // 문자 전송

                ret.put("result", "OK");
                ret.put("message", "가입하신 번호로 ID가 전송되었습니다.");
            }
        } catch (Exception e) {
            ret.put("result", "FAIL");
            ret.put("message", "서버 오류가 발생했습니다. 고객센터에 문의해주세요.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json; charset=UTF-8");

        return new ResponseEntity<>(ret, headers, HttpStatus.OK);
    }


    /**
     * 비밀번호 찾기 메서드
     */
    @ResponseBody
    @RequestMapping(value = "/findPwd")
    public ResponseEntity<Map<String, String>> findPwd(@RequestBody Map<String, String> row
            , HttpServletRequest request, Model model, HttpServletResponse response, HttpSession session) {
        Map<String, String> ret = new HashMap<>();

        try {
            Map<String, String> memberInfo = sqlSession.selectOne("account.findPwd", row);

            if (memberInfo == null) {
                ret.put("result", "FAIL");
                ret.put("message", "가입하신 회원 정보를 찾을 수 없습니다.");
            } else {
                String mbId = memberInfo.get("mbId");
                String mbNm = memberInfo.get("mbNm");
                String phone = memberInfo.get("phone");

                // 비밀번호 초기화
                Map<String, String> updateMap = new HashMap<>();
                updateMap.put("mbId", mbId);
                updateMap.put("newPwd", "HEX(AES_ENCRYPT(SUBSTRING(REPLACE(UUID(), '-', ''), 1, 10), '" + SessionConstant.HKEY + "'))");

                int updateResult = sqlSession.update("account.changePwd", updateMap);
                if (updateResult == 0) {
                    ret.put("result", "FAIL");
                    ret.put("message", "비밀번호 초기화 중 오류가 발생했습니다.");
                } else {
                    // 초기화된 비밀번호 조회
                    row.put("hkey", SessionConstant.HKEY);
                    String tempPwd = sqlSession.selectOne("account.getPwdById", row);

                    // 문자 전송

                    ret.put("result", "OK");
                    ret.put("message", "가입하신 번호로 비밀번호가 전송되었습니다.");
                }
            }
        } catch (Exception e) {
            ret.put("result", "FAIL");
            ret.put("message", "서버 오류가 발생했습니다. 고객센터에 문의해주세요.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json; charset=UTF-8");

        return new ResponseEntity<>(ret, headers, HttpStatus.OK);
    }


    /**
     * 비밀번호 변경 메서드
     */
    @ResponseBody
    @RequestMapping(value = "/changePassword", method = {RequestMethod.POST})
    public ResponseEntity<Map<String, String>> changePwd(@RequestBody Map<String, String> row
            , HttpServletRequest request, Model model, HttpServletResponse response, HttpSession session) {
        Map<String, String> ret = new HashMap<>();

        String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
        String mbId = session.getAttribute(SessionConstant.SESSION_LID).toString();
        String currentPwd = "";
        String newPwd = "";

        try {
            currentPwd = commAESUtil.decrypt(row.get("currentPwd"), sc);
            newPwd = commAESUtil.decrypt(row.get("newPwd"), sc);
        } catch (Exception e) {
            ret.put("result", "FAIL");
            ret.put("message", "정보가 정확하지 않습니다.");

            HttpHeaders headers = new HttpHeaders();
            headers.add("content-type", "application/json; charset=UTF-8");

            return new ResponseEntity<>(ret, headers, HttpStatus.OK);
        }

        row.put("mbId", mbId);
        row.put("currentPwd", currentPwd);
        row.put("newPwd", newPwd);
        row.put("hkey", SessionConstant.HKEY);

        String dbPassword = sqlSession.selectOne("account.getPwdById", row);

        if (dbPassword == null) {
            ret.put("result", "FAIL");
            ret.put("message", "회원 정보를 찾을 수 없습니다.");
        } else {
            // 현재 비밀번호 일치하는지 확인
            if (!dbPassword.equals(currentPwd)) {
                ret.put("result", "FAIL");
                ret.put("message", "현재 비밀번호가 일치하지 않습니다.");
            } else {
                // 새 비밀번호 유효성 검증
                try {
                    validateNewPassword(mbId, newPwd);
                } catch (IllegalArgumentException e) {
                    ret.put("result", "FAIL");
                    ret.put("message", e.getMessage());

                    HttpHeaders headers = new HttpHeaders();
                    headers.add("content-type", "application/json; charset=UTF-8");
                    return new ResponseEntity<>(ret, headers, HttpStatus.OK);
                }

                // 새 비밀번호 암호화
                newPwd = "HEX(AES_ENCRYPT('" + newPwd + "','" + SessionConstant.HKEY + "'))";

                // 비밀번호 업데이트
                Map<String, String> updateMap = new HashMap<>();
                updateMap.put("mbId", mbId);
                updateMap.put("newPwd", newPwd);

                int updateResult = sqlSession.update("account.changePwd", updateMap);

                if (updateResult > 0) {
                    ret.put("result", "OK");
                    ret.put("message", "비밀번호가 성공적으로 변경되었습니다.");

                    // 비밀번호 변경 후 session 초기화
                    commService.ResetSessionMemberInfo(session);
                } else {
                    ret.put("result", "FAIL");
                    ret.put("message", "비밀번호 변경에 실패했습니다.");
                }
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json; charset=UTF-8");

        return new ResponseEntity<Map<String, String>>(ret, headers, HttpStatus.OK);
    }


    /**
     * 회원정보 변경 메서드
     */
    @ResponseBody
    @RequestMapping(value = "/changeMemberInfo", method = {RequestMethod.POST})
    public ResponseEntity<Map<String, String>> changeMemberInfo(@RequestPart(value = "key") Map<String, Object> row
            , HttpServletRequest request, Model model, HttpServletResponse response, HttpSession session) {
        Map<String, String> ret = new HashMap<>();

        // 비밀번호 확인 로직
        String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
        String mbId = session.getAttribute(SessionConstant.SESSION_LID).toString();
//        String currentPwd = "";
//        String newPwd = "";
//
//        try {
//            currentPwd = commAESUtil.decrypt((String) row.get("currentPwd"), sc);
//            newPwd = commAESUtil.decrypt((String) row.get("newPwd"), sc);
//        } catch (Exception e) {
//            ret.put("result", "FAIL");
//            ret.put("message", "정보가 정확하지 않습니다.");
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("content-type", "application/json; charset=UTF-8");
//
//            return new ResponseEntity<>(ret, headers, HttpStatus.OK);
//        }
//
//        row.put("mbId", mbId);
//        row.put("currentPwd", currentPwd);
//        row.put("newPwd", newPwd);
//        row.put("hkey", SessionConstant.HKEY);
//
//        String dbPassword = sqlSession.selectOne("account.getPwdById", row);
//
//        // 현재 비밀번호 일치하는지 확인
//        if (!dbPassword.equals(currentPwd)) {
//            ret.put("result", "FAIL");
//            ret.put("message", "현재 비밀번호가 일치하지 않습니다.");
//        } else {
//            // 새 비밀번호 유효성 검증
//            try {
//                validateNewPassword(mbId, newPwd);
//            } catch (IllegalArgumentException e) {
//                ret.put("result", "FAIL");
//                ret.put("message", e.getMessage());
//
//                HttpHeaders headers = new HttpHeaders();
//                headers.add("content-type", "application/json; charset=UTF-8");
//                return new ResponseEntity<>(ret, headers, HttpStatus.OK);
//            }
//
//            // 새 비밀번호 암호화
//            newPwd = "HEX(AES_ENCRYPT('" + newPwd + "','" + SessionConstant.HKEY + "'))";

            // 회원 정보 업데이트
            Map<String, String> updateMap = new HashMap<>();
            updateMap.put("memberId", mbId);
//            updateMap.put("newPwd", newPwd);
            updateMap.put("email", (String) row.get("email"));
            updateMap.put("agreeSms", (String) row.get("agreeSms"));
            updateMap.put("agreeEmail", (String) row.get("agreeEmail"));
            updateMap.put("zipCode", (String) row.get("zipCode"));
            updateMap.put("address", (String) row.get("addr"));
            updateMap.put("addressDetail", (String) row.get("addrDetail"));

            int updateResult = sqlSession.update("niceCert.changeMemberInfo", updateMap);

            if (updateResult > 0) {
                ret.put("result", "OK");
                ret.put("message", "회원 정보가 성공적으로 변경되었습니다.");
            } else {
                ret.put("result", "FAIL");
                ret.put("message", "회원 정보 변경에 실패했습니다.");
            }
//        }


        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json; charset=UTF-8");

        return new ResponseEntity<>(ret, headers, HttpStatus.OK);
    }


    /**
     * 회원 탈퇴 메서드
     */
    @ResponseBody
    @RequestMapping(value = "/cancelMember")
    public ResponseEntity<Map<String, String>> cancelMember(@RequestBody Map<String, String> row
            , HttpServletRequest request, Model model, HttpServletResponse response, HttpSession session) {
        Map<String, String> ret = new HashMap<>();

        try {
            int result = sqlSession.delete("account.cancelMember", row.get("mbId"));

            if(result > 0) {
                ret.put("result", "OK");
                ret.put("message", "회원 탈퇴가 정상적으로 완료되었습니다.");

                // 탈퇴 후 session 초기화
                commService.ResetSessionMemberInfo(session);
            } else {
                ret.put("result", "FAIL");
                ret.put("message", "회원 탈퇴 중 오류가 발생했습니다. 고객센터에 문의해주세요.");
            }
        } catch (Exception e) {
            ret.put("result", "FAIL");
            ret.put("message", "서버 오류가 발생했습니다. 고객센터에 문의해주세요.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json; charset=UTF-8");

        return new ResponseEntity<>(ret, headers, HttpStatus.OK);
    }


    /**
     * 비밀번호 유효성 체크
     */
    private void validateNewPassword(String mbId, String password) {
        // 1. 길이 체크 (8~10자)
        if (password.length() < 8 || password.length() > 10) {
            throw new IllegalArgumentException("비밀번호는 8자리 이상, 10자리 이하로 입력해주세요.");
        }

        // 2. 문자 + 숫자 조합 체크
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,10}$")) {
            throw new IllegalArgumentException("비밀번호는 문자와 숫자를 각각 최소 1개 이상 포함해야 합니다.");
        }

        // 3. 동일 문자/숫자 4번 이상 반복 체크
        int count = 1;
        for (int i = 1; i < password.length(); i++) {
            if (password.charAt(i) == password.charAt(i - 1)) {
                count++;
                if (count >= 4) {
                    throw new IllegalArgumentException("동일한 문자가 4번 이상 반복될 수 없습니다.");
                }
            } else {
                count = 1;
            }
        }

        // 4. 아이디와 동일 여부 체크
        if (password.equals(mbId)) {
            throw new IllegalArgumentException("비밀번호는 아이디와 같을 수 없습니다.");
        }
    }

}



