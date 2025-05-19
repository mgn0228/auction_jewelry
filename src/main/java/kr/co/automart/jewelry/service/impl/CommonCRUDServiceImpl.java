package kr.co.automart.jewelry.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.util.MultiValueMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.automart.jewelry.common.FileManager;
import kr.co.automart.jewelry.common.Init_Lib_V1;
import kr.co.automart.jewelry.common.SessionConstant;
import kr.co.automart.jewelry.service.CommonCRUDService;
import kr.co.automart.jewelry.service.CommonService;

//@Slf4j
@Service
public class CommonCRUDServiceImpl implements CommonCRUDService {
	
	@Autowired
	private	Init_Lib_V1				initLib;
	
	@Autowired
	private	CommonService			commonService;
	
	@Autowired
	private FileManager				fileManger;
	
	@Autowired
	@Qualifier("sqlSession")
	private SqlSessionTemplate sqlSession;
	
	
	@Override
	public List<Map<String, Object>> getContentList(HttpSession session, String pid, Map<String, Object> row) {
		// TODO Auto-generated method stub
		
		Map<String, String> meminfo = commonService.getSessionMemberInfo(session);
		String gid = meminfo.get("mbGrp");
		String lvl = meminfo.get("mbLvl");
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		List<Map<String, String>> contentInfo = initLib.getContentInfoList(session, pid);
		
		List<Map<String, Object>> oneContent = null;
		
		for(Map<String, String> item: contentInfo) {
			
			if ((item.get("propCd")).equals("getPids")) {
				String subpid = item.get("propVal");
				List<Map<String, String>> dbSchemaInfo = initLib.getDBSchemaList(session, gid, subpid, lvl, "list");
				
				dbSchemaInfo = initLib.makeFieldList(dbSchemaInfo, session, gid, pid, lvl, "list");
				
				if (dbSchemaInfo.size()>0) {
					
					String sql = "";
					String tblParamWhere = initLib.makeSearchWhere(dbSchemaInfo, row, sc);
//					sql = initLib.makeListQueryString(session, dbSchemaInfo);
					
					if ("1".equals(dbSchemaInfo.get(0).get("qryType"))) {
						sql = initLib.makeListQueryString(session, dbSchemaInfo, tblParamWhere);
						sql = " SELECT " + makeFieldList4Table(dbSchemaInfo) + " FROM ( " + sql + ") src "
								+ makeOuterQuery(dbSchemaInfo, row);
						
					} else if ("11".equals(dbSchemaInfo.get(0).get("qryType"))) {
						sql = initLib.makeListQueryString_ProcedureCall(session, dbSchemaInfo,row);
					}
					
					sql = initLib.ReplaceRequestData(sql,row,session);
					
//					sql = " SELECT " + makeFieldList4Table(dbSchemaInfo) + " FROM ( " + sql + ") src "
//							+ makeSearchWhere(dbSchemaInfo, row) + makeOuterQuery(dbSchemaInfo, row);
					
					Map<String,String> param = new HashMap<String,String>();
					param.put("sql", sql);
					
					List<Map<String,Object>> ret = sqlSession.selectList("lib_v1.0.commonSql", param);
					
					ret = initLib.makeEncryptList(dbSchemaInfo, ret, sc);
					oneContent = ret;
				}
				break;
			}
		}
		
		return oneContent;
	}
	
	
	@Override
	public Map<String, Object> getOneContent(HttpSession session, String pid, Map<String, Object> row) {
		// TODO Auto-generated method stub
		
		Map<String, String> meminfo = commonService.getSessionMemberInfo(session);
		String gid = meminfo.get("mbGrp");
		String lvl = meminfo.get("mbLvl");
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		List<Map<String, String>> contentInfo = initLib.getContentInfoList(session, pid);
		
		Map<String, Object> oneContent = null;
		
		for(Map<String, String> item: contentInfo) {
			
			if ((item.get("propCd")).equals("getPids")) {
				String subpid = item.get("propVal");
				List<Map<String, String>> dbSchemaInfo = initLib.getDBSchemaList(session, gid, subpid, lvl, "list");
				
				dbSchemaInfo = initLib.makeFieldList(dbSchemaInfo, session, gid, pid, lvl, "list");
				
				if (dbSchemaInfo.size()>0) {
					
					String sql = "";
					String tblParamWhere = initLib.makeSearchWhere(dbSchemaInfo, row, sc);
//					sql = initLib.makeListQueryString(session, dbSchemaInfo);
					sql = initLib.makeListQueryString(session, dbSchemaInfo, tblParamWhere);
					sql = initLib.ReplaceRequestData(sql,row,session);
					
					sql = " SELECT " + makeFieldList4Form(dbSchemaInfo) + " FROM ( " + sql + ") src " + makeWhere(dbSchemaInfo, row);
					
					Map<String,String> param = new HashMap<String,String>();
					param.put("sql", sql);
					
					List<Map<String,Object>> ret = sqlSession.selectList("lib_v1.0.commonSql", param);
					
					if (ret.size()>0) {
						ret = initLib.makeEncryptList(dbSchemaInfo, ret, sc);
						oneContent = ret.get(0);
					}
				}
				break;
			}
		}
		
		return oneContent;
	}
	
	
	
