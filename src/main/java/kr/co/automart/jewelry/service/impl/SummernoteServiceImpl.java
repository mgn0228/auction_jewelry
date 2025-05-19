package kr.co.automart.jewelry.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.automart.jewelry.common.FileManager;
import kr.co.automart.jewelry.common.Init_Lib_V1;
import kr.co.automart.jewelry.service.CommonService;
import kr.co.automart.jewelry.service.SummernoteService;

@Service
public class SummernoteServiceImpl implements SummernoteService {
	
	@Autowired
	private	Init_Lib_V1				initLib;
	
	@Autowired
	private	CommonService			commonService;
	
	@Autowired
	private FileManager				fileManger;

	@Override
	public Map<String, Object> insertSummernoteImg(HttpServletRequest request, HttpSession session, String pid,
			Map<String, Object> row) {

		Map<String, String> meminfo = commonService.getSessionMemberInfo(session);
		String gid = meminfo.get("mbGrp");
		String lvl = meminfo.get("mbLvl");
		String comCd = meminfo.get("comCd");
		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultiValueMap<String, MultipartFile>  mpmap = multipartRequest.getMultiFileMap();
		
//		Map<String, Object> newrow = fileManger.uploadFileSave(row, mpmap, session, comCd, pid);
		row = fileManger.uploadFileSave(row, mpmap, session, comCd, pid);
		
		return row;
	}

}
