package kr.co.automart.jewelry.controller;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.automart.jewelry.common.SessionConstant;
import kr.co.automart.jewelry.service.CommonService;


@Controller
@RequestMapping("/company")
public class CompanyController {
	
	@Autowired
	private CommonService commService;
	
	@Autowired
	@Qualifier("sqlSession")
	private SqlSessionTemplate sqlSession;
	
	private	String			_pid = "";
	
	@RequestMapping(value="/reqNewCom")
	public String home(HttpServletRequest request, Model model, HttpSession session) {
		
		_pid = "reqNewCom";
		commService.initService(session,model,_pid, null);
		
		model.addAttribute("pid",_pid);
		return "regNewCompany";
	}
	
	
	@ResponseBody
	@RequestMapping(value="/newCompany")
	public ResponseEntity<Map<String, String>> newCompany(@RequestBody Map<String, String> row, HttpServletRequest request, Model model) {
		
		_pid = "newCompany";
		
		row.put("dbNm", SessionConstant.SP_DBNM);
		row.put("hkey", SessionConstant.HKEY);
		
		sqlSession.insert("company.ceateRequestNewCompany", row);
		String no = row.get("no");
		
		Map<String, String> ret = new HashMap<String, String> ();
		
		if (no == null) {
			ret.put("result", "FAIL");
		} else {
			ret.put("result", "OK");
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<Map<String, String>>(ret, headers,HttpStatus.OK);
		
	}
	
	
	
}



