package kr.co.automart.jewelry.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
//import lombok.extern.slf4j.Slf4j;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

//@Slf4j
@Service
public class Init_Lib_V1 {
	
	@Autowired
	@Qualifier("sqlSession")
	private SqlSessionTemplate sqlSession;
	
	
	@Autowired
	private CommonUtilAES256 commAESUtil;
	
	private	int	menuIdx = 0;
	private int	menuIdx4pid = -1;
	private	String cmCode = "";
	private	String menuNav = "<li><a href=\"/\"><i class=\"fa fa-dashboard\"></i> Home</a></li>";
	
	
	public Map<String,String> getMenu(Model model, HttpSession session,String pid) {
		
		String menuStr = "";
		String gid = (String) session.getAttribute(SessionConstant.SESSION_GROUP);
		String lvl = (String) session.getAttribute(SessionConstant.SESSION_LVL);
		
		List<Map<String,String>> menuInfo = getMenuList(session,gid,lvl);
		
		menuIdx = 0;
		menuIdx4pid = -1;
		menuNav = "<li><a href=\"/\"><i class=\"fa fa-dashboard\"></i> Home</a></li>";
		menuNav = "";
		cmCode = "";
		menuStr = getOneDepthString(menuInfo,menuIdx,1,"",pid);
		model.addAttribute("menus", menuStr);
//		menuNav = "<li><a href=\"/\"><i class=\"fa fa-home\"></i> Home</a></li>" + menuNav;
		model.addAttribute("menuNav", menuNav);
		
		Map<String,String> cm = null;
		
		if (menuIdx4pid != -1) {
			cm = menuInfo.get(menuIdx4pid);
			model.addAttribute("cm", cm);
		}
		return cm;
	}
	
	
	
	private	String getOneDepthString(List<Map<String,String>> menuInfo, int idx, int depth, String pcode, String pid) {
		String ret = "";
		int i = idx;
		int prevDepth = depth;
		
		while (i < menuInfo.size()) {
			Map<String,String> cm = menuInfo.get(i);
			String	cur_pcode						= cm.get("p_menu_code").trim();
			int		cur_depth						= Integer.valueOf(cm.get("menuDepth"));
			String	cur_menuTitle					= cm.get("menuTitle").trim();
			String	cur_jsClass						= cm.get("jsClass").trim();
			String	cur_menu_code_value				= cm.get("menu_code_value").trim();
			String	active = "";
			String	cur_grpTitle					= cm.get("grptTitle").trim();
			
			String	cur_visible				= cm.get("visible").trim();
			
			if ((cur_jsClass).equals("/"+pid)) {
				menuIdx4pid = i;
//				menuNav = "<li class=\"active\">"+cur_menuTitle+"</li>";
				menuNav = cur_menuTitle;
				active = " active ";
				cmCode = cur_menu_code_value;
			}
			
			
			if (cur_visible != null && "false".equals(cur_visible.toLowerCase())) {
				
				if ((cur_jsClass).equals("/"+pid)) {
					menuIdx4pid = i;
					cmCode = cur_menu_code_value;
				}
				
				menuIdx = i;
				i++;
				continue;
			}
			
/*			
			if ((cur_jsClass).equals("/"+pid)) {
				menuIdx4pid = i;
//				menuNav = "<li class=\"active\">"+cur_menuTitle+"</li>";
				menuNav = cur_menuTitle;
				active = " active ";
				cmCode = cur_menu_code_value;
			}
			
*/			
			
			String	next_pcode = "";
			int		next_depth = 0;
			Map<String,String> cm_next = null;
			if (i<menuInfo.size()-1) {
				cm_next = menuInfo.get(i+1);
			}
			if (cm_next != null) {
				next_pcode		= cm_next.get("p_menu_code");
				next_depth		= Integer.valueOf(cm_next.get("menuDepth"));
			}
			
			if (prevDepth > cur_depth) return ret;
			if (!pcode.equals(cur_pcode) && cur_depth > 1) return ret;
			
			
			if (!"".equals(cur_grpTitle)) {
//				ret += "<li class=\"header\">"+cur_grpTitle+"</li>";
			}

			
			
			if (prevDepth == cur_depth) {
				
				String submenus = "";
				if (next_pcode.equals(cur_menu_code_value) && cur_depth < next_depth) {
					
					submenus += getOneDepthString(menuInfo, i+1, cur_depth+1, cur_menu_code_value, pid);
					
					if ("".equals(active) && cmCode.startsWith(cur_menu_code_value)) {
//						submenus = "<ul class=\"treeview-menu active \">" + submenus;
						submenus = "<div class=\"submenu\"><ul>" + submenus;
						
//						menuNav = "<li><a href=\""+cur_jsClass+"\">"+cur_menuTitle+"</a></li>"+menuNav;
						menuNav = cur_menuTitle;
						
					} else {
//						submenus = "<ul class=\"treeview-menu\">" + submenus;
						submenus = "<div class=\"submenu\"><ul>" + submenus;
					}
					submenus += "</ul></div>";
					
					i = menuIdx;
				}

				
/*				
				<li class="on"><a href="#">공매공고</a></li>
				<li><a href="#">공매물건</a>
					<div class="submenu">
						<ul>
							<li><a href="/">진행물건검색</a></li>
							<li><a href="/">매각결과검색</a></li>
						</ul>
					</div>
				</li>

*/				
				
				if ("".equals(active) && cmCode.startsWith(cur_menu_code_value)) {
					active = " on ";
				}
				
				ret += "<li class=\" " + active + "\">";
				
				if ("".equals(cur_jsClass)) {
					ret += "<a href=\"#\">";
				} else {
					ret += "<a href=\""+cur_jsClass+"\">";
				}
				
//				if (cur_depth > 1) ret += "<i class=\"fa fa-circle-o\"></i> ";
				ret += cur_menuTitle;
//				if (next_pcode.equals(cur_menu_code_value) && cur_depth < next_depth) {
//					ret += "<span class=\"pull-right-container\">";
//					ret += "<i class=\"fa fa-angle-left pull-right\"></i>";
//					ret += "</span>";
//				}
				ret += "</a>";
				ret += submenus;
				
				ret += "</li>";
				
			};
			
			menuIdx = i;
			i = i+1;
			
		}
		
		return ret;
	}
	
	
	public List<Map<String,String>> getSubContentList(HttpSession session, List<Map<String, String>> dbSchemaInfo
			,Map<String, Object> reqdata) {
		
		String sql = "";
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		String tblParamWhere = makeSearchWhere(dbSchemaInfo, reqdata, sc);
//		sql = makeListQueryString(session, dbSchemaInfo, tblParamWhere);
		
		
		if ("1".equals(dbSchemaInfo.get(0).get("qryType")) ) {
			sql = makeListQueryString(session, dbSchemaInfo, tblParamWhere);
		} else if ("11".equals(dbSchemaInfo.get(0).get("qryType")) ) {
			sql = makeListQueryString_ProcedureCall(session, dbSchemaInfo, reqdata);
		}
		
		sql = ReplaceRequestData(sql,reqdata,session);
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("sql", sql);
		
		List<Map<String,String>> ret = sqlSession.selectList("lib_v1.0.commonSql", param);
		
		ret = makeEncryptList_String(dbSchemaInfo, ret, sc);
		
		return ret;
		
	}

