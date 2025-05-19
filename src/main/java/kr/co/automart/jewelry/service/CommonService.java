package kr.co.automart.jewelry.service;

import java.util.Map;

import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public interface CommonService {
	
	public boolean	checkLogin(HttpServletRequest request, Model model);

	public void		initService(HttpSession session, Model model, String pid, HttpServletRequest request);
	
	public void SetSessionMemberInfo(HttpSession session ,Map<String, String> row);
	public Map<String, String> getSessionMemberInfo(HttpSession session);

	void ResetSessionMemberInfo(HttpSession session);

	Map<String,String> getMenu(HttpSession session, Model model, String pid);
	
	public void SetSessionMemberInfo4Guest(HttpSession session ,Map<String, String> row);
}
