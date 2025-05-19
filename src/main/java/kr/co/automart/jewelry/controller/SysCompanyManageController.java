package kr.co.automart.jewelry.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.automart.jewelry.common.SessionConstant;
import kr.co.automart.jewelry.service.CommonService;
import kr.co.automart.jewelry.service.SysCompanyManageService;




@RestController
public class SysCompanyManageController {
	
	@Autowired
	private CommonService commService;
	
	@Autowired
	private SysCompanyManageService service;
	
	
	@ResponseBody
	@RequestMapping(value="/sysCompanyManage/createComDB")
	public ResponseEntity<List<Map<String, Object>>> createComDB (
			@RequestBody Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
//		String pid="sysCompanyDB";
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		if (sessionMemInfo.get("mbId") != null) {
			if (!checkSystemManager(session)) {
				Map<String, String> retone = new HashMap<String, String>();
				retone.put("result", "Fail");
				retone.put("message", SessionConstant.ERROR_MSG_NOAUTH);
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("content-type", "application/json; charset=UTF-8");
				
				return new ResponseEntity<List<Map<String, Object>>>(ret, headers,HttpStatus.OK);
			}
			
		}
		
		ret = service.createComDB(session ,row);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<List<Map<String, Object>>>(ret, headers,HttpStatus.OK);
		
	}
	
	
	@ResponseBody
	@RequestMapping(value="/sysCompanyManage/dropComDB")
	public ResponseEntity<List<Map<String, Object>>> dropComDB (
			@RequestBody Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
//		String pid="sysCompanyDB";
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		if (sessionMemInfo.get("mbId") != null) {
			if (!checkSystemManager(session)) {
				Map<String, String> retone = new HashMap<String, String>();
				retone.put("result", "Fail");
				retone.put("message", SessionConstant.ERROR_MSG_NOAUTH);
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("content-type", "application/json; charset=UTF-8");
				
				return new ResponseEntity<List<Map<String, Object>>>(ret, headers,HttpStatus.OK);
			}
			
		}
		
		ret = service.dropComDB(session ,row);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<List<Map<String, Object>>>(ret, headers,HttpStatus.OK);
		
	}

	private boolean checkSystemManager(HttpSession session) {
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		if ( !(SessionConstant.SYSTEM_MANAGER_COMCODE).equals(sessionMemInfo.get("comCd")) 
			|| !(SessionConstant.SYSTEM_MANAGER_GROUP).equals(sessionMemInfo.get("mbGrp")) ) {
			return false;
		}
		
		return true;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value="/sysCompanyManage/createComTable")
	public ResponseEntity<List<Map<String, Object>>> createComTable (
			@RequestBody Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
//		String pid="sysCompanyTable";
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		if (sessionMemInfo.get("mbId") != null) {
			if (!checkSystemManager(session)) {
				Map<String, String> retone = new HashMap<String, String>();
				retone.put("result", "Fail");
				retone.put("message", SessionConstant.ERROR_MSG_NOAUTH);
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("content-type", "application/json; charset=UTF-8");
				
				return new ResponseEntity<List<Map<String, Object>>>(ret, headers,HttpStatus.OK);
			}
			
		}
		
		ret = service.createComDefaultTable(session ,row);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<List<Map<String, Object>>>(ret, headers,HttpStatus.OK);
		
	}
	
	
	
	
	@ResponseBody
	@RequestMapping(value="/sysCompanyManage/dropComTable")
	public ResponseEntity<List<Map<String, Object>>> dropComTable (
			@RequestBody Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
//		String pid="sysCompanyTable";
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		if (sessionMemInfo.get("mbId") != null) {
			if (!checkSystemManager(session)) {
				Map<String, String> retone = new HashMap<String, String>();
				retone.put("result", "Fail");
				retone.put("message", SessionConstant.ERROR_MSG_NOAUTH);
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("content-type", "application/json; charset=UTF-8");
				
				return new ResponseEntity<List<Map<String, Object>>>(ret, headers,HttpStatus.OK);
			}
			
		}
		
		ret = service.dropComTable(session ,row);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<List<Map<String, Object>>>(ret, headers,HttpStatus.OK);
		
	}
	
	
	
	
	@ResponseBody
	@RequestMapping(value="/sysCompanyTableField/insert", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> sysCompanyTableField_create (
			@RequestPart(value = "key") Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
		
//		String pid="sysCompanyTable";
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
		Map<String, Object> ret = new HashMap<String, Object> ();
		
		if (sessionMemInfo.get("mbId") != null) {
			if (!checkSystemManager(session)) {
				Map<String, String> retone = new HashMap<String, String>();
				retone.put("result", "Fail");
				retone.put("message", SessionConstant.ERROR_MSG_NOAUTH);
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("content-type", "application/json; charset=UTF-8");
				
				return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
			}
			
		}
		
		ret = service.createComField(session ,row);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
		
	}
	
	
	@ResponseBody
	@RequestMapping(value="/sysCompanyTableField/update", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> sysCompanyTableField_update (
			@RequestPart(value = "key") Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
		
//		String pid="sysCompanyTable";
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
		Map<String, Object> ret = new HashMap<String, Object> ();
		
		if (sessionMemInfo.get("mbId") != null) {
			if (!checkSystemManager(session)) {
				Map<String, String> retone = new HashMap<String, String>();
				retone.put("result", "Fail");
				retone.put("message", SessionConstant.ERROR_MSG_NOAUTH);
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("content-type", "application/json; charset=UTF-8");
				
				return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
			}
			
		}
		
		ret = service.updateComField(session ,row);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
		
	}
	
		
	
	@ResponseBody
	@RequestMapping(value="/sysCompanyTableField/delete", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> sysCompanyTableField_delete (
			@RequestBody Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
		
		
//		Map<String, String[]> ttt = request.getParameterMap();
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
		Map<String, Object> ret = new HashMap<String, Object> ();
		
		if (sessionMemInfo.get("mbId") != null) {
			if (!checkSystemManager(session)) {
				Map<String, String> retone = new HashMap<String, String>();
				retone.put("result", "Fail");
				retone.put("message", SessionConstant.ERROR_MSG_NOAUTH);
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("content-type", "application/json; charset=UTF-8");
				
				return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
			}
			
		}
		
		ret = service.deleteComField(session ,row);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
		
	}
	
	
	
	
	@ResponseBody
	@RequestMapping(value="/sysSetContentPage/createNewContentPID")
	public ResponseEntity<Map<String, Object>> createNewContentPID (
			@RequestBody Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
//		String pid="sysCompanyDB";
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
		Map<String, Object> ret = new HashMap<String, Object> ();
		
		if (sessionMemInfo.get("mbId") != null) {
			if (!checkSystemManager(session)) {
				Map<String, String> retone = new HashMap<String, String>();
				retone.put("result", "Fail");
				retone.put("message", SessionConstant.ERROR_MSG_NOAUTH);
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("content-type", "application/json; charset=UTF-8");
				
				return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
			}
			
		}
		
		ret = service.createNewContentPID(session ,row);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
		
	}
	
	
	
}



