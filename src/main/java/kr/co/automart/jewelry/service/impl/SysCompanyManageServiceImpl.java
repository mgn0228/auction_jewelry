package kr.co.automart.jewelry.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.multipart.MultipartHttpServletRequest;
//import org.springframework.util.MultiValueMap;

//import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.automart.jewelry.common.SessionConstant;
import kr.co.automart.jewelry.service.SysCompanyManageService;

//import com.habi.service_platform.common.FileManager;

//@Slf4j
@Service
public class SysCompanyManageServiceImpl implements SysCompanyManageService {
	
	@Autowired
	@Qualifier("sqlSession")
	private SqlSessionTemplate sqlSession;
	
	
	@Override
	public List<Map<String, Object>> createComDB(HttpSession session, Map<String, Object> row) {
		
//		Map<String, String> meminfo = commonService.getSessionMemberInfo(session);
//		String gid = meminfo.get("mbGrp");
//		String lvl = meminfo.get("mbLvl");
//		String  = meminfo.get("comCd");
		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		String comCd = (String) row.get("comCd");
		
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("comDbNm", SessionConstant.DB_PRE_COMDB+comCd);
		param.put("comCd", comCd);
		
		int cnt = sqlSession.selectOne("sysCompanyManage.checkCompanyDB", param);
		Map<String, Object> one = new HashMap<String, Object>();
		
		if (cnt < 1) {
			try {
				sqlSession.insert("sysCompanyManage.createCompanyDB", param);
				one.put("result", "OK");
				one.put("message", "Database를 생성하였습니다.");
			} catch (Exception e) {
				one.put("result", "Fail");
				one.put("message", "Database생성에 오류가 있습니다.");
			}
		} else {
			one.put("result", "Fail");
			one.put("message", "선택하신 Database는 이미 존재합니다.");
		}
		ret.add(one);
		return ret;
		
	}
	
	@Override
	public List<Map<String, Object>> dropComDB(HttpSession session, Map<String, Object> row) {
		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		
		String comCd = (String) row.get("comCd");
		
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("comDbNm", SessionConstant.DB_PRE_COMDB+comCd);
		param.put("comCd", comCd);
		
		int cnt = sqlSession.selectOne("sysCompanyManage.checkCompanyDB", param);
		Map<String, Object> one = new HashMap<String, Object>();
		
		if (cnt < 1) {
			one.put("result", "Fail");
			one.put("message", "선택하신 업제의 Database가 존재하지 않습니다.");
		} else {
			try {
				sqlSession.insert("sysCompanyManage.dropCompanyDB", param);
				one.put("result", "OK");
				one.put("message", "Database를 삭제하였습니다.");
			} catch (Exception e) {
				one.put("result", "Fail");
				one.put("message", "Database삭제에 오류가 있습니다.");
			}
		}
		ret.add(one);
		return ret;
		
	}
	
	
	@Override
	public List<Map<String, Object>> createComDefaultTable(HttpSession session, Map<String, Object> row) {
		
//		Map<String, String> meminfo = commonService.getSessionMemberInfo(session);
//		String gid = meminfo.get("mbGrp");
//		String lvl = meminfo.get("mbLvl");
//		String  = meminfo.get("comCd");
		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		Map<String, Object> one = new HashMap<String, Object>();
		
		String comCd = (String) row.get("comCd");
		String comTableNm = (String) row.get("comTableNm");
		String comSysOptTblEngine = (String) row.get("comSysOptTblEngine");
		String checkcomTableNm = comTableNm.replaceAll("[^0-9a-zA-Z_]","");
		checkcomTableNm = checkcomTableNm.replaceAll(" ", "");
		if (!comTableNm.equals(checkcomTableNm)) {
			one.put("result", "Fail");
			one.put("message", "Table이름은 영문,숫자,밑줄문자(_) 만 가능합니다.");
			ret.add(one);
			return ret;
		}
		
		if (comSysOptTblEngine == null || "".equals(comSysOptTblEngine)) {
			comSysOptTblEngine = "InnoDB";
		}
		
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("comDbNm", SessionConstant.DB_PRE_COMDB+comCd);
		param.put("comTableNm", comTableNm);
		param.put("comCd", comCd);
		param.put("comSysOptTblEngine", comSysOptTblEngine );
		
		int cnt = sqlSession.selectOne("sysCompanyManage.checkCompanyTable", param);
		
		if (cnt < 1) {
			try {
				sqlSession.insert("sysCompanyManage.createCompanyDefaultTable", param);
				one.put("result", "OK");
				one.put("message", "Table을 생성하였습니다.");
			} catch (Exception e) {
				e.printStackTrace();
				one.put("result", "Fail");
				one.put("message", "Table생성에 오류가 있습니다.");
			}
		} else {
			one.put("result", "Fail");
			one.put("message", "선택하신 Table은 이미 존재합니다.");
		}
		ret.add(one);
		return ret;
		
	}
	
	
	
