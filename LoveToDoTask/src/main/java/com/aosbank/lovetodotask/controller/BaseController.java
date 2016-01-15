package com.aosbank.lovetodotask.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aosbank.lovetodotask.dao.MysqlDao;
import com.aosbank.lovetodotask.dao.RedisDao;
import com.aosbank.lovetodotask.utils.Base64Util;

/**
 * 
 * @author leei
 *
 */
@Component
public class BaseController {

	@Autowired
	protected MysqlDao dao;
	@Autowired
	protected RedisDao redis;
	
	private static Pattern usqlp = Pattern.compile("update.*? set\\s+(.*)\\s+where.*");
	protected static Map<String, String> confMap = new HashMap<String, String>();
	
	static {
		confMap = MysqlDao.getInstance().getConfMap();
	}
	
	protected String checkUpdateSql (String usql) {
		try {
			Matcher matcher = usqlp.matcher(usql);
			if (matcher.find()) {
				String params = matcher.group(1);
				String[] kvs = params.split(",");
				for (int index = 0; index < kvs.length; index++) {
					String kv = kvs[index];
					String[] kvArr = kv.trim().split("=");
					if (StringUtils.isBlank(kvArr[1].replace("'", ""))) {
						if (index != (kvs.length - 1)) {
							usql = usql.replaceAll(kv + "\\s?,", "");
						} else {
							usql = usql.replace(kv, "");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usql;
	}
	
	/**
	 * 检测用户信息，超时了需要重新登陆
	 * @param useridEncode
	 * @return not null 未过期 null 已过期
	 */
	public Map<String, String> checkLoginStatus (String useridEncode) {
		int userId = 0;
		Map<String, String> userInfoMap = null;
		try {
			userId = Base64Util.decode(useridEncode);
			userInfoMap = redis.getUserInfo(userId);
		} catch (Exception e) {
			return userInfoMap;
		}
		return userInfoMap;
	}
	
	
}