	@Override
	public Map<String, Object> insertCommonCRUD(HttpServletRequest request, HttpSession session, String pid, Map<String, Object> row) {
		// TODO Auto-generated method stub
		
		Map<String, Object> ret = new HashMap<String, Object>();

		Map<String, Object> newRow = new HashMap<String, Object>();
		
		Map<String, String> meminfo = commonService.getSessionMemberInfo(session);
		String gid = meminfo.get("mbGrp");
		String lvl = meminfo.get("mbLvl");
		String comCd = meminfo.get("comCd");
		
		List<Map<String, String>> accessAuth = initLib.getAccessAuthority(session, gid, pid);
		
		if (accessAuth.size() > 0 && !"Y".equals(accessAuth.get(0).get("availUpdate"))) {
			ret.put("result", "Fail");
			ret.put("message", SessionConstant.ERROR_MSG_NOAUTH);
		} else {
			
			
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			MultiValueMap<String, MultipartFile>  mpmap = multipartRequest.getMultiFileMap();
			
//			Map<String, Object> newrow = fileManger.uploadFileSave(row, mpmap, session, comCd, pid);
			row = fileManger.uploadFileSave(row, mpmap, session, comCd, pid);
			
			List<Map<String, String>> contentInfo = initLib.getContentInfoList(session, pid);
			
			for(Map<String, String> item: contentInfo) {
				
				if ((item.get("propCd")).equals("getPids")) {
					String subpid = item.get("propVal");
					List<Map<String, String>> dbSchemaInfo = initLib.getDBSchemaList(session, gid, subpid, lvl, "save");
					
					String prevQryGroup = "";
					int prevQryGroupIdx = -1;
					
					if (dbSchemaInfo.size()>0) {
						
						boolean loop = true;
						while (loop == true) {
							
							List<Map<String, String>> tmpdbSchemaInfo = new ArrayList<>(dbSchemaInfo);
							String tmpQryGroup = prevQryGroup;
							int len= tmpdbSchemaInfo.size();
							if ("".equals(tmpQryGroup)) {
								tmpQryGroup = tmpdbSchemaInfo.get(0).get("qryGroup");
								prevQryGroupIdx = 0;
							} else {
								int i;
								for (i=prevQryGroupIdx+1;i<len;i++) {
									if (!tmpQryGroup.equals( tmpdbSchemaInfo.get(i).get("qryGroup")) ) {
										tmpQryGroup = tmpdbSchemaInfo.get(i).get("qryGroup");
										prevQryGroupIdx = i;
										break;
									}
								}
								if (i >= len) {
									break;
								}
							}
							
							for (int i=0;i<len;i++) {
								if (!tmpQryGroup.equals( tmpdbSchemaInfo.get(len-i-1).get("qryGroup")) ) {
									tmpdbSchemaInfo.remove(len-i-1);
								}
							}
							
							
							String sql = "";
							
							String checkResult = initLib.CheckRequirednDuplicate(session, tmpdbSchemaInfo, row, "C");
							if (!"".equals(checkResult)) {
								ret.put("result", "Fail");
								ret.put("message", checkResult);
								break;
							}
							
							
							Map<String,Object> param = new HashMap<String,Object>();
							
							if ("1".equals(tmpdbSchemaInfo.get(0).get("qryType"))) { // normal update
								sql = initLib.makeInsertQueryString(session, tmpdbSchemaInfo, row);
								
							} else if ("2".equals(tmpdbSchemaInfo.get(0).get("qryType"))) { // delete previous data and insert data
								sql = initLib.makeInsertQueryString_Delete_Insert(session, tmpdbSchemaInfo, row);
								
							} else if ("3".equals(tmpdbSchemaInfo.get(0).get("qryType"))) { // delete previous data and insert list from data split ","
								sql = initLib.makeInsertQueryString_Delete_Insert_List(session, tmpdbSchemaInfo, row);
								
							} else if ("11".equals(tmpdbSchemaInfo.get(0).get("qryType"))) { // procedure call
								sql = initLib.makeInsertQueryString_ProcedureCall(session, tmpdbSchemaInfo, row);
							}
							
							sql = initLib.ReplaceRequestData(sql,row,session);
							
							String [] sqls =  sql.split("#;#");
							String keyval = "";
							for(int i=0;i<sqls.length;i++) {
								param.put("sql", sqls[i]);
								sqlSession.insert("lib_v1.0.commonSqlInsert", param);
								if (i==0 && "".equals(prevQryGroup)) {
									keyval = String.valueOf(param.get("no"));
								}
							}
							
							
							if ( "".equals(prevQryGroup)) {
								param.put("no", keyval);
								newRow = getOneContent(session, pid, param);
								
								if (newRow == null) {
									row.put("key_f", "");
								} else {
									row.put("key_f", newRow.get("key_f"));
								}
								
								ret.put("content", newRow);
								ret.put("result", "OK");
							}
							
							prevQryGroup = tmpQryGroup;
							
						}
						
					}
					break;
				}
			}
		}
		
		
		return ret;
	}
	
	
	
//	@Override
	public Map<String, Object> insertCommonCRUD_org(HttpServletRequest request, HttpSession session, String pid, Map<String, Object> row) {
		// TODO Auto-generated method stub
		
		Map<String, Object> ret = new HashMap<String, Object>();

		
		Map<String, String> meminfo = commonService.getSessionMemberInfo(session);
		String gid = meminfo.get("mbGrp");
		String lvl = meminfo.get("mbLvl");
		String comCd = meminfo.get("comCd");
		
		List<Map<String, String>> accessAuth = initLib.getAccessAuthority(session, gid, pid);
		
		if (accessAuth.size() > 0 && !"Y".equals(accessAuth.get(0).get("availCreate"))) {
			ret.put("result", "Fail");
			ret.put("message", SessionConstant.ERROR_MSG_NOAUTH);
		} else {
			
			
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			MultiValueMap<String, MultipartFile>  mpmap = multipartRequest.getMultiFileMap();
			
//			Map<String, Object> newrow = fileManger.uploadFileSave(row, mpmap, session, comCd, pid);
			row = fileManger.uploadFileSave(row, mpmap, session, comCd, pid);
			
			List<Map<String, String>> contentInfo = initLib.getContentInfoList(session, pid);
			
			for(Map<String, String> item: contentInfo) {
				
				if ((item.get("propCd")).equals("getPids")) {
					String subpid = item.get("propVal");
					List<Map<String, String>> dbSchemaInfo = initLib.getDBSchemaList(session, gid, subpid, lvl, "save");
					
					if (dbSchemaInfo.size()>0) {
						String sql = "";
						
						String checkResult = initLib.CheckRequirednDuplicate(session, dbSchemaInfo, row, "C");
						if (!"".equals(checkResult)) {
							ret.put("result", "Fail");
							ret.put("message", checkResult);
							break;
						}
						
						sql = initLib.makeInsertQueryString(session, dbSchemaInfo, row);
						sql = initLib.ReplaceRequestData(sql,row,session);
						
						Map<String,Object> param = new HashMap<String,Object>();
						param.put("sql", sql);
						
						sqlSession.insert("lib_v1.0.commonSqlInsert", param);
						
						String no = String.valueOf(param.get("no"));
						param.remove("no");
						param.put("no", no);
						
						if (no == null || "".equals(no)) {
							ret.put("result", "Fail");
							ret.put("message", SessionConstant.ERROR_MSG_SQLINSERT);
						} else {
							ret.put("content", getOneContent(session, pid, param));
							ret.put("result", "OK");
						}
						
					}
					break;
				}
			}
		}
		
		
		return ret;
	}
	
	
	
