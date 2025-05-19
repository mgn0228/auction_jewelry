package kr.co.automart.jewelry.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.automart.jewelry.common.Init_Lib_V1;
import kr.co.automart.jewelry.service.ExcelService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExcelServiceImpl implements ExcelService {
	
	@Autowired
	@Qualifier("sqlSession")
	private SqlSessionTemplate sqlSession;
	
	@Override
	public Map<String, Object> excelInsert(MultipartFile file) throws IOException {
		// TODO Auto-generated method stub
		List<Map<String, Object>> paramList = new ArrayList<>();
		List<Map<String, Object>> detailParamList = new ArrayList<>();
		Map<String, Object> result = new HashMap<>();
		byte[] fileBytes = file.getBytes();
		
		File tempFile = File.createTempFile("upload_", file.getOriginalFilename());
	    file.transferTo(tempFile);
		
		try (InputStream is = new ByteArrayInputStream(fileBytes)) {
			
			FileMagic fileMagic = FileMagic.valueOf(is);
			Workbook workbook;
			
			if (fileMagic == FileMagic.OOXML) {
				workbook = new XSSFWorkbook(new ByteArrayInputStream(fileBytes));  // XLSX 처리
			} else if (fileMagic == FileMagic.OLE2) {
				workbook = new HSSFWorkbook(new ByteArrayInputStream(fileBytes));  // XLS 처리
			} else {
				throw new IllegalArgumentException("지원되지 않는 파일 형식입니다. 올바른 XLS 또는 XLSX 파일을 업로드하세요.");
			}

			Sheet sheet = workbook.getSheetAt(0);
			
			String orderNo = "";
			String paymentMethod = "";
			String perchaser = "";
			String paymentDt = "";
			String status = "";
			
			for (Row row : sheet) {
				
				if (row.getRowNum() == 0) continue; // excel 첫번째 줄 스킵
				
				Map<String, Object> param = new HashMap<>();
				Map<String, Object> detailParam = new HashMap<>();
				if (row.getCell(1).getStringCellValue() != "") {
					// 주문번호가 있는 행일 경우 주문번호에 대한 총 결제 금액 정보 추가
					
					param.put("orderNo", row.getCell(1).getStringCellValue());
					param.put("paymentMethod", row.getCell(7).getStringCellValue());
					param.put("status", row.getCell(8).getStringCellValue());
					param.put("paymentAmount", Integer.parseInt(row.getCell(9).getStringCellValue().replaceAll(",", "")));
					param.put("shippingFee", Integer.parseInt(row.getCell(10).getStringCellValue().replaceAll(",", "")));
					param.put("rewardPoint", Integer.parseInt(row.getCell(11).getStringCellValue().replaceAll(",", "")));
					param.put("totalAmount", Integer.parseInt(row.getCell(12).getStringCellValue().replaceAll(",", "")));
					param.put("perchaser", row.getCell(15).getStringCellValue().substring(0, 3));
					param.put("paymentDt", row.getCell(18).getStringCellValue());
					
					paramList.add(param);
					
					orderNo = row.getCell(1).getStringCellValue();
					paymentMethod = row.getCell(7).getStringCellValue();
					status = row.getCell(8).getStringCellValue();
					perchaser = row.getCell(15).getStringCellValue().substring(0, 3);
					paymentDt = row.getCell(18).getStringCellValue();
					
					detailParam.put("orderNo", orderNo);
					detailParam.put("productNm", row.getCell(2).getStringCellValue());
					detailParam.put("paymentMethod", paymentMethod);
					detailParam.put("status", status);
					detailParam.put("productPrice", Integer.parseInt(row.getCell(4).getStringCellValue().replaceAll(",", "")));
					detailParam.put("productQuantity", Integer.parseInt(row.getCell(5).getStringCellValue().replaceAll(",", "")));
					detailParam.put("totalPrice", Integer.parseInt(row.getCell(6).getStringCellValue().replaceAll(",", "")));
					detailParam.put("perchaser", perchaser);
					detailParam.put("paymentDt", paymentDt);
					detailParamList.add(detailParam);
				} else {
					// 주문번호가 있는 행일 경우 주문번호에 대한 총 결제 금액 정보 추가
					
					detailParam.put("orderNo", orderNo);
					detailParam.put("productNm", row.getCell(2).getStringCellValue());
					detailParam.put("paymentMethod", paymentMethod);
					detailParam.put("status", status);
					detailParam.put("productPrice", Integer.parseInt(row.getCell(4).getStringCellValue().replaceAll(",", "")));
					detailParam.put("productQuantity", Integer.parseInt(row.getCell(5).getStringCellValue().replaceAll(",", "")));
					detailParam.put("totalPrice", Integer.parseInt(row.getCell(6).getStringCellValue().replaceAll(",", "")));
					detailParam.put("perchaser", perchaser);
					detailParam.put("paymentDt", paymentDt);
					
					detailParamList.add(detailParam);
				}
				
			}
		}
		int insertOrder = sqlSession.insert("excel.insertOrder", paramList);
		int insertOrderDetail = sqlSession.insert("excel.insertOrderDetail", detailParamList);
		if (insertOrder > 0 && insertOrderDetail > 0) {
			result.put("ret", "success");
		}
		return result;
	}

}