	@Override
	public List<Map<String, Object>> dropComTable(HttpSession session, Map<String, Object> row) {
		
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>> ();
		Map<String, Object> one = new HashMap<String, Object>();
		
		String comCd = (String) row.get("comCd");
		String comTableNm = (String) row.get("comTableNm");
		String comSysOptTblEngine = (String) row.get("comSysOptTblEngine");
		String checkcomTableNm = comTableNm.replaceAll("[^0-9a-zA-Z_]","");
		checkcomTableNm = checkcomTableNm.replaceAll(" ", "");
		if (!comTableNm.equals(checkcomTableNm)) {
			one.put("result", "Fail");
			one.put("message", "Table이름은 영문,숫자,밑줄문자(_) 만 가능합니다.");
			ret.add(one);
			return ret;
		}
		
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("comDbNm", SessionConstant.DB_PRE_COMDB+comCd);
		param.put("comTableNm", comTableNm);
		param.put("comCd", comCd);
		param.put("comSysOptTblEngine", comSysOptTblEngine );
		
		int cnt = sqlSession.selectOne("sysCompanyManage.checkCompanyTable", param);
		
		if (cnt > 0) {
			try {
				sqlSession.delete("sysCompanyManage.dropCompanyTable", param);
				one.put("result", "OK");
				one.put("message", "Table을 삭제하였습니다.");
			} catch (Exception e) {
				e.printStackTrace();
				one.put("result", "Fail");
				one.put("message", "Table삭제에 오류가 있습니다.");
			}
		} else {
			one.put("result", "Fail");
			one.put("message", "선택하신 Table이 존재지 않습니다.");
		}
		ret.add(one);
		return ret;
		
	}
	
	
	
	@Override
	public Map<String, Object> createComField(HttpSession session, Map<String, Object> row) {
		
		Map<String, Object> ret = new HashMap<String, Object>();
//		Map<String, Object> one = new HashMap<String, Object>();
		
		
		
		String comCd = (String) row.get("comCd");
		String databaseNm = (String) row.get("databaseNm");
		String comTableCd = (String) row.get("comTableCd");
		String colDefault = (String) row.get("colDefault");
		String colNullable = (String) row.get("colNullable");
		String colDataType = (String) row.get("colDataType");
		
		String colMaxLength = ((String) row.get("colMaxLength")).trim();
		String colComment = (String) row.get("colComment");
		
		String colNm = ((String) row.get("colNm")).trim();
		
		String checkVal = colNm.replaceAll("[^0-9a-zA-Z_]","");
		checkVal = checkVal.replaceAll(" ", "");
		if (!checkVal.equals(colNm)) {
			ret.put("result", "Fail");
			ret.put("message", "필드이름은 영문,숫자,밑줄문자(_) 만 가능합니다.");
			return ret;
		}
		checkVal = colMaxLength.replaceAll("[^0-9]","");
		checkVal = checkVal.replaceAll(" ", "");
		if (!checkVal.equals(colMaxLength)) {
			ret.put("result", "Fail");
			ret.put("message", "Data길이는 숫자만 가능합니다.");
			return ret;
		}
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("comDbNm", SessionConstant.DB_PRE_COMDB+comCd);
		param.put("comCd", comCd);
		param.put("databaseNm", databaseNm);
		param.put("comTableCd", comTableCd);
		param.put("comTableNm", comTableCd);
		param.put("colNm", colNm);
		param.put("colDefault", colDefault );
		param.put("colNullable", colNullable );
		param.put("colDataType", colDataType );
		param.put("colMaxLength", colMaxLength );
		param.put("colComment", colComment );
		
		int cnt = sqlSession.selectOne("sysCompanyManage.checkCompanyTableField", param);
		
		if (cnt == 0) {
			try {
				sqlSession.delete("sysCompanyManage.createCompanyTableField", param);
				ret.put("result", "OK");
				ret.put("message", "Field를 추가하였습니다.");
			} catch (Exception e) {
				e.printStackTrace();
				ret.put("result", "Fail");
				ret.put("message", "Field 추가에 오류가 있습니다.");
			}
		} else {
			ret.put("result", "Fail");
			ret.put("message", "선택하신 Field가 존재합니다.");
		}
		
		return ret;
		
	}
	
	
	
