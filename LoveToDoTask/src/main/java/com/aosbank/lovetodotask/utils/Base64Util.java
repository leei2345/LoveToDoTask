package com.aosbank.lovetodotask.utils;
import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class Base64Util {
 
    /**
     * 将二进制数据编码为BASE64字符串
     * @param binaryData
     * @return
     */
    public static String encode(int userId) {
    	int res = userId*13;
    	String base64Str = String.valueOf(res);
        try {
        	String resStr = new String(Base64.encodeBase64(base64Str.getBytes()));
            return resStr.replaceAll("\r\n", "");
        } catch (Exception e) {
            return null;
        }
    }
     
    /**
     * 将BASE64字符串恢复为二进制数据
     * @param base64String
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static int decode(String base64String) {
    	byte[] resArr = Base64.decodeBase64(base64String.getBytes());
    	String intStr = null;
    	int userId = 0;
		try {
			intStr = new String(resArr, "UTF-8");
			userId = Integer.parseInt(intStr)/13;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return userId;
    }
    
    /**
     * 将二进制数据编码为BASE64字符串
     * @param binaryData
     * @return
     */
    public static String encodeMobile(String mobile) {
    	if (StringUtils.isBlank(mobile)) {
    		return null;
    	}
    	byte[] mobileArr = mobile.getBytes();
        try {
            return new String(Base64.encodeBase64(mobileArr), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    /**
     * 将二进制数据编码为BASE64字符串
     * @param binaryData
     * @return
     */
    public static String encode(String str) {
    	if (StringUtils.isBlank(str)) {
    		return null;
    	}
    	byte[] arr = str.getBytes();
        try {
            return new String(Base64.encodeBase64(arr), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    /**
     * 将二进制数据编码为BASE64字符串
     * @param binaryData
     * @return
     */
    public static String decodeMobile(String baseStr) {
    	if (StringUtils.isBlank(baseStr)) {
    		return null;
    	}
    	byte[] mobileArr = baseStr.getBytes();
        try {
            return new String(Base64.decodeBase64(mobileArr), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    
    
    public static void main(String[] args) {
		System.out.println(encode(2));
	}
 
}