	@Override
	public Map<String, Object> updateCommonCRUD(HttpServletRequest request, HttpSession session, String pid, Map<String, Object> row) {
		// TODO Auto-generated method stub
		
		Map<String, Object> ret = new HashMap<String, Object>();

		
		Map<String, String> meminfo = commonService.getSessionMemberInfo(session);
		String gid = meminfo.get("mbGrp");
		String lvl = meminfo.get("mbLvl");
		String comCd = meminfo.get("comCd");
		
		List<Map<String, String>> accessAuth = initLib.getAccessAuthority(session, gid, pid);
		
		if (accessAuth.size() > 0 && !"Y".equals(accessAuth.get(0).get("availUpdate"))) {
			ret.put("result", "Fail");
			ret.put("message", SessionConstant.ERROR_MSG_NOAUTH);
		} else {
			
			
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			MultiValueMap<String, MultipartFile>  mpmap = multipartRequest.getMultiFileMap();
			
//			Map<String, Object> newrow = fileManger.uploadFileSave(row, mpmap, session, comCd, pid);
			row = fileManger.uploadFileSave(row, mpmap, session, comCd, pid);
			
			List<Map<String, String>> contentInfo = initLib.getContentInfoList(session, pid);
			
			for(Map<String, String> item: contentInfo) {
				
				if ((item.get("propCd")).equals("getPids")) {
					String subpid = item.get("propVal");
					List<Map<String, String>> dbSchemaInfo = initLib.getDBSchemaList(session, gid, subpid, lvl, "save");
					
					String prevQryGroup = "";
					int prevQryGroupIdx = -1;
					
					if (dbSchemaInfo.size()>0) {
						
						boolean loop = true;
						while (loop == true) {
							
							List<Map<String, String>> tmpdbSchemaInfo = new ArrayList<>(dbSchemaInfo);
							String tmpQryGroup = prevQryGroup;
							int len= tmpdbSchemaInfo.size();
							if ("".equals(tmpQryGroup)) {
								tmpQryGroup = tmpdbSchemaInfo.get(0).get("qryGroup");
								prevQryGroupIdx = 0;
							} else {
								int i;
								for (i=prevQryGroupIdx+1;i<len;i++) {
									if (!tmpQryGroup.equals( tmpdbSchemaInfo.get(i).get("qryGroup")) ) {
										tmpQryGroup = tmpdbSchemaInfo.get(i).get("qryGroup");
										prevQryGroupIdx = i;
										break;
									}
								}
								if (i >= len) {
									break;
								}
							}
							
							for (int i=0;i<len;i++) {
								if (!tmpQryGroup.equals( tmpdbSchemaInfo.get(len-i-1).get("qryGroup")) ) {
									tmpdbSchemaInfo.remove(len-i-1);
								}
							}
							
							
							String sql = "";
							
							String checkResult = initLib.CheckRequirednDuplicate(session, tmpdbSchemaInfo, row, "U");
							if (!"".equals(checkResult)) {
								ret.put("result", "Fail");
								ret.put("message", checkResult);
								break;
							}
							
							
							Map<String,Object> param = new HashMap<String,Object>();
							
							if ("1".equals(tmpdbSchemaInfo.get(0).get("qryType"))) { // normal update
								sql = initLib.makeUpdateQueryString(session, tmpdbSchemaInfo, row);
//								sql = initLib.ReplaceRequestData(sql,row,session);
								
//								param.put("sql", sql);
//								sqlSession.update("lib_v1.0.commonSqlUpdate", param);
								
							} else if ("2".equals(tmpdbSchemaInfo.get(0).get("qryType"))) { // delete previous data and insert data
								sql = initLib.makeUpdateQueryString_Delete_Insert(session, tmpdbSchemaInfo, row);
//								sql = initLib.ReplaceRequestData(sql,row,session);
								
//								param.put("sql", sql);
//								sqlSession.update("lib_v1.0.commonSqlUpdate", param);
								
							} else if ("3".equals(tmpdbSchemaInfo.get(0).get("qryType"))) { // delete previous data and insert list from data split ","
								sql = initLib.makeUpdateQueryString_Delete_Insert_List(session, tmpdbSchemaInfo, row);
//								sql = initLib.ReplaceRequestData(sql,row,session);
								
//								param.put("sql", sql);
//								sqlSession.update("lib_v1.0.commonSqlUpdate", param);
								
							} else if ("11".equals(tmpdbSchemaInfo.get(0).get("qryType"))) { // procedure call
								sql = initLib.makeUpdateQueryString_ProcedureCall(session, tmpdbSchemaInfo, row);
								
							}
							
							sql = initLib.ReplaceRequestData(sql,row,session);
							
							String [] sqls =  sql.split("#;#");
							for(int i=0;i<sqls.length;i++) {
								param.put("sql", sqls[i]);
								sqlSession.update("lib_v1.0.commonSqlUpdate", param);
							}
							
							
							if ( "".equals(prevQryGroup)) {
								
/*								
								String sqlwheres[] = sql.split("WHERE");
								
								Map<String,Object> newparam = new HashMap<String,Object>();
								String sqlwhere = sqlwheres[sqlwheres.length - 1];
//								String key = sqlwhere.split("=")[0];
								String keyval = sqlwhere.split("=")[1];
								keyval = keyval.replaceAll("'", "");
								newparam.put("no", keyval);
								ret.put("content", getOneContent(session, pid, newparam));
								
*/								
								ret.put("result", "OK");
							}
							
							prevQryGroup = tmpQryGroup;
							
						}
						
					}
					break;
				}
			}
		}
		
		
		return ret;
	}
	
	
	
//	@Override
	public Map<String, Object> updateCommonCRUD_org(HttpServletRequest request, HttpSession session, String pid, Map<String, Object> row) {
		// TODO Auto-generated method stub
		
		Map<String, Object> ret = new HashMap<String, Object>();

		
		Map<String, String> meminfo = commonService.getSessionMemberInfo(session);
		String gid = meminfo.get("mbGrp");
		String lvl = meminfo.get("mbLvl");
		String comCd = meminfo.get("comCd");
		
		List<Map<String, String>> accessAuth = initLib.getAccessAuthority(session, gid, pid);
		
		if (accessAuth.size() > 0 && !"Y".equals(accessAuth.get(0).get("availUpdate"))) {
			ret.put("result", "Fail");
			ret.put("message", SessionConstant.ERROR_MSG_NOAUTH);
		} else {
			
			
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			MultiValueMap<String, MultipartFile>  mpmap = multipartRequest.getMultiFileMap();
			
//			Map<String, Object> newrow = fileManger.uploadFileSave(row, mpmap, session, comCd, pid);
			row = fileManger.uploadFileSave(row, mpmap, session, comCd, pid);
			
			List<Map<String, String>> contentInfo = initLib.getContentInfoList(session, pid);
			
			for(Map<String, String> item: contentInfo) {
				
				if ((item.get("propCd")).equals("getPids")) {
					String subpid = item.get("propVal");
					List<Map<String, String>> dbSchemaInfo = initLib.getDBSchemaList(session, gid, subpid, lvl, "save");
					
					if (dbSchemaInfo.size()>0) {
						String sql = "";
						
						String checkResult = initLib.CheckRequirednDuplicate(session, dbSchemaInfo, row, "U");
						if (!"".equals(checkResult)) {
							ret.put("result", "Fail");
							ret.put("message", checkResult);
							break;
						}
						
						sql = initLib.makeUpdateQueryString(session, dbSchemaInfo, row);
						sql = initLib.ReplaceRequestData(sql,row,session);
						
						Map<String,Object> param = new HashMap<String,Object>();
						param.put("sql", sql);
						sqlSession.update("lib_v1.0.commonSqlUpdate", param);
						
						String sqlwheres[] = sql.split("WHERE");
						
						String sqlwhere = sqlwheres[sqlwheres.length - 1];
//						String key = sqlwhere.split("=")[0];
						String keyval = sqlwhere.split("=")[1];
						param.put("no", keyval);
						ret.put("content", getOneContent(session, pid, param));
						ret.put("result", "OK");
						
					}
					break;
				}
			}
		}
		
		
		return ret;
	}
	
	
	