	@Override
	public Map<String, Object> updateComField(HttpSession session, Map<String, Object> row) {
		
		Map<String, Object> ret = new HashMap<String, Object>();
//		Map<String, Object> one = new HashMap<String, Object>();
		
		String comCd = (String) row.get("comCd");
		String databaseNm = (String) row.get("databaseNm");
		String comTableCd = (String) row.get("comTableCd");
		String colDefault = (String) row.get("colDefault");
		String colNullable = (String) row.get("colNullable");
		String colDataType = (String) row.get("colDataType");
		
		String colMaxLength = ((String) row.get("colMaxLength")).trim();
		String colComment = (String) row.get("colComment");
		
		String colNm = ((String) row.get("colNm")).trim();
		
		String checkVal = colNm.replaceAll("[^0-9a-zA-Z_]","");
		checkVal = checkVal.replaceAll(" ", "");
		if (!checkVal.equals(colNm)) {
			ret.put("result", "Fail");
			ret.put("message", "필드이름은 영문,숫자,밑줄문자(_) 만 가능합니다.");
			return ret;
		}
		checkVal = colMaxLength.replaceAll("[^0-9]","");
		checkVal = checkVal.replaceAll(" ", "");
		if (!checkVal.equals(colMaxLength)) {
			ret.put("result", "Fail");
			ret.put("message", "Data길이는 숫자만 가능합니다.");
			return ret;
		}
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("comDbNm", SessionConstant.DB_PRE_COMDB+comCd);
		param.put("comCd", comCd);
		param.put("databaseNm", databaseNm);
		param.put("comTableCd", comTableCd);
		param.put("comTableNm", comTableCd);
		param.put("colNm", colNm);
		param.put("colDefault", colDefault );
		param.put("colNullable", colNullable );
		param.put("colDataType", colDataType );
		param.put("colMaxLength", colMaxLength );
		param.put("colComment", colComment );
		
		int cnt = sqlSession.selectOne("sysCompanyManage.checkCompanyTableField", param);
		
		if (cnt > 0) {
			try {
				sqlSession.update("sysCompanyManage.updateCompanyTableField", param);
				ret.put("result", "OK");
				ret.put("message", "Field를 수정하였습니다.");
			} catch (Exception e) {
				e.printStackTrace();
				ret.put("result", "Fail");
				ret.put("message", "Field 수정에 오류가 있습니다.");
			}
		} else {
			ret.put("result", "Fail");
			ret.put("message", "선택하신 Field가 존재하지 않습니다.");
		}
		
		return ret;
		
	}
	
	
	@Override
	public Map<String, Object> deleteComField(HttpSession session, Map<String, Object> row) {
		
		Map<String, Object> ret = new HashMap<String, Object>();
//		Map<String, Object> one = new HashMap<String, Object>();
		
		String no = (String) row.get("no");
		no = no.replaceAll("]", "");
		
/*		
		String comCd = (String) row.get("comCd");
		String databaseNm = (String) row.get("databaseNm");
		String comTableCd = (String) row.get("comTableCd");
		String colDefault = (String) row.get("colDefault");
		String colNullable = (String) row.get("colNullable");
		String colDataType = (String) row.get("colDataType");
		
		String colMaxLength = ((String) row.get("colMaxLength")).trim();
		String colComment = (String) row.get("colComment");
		
		String colNm = ((String) row.get("colNm")).trim();
*/		
		
		String [] fdinfos = no.split("\\[");
		
		if (fdinfos.length < 3) {
			ret.put("result", "Fail");
			ret.put("message", "정보가 확실하지 않습니다.");
			return ret;
		}
		
		Map<String,String> param = new HashMap<String,String>();
		
		param.put("comDbNm", fdinfos[0]);
		param.put("comTableNm", fdinfos[1]);
		param.put("colNm", fdinfos[2]);
		
		int cnt = sqlSession.selectOne("sysCompanyManage.checkCompanyTableField", param);
		
		if (cnt > 0) {
			try {
				sqlSession.delete("sysCompanyManage.deleteCompanyTableField", param);
				ret.put("result", "OK");
				ret.put("message", "Field를 삭제하였습니다.");
			} catch (Exception e) {
				e.printStackTrace();
				ret.put("result", "Fail");
				ret.put("message", "Field 삭제에 오류가 있습니다.");
			}
		} else {
			ret.put("result", "Fail");
			ret.put("message", "선택하신 Field가 존재하지 않습니다.");
		}
		
		return ret;
		
	}
	
	
	
	@Override
	public Map<String, Object> createNewContentPID(HttpSession session, Map<String, Object> row) {
		
//		Map<String, String> meminfo = commonService.getSessionMemberInfo(session);
//		String gid = meminfo.get("mbGrp");
//		String lvl = meminfo.get("mbLvl");
//		String  = meminfo.get("comCd");
		
//		Map<String, Object> ret = new HashMap<String, Object> ();
		
		String comCd = (String) row.get("comCd");
		String newPid = (String) row.get("newPid");
		String gid = (String) row.get("gid");
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("newPid", newPid);
		param.put("comCd", comCd);
		param.put("gid", gid);
		
		int cnt = sqlSession.selectOne("sysCompanyManage.checkCompanyContentPID", param);
		Map<String, Object> one = new HashMap<String, Object>();
		
		if (cnt < 1) {
			try {
				sqlSession.insert("sysCompanyManage.createCompanyContentPID", param);
				one.put("result", "OK");
				one.put("message", "Database를 생성하였습니다.");
			} catch (Exception e) {
				one.put("result", "Fail");
				one.put("message", "Database생성에 오류가 있습니다.");
			}
		} else {
			one.put("result", "Fail");
			one.put("message", "선택하신 Database는 이미 존재합니다.");
		}
//		ret.add(one);
		return one;
		
	}
	
	
	
}