	public String ReplaceRequestData(String sql, Map<String, Object> reqdata, HttpSession session) {
		
		if (reqdata != null) {
			for (String key : reqdata.keySet()) {
				String tmp = "#\\{"+key+"\\}";
				Object p = reqdata.get(key);
				if (p == null) {
					continue;
				}
				if (p instanceof String) {
					sql = sql.replaceAll(tmp, convertSpecialStringInQuery((String) reqdata.get(key)));
				} else if (p instanceof String[]) {
					String [] ra = (String[]) reqdata.get(key);
					sql = sql.replaceAll(tmp, convertSpecialStringInQuery(ra[0]));
				} else if (p instanceof Integer) {
					int tmpi = (int) reqdata.get(key);
					sql = sql.replaceAll(tmp, convertSpecialStringInQuery( String.valueOf(tmpi) ));
				} else {
					sql = sql.replaceAll(tmp, convertSpecialStringInQuery((String) reqdata.get(key)));
				}
				
				
			}
		}
		
		sql = sql.replaceAll("#\\{sessionlid\\}", session.getAttribute(SessionConstant.SESSION_LID) == null ? "":(String) session.getAttribute(SessionConstant.SESSION_LID));
		sql = sql.replaceAll("#\\{sessiongid\\}", session.getAttribute(SessionConstant.SESSION_GROUP) == null ? "":(String) session.getAttribute(SessionConstant.SESSION_GROUP));
		sql = sql.replaceAll("#\\{sessioncomcd\\}", session.getAttribute(SessionConstant.SESSION_COMCD) == null ? "":(String) session.getAttribute(SessionConstant.SESSION_COMCD));
		sql = sql.replaceAll("#\\{sessionlvl\\}", session.getAttribute(SessionConstant.SESSION_LVL) == null ? "":(String) session.getAttribute(SessionConstant.SESSION_LVL));
		sql = sql.replaceAll("#\\{sessionId\\}", session.getAttribute(SessionConstant.SESSION_ID) == null ? "":(String) session.getAttribute(SessionConstant.SESSION_ID));
		sql = sql.replaceAll("#\\{sessionComDbPrefix\\}", (String) SessionConstant.DB_PRE_COMDB);
		sql = sql.replace("#{hkey}", SessionConstant.HKEY);
		
/*		
		sql = sql.replaceAll("#\\{sessionlid\\}", (String) session.getAttribute(SessionConstant.SESSION_LID));
		sql = sql.replaceAll("#\\{sessiongid\\}", (String) session.getAttribute(SessionConstant.SESSION_GROUP));
		sql = sql.replaceAll("#\\{sessioncomcd\\}", (String) session.getAttribute(SessionConstant.SESSION_COMCD));
		sql = sql.replaceAll("#\\{sessionlvl\\}", (String) session.getAttribute(SessionConstant.SESSION_LVL));
		sql = sql.replaceAll("#\\{sessionComDbPrefix\\}", (String) SessionConstant.DB_PRE_COMDB);
//		sql = sql.replaceAll("#\\{hkey\\}", SessionConstant.HKEY);
*/
		String tmp = SessionConstant.DB_PRE_COMDB + ((String) session.getAttribute(SessionConstant.SESSION_COMCD));
		sql = sql.replaceAll("#\\{ccd\\}", tmp);
		
		return sql;
		
	}
	
	
	public String makeListQueryString(HttpSession session, List<Map<String, String>> dbSchemaInfo, String tblParamWhere) {
		
		String selFdList = "";
		String prevtblseq = "";
		String sqlFrom = "";
		String sqlOrderBy = "";
		String sqlGroupBy = "";
		String sqlWhere = "";
		String sqlJoin = "";
		String sql = "";
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			Map<String, String> item = dbSchemaInfo.get(i);
			
			if ("".equals(prevtblseq)) {
				sqlFrom += " FROM " + item.get("dbNm") + "." + item.get("tblNm") + " " + item.get("tblAlias")+"\n";
				if (!"".equals(item.get("tblOrder").trim())) {
					sqlOrderBy += " ORDER BY " + item.get("tblOrder")+"\n";
				}
				if (!"".equals(item.get("tblGroupBy").trim())) {
					sqlGroupBy += " GROUP BY " + item.get("tblGroupBy")+"\n";
				}
				if (!"".equals(item.get("tblWhere").trim())) {
					sqlWhere += " WHERE " + item.get("tblWhere")+"\n";
				}
				
			} else if (!prevtblseq.equals(String.valueOf(item.get("tblSeq")))) {
				switch (item.get("qryType")){
					case "1" :
						sqlJoin += " LEFT OUTER JOIN " + item.get("dbNm") + "." + item.get("tblNm") + " " + item.get("tblAlias");
						sqlJoin += " ON " + item.get("tblWhere")+"\n";
						break;
				}
			}
			
			
			prevtblseq = String.valueOf(item.get("tblSeq"));
			
			if ("Y".equals(item.get("fdNoSelect").trim().toUpperCase() ) ) {
				if (!"".equals(selFdList)) selFdList += ",";
				selFdList += "\t '' \t " +item.get("fdAliasNm") + "\n";
				continue;
			}
			
			switch (item.get("fdDataType").trim().toLowerCase()) {
			case "int" :
			case "smallint" :
			case "double" :
			case "float" :
				if (!"".equals(selFdList)) selFdList += ",";
				selFdList += "\t IFNULL("+item.get("tblAlias")+"."+item.get("fdNm") + ",0) \t " +item.get("fdAliasNm") + "\n";
				break;
				
			case "varchar" :
			case "text" :
			case "longtext" :
			case "char" :
				if (!"".equals(selFdList)) selFdList += ",";
				if ("Y".equals(item.get("fdIsEncrypt").toUpperCase())) {
					selFdList += " CONVERT(IF("+item.get("tblAlias")+"."+item.get("fdNm")+" IS NULL,''"
							+ ", AES_DECRYPT(UNHEX("+item.get("tblAlias")+"."+item.get("fdNm")+"),'"+SessionConstant.HKEY+"')) USING UTF8) \t " +item.get("fdAliasNm") + "\n";
				} else {
					selFdList += " IFNULL("+item.get("tblAlias")+"."+item.get("fdNm") + ",'') \t " +item.get("fdAliasNm") + "\n";
				}
				break;
				
			case "" :
				if (!"".equals(selFdList)) selFdList += ",";
				selFdList += " IFNULL("+item.get("fdNm") + ",'') \t " +item.get("fdAliasNm") + "\n";
				break;
			case "date" :
				if (!"".equals(selFdList)) selFdList += ",";
				selFdList += " IFNULL(DATE_FORMAT("+item.get("tblAlias")+"."+item.get("fdNm") + ", '%Y-%m-%d'),'') " +item.get("fdAliasNm") + "\n";
				break;
			case "timestamp" :
			case "datetime" :
				if (!"".equals(selFdList)) selFdList += ",";
				selFdList += " IFNULL(DATE_FORMAT("+item.get("tblAlias")+"."+item.get("fdNm") + ", '%Y-%m-%d %H:%i:%s'),'') " +item.get("fdAliasNm") + "\n";
				break;
			case "time" :
				if (!"".equals(selFdList)) selFdList += ",";
				selFdList += " IFNULL(DATE_FORMAT("+item.get("tblAlias")+"."+item.get("fdNm") + ", '%H:%i:%s'),'') " +item.get("fdAliasNm") + "\n";
				break;
			case "expression" :
				if (!"".equals(selFdList)) selFdList += ",";
				selFdList += item.get("fdNm")+ " " + item.get("fdAliasNm") + "\n";
				break;
			}
		}
		
		if (!"".equals(selFdList)) selFdList = " SELECT " + selFdList;
		sql = selFdList + sqlFrom + sqlJoin + sqlWhere + tblParamWhere + sqlGroupBy + sqlOrderBy;
		
