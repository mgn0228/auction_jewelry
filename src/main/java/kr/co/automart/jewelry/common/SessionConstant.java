package kr.co.automart.jewelry.common;

public class SessionConstant {
	
	
	public static final String SP_DBNM = "AM_JEWELLERY_SYSTEM";			// Database Name
	
//	public static final String SP_DBNM_ARGOS_DEMO = "IOT_TempM";		// Argos Demo DB Name
	
	
//	public static final String HKEY = "ntsserver!@#";					// hkey value
//	public static final String HKEY = "spserver!@#";					// hkey value
	public static final String HKEY = "automart!@#$";					// hkey value
	
	
	public static final String DB_PRE_COMDB = "AMJWLY_";					// prefix db name for company
	
	
	public static final String DEFAULT_COMCD = "CM_GUEST";				// default com code value
	public static final String DEFAULT_IMG_MEMBER = "/img/common/defaultUser.png";						// member default image
	
	
	
	
	
	
	
	public static final String SESSION_COMCD = "comCd";					// session login user Com Code
	public static final String SESSION_COMNM = "comNm";					// session login user Com Name
	public static final String SESSION_LID = "lid";						// session login id key
	public static final String SESSION_LVL = "lvl";						// session level key
	public static final String SESSION_LNM = "lnm";						// session login User Name
	public static final String SESSION_LIMG = "limg";					// session login User Imgae
	public static final String SESSION_LREGDT = "lregDt";					// session login User Regist DateTime
	public static final String SESSION_GROUP = "lgrp";						// session login User Group
	
	public static final String SESSION_SECRETUUID = "sid_amjewerly";		// session secret uuid
	public static final String SESSION_ID = "sessionId";				// httpRequest session id

	
	
	
	public static final String ERROR_MSG_NOAUTH					= "권한이 없습니다";	
	public static final String ERROR_MSG_SQLINSERT				= "등록에 오류가 있습니다.";
	public static final String ERROR_MSG_INVALIDDATA			= "자료에 오류가 있습니다.";
	
	
	
	
	public static final String UPLOAD_FILEPATH_WINDOW			= "D:\\\\tempupload\\AM_JEWELRY\\";
	public static final String UPLOAD_FILEPATH_LINUX			= "/data1/htdocs/meta_resources/AM_JEWELRY/";
	public static final String UPLOAD_FILE_TABLENAME			= "UploadFiles";
	
	
	
	public static final String SYSTEM_MANAGER_GROUP				= "system";
	public static final String SYSTEM_MANAGER_COMCODE			= "CM_SYSTEM";
	
	
}
