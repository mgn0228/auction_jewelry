package kr.co.automart.jewelry.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.automart.jewelry.common.FileManager;
import kr.co.automart.jewelry.service.CommonCRUDService;
import kr.co.automart.jewelry.service.CommonService;




@RestController
public class CommonCRUDController {
	
	@Autowired
	private CommonService commService;
	
	@Autowired
	private CommonCRUDService commonCRUDService;

	@Autowired
	private FileManager fm;
	
	
	@ResponseBody
	@RequestMapping(value="/{pid}/list")
	public ResponseEntity<List<Map<String, Object>>> list(@PathVariable String pid, @RequestBody Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
		
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
//		if (sessionMemInfo.get("mbId") != null) {
			ret = commonCRUDService.getContentList(session, pid, row);
//		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<List<Map<String, Object>>>(ret, headers,HttpStatus.OK);
		
	}
	
	
	@ResponseBody
	@RequestMapping(value="/{pid}/one")
	public ResponseEntity<Map<String, Object>> one(@PathVariable String pid, @RequestBody Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
		
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
		Map<String, Object> ret = new HashMap<String, Object> ();
		
//		if (sessionMemInfo.get("mbId") != null) {
			ret = commonCRUDService.getOneContent(session, pid, row);
//		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
		
	}
	
	
	@ResponseBody
	@RequestMapping(value="/{pid}/insert", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> insert(@PathVariable String pid
			, @RequestPart(value = "key") Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
		
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
//		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//		MultiValueMap<String, MultipartFile>  mpmap = multipartRequest.getMultiFileMap();
		
		Map<String, Object> ret = new HashMap<String, Object>();
		
		
//		if (sessionMemInfo.get("mbId") != null) {
			ret = commonCRUDService.insertCommonCRUD(request, session, pid, row);
//		} else {
			
//		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
		
	}
	
	
	@ResponseBody
	@RequestMapping(value="/{pid}/update", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> update(@PathVariable String pid
			, @RequestPart(value = "key") Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
		
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
//		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//		MultiValueMap<String, MultipartFile>  mpmap = multipartRequest.getMultiFileMap();
		
		Map<String, Object> ret = new HashMap<String, Object>();
		
		
//		if (sessionMemInfo.get("mbId") != null) {
			ret = commonCRUDService.updateCommonCRUD(request, session, pid, row);
//		} else {
			
//		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
		
	}
	
	@ResponseBody
	@RequestMapping(value="/{pid}/delete", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> delete(@PathVariable String pid
			, @RequestBody Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
		
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
		Map<String, Object> ret = new HashMap<String, Object>();
		
		
//		if (sessionMemInfo.get("mbId") != null) {
			ret = commonCRUDService.deleteCommonCRUD(session, pid, row);
//		} else {
			
//		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
		
	}
	
	
	@ResponseBody
	@RequestMapping(value="/modal/{pid}")
	public ResponseEntity<Map<String, Object>> modallist(@PathVariable String pid, @RequestBody Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
		
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>> ();
//		List<Map<String, Object>> fieldinfo = new ArrayList<Map<String, Object>> ();
		Map<String, Object> ret = new HashMap<String, Object>();
		
//		if (sessionMemInfo.get("mbId") != null) {
			ret = commonCRUDService.getModalContentList(session, pid, row);
//			list = commonCRUDService.getFieldInfo(session, pid, row);
//			ret.put("list", list);
//			ret.put("field", fieldinfo);
//		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
		
	}
	
	
	
	
	@ResponseBody
	@RequestMapping(value="/download/{fnkey}")
	public ResponseEntity<?> fileDownLoad(@PathVariable String fnkey
			, HttpServletRequest request, Model model, HttpSession session) {
		
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		
/*		
		if (sessionMemInfo.get("mbId") == null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("content-type", "text/html; charset=UTF-8");
			String html = "<script language='javascript'>alert('권한이 없습니다.');</script>";
			return new ResponseEntity<>(html, headers,HttpStatus.OK);
		}
*/		
		Map<String, Object> getFileInfo = fm.getFileInfo(fnkey);
		
		if (getFileInfo == null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("content-type", "text/html; charset=UTF-8");
			String html = "<script language='javascript'>alert('자료가 없습니다.');</script>";
			return new ResponseEntity<>(html, headers,HttpStatus.OK);
		}
		
		String savefn="";
		try {
			savefn = URLEncoder.encode((String) getFileInfo.get("fn_target"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			HttpHeaders headers = new HttpHeaders();
			headers.add("content-type", "text/html; charset=UTF-8");
			String html = "<script language='javascript'>alert('자료가 없습니다.');</script>";
			return new ResponseEntity<>(html, headers,HttpStatus.OK);
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/octet-stream;charset=UTF-8;");
		headers.add("Content-disposition", "attachment;filename=" + savefn);
		return new ResponseEntity<>(getFileInfo.get("resource"), headers,HttpStatus.OK);
		
	}
	
	
}