	@Override
	public Map<String, Object> deleteCommonCRUD(HttpSession session, String pid, Map<String, Object> row) {
		// TODO Auto-generated method stub
		
		Map<String, Object> ret = new HashMap<String, Object>();

		
		Map<String, String> meminfo = commonService.getSessionMemberInfo(session);
		String gid = meminfo.get("mbGrp");
		String lvl = meminfo.get("mbLvl");
		
		List<Map<String, String>> accessAuth = initLib.getAccessAuthority(session, gid, pid);
		
		if (accessAuth.size() > 0 && !"Y".equals(accessAuth.get(0).get("availDelete"))) {
			ret.put("result", "Fail");
			ret.put("message", SessionConstant.ERROR_MSG_NOAUTH);
		} else {
			
			List<Map<String, String>> contentInfo = initLib.getContentInfoList(session, pid);
			
			for(Map<String, String> item: contentInfo) {
				
				if ((item.get("propCd")).equals("getPids")) {
					String subpid = item.get("propVal");
					List<Map<String, String>> dbSchemaInfo = initLib.getDBSchemaList(session, gid, subpid, lvl, "save");
					
					if (dbSchemaInfo.size()>0) {
						String sql = "";
						
						sql = initLib.makeDeleteQueryString(session, dbSchemaInfo, row);
						sql = initLib.ReplaceRequestData(sql,row,session);
						
						String [] sqls =  sql.split("#;#");
						for(int i=0;i<sqls.length;i++) {
//							param.put("sql", sqls[i]);
//							sqlSession.update("lib_v1.0.commonSqlUpdate", param);
							
							Map<String,Object> param = new HashMap<String,Object>();
							param.put("sql", sqls[i]);
							sqlSession.delete("lib_v1.0.commonSqlDelete", param);
						}

						
//						Map<String,Object> param = new HashMap<String,Object>();
//						param.put("sql", sql);
//						sqlSession.delete("lib_v1.0.commonSqlDelete", param);
						
						ret.put("result", "OK");
						
					}
					break;
				}
			}
		}
		
		
		return ret;
	}
	
	
	
//	@Override
	public Map<String, Object> deleteCommonCRUD_org(HttpSession session, String pid, Map<String, Object> row) {
		// TODO Auto-generated method stub
		
		Map<String, Object> ret = new HashMap<String, Object>();

		
		Map<String, String> meminfo = commonService.getSessionMemberInfo(session);
		String gid = meminfo.get("mbGrp");
		String lvl = meminfo.get("mbLvl");
		
		List<Map<String, String>> accessAuth = initLib.getAccessAuthority(session, gid, pid);
		
		if (accessAuth.size() > 0 && !"Y".equals(accessAuth.get(0).get("availDelete"))) {
			ret.put("result", "Fail");
			ret.put("message", SessionConstant.ERROR_MSG_NOAUTH);
		} else {
			
			List<Map<String, String>> contentInfo = initLib.getContentInfoList(session, pid);
			
			for(Map<String, String> item: contentInfo) {
				
				if ((item.get("propCd")).equals("getPids")) {
					String subpid = item.get("propVal");
					List<Map<String, String>> dbSchemaInfo = initLib.getDBSchemaList(session, gid, subpid, lvl, "save");
					
					if (dbSchemaInfo.size()>0) {
						String sql = "";
						
						sql = initLib.makeDeleteQueryString(session, dbSchemaInfo, row);
						sql = initLib.ReplaceRequestData(sql,row,session);
						
						Map<String,Object> param = new HashMap<String,Object>();
						param.put("sql", sql);
						sqlSession.delete("lib_v1.0.commonSqlDelete", param);
						
						ret.put("result", "OK");
						
					}
					break;
				}
			}
		}
		
		
		return ret;
	}
	
	
	
