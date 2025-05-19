package kr.co.automart.jewelry.service;

import java.util.List;
import java.util.Map;

//import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public interface SysCompanyManageService {
	
	public List<Map<String, Object>> createComDB(HttpSession session, Map<String, Object> row);
	public List<Map<String, Object>> dropComDB(HttpSession session, Map<String, Object> row);
	
	public List<Map<String, Object>> createComDefaultTable(HttpSession session, Map<String, Object> row);
	public List<Map<String, Object>> dropComTable(HttpSession session, Map<String, Object> row);
	
	public Map<String, Object> createComField(HttpSession session, Map<String, Object> row);
	public Map<String, Object> updateComField(HttpSession session, Map<String, Object> row);
	public Map<String, Object> deleteComField(HttpSession session, Map<String, Object> row);
	
	public Map<String, Object> createNewContentPID(HttpSession session, Map<String, Object> row);
	
	
}
