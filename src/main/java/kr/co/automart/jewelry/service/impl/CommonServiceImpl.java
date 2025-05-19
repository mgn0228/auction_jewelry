package kr.co.automart.jewelry.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.automart.jewelry.common.ConfigManager;
import kr.co.automart.jewelry.common.Init_Lib_V1;
import kr.co.automart.jewelry.common.SessionConstant;
import kr.co.automart.jewelry.service.CommonService;

//@Slf4j
@Service
public class CommonServiceImpl implements CommonService {
	
	@Autowired
	private	ConfigManager			cfgMnger;
	
	@Autowired
	private	Init_Lib_V1				initLib;
	
	@Override
	public boolean checkLogin(HttpServletRequest request, Model model) {
		
		Map<String, String> meminfo = this.getSessionMemberInfo(request.getSession());
		
		if (meminfo.get("mbId") == null) return false;
		
		model.addAttribute("comCd",meminfo.get("comCd"));
		model.addAttribute("comNm",meminfo.get("comNm"));
		model.addAttribute("mbNm",meminfo.get("mbNm"));
		model.addAttribute("mbId",meminfo.get("mbId"));
		model.addAttribute("mbLvl",meminfo.get("mbLvl"));
		model.addAttribute("mbImg",meminfo.get("mbImg"));
		model.addAttribute("mbRegDt",meminfo.get("mbRegDt"));
		
		return true;
	}
	
	
	@Override
	public void initService(HttpSession session, Model model, String pid, HttpServletRequest request) {
		
		Map<String, Object> reqdata = null;
		
		if (request != null) {
			reqdata = initLib.extractRequest(request);
			model.addAttribute("requestData", reqdata);
		}
		
		
		model.addAttribute("serviceName", cfgMnger.getServiceName());
		
		Map<String, String> meminfo = this.getSessionMemberInfo(session);
		String gid = meminfo.get("mbGrp");
		String lvl = meminfo.get("mbLvl");
		
		List<Map<String, String>> contentInfo = initLib.getContentInfoList(session, pid);
		
		List<Map<String, String>> accessAuth = initLib.getAccessAuthority(session, gid, pid);
		model.addAttribute("accessAuth", accessAuth);
		
		String pid4page = "";
		
		for(Map<String, String> item: contentInfo) {
			
			if ((item.get("propCd")).equals("getPids") || (item.get("propCd")).equals("getInfos")) {
				String subpid = item.get("propVal");
				String procInit = item.get("pidProcInit");
//				if (!"Y".equals(procInit)) continue;
				
				if ((item.get("propCd")).equals("getPids")) {
					pid4page = "Y";
				} else {
					pid4page = "N";
				}
				
				List<Map<String, String>> dbSchemaInfo = initLib.getDBSchemaList(session, gid, subpid, lvl, "list");
				
				dbSchemaInfo = initLib.makeFieldList(dbSchemaInfo, session, gid, pid, lvl, "list");
				
				if (dbSchemaInfo.size()>0) {
					
					List<Map<String, String>> subContentInfo = new ArrayList<Map<String, String>>();
					if ("Y".equals(procInit)) subContentInfo = initLib.getSubContentList(session, dbSchemaInfo, reqdata);
					
					if ("Y".equals(pid4page)) {
//						pid4page = subpid;
						model.addAttribute("mainContent", subContentInfo);
						
						Collections.sort(dbSchemaInfo, new Comparator<Object>() {
							// Comparable 인터페이스를 구현하여 전달
							@SuppressWarnings("unchecked")
							@Override
							public int compare(Object s1, Object s2) {
								return (Integer)((Map<String,Object>)s1).get("fdSeq") - (Integer)((Map<String,Object>)s2).get("fdSeq");
							}
						});

						
						model.addAttribute("mainContentSchema", dbSchemaInfo);
					} else {
						model.addAttribute(subpid, subContentInfo);
					}
				}
				
			} else if ((item.get("propCd")).equals("getPidsSub") ) {
				String subpid = item.get("propVal");
				List<Map<String, String>> subContentInfo = new ArrayList<Map<String, String>>();
				
				List<Map<String, String>> dbSchemaInfo2 = initLib.getDBSchemaList(session, gid, subpid, lvl, "list");
				dbSchemaInfo2 = initLib.makeFieldList(dbSchemaInfo2, session, gid, pid, lvl, "list");
				model.addAttribute("getPidsSub", subpid);
				model.addAttribute("pSubContent", subContentInfo);
				
				
				Collections.sort(dbSchemaInfo2, new Comparator<Object>() {
					// Comparable 인터페이스를 구현하여 전달
					@SuppressWarnings("unchecked")
					@Override
					public int compare(Object s1, Object s2) {
						return (Integer)((Map<String,Object>)s1).get("fdSeq") - (Integer)((Map<String,Object>)s2).get("fdSeq");
					}
				});
				
				
				model.addAttribute("pSubContentSchema", dbSchemaInfo2);
				
				
				
//				List<Map<String, String>> accessAuthSub = initLib.getAccessAuthority(session, gid, subpid);
				model.addAttribute("accessAuthSub", accessAuth);
				
			} else {
				model.addAttribute(item.get("propCd"), item.get("propVal"));
			}
			
		}
	}
	
	
	
	
	