	@Override
	public String makeFieldList4Form(List<Map<String, String>> dbSchemaInfo) {
		
		String ret = "";
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			String isForm = dbSchemaInfo.get(i).get("fdIsForm");
			String fdnm = dbSchemaInfo.get(i).get("fdAliasNm");
			if ("Y".equals(isForm)) {
				if (!"".equals(ret)) {
					ret += " , ";
				}
				ret += fdnm;
			}
		};
		
		return ret;
	}	
	
	
	


	private String makeFieldList4Table(List<Map<String, String>> dbSchemaInfo) {
		
		String ret = "";
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			String isForm = dbSchemaInfo.get(i).get("fdIsTable");
			String fdnm = dbSchemaInfo.get(i).get("fdAliasNm");
			if ("Y".equals(isForm)) {
				if (!"".equals(ret)) {
					ret += " , ";
				}
				ret += fdnm;
			}
		};
		
		return ret;
	}	

/*	
	
	private String makeFieldTitleList4Table(List<Map<String, String>> dbSchemaInfo) {
		
		String ret = "";
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			String isForm = dbSchemaInfo.get(i).get("fdIsTable");
			String fdnm = dbSchemaInfo.get(i).get("fdLabel");
			if ("Y".equals(isForm)) {
				if (!"".equals(ret)) {
					ret += " , ";
				}
				ret += fdnm;
			}
		};
		
		return ret;
	}	
	
	
*/	
	
	@Override
	public String makeWhere(List<Map<String, String>> dbSchemaInfo, Map<String,Object> row) {
		
		String ret = "";
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			String fdnm = dbSchemaInfo.get(i).get("fdAliasNm");
			for( Map.Entry<String, Object> entry : row.entrySet() ){
				String strKey = entry.getKey();
				if (fdnm.equals(strKey)) {
					String strValue = (String) entry.getValue();
					if (!"".equals(ret)) {
						ret += " AND ";
					}
					ret += strKey + " = '" + strValue + "'";
				}
			};
		};
		
		if (!"".equals(ret)) ret = " WHERE " + ret;
		return ret;
	}	
	
	
