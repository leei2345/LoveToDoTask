package com.aosbank.lovetodotask.utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;

import com.aosbank.lovetodotask.pojo.BaseType;

public class Base64Util {
 
    /**
     * 将二进制数据编码为BASE64字符串
     * @param binaryData
     * @return
     */
    public static String encode(int userId, BaseType type) {
    	int res = userId*type.num;
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
    public static int decode(String base64String, BaseType type) {
    	byte[] resArr = Base64.decodeBase64(base64String.getBytes());
    	String intStr = null;
    	int userId = 0;
		try {
			intStr = new String(resArr, "UTF-8");
			userId = Integer.parseInt(intStr)/type.num;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return userId;
    }

    
    public static void main(String[] args) throws IOException {
    	BufferedReader reader = new BufferedReader(new FileReader("/home/leei/vm_change/切字.txt"));
    	String line;
    	Set<String> set = new HashSet<String>();
    	while ((line = reader.readLine()) != null) {
    		char[] ca = line.toCharArray();
    		for (char c : ca) {
				set.add(String.valueOf(c));
			}
    	}
    	BufferedWriter writer = new BufferedWriter(new FileWriter("/home/leei/vm_change/result.txt"));
    	for (String str : set) {
			writer.write(str + "\r\n");
		}
    	writer.flush();
    	writer.close();
    	reader.close();
    }

}
