package kr.co.automart.jewelry.service.impl;

//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

//import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.automart.jewelry.service.HomeService;

//@Slf4j
@Service
public class HomeServiceImpl implements HomeService {
	
	
	@Autowired
	@Qualifier("sqlSession")
	private SqlSessionTemplate sqlSession;

	
	@Override
	public void getDashBoardInfo(Model model, HttpSession session) {
		
		getTopSummaryData(model,session);
	}
	
	private void getTopSummaryData(Model model, HttpSession session) {
		
		String cm = (String) session.getAttribute("SESSION_COMCD");
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", cm);
		
//		List<Map<String,Object>> ret = sqlSession.selectList("home.commonSql", param);
	}
	
}