/*
	private String makeSearchWhere(List<Map<String, String>> dbSchemaInfo, Map<String,Object> row) {
		
		String ret = "";
		String outer_Where = "";
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			String tblOuterWhere = dbSchemaInfo.get(i).get("tblOuterWhere");
			
			if (!"".equals(tblOuterWhere.trim())) {
				if (!"".equals(outer_Where)) outer_Where += " AND ";
				outer_Where += tblOuterWhere;
			}
			
			String fdnm = dbSchemaInfo.get(i).get("fdAliasNm");
			String fdTagType = dbSchemaInfo.get(i).get("fdTagType").toUpperCase();
			for( Map.Entry<String, Object> entry : row.entrySet() ){
				String strKey = entry.getKey();
				if (fdnm.equals(strKey)) {
					String strValue = (String) entry.getValue();
					if (!"".equals(strValue)) {
						if (!"".equals(ret)) {
							ret += " AND ";
						}
						
						if ("SELECT".equals(fdTagType)) {
							ret += strKey + " = '" + strValue + "'";
						} else if ("PERIOD_DATETIME".equals(fdTagType) || "PERIOD_DATE".equals(fdTagType)) {
							String[] sted = strValue.split("~");
							if (sted.length == 2) {
								ret += strKey + " BETWEEN '" + sted[0].trim() + "' AND '" + sted[1].trim() + "' ";
							} else {
								ret += strKey + " BETWEEN '' AND '' ";
							}
						} else if ("CHECKBOX".equals(fdTagType)) {
							String[] vals = strValue.split(",");
							String tmpStrVal = "";
							for (int j=0;j<vals.length;j++) {
								if (!"".equals(tmpStrVal)) tmpStrVal += ",";
								tmpStrVal += "'"+vals[j]+"'";
							}
							ret += strKey + " IN ( "+tmpStrVal + ") ";
						} else {
							ret += strKey + " LIKE '%" + strValue + "%'";
						}
						
					}
				}
			};
		};
		
		if (!"".equals(ret)) {
			ret = " WHERE " + ret;
		}
		if (!"".equals(outer_Where)) {
			if ("".equals(ret)) ret = " WHERE " + outer_Where;
			else ret += " AND " + outer_Where;
		}
		
		return ret;
	}	
	
*/	
	
	
	

	private String makeOuterQuery(List<Map<String, String>> dbSchemaInfo, Map<String,Object> row) {
		
		String ret = "";
		
		String outer_Where = "";
		String outer_Groupby = "";
		String outer_Orderby = "";
		String preTblseq = "";
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			String tblSeq = String.valueOf(dbSchemaInfo.get(i).get("tblSeq"));
			
			
			if (tblSeq.equals(preTblseq)) continue;
			preTblseq = tblSeq;
			String tblOuterWhere = dbSchemaInfo.get(i).get("tblOuterWhere");
			String tblOterGroupBy = dbSchemaInfo.get(i).get("tblOterGroupBy");
			String tblOterOrderBy = dbSchemaInfo.get(i).get("tblOterOrderBy");
			
			if (!"".equals(tblOuterWhere.trim())) {
				if (!"".equals(outer_Where)) outer_Where += " AND ";
				outer_Where += tblOuterWhere;
			}
			if (!"".equals(tblOterGroupBy.trim())) {
				if (!"".equals(outer_Groupby)) outer_Groupby += " , ";
				outer_Groupby += " " + tblOterGroupBy + " ";
			}
			if (!"".equals(tblOterOrderBy.trim())) {
				if (!"".equals(outer_Orderby)) outer_Orderby += " , ";
				outer_Orderby += " " + tblOterOrderBy + " ";
			}
		}
		
		if (!"".equals(outer_Where)) outer_Where = " WHERE " + outer_Where;
		if (!"".equals(outer_Groupby)) outer_Groupby = " GROUP BY " + outer_Groupby;
		if (!"".equals(outer_Orderby)) outer_Orderby = " ORDER BY " + outer_Orderby;
		ret = outer_Where + " " + outer_Groupby + " " + outer_Orderby;
		return ret;
	}	
	
	
	
	@Override
	public Map<String, Object> getModalContentList(HttpSession session, String pid, Map<String, Object> row) {
		// TODO Auto-generated method stub
		
		Map<String, String> meminfo = commonService.getSessionMemberInfo(session);
		String gid = meminfo.get("mbGrp");
		String lvl = meminfo.get("mbLvl");
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		List<Map<String, Object>> fieldInfo = new ArrayList<Map<String, Object>>();
		Map<String, Object> oneContent = new HashMap<String, Object>();
		
		List<Map<String, String>> dbSchemaInfo = initLib.getDBSchemaList(session, gid, pid, lvl, "list");
		
		dbSchemaInfo = initLib.makeFieldList(dbSchemaInfo, session, gid, pid, lvl, "list");
		
		if (dbSchemaInfo.size()>0) {
			
			String sql = "";
			String tblParamWhere = initLib.makeSearchWhere(dbSchemaInfo, row, sc);
//			sql = initLib.makeListQueryString(session, dbSchemaInfo);
			sql = initLib.makeListQueryString(session, dbSchemaInfo, tblParamWhere);
			sql = initLib.ReplaceRequestData(sql,row,session);
					
//			sql = " SELECT " + makeFieldList4Table(dbSchemaInfo) + " FROM ( " + sql + ") src "
//					+ makeSearchWhere(dbSchemaInfo, row) + makeOuterQuery(dbSchemaInfo, row);
			
			sql = " SELECT " + makeFieldList4Table(dbSchemaInfo) + " FROM ( " + sql + ") src "
					+ makeOuterQuery(dbSchemaInfo, row);
			
			Map<String,String> param = new HashMap<String,String>();
			param.put("sql", sql);
			
			List<Map<String,Object>> ret = sqlSession.selectList("lib_v1.0.commonSql", param);
			oneContent.put("list", ret);
			
			
			Collections.sort(dbSchemaInfo, new Comparator<Object>() {
				// Comparable 인터페이스를 구현하여 전달
				@SuppressWarnings("unchecked")
				@Override
				public int compare(Object s1, Object s2) {
					return (Integer)((Map<String,Object>)s1).get("fdSeq") - (Integer)((Map<String,Object>)s2).get("fdSeq");
				}
			});
			
			for (Map<String, String> item : dbSchemaInfo) {
				if ( "Y".equals(item.get("fdIsTable")) ) {
					Map<String, Object> tmp = new HashMap<String, Object>();
					tmp.put("fdCd", item.get("fdAliasNm"));
					tmp.put("fdLbl", item.get("fdLabel"));
					fieldInfo.add(tmp);
				}
			}
			oneContent.put("field", fieldInfo);
			
		} else {
			oneContent.put("list", fieldInfo);
			oneContent.put("field", fieldInfo);
		}
		
		return oneContent;
	}
	
	
}