		return sql;
		
	}
	
	
	
	
	
	public String makeListQueryString_ProcedureCall(HttpSession session, List<Map<String, String>> dbSchemaInfo, Map<String, Object> row) {
		
		String FdValList = "";
		String sql = "";
		String sqlFrom = "";
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			Map<String, String> item = dbSchemaInfo.get(i);
			String fdAliasnm = item.get("fdAliasNm");
//			String fdNm = item.get("fdNm");
			String fdIsTable = item.get("fdIsTable");
			String autoCode = item.get("fdAutoCode");
			if ( !"Y".equals(fdIsTable)) continue;
			
			if ("".equals(sqlFrom)) {
				sqlFrom = " " + item.get("dbNm") + "." + item.get("tblNm") + " ";
			}
			
			String fdval = "";
			boolean findkey = false;
			for (String key : row.keySet()) {
				if (fdAliasnm.equals(key)) {
					findkey = true;
//					fdval = row.get(key)==null?"":((String) row.get(key)).trim();
					fdval = row.get(key)==null?"":(String.valueOf(row.get(key))).trim();
					
					fdval = convertSpecialStringInQuery(fdval);
					String fdEncrypt = item.get("fdIsEncrypt").toUpperCase();
					
					if ("Y".equals(fdEncrypt)) {
						try {
							fdval = commAESUtil.decrypt(fdval, sc);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						fdval = " HEX(AES_ENCRYPT('"+fdval+"','"+SessionConstant.HKEY+"')) ";
					}
					
					switch (item.get("fdDataType").trim().toLowerCase()) {
					case "int" :
					case "smallint" :
					case "double" :
					case "float" :
						if (!"".equals(FdValList)) {
							FdValList += ",";
						}
						if ("".equals(fdval)) fdval = "0";
						FdValList += fdval;
						break;
						
					case "varchar" :
					case "text" :
					case "longtext" :
					case "char" :
						if (!"".equals(FdValList)) {
							FdValList += ",";
						}
						if (!"Y".equals(item.get("fdIsEncrypt").toUpperCase())) FdValList += "'" + fdval + "'";
						else FdValList += fdval;
						break;
						
					case "expression" :
						if (!"".equals(FdValList)) {
							FdValList += ",";
						}
						FdValList += item.get("fdNm");
						break;
					case "" :
						break;
						
					case "date" :
					case "time" :
					case "timestamp" :
					case "datetime" :
						if (!"".equals(FdValList)) {
							FdValList += ",";
						}
						if ("".equals(fdval)) {
							FdValList += " NULL ";
						} else {
							FdValList += " '" + fdval + "'";
						}
						break;
					}
					
				}
				
			}
//			if ("".equals(fdval)) {
			if (findkey == false) {
				if (!"".equals(FdValList)) {
					FdValList += ",";
				}
				FdValList += autoCode;
			}
			
		}
		
		if (!"".equals(FdValList)) {
			
			sql = " CALL " + sqlFrom + " ( " + FdValList + ")";
		}
		
		return sql;
		
	}
	
	
	
	public String makeInsertQueryString(HttpSession session, List<Map<String, String>> dbSchemaInfo, Map<String, Object> row) {
		
		String selFdList = "";
		String FdValList = "";
		String prevtblseq = "";
		String sqlFrom = "";
		String sql = "";
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			Map<String, String> item = dbSchemaInfo.get(i);
			String fdAliasnm = item.get("fdAliasNm");
			String fdNm = item.get("fdNm");
			String fdIsForm = item.get("fdIsForm");
			if ( !"Y".equals(fdIsForm)) continue;
			
			for (String key : row.keySet()) {
				if (fdAliasnm.equals(key)) {
//					String fdval = row.get(key)==null?"":((String) row.get(key)).trim();
					String fdval = row.get(key)==null?"":(String.valueOf(row.get(key))).trim();
					String fdNoSaveBlank = item.get("fdNoSaveBlank").toLowerCase();
					String fdExtra = item.get("fdExtra").toLowerCase();
					String fdAutoCode = item.get("fdAutoCode");
					String fdEncrypt = item.get("fdIsEncrypt").toUpperCase();
					
					fdval = convertSpecialStringInQuery(fdval);
					
					if ("".equals(prevtblseq)) {
						sqlFrom = " " + item.get("dbNm") + "." + item.get("tblNm") + " ";
					}
					
					prevtblseq = String.valueOf(item.get("tblSeq"));
					
					if ("auto_increment".equals(fdExtra)) {
						continue;
					}
					if ("y".equals(fdNoSaveBlank) && "".equals(fdval)) {
						continue;
					}
					if (!"".equals(fdAutoCode)) {
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						FdValList += fdAutoCode;
						selFdList += fdNm;
						continue;
					}
					
					if ("Y".equals(fdEncrypt)) {
						try {
							fdval = commAESUtil.decrypt(fdval, sc);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						fdval = " HEX(AES_ENCRYPT('"+fdval+"','"+SessionConstant.HKEY+"')) ";
					}
					
					switch (item.get("fdDataType").trim().toLowerCase()) {
					case "int" :
					case "smallint" :
					case "double" :
					case "float" :
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						if ("".equals(fdval)) fdval = "0";
						FdValList += fdval;
						selFdList += fdNm ;
						break;
						
					case "varchar" :
					case "text" :
					case "longtext" :
					case "char" :
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						selFdList += fdNm;
						if (!"Y".equals(item.get("fdIsEncrypt").toUpperCase())) FdValList += "'" + fdval + "'";
						else FdValList += fdval;
						break;
						
					case "expression" :
					case "" :
						break;
						
					case "date" :
					case "time" :
					case "timestamp" :
					case "datetime" :
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						selFdList += fdNm ;
						if ("".equals(fdval)) {
							FdValList += " NULL ";
						} else {
							FdValList += " '" + fdval + "'";
						}
						break;
					}
					
				}
				
			}
			
		}
		
		
		if (!"".equals(selFdList)) {
			selFdList += " ,REG_ID ,MOD_ID ,REG_DT ,MOD_DT ";
			FdValList += " ,'#{sessionlid}' ,'#{sessionlid}' ,NOW() ,NOW() ";
			sql = " INSERT INTO " + sqlFrom + " ( " + selFdList + " ) VALUES (" + FdValList + ")";
		}
		
		return sql;
		
	}
	
	
	public String makeInsertQueryString_Delete_Insert(HttpSession session, List<Map<String, String>> dbSchemaInfo, Map<String, Object> row) {
		
		String selFdList = "";
		String FdValList = "";
		String prevtblseq = "";
		String sqlFrom = "";
		String sql = "";
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		String deleteSql= "";
		String deleteSql_Where= "";
		boolean exist_fdNoSaveBlank = false;
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			Map<String, String> item = dbSchemaInfo.get(i);
			String fdAliasnm = item.get("fdAliasNm");
			String fdNm = item.get("fdNm");
			String fdIsForm = item.get("fdIsForm");
//			if ( !"Y".equals(fdIsForm)) continue;
			
			for (String key : row.keySet()) {
				if (fdAliasnm.equals(key)) {
//					String fdval = row.get(key)==null?"":((String) row.get(key)).trim();
					String fdval = row.get(key)==null?"":(String.valueOf( row.get(key)) ).trim();
					String fdNoSaveBlank = item.get("fdNoSaveBlank").toLowerCase();
					String fdExtra = item.get("fdExtra").toLowerCase();
					String fdAutoCode = item.get("fdAutoCode").toLowerCase();
					String fdEncrypt = item.get("fdIsEncrypt").toUpperCase();
					
					fdval = convertSpecialStringInQuery(fdval);
					
					if ("".equals(prevtblseq)) {
						sqlFrom = " " + item.get("dbNm") + "." + item.get("tblNm") + " ";
					}
					
					prevtblseq = String.valueOf(item.get("tblSeq"));
					
					if ("auto_increment".equals(fdExtra)) {
						continue;
					}
					if ("y".equals(fdNoSaveBlank) && "".equals(fdval)) {
						exist_fdNoSaveBlank = true;
						continue;
					}
					if (!"".equals(fdAutoCode)) {
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						FdValList += fdAutoCode;
						selFdList += fdNm;
						continue;
					}
					
					if ("Y".equals(fdEncrypt)) {
						try {
							fdval = commAESUtil.decrypt(fdval, sc);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						fdval = " HEX(AES_ENCRYPT('"+fdval+"','"+SessionConstant.HKEY+"')) ";
					}
					
					
					if ("deletekey".equals(fdExtra)) {
						if ("".equals(deleteSql_Where)) {
							deleteSql_Where = " WHERE ";
						} else {
							deleteSql_Where += " AND ";
						}
						deleteSql_Where += fdNm + " = '" + fdval + "' ";
						if ("".equals(deleteSql)) {
							deleteSql = " DELETE FROM " + item.get("dbNm") + "." + item.get("tblNm") + " "; 
						}
					}
					
					if ( !"Y".equals(fdIsForm)) continue;

					if (!"deletekey".equals(fdExtra)) {
						
						switch (item.get("fdDataType").trim().toLowerCase()) {
						case "int" :
						case "smallint" :
						case "double" :
						case "float" :
							if (!"".equals(selFdList)) {
								selFdList += ",";
								FdValList += ",";
							}
							if ("".equals(fdval)) fdval = "0";
							FdValList += fdval;
							selFdList += fdNm ;
							break;
							
						case "varchar" :
						case "text" :
						case "longtext" :
						case "char" :
							if (!"".equals(selFdList)) {
								selFdList += ",";
								FdValList += ",";
							}
							selFdList += fdNm;
							if (!"Y".equals(item.get("fdIsEncrypt").toUpperCase())) FdValList += "'" + fdval + "'";
							else FdValList += fdval;
							break;
							
						case "expression" :
						case "" :
							break;
							
						case "date" :
						case "time" :
						case "timestamp" :
						case "datetime" :
							if (!"".equals(selFdList)) {
								selFdList += ",";
								FdValList += ",";
							}
							selFdList += fdNm ;
							if ("".equals(fdval)) {
								FdValList += " NULL ";
							} else {
								FdValList += " '" + fdval + "'";
							}
							break;
						}
						
					} else {
						
						switch (item.get("fdDataType").trim().toLowerCase()) {
						case "int" :
						case "smallint" :
						case "double" :
						case "float" :
							if (!"".equals(selFdList)) {
								selFdList += ",";
								FdValList += ",";
							}
							if ("".equals(fdval)) fdval = "0";
							FdValList += fdval;
							selFdList += fdNm ;
							break;
							
						case "varchar" :
						case "text" :
						case "longtext" :
						case "char" :
							if (!"".equals(selFdList)) {
								selFdList += ",";
								FdValList += ",";
							}
							selFdList += fdNm;
							if (!"Y".equals(item.get("fdIsEncrypt").toUpperCase())) FdValList += "'" + fdval + "'";
							else FdValList += fdval;
							break;
							
						case "expression" :
						case "" :
							break;
							
						case "date" :
						case "time" :
						case "timestamp" :
						case "datetime" :
							if (!"".equals(selFdList)) {
								selFdList += ",";
								FdValList += ",";
							}
							selFdList += fdNm ;
							if ("".equals(fdval)) {
								FdValList += " NULL ";
							} else {
								FdValList += " '" + fdval + "'";
							}
							break;
						}
						
					}
					
					
				}
				
			}
			
		}
		
		
		if (!"".equals(selFdList)) {
			selFdList += " ,REG_ID ,MOD_ID ,REG_DT ,MOD_DT ";
			FdValList += " ,'#{sessionlid}' ,'#{sessionlid}' ,NOW() ,NOW() ";
			sql = " INSERT INTO " + sqlFrom + " ( " + selFdList + " ) VALUES (" + FdValList + ")";
		}
		
		if (!"".equals(deleteSql)) {
			if ( exist_fdNoSaveBlank == false ) {
				sql = deleteSql + deleteSql_Where + " #;# " + sql+ " ";
			} else {
				sql = deleteSql + deleteSql_Where + "  ";
			}
		}
		
		return sql;
		
	}
	
	
	public String makeInsertQueryString_Delete_Insert_List(HttpSession session, List<Map<String, String>> dbSchemaInfo, Map<String, Object> row) {
		
		String selFdList = "";
		String FdValList = "";
		String prevtblseq = "";
		String sqlFrom = "";
		String sql = "";
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		String deleteSql= "";
		String deleteSql_Where= "";
		List<String> listFdNm = new ArrayList<>();
		List<String> listFdVal = new ArrayList<>();
		boolean exist_fdNoSaveBlank = false;
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			Map<String, String> item = dbSchemaInfo.get(i);
			String fdAliasnm = item.get("fdAliasNm");
			String fdNm = item.get("fdNm");
			String fdIsForm = item.get("fdIsForm");
//			if ( !"Y".equals(fdIsForm)) continue;
			
			for (String key : row.keySet()) {
				if (fdAliasnm.equals(key)) {
//					String fdval = row.get(key)==null?"":((String) row.get(key)).trim();
					String fdval = row.get(key)==null?"":(String.valueOf(row.get(key))).trim();
					
					if (fdval.split(",",-1).length > 1) {
						listFdNm.add(fdNm);
						listFdVal.add(fdval);
						fdval = "#{"+fdNm+"}";
					}
					
					String fdNoSaveBlank = item.get("fdNoSaveBlank").toLowerCase();
					String fdExtra = item.get("fdExtra").toLowerCase();
					String fdAutoCode = item.get("fdAutoCode").toLowerCase();
					String fdEncrypt = item.get("fdIsEncrypt").toUpperCase();
					
					fdval = convertSpecialStringInQuery(fdval);
					
					if ("".equals(prevtblseq)) {
						sqlFrom = " " + item.get("dbNm") + "." + item.get("tblNm") + " ";
					}
					
					prevtblseq = String.valueOf(item.get("tblSeq"));
					
					if ("auto_increment".equals(fdExtra)) {
						continue;
					}
					if ("y".equals(fdNoSaveBlank) && "".equals(fdval)) {
						exist_fdNoSaveBlank = true;
						continue;
					}
					if (!"".equals(fdAutoCode)) {
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						FdValList += fdAutoCode;
						selFdList += fdNm;
						continue;
					}
					
					if ("Y".equals(fdEncrypt)) {
						try {
							fdval = commAESUtil.decrypt(fdval, sc);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						fdval = " HEX(AES_ENCRYPT('"+fdval+"','"+SessionConstant.HKEY+"')) ";
					}
					
					
					if ("deletekey".equals(fdExtra)) {
						if ("".equals(deleteSql_Where)) {
							deleteSql_Where = " WHERE ";
						} else {
							deleteSql_Where += " AND ";
						}
						deleteSql_Where += fdNm + " = '" + fdval + "' ";
						if ("".equals(deleteSql)) {
							deleteSql = " DELETE FROM " + item.get("dbNm") + "." + item.get("tblNm") + " "; 
						}
					}
					
					if ( !"Y".equals(fdIsForm)) continue;

					switch (item.get("fdDataType").trim().toLowerCase()) {
					case "int" :
					case "smallint" :
					case "double" :
					case "float" :
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						if ("".equals(fdval)) fdval = "0";
						FdValList += fdval;
						selFdList += fdNm ;
						break;
						
					case "varchar" :
					case "text" :
					case "longtext" :
					case "char" :
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						selFdList += fdNm;
						if (!"Y".equals(item.get("fdIsEncrypt").toUpperCase())) FdValList += "'" + fdval + "'";
						else FdValList += fdval;
						break;
						
					case "expression" :
					case "" :
						break;
						
					case "date" :
					case "time" :
					case "timestamp" :
					case "datetime" :
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						selFdList += fdNm ;
						if ("".equals(fdval)) {
							FdValList += " NULL ";
						} else {
							FdValList += " '" + fdval + "'";
						}
						break;
					}
					
				}
				
			}
			
		}
		
		if (!"".equals(selFdList)) {
			selFdList += " ,REG_ID ,MOD_ID ,REG_DT ,MOD_DT ";
			FdValList += " ,'#{sessionlid}' ,'#{sessionlid}' ,NOW() ,NOW() ";
			
			if (listFdNm.size() > 0) {
				int fdcnt = listFdNm.size();
				int listcnt = listFdVal.get(0).split(",",-1).length;
				String multival = "";
				for (int i=0;i<listcnt;i++) {
					if (!"".equals(multival)) {
						multival += ",";
					}
					String tmpValinfo = FdValList;
					for (int j=0;j<fdcnt;j++) {
						tmpValinfo = tmpValinfo.replaceAll("#\\{"+listFdNm.get(j)+"\\}", "#{"+listFdNm.get(j)+"_"+i+"}");
					}
					multival += "(" + tmpValinfo + ")";
				}
				for (int i=0;i<fdcnt;i++) {
					String fdNm = listFdNm.get(i);
					String [] valist = listFdVal.get(i).split(",",-1);
					
					for (int j=0;j<valist.length;j++) {
						multival = multival.replaceAll("#\\{"+fdNm+"_"+j+"\\}", valist[j]);
					}
				}
				
				sql = " INSERT INTO " + sqlFrom + " ( " + selFdList + " ) VALUES " + multival ;
				
			} else {
				sql = " INSERT INTO " + sqlFrom + " ( " + selFdList + " ) VALUES (" + FdValList + ")";
			}
		}
		
		if (!"".equals(deleteSql)) {
			if (exist_fdNoSaveBlank == false) {
				sql = deleteSql + deleteSql_Where + " #;# " + sql+ " ";
			} else {
				sql = deleteSql + deleteSql_Where + " ";
			}
		}
		
		return sql;
		
	}
	
	
	
	
	public String makeInsertQueryString_ProcedureCall(HttpSession session, List<Map<String, String>> dbSchemaInfo, Map<String, Object> row) {
		
		String FdValList = "";
		String sql = "";
		String sqlFrom = "";
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			Map<String, String> item = dbSchemaInfo.get(i);
			String fdAliasnm = item.get("fdAliasNm");
			String fdIsForm = item.get("fdIsForm");
			if ( !"Y".equals(fdIsForm)) continue;
			
			if ("".equals(sqlFrom)) {
				sqlFrom = " " + item.get("dbNm") + "." + item.get("tblNm") + " ";
			}
			
			
			for (String key : row.keySet()) {
				if (fdAliasnm.equals(key)) {
//					String fdval = row.get(key)==null?"":((String) row.get(key)).trim();
					String fdval = row.get(key)==null?"":(String.valueOf( row.get(key)) ).trim();
					
					fdval = convertSpecialStringInQuery(fdval);
					String fdEncrypt = item.get("fdIsEncrypt").toUpperCase();
					
					if ("Y".equals(fdEncrypt)) {
						try {
							fdval = commAESUtil.decrypt(fdval, sc);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						fdval = " HEX(AES_ENCRYPT('"+fdval+"','"+SessionConstant.HKEY+"')) ";
					}
					
					switch (item.get("fdDataType").trim().toLowerCase()) {
					case "int" :
					case "smallint" :
					case "double" :
					case "float" :
						if (!"".equals(FdValList)) {
							FdValList += ",";
						}
						if ("".equals(fdval)) fdval = "0";
						FdValList += fdval;
						break;
						
					case "varchar" :
					case "text" :
					case "longtext" :
					case "char" :
						if (!"".equals(FdValList)) {
							FdValList += ",";
						}
						if (!"Y".equals(item.get("fdIsEncrypt").toUpperCase())) FdValList += "'" + fdval + "'";
						else FdValList += fdval;
						break;
						
					case "expression" :
						if (!"".equals(FdValList)) {
							FdValList += ",";
						}
						FdValList += item.get("fdNm");
						break;
					case "" :
						break;
						
					case "date" :
					case "time" :
					case "timestamp" :
					case "datetime" :
						if (!"".equals(FdValList)) {
							FdValList += ",";
						}
						if ("".equals(fdval)) {
							FdValList += " NULL ";
						} else {
							FdValList += " '" + fdval + "'";
						}
						break;
					}
					
				}
				
			}
			
		}
		
		if (!"".equals(FdValList)) {
			
			sql = " CALL " + sqlFrom + " ( " + FdValList + ")";
		}
		
		return sql;
		
	}
	
	
	
	
	
	public String makeUpdateQueryString(HttpSession session, List<Map<String, String>> dbSchemaInfo, Map<String, Object> row) {
		
		String selFdList = "";
		String prevtblseq = "";
		String sqlFrom = "";
		String sqlWhere = "";
		String sql = "";
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			Map<String, String> item = dbSchemaInfo.get(i);
			String fdAliasnm = item.get("fdAliasNm");
			String fdNm = item.get("fdNm");
			String fdIsForm = item.get("fdIsForm");
			if ( !"Y".equals(fdIsForm)) continue;
			
			for (String key : row.keySet()) {
				if (fdAliasnm.equals(key)) {
//					String fdval = row.get(key)==null?"":((String) row.get(key)).trim();
					String fdval = row.get(key)==null?"":(String.valueOf( row.get(key)) ).trim();
					String fdNoSaveBlank = item.get("fdNoSaveBlank").toLowerCase();
					String fdExtra = item.get("fdExtra").toLowerCase();
//					String fdAutoCode = item.get("fdAutoCode").toLowerCase();
					String fdAutoCode = item.get("fdAutoCode");
					String fdEncrypt = item.get("fdIsEncrypt").toUpperCase();
					
					fdval = convertSpecialStringInQuery(fdval);
					
					if ("".equals(prevtblseq)) {
						sqlFrom = " " + item.get("dbNm") + "." + item.get("tblNm") + " ";
					}
					
					prevtblseq = String.valueOf(item.get("tblSeq"));
					
					if ("auto_increment".equals(fdExtra) || "key_field".equals(fdExtra)) {
						int listcnt = fdval.split(",",-1).length;
						String [] fdvals = fdval.split(",",-1);
						
						try {

							if (listcnt > 1) {
								for (int tt=0;tt<listcnt;tt++) {
									if ("Y".equals(fdEncrypt)) {
										fdvals[tt] = commAESUtil.decrypt(fdvals[tt], sc);
										fdvals[tt] = convertSpecialStringInQuery(fdvals[tt]);
										fdvals[tt] = " HEX(AES_ENCRYPT('"+convertSpecialStringInQuery(fdvals[tt])+"','"+SessionConstant.HKEY+"')) ";
									} else {
										fdvals[tt] = convertSpecialStringInQuery(fdvals[tt]);
									}
								}
								
								String fdvalstr = "";
								for (int tt=0;tt<listcnt;tt++) {
									if (!"".equals(fdvalstr)) fdvalstr += ",";
									if (!"Y".equals(fdEncrypt)) {
										fdvalstr += " '" + fdvals[tt] + "' ";
									} else {
										fdvalstr += " " + fdvals[tt] + " ";
									}
								}
								if (!"".equals(sqlWhere)) {
									sqlWhere += " AND " + fdNm + " IN ( " + fdvalstr + " )";
								} else {
									sqlWhere += " WHERE " + fdNm + " IN ( " + fdvalstr + " )";
								}
							} else {
								if ("Y".equals(fdEncrypt)) {
									fdval = commAESUtil.decrypt(fdval, sc);
									fdval = convertSpecialStringInQuery(fdval);
									fdval = " HEX(AES_ENCRYPT('"+convertSpecialStringInQuery(fdval)+"','"+SessionConstant.HKEY+"')) ";
								} else {
									fdval = "'" + convertSpecialStringInQuery(fdval) +"'";
								}
								if (!"".equals(sqlWhere)) {
									sqlWhere += " AND " + fdNm + " = " + fdval + " ";
								} else {
									sqlWhere += " WHERE " + fdNm + " = " + fdval + " ";
								}
							}
						
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						continue;
					}
					
					
					
					if ("Y".equals(fdEncrypt)) {
						try {
							fdval = commAESUtil.decrypt(fdval, sc);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						fdval = " HEX(AES_ENCRYPT('"+fdval+"','"+SessionConstant.HKEY+"')) ";
					}
					
					
					
					if ("y".equals(fdNoSaveBlank) && "".equals(fdval)) {
						continue;
					}
					if (!"".equals(fdAutoCode)) {
						if (!"".equals(selFdList)) selFdList += ",";
						selFdList += fdNm + " = " + fdAutoCode;
						continue;
					}
					
					switch (item.get("fdDataType").trim().toLowerCase()) {
					case "int" :
					case "smallint" :
					case "double" :
					case "float" :
						if (!"".equals(selFdList)) selFdList += ",";
						if ("".equals(fdval)) fdval = "0";
						selFdList += fdNm + " = " + fdval;
						break;
						
					case "varchar" :
					case "text" :
					case "longtext" :
					case "char" :
						if (!"".equals(selFdList)) selFdList += ",";
						selFdList += fdNm + " = ";
						if (!"Y".equals(item.get("fdIsEncrypt").toUpperCase())) selFdList += "'" + fdval + "'";
						else selFdList += fdval;
						break;
						
					case "expression" :
					case "" :
						break;
						
					case "date" :
					case "time" :
					case "timestamp" :
					case "datetime" :
						if (!"".equals(selFdList)) selFdList += ",";
						if ("".equals(fdval)) {
							selFdList += fdNm + " = " + "NULL ";
						} else {
							selFdList += fdNm + " = " + "'" + fdval + "'";
						}
						break;
					}
					
				}
				
			}
			
		}
		
		
		if (!"".equals(selFdList)) {
			selFdList += " ,MOD_ID = '#{sessionlid}' ,MOD_DT = NOW() ";
			sql = " UPDATE " + sqlFrom + " SET " + selFdList + sqlWhere;
		}
		
		return sql;
		
	}
	
	
	public String makeUpdateQueryString_Delete_Insert(HttpSession session, List<Map<String, String>> dbSchemaInfo, Map<String, Object> row) {
		
		String selFdList = "";
		String FdValList = "";
		String prevtblseq = "";
		String sqlFrom = "";
		String sql = "";
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		String deleteSql= "";
		String deleteSql_Where= "";
		boolean exist_fdNoSaveBlank = false;
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			Map<String, String> item = dbSchemaInfo.get(i);
			String fdAliasnm = item.get("fdAliasNm");
			String fdNm = item.get("fdNm");
			String fdIsForm = item.get("fdIsForm");
//			if ( !"Y".equals(fdIsForm)) continue;
			
			for (String key : row.keySet()) {
				if (fdAliasnm.equals(key)) {
//					String fdval = row.get(key)==null?"":((String) row.get(key)).trim();
					String fdval = row.get(key)==null?"":(String.valueOf(row.get(key))).trim();
					String fdNoSaveBlank = item.get("fdNoSaveBlank").toLowerCase();
					String fdExtra = item.get("fdExtra").toLowerCase();
					String fdAutoCode = item.get("fdAutoCode").toLowerCase();
					String fdEncrypt = item.get("fdIsEncrypt").toUpperCase();
					
					fdval = convertSpecialStringInQuery(fdval);
					
					if ("".equals(prevtblseq)) {
						sqlFrom = " " + item.get("dbNm") + "." + item.get("tblNm") + " ";
					}
					
					prevtblseq = String.valueOf(item.get("tblSeq"));
					
					if ("auto_increment".equals(fdExtra)) {
						continue;
					}
					if ("y".equals(fdNoSaveBlank) && "".equals(fdval)) {
						exist_fdNoSaveBlank = true;
						continue;
					}
					if (!"".equals(fdAutoCode)) {
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						FdValList += fdAutoCode;
						selFdList += fdNm;
						continue;
					}
					
					if ("Y".equals(fdEncrypt)) {
						try {
							fdval = commAESUtil.decrypt(fdval, sc);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						fdval = " HEX(AES_ENCRYPT('"+fdval+"','"+SessionConstant.HKEY+"')) ";
					}
					
					
					if ("deletekey".equals(fdExtra)) {
						if ("".equals(deleteSql_Where)) {
							deleteSql_Where = " WHERE ";
						} else {
							deleteSql_Where += " AND ";
						}
						deleteSql_Where += fdNm + " = '" + fdval + "' ";
						if ("".equals(deleteSql)) {
							deleteSql = " DELETE FROM " + item.get("dbNm") + "." + item.get("tblNm") + " "; 
						}
					}
					
					if ( !"Y".equals(fdIsForm)) continue;

					
					switch (item.get("fdDataType").trim().toLowerCase()) {
					case "int" :
					case "smallint" :
					case "double" :
					case "float" :
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						if ("".equals(fdval)) fdval = "0";
						FdValList += fdval;
						selFdList += fdNm ;
						break;
						
					case "varchar" :
					case "text" :
					case "longtext" :
					case "char" :
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						selFdList += fdNm;
						if (!"Y".equals(item.get("fdIsEncrypt").toUpperCase())) FdValList += "'" + fdval + "'";
						else FdValList += fdval;
						break;
						
					case "expression" :
					case "" :
						break;
						
					case "date" :
					case "time" :
					case "timestamp" :
					case "datetime" :
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						selFdList += fdNm ;
						if ("".equals(fdval)) {
							FdValList += " NULL ";
						} else {
							FdValList += " '" + fdval + "'";
						}
						break;
					}
					
				}
				
			}
			
		}
		
		
		if (!"".equals(selFdList)) {
			selFdList += " ,REG_ID ,MOD_ID ,REG_DT ,MOD_DT ";
			FdValList += " ,'#{sessionlid}' ,'#{sessionlid}' ,NOW() ,NOW() ";
			sql = " INSERT INTO " + sqlFrom + " ( " + selFdList + " ) VALUES (" + FdValList + ")";
		}
		
		if (!"".equals(deleteSql)) {
			if ( exist_fdNoSaveBlank == false ) {
				sql = deleteSql + deleteSql_Where + " #;# " + sql+ " ";
			} else {
				sql = deleteSql + deleteSql_Where + "  ";
			}
		}
		
		return sql;
		
	}
	
	
	public String makeUpdateQueryString_Delete_Insert_List(HttpSession session, List<Map<String, String>> dbSchemaInfo, Map<String, Object> row) {
		
		String selFdList = "";
		String FdValList = "";
		String prevtblseq = "";
		String sqlFrom = "";
		String sql = "";
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		String deleteSql= "";
		String deleteSql_Where= "";
		List<String> listFdNm = new ArrayList<>();
		List<String> listFdVal = new ArrayList<>();
		boolean exist_fdNoSaveBlank = false;
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			Map<String, String> item = dbSchemaInfo.get(i);
			String fdAliasnm = item.get("fdAliasNm");
			String fdNm = item.get("fdNm");
			String fdIsForm = item.get("fdIsForm");
//			if ( !"Y".equals(fdIsForm)) continue;
			
			for (String key : row.keySet()) {
				if (fdAliasnm.equals(key)) {
//					String fdval = row.get(key)==null?"":((String) row.get(key)).trim();
					String fdval = row.get(key)==null?"":(String.valueOf( row.get(key)) ).trim();
					
					if (fdval.split(",",-1).length > 1) {
						listFdNm.add(fdNm);
						listFdVal.add(fdval);
						fdval = "#{"+fdNm+"}";
					}
					
					String fdNoSaveBlank = item.get("fdNoSaveBlank").toLowerCase();
					String fdExtra = item.get("fdExtra").toLowerCase();
					String fdAutoCode = item.get("fdAutoCode").toLowerCase();
					String fdEncrypt = item.get("fdIsEncrypt").toUpperCase();
					
					fdval = convertSpecialStringInQuery(fdval);
					
					if ("".equals(prevtblseq)) {
						sqlFrom = " " + item.get("dbNm") + "." + item.get("tblNm") + " ";
					}
					
					prevtblseq = String.valueOf(item.get("tblSeq"));
					
					if ("auto_increment".equals(fdExtra)) {
						continue;
					}
					if ("y".equals(fdNoSaveBlank) && "".equals(fdval)) {
						exist_fdNoSaveBlank = true;
						continue;
					}
					if (!"".equals(fdAutoCode)) {
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						FdValList += fdAutoCode;
						selFdList += fdNm;
						continue;
					}
					
					if ("Y".equals(fdEncrypt)) {
						try {
							fdval = commAESUtil.decrypt(fdval, sc);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						fdval = " HEX(AES_ENCRYPT('"+fdval+"','"+SessionConstant.HKEY+"')) ";
					}
					
					
					if ("deletekey".equals(fdExtra)) {
						if ("".equals(deleteSql_Where)) {
							deleteSql_Where = " WHERE ";
						} else {
							deleteSql_Where += " AND ";
						}
						deleteSql_Where += fdNm + " = '" + fdval + "' ";
						if ("".equals(deleteSql)) {
							deleteSql = " DELETE FROM " + item.get("dbNm") + "." + item.get("tblNm") + " "; 
						}
					}
					
					if ( !"Y".equals(fdIsForm)) continue;

					switch (item.get("fdDataType").trim().toLowerCase()) {
					case "int" :
					case "smallint" :
					case "double" :
					case "float" :
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						if ("".equals(fdval)) fdval = "0";
						FdValList += fdval;
						selFdList += fdNm ;
						break;
						
					case "varchar" :
					case "text" :
					case "longtext" :
					case "char" :
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						selFdList += fdNm;
						if (!"Y".equals(item.get("fdIsEncrypt").toUpperCase())) FdValList += "'" + fdval + "'";
						else FdValList += fdval;
						break;
						
					case "expression" :
					case "" :
						break;
						
					case "date" :
					case "time" :
					case "timestamp" :
					case "datetime" :
						if (!"".equals(selFdList)) {
							selFdList += ",";
							FdValList += ",";
						}
						selFdList += fdNm ;
						if ("".equals(fdval)) {
							FdValList += " NULL ";
						} else {
							FdValList += " '" + fdval + "'";
						}
						break;
					}
					
				}
				
			}
			
		}
		
		if (!"".equals(selFdList)) {
			selFdList += " ,REG_ID ,MOD_ID ,REG_DT ,MOD_DT ";
			FdValList += " ,'#{sessionlid}' ,'#{sessionlid}' ,NOW() ,NOW() ";
			
			if (listFdNm.size() > 0) {
				int fdcnt = listFdNm.size();
				int listcnt = listFdVal.get(0).split(",",-1).length;
				String multival = "";
				for (int i=0;i<listcnt;i++) {
					if (!"".equals(multival)) {
						multival += ",";
					}
					String tmpValinfo = FdValList;
					for (int j=0;j<fdcnt;j++) {
						tmpValinfo = tmpValinfo.replaceAll("#\\{"+listFdNm.get(j)+"\\}", "#{"+listFdNm.get(j)+"_"+i+"}");
					}
					multival += "(" + tmpValinfo + ")";
				}
				for (int i=0;i<fdcnt;i++) {
					String fdNm = listFdNm.get(i);
					String [] valist = listFdVal.get(i).split(",",-1);
					
					for (int j=0;j<valist.length;j++) {
						multival = multival.replaceAll("#\\{"+fdNm+"_"+j+"\\}", valist[j]);
					}
				}
				
				sql = " INSERT INTO " + sqlFrom + " ( " + selFdList + " ) VALUES " + multival ;
				
			} else {
				sql = " INSERT INTO " + sqlFrom + " ( " + selFdList + " ) VALUES (" + FdValList + ")";
			}
		}
		
		if (!"".equals(deleteSql)) {
			if (exist_fdNoSaveBlank == false) {
				sql = deleteSql + deleteSql_Where + " #;# " + sql+ " ";
			} else {
				sql = deleteSql + deleteSql_Where + " ";
			}
		}
		
		return sql;
		
	}
	
	
	
	public String makeUpdateQueryString_ProcedureCall(HttpSession session, List<Map<String, String>> dbSchemaInfo, Map<String, Object> row) {
		
		String FdValList = "";
		String sql = "";
		String sqlFrom = "";
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			Map<String, String> item = dbSchemaInfo.get(i);
			String fdAliasnm = item.get("fdAliasNm");
//			String fdNm = item.get("fdNm");
			String fdIsForm = item.get("fdIsForm");
			if ( !"Y".equals(fdIsForm)) continue;
			
			if ("".equals(sqlFrom)) {
				sqlFrom = " " + item.get("dbNm") + "." + item.get("tblNm") + " ";
			}
			
			
			
			for (String key : row.keySet()) {
				if (fdAliasnm.equals(key)) {
//					String fdval = row.get(key)==null?"":((String) row.get(key)).trim();
					String fdval = row.get(key)==null?"":(String.valueOf(row.get(key))).trim();
					
					fdval = convertSpecialStringInQuery(fdval);
					String fdEncrypt = item.get("fdIsEncrypt").toUpperCase();
					
					if ("Y".equals(fdEncrypt)) {
						try {
							fdval = commAESUtil.decrypt(fdval, sc);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						fdval = " HEX(AES_ENCRYPT('"+fdval+"','"+SessionConstant.HKEY+"')) ";
					}
					
					switch (item.get("fdDataType").trim().toLowerCase()) {
					case "int" :
					case "smallint" :
					case "double" :
					case "float" :
						if (!"".equals(FdValList)) {
							FdValList += ",";
						}
						if ("".equals(fdval)) fdval = "0";
						FdValList += fdval;
						break;
						
					case "varchar" :
					case "text" :
					case "longtext" :
					case "char" :
						if (!"".equals(FdValList)) {
							FdValList += ",";
						}
						if (!"Y".equals(item.get("fdIsEncrypt").toUpperCase())) FdValList += "'" + fdval + "'";
						else FdValList += fdval;
						break;
						
					case "expression" :
						if (!"".equals(FdValList)) {
							FdValList += ",";
						}
						FdValList += item.get("fdNm");
						break;
					case "" :
						break;
						
					case "date" :
					case "time" :
					case "timestamp" :
					case "datetime" :
						if (!"".equals(FdValList)) {
							FdValList += ",";
						}
						if ("".equals(fdval)) {
							FdValList += " NULL ";
						} else {
							FdValList += " '" + fdval + "'";
						}
						break;
					}
					
				}
				
			}
			
		}
		
		if (!"".equals(FdValList)) {
			
			sql = " CALL " + sqlFrom + " ( " + FdValList + ")";
		}
		
		return sql;
		
	}
	
	
	
	public String makeDeleteQueryString(HttpSession session, List<Map<String, String>> dbSchemaInfo, Map<String, Object> row) {
		
		String prevtblseq = "";
		String sqlFrom = "";
		String sqlWhere = "";
		String sql = "";
		String prevQryGroup = "";
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		int idx = 0;
		
		while (idx < dbSchemaInfo.size() ) {
			
			prevtblseq = "";
			sqlFrom = "";
			sqlWhere = "";
			
			for (int i=idx;i<dbSchemaInfo.size();i++) {
				
				Map<String, String> item = dbSchemaInfo.get(i);
				
				if (i == idx) {
					prevQryGroup = item.get("qryGroup");
				} else if (!prevQryGroup.equals(item.get("qryGroup")) ) {
					break;
				}
				
				String fdAliasnm = item.get("fdAliasNm");
				String fdNm = item.get("fdNm");
				
				for (String key : row.keySet()) {
					if (fdAliasnm.equals(key)) {
						String fdval = row.get(key)==null?"":(String.valueOf(row.get(key)));
						String fdvals[];
						String fdvalstr = "";
						
//						String fdNoSaveBlank = item.get("fdNoSaveBlank").toLowerCase();
						String fdExtra = item.get("fdExtra").toLowerCase();
//						String fdAutoCode = item.get("fdAutoCode").toLowerCase();
						String fdEncrypt = item.get("fdIsEncrypt").toUpperCase();
						
//						fdval = convertSpecialStringInQuery(fdval);
						
						if ("".equals(prevtblseq)) {
							sqlFrom = " " + item.get("dbNm") + "." + item.get("tblNm") + " ";
						}
						
						prevtblseq = String.valueOf(item.get("tblSeq"));
						
						int listcnt = fdval.split(",", -1).length;
						fdvals = fdval.split(",", -1);
						
						try {

							if (listcnt > 1) {
								for (int tt=0;tt<listcnt;tt++) {
									if ("Y".equals(fdEncrypt)) {
										fdvals[tt] = commAESUtil.decrypt(fdvals[tt], sc);
										fdvals[tt] = convertSpecialStringInQuery(fdvals[tt]);
										fdvals[tt] = " HEX(AES_ENCRYPT('"+convertSpecialStringInQuery(fdvals[tt])+"','"+SessionConstant.HKEY+"')) ";
									} else {
										fdvals[tt] = convertSpecialStringInQuery(fdvals[tt]);
									}
								}
							} else {
								if ("Y".equals(fdEncrypt)) {
									fdval = commAESUtil.decrypt(fdval, sc);
									fdval = convertSpecialStringInQuery(fdval);
									fdval = " HEX(AES_ENCRYPT('"+convertSpecialStringInQuery(fdval)+"','"+SessionConstant.HKEY+"')) ";
								} else {
									fdval = convertSpecialStringInQuery(fdval);
								}
							}
						
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						
						if ("auto_increment".equals(fdExtra) || "deletekey".equals(fdExtra)) {
							if (listcnt > 1) {
								fdvalstr = "";
								for (int tt=0;tt<listcnt;tt++) {
									if (!"".equals(fdvalstr)) fdvalstr += ",";
									if (!"Y".equals(fdEncrypt)) {
										fdvalstr += " '" + fdvals[tt] + "' ";
									} else {
										fdvalstr += " " + fdvals[tt] + " ";
									}
								}
							} else {
								fdvalstr = fdval;
								if (!"Y".equals(fdEncrypt)) {
									fdvalstr = " '" + fdval + "' ";
								}
							}
							if (!"".equals(sqlWhere)) sqlWhere += " AND ";
							sqlWhere += " " + fdNm + " IN ( " + fdvalstr + ") ";

//							sqlWhere = " " + fdNm + " = " + fdval;
							continue;
						}
						
						switch (item.get("fdDataType").trim().toLowerCase()) {
						case "int" :
						case "smallint" :
						case "double" :
						case "float" :
//							if (!"".equals(sqlWhere)) sqlWhere += " AND ";
//							sqlWhere += fdNm + " = " + fdval;
							
							if (listcnt > 1) {
								fdvalstr = "";
								for (int tt=0;tt<listcnt;tt++) {
									if (!"".equals(fdvalstr)) fdvalstr += ",";
									fdvalstr += " " + fdvals[tt] + " ";
								}
							} else {
								fdvalstr = fdval;
							}
							if (!"".equals(sqlWhere)) sqlWhere += " AND ";
							sqlWhere += " " + fdNm + " IN ( " + fdvalstr + ") ";
							break;
							
						case "varchar" :
						case "text" :
						case "longtext" :
						case "char" :
//							if (!"".equals(sqlWhere)) sqlWhere += " AND ";
//							sqlWhere += fdNm + " = ";
//							if (!"Y".equals(item.get("fdIsEncrypt").toUpperCase())) sqlWhere += "'" + fdval + "'";
//							else sqlWhere += fdval;
							
							if (listcnt > 1) {
								fdvalstr = "";
								for (int tt=0;tt<listcnt;tt++) {
									if (!"".equals(fdvalstr)) fdvalstr += ",";
									if (!"Y".equals(fdEncrypt)) {
										fdvalstr += " '" + fdvals[tt] + "' ";
									} else {
										fdvalstr += " " + fdvals[tt] + " ";
									}
								}
							} else {
								fdvalstr = fdval;
								if (!"Y".equals(fdEncrypt)) {
									fdvalstr = " '" + fdval + "' ";
								}
							}
							if (!"".equals(sqlWhere)) sqlWhere += " AND ";
							sqlWhere += " " + fdNm + " IN ( " + fdvalstr + ") ";
							
							break;
							
							
						case "date" :
						case "timestamp" :
						case "datetime" :
//							if (!"".equals(sqlWhere)) sqlWhere += " AND ";
//							sqlWhere += fdNm + " = " + "'" + fdval + "'";
							
							if (listcnt > 1) {
								fdvalstr = "";
								for (int tt=0;tt<listcnt;tt++) {
									if (!"".equals(fdvalstr)) fdvalstr += ",";
									if (!"Y".equals(fdEncrypt)) {
										fdvalstr += " '" + fdvals[tt] + "' ";
									} else {
										fdvalstr += " " + fdvals[tt] + " ";
									}
								}
							} else {
								fdvalstr = fdval;
								if (!"Y".equals(fdEncrypt)) {
									fdvalstr = " '" + fdval + "' ";
								}
							}
							if (!"".equals(sqlWhere)) sqlWhere += " AND ";
							sqlWhere += " " + fdNm + " IN ( " + fdvalstr + ") ";
							
							break;
						}
						
					}
					
				}
				idx = i;
			}
			
			if (!"".equals(sqlWhere)) {
				if ( !"".equals(sql) ) {
					sql += " #;# ";
				}
				
				sql += " DELETE FROM " + sqlFrom + " WHERE " + sqlWhere;
			}
			idx++;
			
		}
		return sql;
		
	}
	
	
	
	
	
	public String makeDeleteQueryString_org(HttpSession session, List<Map<String, String>> dbSchemaInfo, Map<String, Object> row) {
		
		String prevtblseq = "";
		String sqlFrom = "";
		String sqlWhere = "";
		String sql = "";
		String prevQryGroup = "";
		String sc = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		int idx = 0;
		
		while (idx < dbSchemaInfo.size() ) {
			
			prevtblseq = "";
			sqlFrom = "";
			sqlWhere = "";
			
			for (int i=idx;i<dbSchemaInfo.size();i++) {
				
				Map<String, String> item = dbSchemaInfo.get(i);
				
				if (i == idx) {
					prevQryGroup = item.get("qryGroup");
				} else if (!prevQryGroup.equals(item.get("qryGroup")) ) {
					break;
				}
				
				String fdAliasnm = item.get("fdAliasNm");
				String fdNm = item.get("fdNm");
				
				for (String key : row.keySet()) {
					if (fdAliasnm.equals(key)) {
						String fdval = row.get(key)==null?"":(String.valueOf(row.get(key)));
//						String fdNoSaveBlank = item.get("fdNoSaveBlank").toLowerCase();
						String fdExtra = item.get("fdExtra").toLowerCase();
//						String fdAutoCode = item.get("fdAutoCode").toLowerCase();
						String fdEncrypt = item.get("fdIsEncrypt").toUpperCase();
						
						fdval = convertSpecialStringInQuery(fdval);
						
						if ("".equals(prevtblseq)) {
							sqlFrom = " " + item.get("dbNm") + "." + item.get("tblNm") + " ";
						}
						
						prevtblseq = String.valueOf(item.get("tblSeq"));
						
						if ("Y".equals(fdEncrypt)) {
							try {
								fdval = commAESUtil.decrypt(fdval, sc);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							fdval = " HEX(AES_ENCRYPT('"+fdval+"','"+SessionConstant.HKEY+"')) ";
						}

						if ("auto_increment".equals(fdExtra)) {
							sqlWhere = " " + fdNm + " = " + fdval;
							continue;
						}
						
						switch (item.get("fdDataType").trim().toLowerCase()) {
						case "int" :
						case "smallint" :
						case "double" :
						case "float" :
							if (!"".equals(sqlWhere)) sqlWhere += " AND ";
							sqlWhere += fdNm + " = " + fdval;
							break;
							
						case "varchar" :
						case "text" :
						case "longtext" :
						case "char" :
							if (!"".equals(sqlWhere)) sqlWhere += " AND ";
							sqlWhere += fdNm + " = ";
							if (!"Y".equals(item.get("fdIsEncrypt").toUpperCase())) sqlWhere += "'" + fdval + "'";
							else sqlWhere += fdval;
							break;
							
						case "date" :
						case "timestamp" :
						case "datetime" :
							if (!"".equals(sqlWhere)) sqlWhere += " AND ";
							sqlWhere += fdNm + " = " + "'" + fdval + "'";
							break;
						}
						
					}
					
				}
				idx = i;
			}
			
			if (!"".equals(sqlWhere)) {
				if ( !"".equals(sql) ) {
					sql += " #;# ";
				}
				
				sql += " DELETE FROM " + sqlFrom + " WHERE " + sqlWhere;
			}
			idx++;
			
		}
		return sql;
		
	}
	
	
	
	public String CheckRequirednDuplicate(HttpSession session, List<Map<String, String>> dbSchemaInfo, Map<String, Object> row, String mode) {
		
		String sql = "";
		
		String msgRequired = "";
		String msgDuplicate = "";
		
		String uniqKey = "";
		String uniqKeyVal = "";
		
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			Map<String, String> item = dbSchemaInfo.get(i);
			String fdAliasnm = item.get("fdAliasNm");
			String fdNm = item.get("fdNm");
			
			if (i == 0) {
				uniqKey = fdAliasnm;
				uniqKeyVal = (String) row.get(uniqKey);
				uniqKey = fdNm;
			}
			
			for (String key : row.keySet()) {
				if (fdAliasnm.equals(key)) {
//					String fdval = row.get(key)==null ? "":((String) row.get(key)).trim();
					String fdval = row.get(key)==null ? "":(String.valueOf(row.get(key))).trim();
					String fdRequired = item.get("fdRequired").toUpperCase();
					String fdChkDup = item.get("fdChkDup").toUpperCase();
					String fdNoUpdate = item.get("fdNoUpdate").toUpperCase();
					
					fdval = convertSpecialStringInQuery(fdval);
					
					if ("Y".equals(fdRequired) && "".equals(fdval)) {
						if (!"".equals(msgRequired)) {
							msgRequired += ",";
						}
						msgRequired += item.get("fdLabel");
					}
					
					if ("Y".equals(fdChkDup) && ( (i != 0 && "U".equals(mode) && !"Y".equals(fdNoUpdate)) ||  "C".equals(mode) ) ) { // dbSchema 0 index는 primary key로 간주
						
						sql = " SELECT COUNT(*) cnt FROM " + item.get("dbNm") + "." + item.get("tblNm");
						sql += " WHERE " + item.get("fdNm") + " = ";
						
						if ("Y".equals(item.get("fdIsEncrypt").toUpperCase())) {
							fdval = " HEX(AES_ENCRYPT('"+fdval+"','"+SessionConstant.HKEY+"')) ";
						}
						
						switch (item.get("fdDataType").trim().toLowerCase()) {
						case "int" :
						case "smallint" :
						case "double" :
						case "float" :
							sql += fdval;
							break;
							
						case "varchar" :
						case "text" :
						case "longtext" :
						case "char" :
							sql += "'" + fdval + "'";
							break;
						default :
							sql += "'" + fdval + "'";
						}
						
						if (!"".equals(uniqKeyVal) && "U".equals(mode) ) {
							sql = sql + " AND " + uniqKey + " != '" + uniqKeyVal + "' ";
						}
						
						sql = ReplaceRequestData(sql, row, session);
						
						Map<String,Object> param = new HashMap<String,Object>();
						param.put("sql", sql);

						Map<String,Object> ret = sqlSession.selectOne("lib_v1.0.commonSql", param);
						long cnt = (long) ret.get("cnt");
						if (cnt > 0) {
							if (!"".equals(msgDuplicate)) {
								msgDuplicate += ",";
							}
							msgDuplicate += item.get("fdLabel");
						}
					}
					
				}
				
			}
			
		}
		
		
		if (!"".equals(msgRequired)) msgRequired = "["+msgRequired+" 항목은 필수 항목입니다.]";
		if (!"".equals(msgDuplicate)) msgDuplicate = "["+msgDuplicate+" 항목은 기존에 존재하는 자료가 있습니다.]";
		
		return msgRequired+msgDuplicate;
		
	}

	public Map<String,Object> extractRequest(HttpServletRequest request) {
		
		Map<String,Object> ret = new HashMap<String,Object>();
		
		Enumeration<String> e = request.getParameterNames();
		while ( e.hasMoreElements() ){
			String name = (String) e.nextElement();
			String[] reqvalues = request.getParameterValues(name);
			if (reqvalues != null) {
				ret.put(name, String.join(",", reqvalues));
			}
		}
		
		return ret;
		
	}

	
	
	@Cacheable(value="getMenuList", key = "#gid+#lvl")
	public List<Map<String,String>> getMenuList(HttpSession session, String gid, String lvl) {
		
		String comCd = (String) session.getAttribute(SessionConstant.SESSION_COMCD);
		if (comCd == null) comCd = SessionConstant.DEFAULT_COMCD;
//		String gid = (String) session.getAttribute(SessionConstant.SESSION_GROUP);
		if (gid == null) gid = "common";
		lvl = (String) session.getAttribute(SessionConstant.SESSION_LVL);
		if (lvl == null) lvl = "1";
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("gid", gid);
		param.put("lvl", lvl);
		param.put("comCd", comCd);
		param.put("defaultComCd", SessionConstant.DEFAULT_COMCD);
		
/*		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("gid", (String) session.getAttribute(SessionConstant.SESSION_GROUP));
		param.put("lvl", (String) session.getAttribute(SessionConstant.SESSION_LVL));
		param.put("comCd", (String) session.getAttribute(SessionConstant.SESSION_COMCD));
		param.put("defaultComCd", SessionConstant.DEFAULT_COMCD);
*/		
		
		List<Map<String,String>> menuInfo = sqlSession.selectList("lib_v1.0.getMenu", param);// new ArrayList<Map<String,String>>();
		
		return menuInfo;
		
	}
	
	
	
	@Cacheable(value="getContentInfoList", key = "#pid")
	public List<Map<String,String>> getContentInfoList(HttpSession session, String pid) {
		
		String comCd = (String) session.getAttribute(SessionConstant.SESSION_COMCD);
		if (comCd == null) comCd = SessionConstant.DEFAULT_COMCD;
		String gid = (String) session.getAttribute(SessionConstant.SESSION_GROUP);
		if (gid == null) gid = "common";
//		String lvl = (String) session.getAttribute(SessionConstant.SESSION_LVL);
//		if (lvl == null) lvl = "1";
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("pid", pid);
		param.put("comCd", comCd);
		param.put("gid", gid);
		param.put("defaultComCd", SessionConstant.DEFAULT_COMCD);
		
/*		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("pid", pid);
		param.put("comCd", (String) session.getAttribute(SessionConstant.SESSION_COMCD));
		param.put("gid", (String) session.getAttribute(SessionConstant.SESSION_GROUP));
		param.put("defaultComCd", SessionConstant.DEFAULT_COMCD);
		
*/		
		
		List<Map<String,String>> ret = sqlSession.selectList("lib_v1.0.getContentInfo", param);
		
		return ret;
		
	}
	
	@Cacheable(value="getDBSchemaList", key = "#gid+#pid+#lvl+#apiType")
	public List<Map<String,String>> getDBSchemaList(HttpSession session, String gid, String pid, String lvl, String apiType) {
		
		String comCd = (String) session.getAttribute(SessionConstant.SESSION_COMCD);
		if (comCd == null) comCd = SessionConstant.DEFAULT_COMCD;
//		String gid = (String) session.getAttribute(SessionConstant.SESSION_GROUP);
		if (gid == null) gid = "common";
//		String lvl = (String) session.getAttribute(SessionConstant.SESSION_LVL);
		if (lvl == null) lvl = "1";
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("gid", gid);
		param.put("pid", pid);
		param.put("lvl", lvl);
		param.put("apiType", apiType);
//		param.put("defaultimg", SessionConstant.DEFAULT_IMG_MEMBER);
		param.put("comCd", comCd);
		param.put("defaultComCd", SessionConstant.DEFAULT_COMCD);
		
		
/*		
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("gid", gid);
		param.put("pid", pid);
		param.put("lvl", lvl);
		param.put("apiType", apiType);
//		param.put("defaultimg", SessionConstant.DEFAULT_IMG_MEMBER);
		param.put("comCd", (String) session.getAttribute(SessionConstant.SESSION_COMCD));
		param.put("defaultComCd", SessionConstant.DEFAULT_COMCD);
		
*/		
		
		List<Map<String,String>> ret = sqlSession.selectList("lib_v1.0.getDBSchema", param);
		
		return ret;
		
	}
	
	@Cacheable(value="getAccessAuthority", key = "#gid+#pid")
	public List<Map<String,String>> getAccessAuthority(HttpSession session, String gid, String pid) {
		
		String comCd = (String) session.getAttribute(SessionConstant.SESSION_COMCD);
		if (comCd == null) comCd = SessionConstant.DEFAULT_COMCD;
//		String gid = (String) session.getAttribute(SessionConstant.SESSION_GROUP);
		if (gid == null) gid = "common";
//		String lvl = (String) session.getAttribute(SessionConstant.SESSION_LVL);
//		if (lvl == null) lvl = "1";
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("gid", gid);
		param.put("pid", pid);
//		param.put("lvl", lvl);
//		param.put("apiType", apiType);
//		param.put("defaultimg", SessionConstant.DEFAULT_IMG_MEMBER);
		param.put("comCd", comCd);
		param.put("defaultComCd", SessionConstant.DEFAULT_COMCD);
		
		
/*		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("gid", gid);
		param.put("pid", pid);
//		param.put("lvl", lvl);
//		param.put("apiType", apiType);
//		param.put("defaultimg", SessionConstant.DEFAULT_IMG_MEMBER);
		param.put("comCd", (String) session.getAttribute(SessionConstant.SESSION_COMCD));
		param.put("defaultComCd", SessionConstant.DEFAULT_COMCD);
		
*/		
		
		List<Map<String,String>> ret = sqlSession.selectList("lib_v1.0.getAccessAuthority", param);
		
		return ret;
		
	}
	
	
	
	
	@Cacheable(value="makeFieldList", key = "#gid+#pid+#lvl+#apiType")
	public List<Map<String,String>> makeFieldList(List<Map<String,String>> dbschema, HttpSession session, String gid, String pid, String lvl, String apiType) {
		
		Map<String,Object> reqdata = new HashMap<String,Object>();
		
		dbschema.forEach(onerec -> {
			String listPid = onerec.get("fdListPid");
			String list = onerec.get("fdList");
			String tagType = onerec.get("fdTagType");
			
			if (!"".equals(listPid) ) {
				
				List<Map<String, String>> dbSchemaInfo_pid = getDBSchemaList(session, gid, listPid, lvl, "list");
				
				String tagstr = "";
				if (dbSchemaInfo_pid.size()>0) {
					List<Map<String, String>> subContentInfo = getSubContentList(session, dbSchemaInfo_pid, reqdata);
					
					if ("SELECT".equals(tagType.toUpperCase())) {
						for (Map<String, String> map : subContentInfo) {
							tagstr += "<option value='"+map.get("value")+"'>"+map.get("label")+"</option>";
						}
					} else if ("RADIO".equals(tagType.toUpperCase()) || "CHECKBOX".equals(tagType.toUpperCase())) {
						for (Map<String, String> map : subContentInfo) {
							if (!"".equals(tagstr)) tagstr += "##";
							tagstr += map.get("label")+"**"+map.get("value");
						}
					}
				}
				onerec.put("fdListTag", tagstr);
			
			} else if (!"".equals(list) ) {
				
				ObjectMapper mapper = new ObjectMapper();
				try {
					
					List<Map<String, Object>> readValue = mapper.readValue(list, new TypeReference<List<Map<String, Object>>>() {});
					String tagstr = "";
					
					if ("SELECT".equals(tagType.toUpperCase())) {
						for (Map<String, Object> map : readValue) {
							tagstr += "<option value='"+map.get("value")+"'>"+map.get("label")+"</option>";
						}				
					} else if ("RADIO".equals(tagType.toUpperCase()) || "CHECKBOX".equals(tagType.toUpperCase())) {
						for (Map<String, Object> map : readValue) {
							if (!"".equals(tagstr)) tagstr += "##";
							tagstr += map.get("label")+"**"+map.get("value");
						}				
					}
					onerec.put("fdListTag", tagstr);
					
				} catch(IOException e) {
					e.printStackTrace();
					onerec.put("fdListTag", "");
				}
				
			} else {
				onerec.put("fdListTag", "");
			}
			
		});
		
		return dbschema;
		
	}
	
	
	private String convertSpecialStringInQuery(String str) {
		String ret = str;
		ret = ret.replaceAll("'", "''");
//		ret = ret.replaceAll("%", "\\%");
		
		return ret;
	}
	
	
	
	public String makeSearchWhere(List<Map<String, String>> dbSchemaInfo, Map<String,Object> row ,String sc) {
		
		String ret = "";
		String inner_Where = "";
		
		for (int i=0;i<dbSchemaInfo.size();i++) {
			String tblWhere = dbSchemaInfo.get(i).get("tblWhere");
			if (i == 0) {
				inner_Where = tblWhere;
			}
			
			String fdNm = dbSchemaInfo.get(i).get("fdNm");
			String fdAliasNm = dbSchemaInfo.get(i).get("fdAliasNm");
			String tblAliasNm = dbSchemaInfo.get(i).get("tblAlias");
			String fdTagType = dbSchemaInfo.get(i).get("fdTagType").toUpperCase();
			String fdDataType = dbSchemaInfo.get(i).get("fdDataType").toUpperCase();
			String fdIsEncrypt = dbSchemaInfo.get(i).get("fdIsEncrypt");
			String fdExtra = dbSchemaInfo.get(i).get("fdExtra").toLowerCase();
			if (row != null) {
				for( Map.Entry<String, Object> entry : row.entrySet() ){
					String strKey = entry.getKey();
					if (fdAliasNm.equals(strKey)) {
						if (!"EXPRESSION".equals(fdDataType)) {
							strKey = tblAliasNm + "." + fdNm;
						} else {
							strKey = fdNm;
						}
						
//						String strValue = (String) entry.getValue();
						String strValue = String.valueOf(entry.getValue());
						String tmpEncStrValue = strValue;
						try {
							if ("Y".equals(fdIsEncrypt)) {
								tmpEncStrValue = commAESUtil.decrypt(tmpEncStrValue, sc);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (!"".equals(strValue)) {
							if (!"".equals(ret)) {
								ret += " AND ";
							}
							
							if ("auto_increment".equals(fdExtra)) {
								ret += strKey + " = " + tmpEncStrValue + " ";
							} else if ("SELECT".equals(fdTagType)) {
								if ("Y".equals(fdIsEncrypt)) {
									ret += strKey + " = " + " HEX(AES_ENCRYPT('"+tmpEncStrValue+"','"+SessionConstant.HKEY+"')) " + " ";
								} else {
									ret += strKey + " = '" + tmpEncStrValue + "'";
								}

							} else if ("PERIOD_DATETIME".equals(fdTagType)) {
								String[] sted = strValue.split("~");
								if (sted.length == 2) {
//									ret += strKey + " BETWEEN '" + sted[0].trim() + "' AND '" + sted[1].trim() + "' ";

									if ("Y".equals(fdIsEncrypt)) {
										ret += strKey + " BETWEEN " + " HEX(AES_ENCRYPT('"+sted[0].trim()+"','"+SessionConstant.HKEY+"')) " + " AND HEX(AES_ENCRYPT('"+sted[1].trim()+"','"+SessionConstant.HKEY+"')) ";
									} else {
										ret += strKey + " BETWEEN '" + sted[0].trim() + "' AND '" + sted[1].trim() + "' ";
									}

								} else {
									ret += strKey + " BETWEEN '' AND '' ";
								}
							} else if ("CHECKBOX".equals(fdTagType)) {
								String[] vals = strValue.split(",",-1);
								String tmpStrVal = "";
								for (int j=0;j<vals.length;j++) {
									tmpEncStrValue = vals[j];
									try {
										if ("Y".equals(fdIsEncrypt)) {
											tmpEncStrValue = commAESUtil.decrypt(tmpEncStrValue, sc);
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									if (!"".equals(tmpStrVal)) tmpStrVal += ",";

									if ("Y".equals(fdIsEncrypt)) {
										tmpStrVal += " HEX(AES_ENCRYPT('"+tmpEncStrValue+"','"+SessionConstant.HKEY+"')) ";
									} else {
										tmpStrVal += "'"+tmpEncStrValue+"'";
									}
								}
								ret += strKey + " IN ( "+tmpStrVal + ") ";
							} else if ("SELECT_MULTI".equals(fdTagType)) {
								if ("INT".equals(fdDataType)) {
									String[] vals = strValue.split(",",-1);
									String tmpStrVal = "";
									for (int j=0;j<vals.length;j++) {
										if (!"".equals(tmpStrVal)) tmpStrVal += ",";
										tmpStrVal += vals[j];
									}
									ret += strKey + " IN ( "+tmpStrVal + ") ";
								} else {
									String[] vals = strValue.split(",",-1);
									String tmpStrVal = "";
									for (int j=0;j<vals.length;j++) {
										tmpEncStrValue = vals[j];
										try {
											if ("Y".equals(fdIsEncrypt)) {
												tmpEncStrValue = commAESUtil.decrypt(tmpEncStrValue, sc);
											}
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										if (!"".equals(tmpStrVal)) tmpStrVal += ",";
//										tmpStrVal += "'"+tmpEncStrValue+"'";
										if ("Y".equals(fdIsEncrypt)) {
											tmpStrVal += " HEX(AES_ENCRYPT('"+tmpEncStrValue+"','"+SessionConstant.HKEY+"')) ";
										} else {
											tmpStrVal += "'"+tmpEncStrValue+"'";
										}
									}
									ret += strKey + " IN ( "+tmpStrVal + ") ";
								}
							} else {
//								ret += strKey + " LIKE '%" + tmpEncStrValue + "%'";
								if ("Y".equals(fdIsEncrypt)) {
									ret += strKey + " LIKE CONCAT('%'," + " HEX(AES_ENCRYPT('"+tmpEncStrValue+"','"+SessionConstant.HKEY+"')) " + ",'%')";
								} else {
									ret += strKey + " LIKE '%" + tmpEncStrValue + "%'";
								}
							}
							
						}
					}
				};
			}
		};
		
		if (!"".equals(ret)) {
//			ret = " WHERE " + ret;
			if ("".equals(inner_Where)) {
				ret = " WHERE " + ret;
			} else {
				ret = " AND " + ret;
			}
		}
//		if (!"".equals(inner_Where)) {
//			if ("".equals(ret)) ret = " WHERE " + inner_Where;
//			else ret += " AND " + inner_Where;
//		}
		
		return ret;
	}	
	
	
	public List<Map<String, Object>> makeEncryptList(List<Map<String, String>> dbSchemaInfo, List<Map<String, Object>> list, String sc) {
		
		if (list == null || list.size() <= 0) {
			return list;
		}
		
		String encfdnm = "";
		for( Map<String, String> oneitem : dbSchemaInfo ){
			String fdIsTable = oneitem.get("fdIsTable")==null?"":oneitem.get("fdIsTable").toUpperCase();
			String fdIsEncrypt = oneitem.get("fdIsEncrypt")==null?"":oneitem.get("fdIsEncrypt").toUpperCase();
			if ( "Y".equals(fdIsTable) && "Y".equals(fdIsEncrypt) ) {
				if ( !"".equals(encfdnm) ) {
					encfdnm += ",";
				}
				encfdnm += oneitem.get("fdAliasNm");
			}
		}
		
		if ( "".equals(encfdnm) ) return list;
		String [] encfdnms = encfdnm.split(",",-1);
		for( Map<String, Object> oneitem : list ){
			for (int i=0;i<encfdnms.length;i++) {
				String txt = String.valueOf(oneitem.get(encfdnms[i])); // (String) oneitem.get(encfdnms[i]);
//				String txt = (String) oneitem.get(encfdnms[i]);
				
				try {
					if (txt == null) txt="";
					txt = commAESUtil.encrypt(txt ,sc);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				oneitem.put(encfdnms[i], txt);
			}
		}
		
		return list;
		
	}
	
	
	public List<Map<String, String>> makeEncryptList_String(List<Map<String, String>> dbSchemaInfo, List<Map<String, String>> list, String sc) {
		
		if (list == null || list.size() <= 0) {
			return list;
		}
		
		String encfdnm = "";
		for( Map<String, String> oneitem : dbSchemaInfo ){
			String fdIsTable = oneitem.get("fdIsTable")==null?"":oneitem.get("fdIsTable").toUpperCase();
			String fdIsEncrypt = oneitem.get("fdIsEncrypt")==null?"":oneitem.get("fdIsEncrypt").toUpperCase();
			if ( "Y".equals(fdIsTable) && "Y".equals(fdIsEncrypt) ) {
				if ( !"".equals(encfdnm) ) {
					encfdnm += ",";
				}
				encfdnm += oneitem.get("fdAliasNm");
			}
		}
		
		if ( "".equals(encfdnm) ) return list;
		String [] encfdnms = encfdnm.split(",",-1);
		for( Map<String, String> oneitem : list ){
			for (int i=0;i<encfdnms.length;i++) {
				String txt = (String) oneitem.get(encfdnms[i]);
				
				try {
					if (txt == null) txt="";
					txt = commAESUtil.encrypt(txt ,sc);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				oneitem.put(encfdnms[i], txt);
			}
		}
		
		return list;
	}
		
	
	
	
	
}
