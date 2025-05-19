package kr.co.automart.jewelry.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;


public interface ExcelService {
	
	Map<String, Object> excelInsert(MultipartFile file) throws IOException;

}
