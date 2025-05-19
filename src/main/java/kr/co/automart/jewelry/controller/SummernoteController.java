package kr.co.automart.jewelry.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.automart.jewelry.service.CommonCRUDService;
import kr.co.automart.jewelry.service.CommonService;
import kr.co.automart.jewelry.service.SummernoteService;

@RestController
public class SummernoteController {
	
	@Autowired
	CommonService commService;
	
	@Autowired
	SummernoteService summernoteService;

	@PostMapping("/summernoteImgInsert")
	public ResponseEntity<Map<String, Object>> insert(@RequestPart(value = "key") Map<String, Object> row
			, HttpServletRequest request, Model model, HttpSession session) {
		
		Map<String, String> sessionMemInfo = commService.getSessionMemberInfo(session);
		String pid = "/summernoteImgInsert";
		
//		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//		MultiValueMap<String, MultipartFile>  mpmap = multipartRequest.getMultiFileMap();
		
		Map<String, Object> ret = new HashMap<String, Object>();
		
		
		if (sessionMemInfo.get("mbId") != null) {
			ret = summernoteService.insertSummernoteImg(request, session, pid, row);
		} else {
			
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
		
	}
	
}
