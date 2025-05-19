package kr.co.automart.jewelry.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.automart.jewelry.service.CommonService;
import kr.co.automart.jewelry.service.ExcelService;

@RestController
public class ExcelController {
	
	@Autowired
	CommonService commService;
	
	@Autowired
	ExcelService excelService;

	@PostMapping("/shopOrderExcel/insert")
	public ResponseEntity<Map<String, Object>> isnertExcel(Map<String, Object> param, MultipartFile excelFile,HttpServletRequest request, Model model, HttpSession session) throws IOException {
		
		Map<String, Object> ret = excelService.excelInsert(excelFile);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json; charset=UTF-8");
		
		return new ResponseEntity<Map<String, Object>>(ret, headers,HttpStatus.OK);
	}
	
}
