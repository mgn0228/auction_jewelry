package kr.co.automart.jewelry.service;

import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

//import java.util.Map;

//import org.springframework.ui.Model;

//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;

public interface HomeService {
	
	public void getDashBoardInfo(Model model, HttpSession session);

}
