package kr.co.automart.jewelry.service;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public interface CommonCRUDService {
	
	public List<Map<String, Object>> getContentList(HttpSession session, String pid, Map<String, Object> row);
	
	public Map<String, Object> getOneContent(HttpSession session, String pid, Map<String, Object> row);
	
	public Map<String, Object> insertCommonCRUD(HttpServletRequest request, HttpSession session, String pid, Map<String, Object> row);
	
	public Map<String, Object> updateCommonCRUD(HttpServletRequest request, HttpSession session, String pid, Map<String, Object> row);
	
	public Map<String, Object> deleteCommonCRUD(HttpSession session, String pid, Map<String, Object> row);
	
	public String makeFieldList4Form(List<Map<String, String>> dbSchemaInfo);
	public String makeWhere(List<Map<String, String>> dbSchemaInfo, Map<String,Object> row);
	
	public Map<String, Object> getModalContentList(HttpSession session, String pid, Map<String, Object> row);
	
	
}
