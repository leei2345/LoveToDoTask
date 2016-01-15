package com.aosbank.lovetodotask.pojo;

import java.io.Serializable;

import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * @author leei
 *
 */
public class Base implements Serializable {
	
	/**
	 * 搜狗拼音非常好用
	 */
	private static final long serialVersionUID = 1L;
	
	public String toString () {
		String r = com.alibaba.fastjson.JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue);
		return r;
	}
	
}
