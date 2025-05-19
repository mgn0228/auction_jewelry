package kr.co.automart.jewelry.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
//import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//import org.apache.commons.io.FileUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

//import com.habi.service_platform.common.SessionConstant;

import jakarta.servlet.http.HttpSession;

/**
* Account Controller Class
*/


@Service
public class FileManager {
	
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	private String uploadpath = SessionConstant.UPLOAD_FILEPATH_LINUX;
	
	public Map<String, Object> uploadFileSave(Map<String, Object> row, MultiValueMap<String, MultipartFile> mpmap
			, HttpSession session, String cid, String pid) {
		Map<String, Object> ret = row;
		
//		String fnKey = "/";
		String fnKey = "";
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			uploadpath = SessionConstant.UPLOAD_FILEPATH_WINDOW;
		}
		
		String destFolder = uploadpath + (cid==null?"CM_GUEST":cid);
//		fnKey += cid + "/";
		File directoryCid = new File(destFolder);
		if(!directoryCid.exists()) {
			directoryCid.mkdir();
		}
		
		
		destFolder += File.separator + pid;
		fnKey += pid; // + "/";
		File directoryPid = new File(destFolder);
		if(!directoryPid.exists()) {
			directoryPid.mkdir();
		}
		
		
		String subfolder = File.separator + makeSubFolderName();
		destFolder += subfolder;
		fnKey += subfolder + "/";
		File directory = new File(destFolder);
		if(!directory.exists()) {
			directory.mkdir();
		}
		
		
		String destFileName = "";
		
		Object[] keys = mpmap.keySet().toArray();
		
		for (int i=0;i<keys.length;i++) {
			
			if ("key".equals(keys[i])) continue;
			List<MultipartFile> mpFile = mpmap.get(keys[i]);
			String newData = "";
			for (int j=0;j<mpFile.size();j++) {
				MultipartFile file = mpFile.get(j);
				
				String orgFullFileName = file.getOriginalFilename();
				
				if ("".equals(orgFullFileName)) continue;
				
				String extName = orgFullFileName.substring(orgFullFileName.lastIndexOf(".")+1, orgFullFileName.length());
				
//				destFileName = makeUUID()+"."+extName;
//				fnKey += destFileName;
				fnKey = makeUUID();
				fnKey = fnKey.replaceAll("-", "");
				destFileName = fnKey+"."+extName;
				
				File savefile = new File(destFolder + File.separator + destFileName);
				
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("dbNm", SessionConstant.SP_DBNM);
				param.put("tblNm", SessionConstant.UPLOAD_FILE_TABLENAME);
				param.put("cid", cid);
				param.put("pid", pid);
				param.put("fnOrg", orgFullFileName);
				param.put("fnSaved", destFileName);
				param.put("destFolder", destFolder);
				param.put("target", orgFullFileName);
				param.put("fnKey", fnKey);
				param.put("ext", extName);
				param.put("lid", (String)session.getAttribute(SessionConstant.SESSION_LID));
				param.put("hkey", SessionConstant.HKEY);
				sqlSession.insert("lib_v1.0.inserUploadFile", param);
//				param.put("no", param.get("no"));
//				sqlSession.update("lib_v1.0.updateUploadFileKey", param);
//				Map<String, Object> result = sqlSession.selectOne("lib_v1.0.getUploadFileKey", param);
//				fnKey = (String) result.get("fn_key");
				
				try {
					file.transferTo(savefile);
				} catch (IllegalStateException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (!"".equals(newData)) newData += ",";
				newData += fnKey;
			}
			row.replace((String) keys[i], newData);
			
		}
		
		
		return ret;
	}
	
	
	private String makeSubFolderName() {
		String ret = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		
		Calendar c1 = Calendar.getInstance();
		ret = sdf.format(c1.getTime());
		
		return ret;
	}
	
	
/*	
	
	
	private	String makeFileName(String key, int idx) {
		String fileName = key+"_";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Calendar c1 = Calendar.getInstance();
		fileName += sdf.format(c1.getTime());

		fileName += idx;
		
		return fileName;
	}
	
*/	
	
	
	private	String makeUUID() {
		
		return UUID.randomUUID().toString();
		
	}
	
	public Map<String, Object> getFileInfo(String fnkey) {
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("dbNm", SessionConstant.SP_DBNM);
		param.put("tblNm", SessionConstant.UPLOAD_FILE_TABLENAME);
		param.put("fnkey", fnkey);
		Map<String, Object> ret  = sqlSession.selectOne("lib_v1.0.getUploadFileInfoByFnKey", param);
		
		if (ret == null || "".equals(ret.get("fn_key"))) {
			return null; 
		}
		String filename = ret.get("fn_savedFolder")+File.separator+ret.get("fn_saved");
		
		File uploadfile = new File(filename);
		if(!uploadfile.exists()) {
			return null;
		}
		InputStreamResource resource = null;
		Path filePath = Paths.get(filename);
		try {
			resource = new InputStreamResource(new FileInputStream(filePath.toString()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		ret.put("resource", resource);
		
		return ret;
	}
	
	
}