	@Override
	public Map<String,String> getMenu(HttpSession session, Model model,String pid) {
		
		Map<String,String> cm = initLib.getMenu(model, session, pid);
//		model.addAttribute("menus", menustr);
		return cm;
	}
	
	
	@Override
	public void SetSessionMemberInfo(HttpSession session ,Map<String, String> row) {
		session.setAttribute(SessionConstant.SESSION_COMCD, (String)row.get("comCd"));
		session.setAttribute(SessionConstant.SESSION_COMNM, (String)row.get("comNm"));
		session.setAttribute(SessionConstant.SESSION_LID, (String)row.get("mbId"));
		session.setAttribute(SessionConstant.SESSION_LVL, (String)row.get("mbLvl"));
		session.setAttribute(SessionConstant.SESSION_LNM, (String)row.get("mbNm"));
		session.setAttribute(SessionConstant.SESSION_LIMG, (String)row.get("mbImg"));
		session.setAttribute(SessionConstant.SESSION_LREGDT, (String)row.get("regDt"));
		session.setAttribute(SessionConstant.SESSION_GROUP, (String)row.get("mbGrp"));
	}
	
	@Override
	public Map<String, String> getSessionMemberInfo(HttpSession session) {
		Map<String, String> row = new HashMap<String, String>();
		row.put("comCd",(String) session.getAttribute(SessionConstant.SESSION_COMCD));
		row.put("comNm",(String) session.getAttribute(SessionConstant.SESSION_COMNM));
		row.put("mbId",(String) session.getAttribute(SessionConstant.SESSION_LID));
		row.put("mbLvl",(String) session.getAttribute(SessionConstant.SESSION_LVL));
		row.put("mbNm",(String) session.getAttribute(SessionConstant.SESSION_LNM));
		row.put("mbImg",(String) session.getAttribute(SessionConstant.SESSION_LIMG));
		row.put("mbRegDt",(String) session.getAttribute(SessionConstant.SESSION_LREGDT));
		row.put("mbGrp",(String) session.getAttribute(SessionConstant.SESSION_GROUP));
		return row;
	}
	
	@Override
	public void ResetSessionMemberInfo(HttpSession session) {
		session.removeAttribute(SessionConstant.SESSION_COMCD);
		session.removeAttribute(SessionConstant.SESSION_COMNM);
		session.removeAttribute(SessionConstant.SESSION_LID);
		session.removeAttribute(SessionConstant.SESSION_LVL);
		session.removeAttribute(SessionConstant.SESSION_LNM);
		session.removeAttribute(SessionConstant.SESSION_LIMG);
		session.removeAttribute(SessionConstant.SESSION_LREGDT);
		session.removeAttribute(SessionConstant.SESSION_GROUP);
		session.removeAttribute(SessionConstant.SESSION_SECRETUUID);
	}
	
	
	
	@Override
	public void SetSessionMemberInfo4Guest(HttpSession session ,Map<String, String> row) {
		session.setAttribute(SessionConstant.SESSION_COMCD, SessionConstant.DEFAULT_COMCD);
		session.setAttribute(SessionConstant.SESSION_COMNM, "Guest");
		session.setAttribute(SessionConstant.SESSION_LID, "guest");
		session.setAttribute(SessionConstant.SESSION_LVL, "1");
		session.setAttribute(SessionConstant.SESSION_LNM, "비회원");
		session.setAttribute(SessionConstant.SESSION_LIMG, "");
		session.setAttribute(SessionConstant.SESSION_LREGDT, "");
		session.setAttribute(SessionConstant.SESSION_GROUP, "guest");
	}
	
}



