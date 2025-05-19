package kr.co.automart.jewelry.service;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public interface SummernoteService {

	public Map<String, Object> insertSummernoteImg(HttpServletRequest request, HttpSession session, String pid, Map<String, Object> row);
	
}
