package kr.co.automart.jewelry.common;

//import java.util.Arrays;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.text.SimpleDateFormat;
//import java.util.Base64;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



//import javax.crypto.Cipher;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//import javax.xml.bind.DatatypeConverter;
//import java.security.AlgorithmParameters;

//import com.google.common.base.Charsets;
//import com.google.common.hash.Hashing;

//import jakarta.servlet.http.HttpServletRequest;
//import kr.co.automart.exportcouncil.common.utils.mapper.CommonMapper;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonUtilAES256 {
//	public static String alg = "AES/CBC/PKCS5Padding";
//	private final String key = "exportcouncil!@#$";
//	private final String iv = key.substring(0, 16);

	public String encrypt(String text) throws Exception {
		
/*		
		Cipher cipher = Cipher.getInstance(alg);
		SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes(), "AES");
		IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

		byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
		return Base64.getEncoder().encodeToString(encrypted);
*/		
		return encrypt(text,SessionConstant.HKEY);
	}
	
	
	public String encrypt(String text,String secretKey) throws Exception {
		
		String iv = secretKey.substring(0, 16);
		String key = secretKey.substring(0, 32);
		
		try {
			IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
			byte[] encryptedBytes = cipher.doFinal(text.getBytes("UTF-8"));
			
			return Base64.getEncoder().encodeToString(encryptedBytes);
			
		} catch (Exception e) {
//			logger.error("===== exception", e);
			throw new RuntimeException(e);
		}
		
	}
	
	
	public String decrypt(String text) throws Exception {
		
		return decrypt(text,SessionConstant.HKEY);
		
/*		
		
		Cipher cipher = Cipher.getInstance(alg);
		SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes(), "AES");
		IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

		byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
		byte[] decrypted = cipher.doFinal(decodedBytes);
		return new String(decrypted, "UTF-8");
*/		
		
		
	}
	
	
	public String decrypt(String text ,String secretKey) throws Exception {
		
		String iv = secretKey.substring(0, 16);
		String key = secretKey.substring(0, 32);
		
		try {
			IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
			
			byte[] decodedBytes = Base64.getDecoder().decode(text);
			byte[] decryptedBytes = cipher.doFinal(decodedBytes);
			
			return new String(decryptedBytes, "UTF-8");
			
		}catch (Exception e) {
			throw new RuntimeException(e);
		}


		
/*		
		Cipher cipher = Cipher.getInstance(alg);
		SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes(), "AES");
		IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

		byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
		byte[] decrypted = cipher.doFinal(decodedBytes);
		return new String(decrypted, "UTF-8");
		
*/		
		
		
	}
	
}
