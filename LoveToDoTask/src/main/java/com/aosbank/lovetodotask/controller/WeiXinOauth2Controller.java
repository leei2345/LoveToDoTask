package com.aosbank.lovetodotask.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.aosbank.lovetodotask.http.HttpHandle;
import com.aosbank.lovetodotask.pojo.User;
import com.aosbank.lovetodotask.utils.Base64Util;
import com.aosbank.lovetodotask.utils.ConfigUtils;


@Controller
@RequestMapping("/oauth2")
public class WeiXinOauth2Controller extends BaseController {
	
	private static String tempUrl;
	private static String tempGetUserInfoUrl;
	
	static {
		tempUrl = ConfigUtils.getValue("weixin.access.token.url");
		tempGetUserInfoUrl = ConfigUtils.getValue("weixin.getuserinfo");
	}

	/**
	 * 完成微信授权并获取用户id
	 * @param request
	 * @param code
	 * @param response
	 */
	@RequestMapping(value="login", method=RequestMethod.GET)
	public void top100BuyOrSaleStock(HttpServletRequest request, @RequestParam("code") String code, HttpServletResponse response){
		if (StringUtils.isBlank(code)) {
			String path = "index.jsp?msg=code_is_null";
			try {
				response.sendRedirect(path);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			return;
		}
		String getAccessTokenUrl = tempUrl.replace("$$", code);
		String accessToken = null;
		String openId = null;
		try {
			HttpHandle http = new HttpHandle();
			String accessGet = http.httpGet(getAccessTokenUrl);
			JSONObject object = JSONObject.parseObject(accessGet);
			accessToken = object.getString("access_token");
			openId = object.getString("openid");
			if (StringUtils.isBlank(openId) || StringUtils.isBlank(accessToken)) {
				String path = "index.jsp?msg=weixin_oauth_error";
				try {
					response.sendRedirect(path);
				} catch (IOException in) {
					in.printStackTrace();
				}
				return;
			}
		} catch (Exception e) {
			String path = "index.jsp?msg=weixin_oauth_error";
			try {
				response.sendRedirect(path);
			} catch (IOException in) {
				in.printStackTrace();
			}
			return;
		}
		int userId = 0;
		int score = 0;
		String getUserInfoUrl = tempGetUserInfoUrl.replace("##", accessToken).replace("$$", openId);
		HttpHandle http = new HttpHandle();
		String userInfo = http.httpGet(getUserInfoUrl);
		try {
			JSONObject object = JSONObject.parseObject(userInfo);
			String nickName = object.getString("nickname");
			int sex = object.getIntValue("sex");
			String province = object.getString("province");
			String city = object.getString("city");
			String country = object.getString("country");
			String headimgurl = object.getString("headimgurl");
			String unionid = object.getString("unionid");
			String sql = "select id,score from tb_user where openid='" + openId + "'";
			List<Map<String, Object>> userRes = dao.select(sql);
			if (userRes.size() == 0) {
				String insertSql = "insert into tb_user (openid,nickname,sex,province,city,country,headimgurl,unionid,ltime,utime) values ("
						+ "'" + openId + "','" + nickName + "'," + sex + ",'" + province + "','" + city + "','" + country + "','" + headimgurl + "','" + unionid + "',now(),now())";
				userId = dao.insertAndGetId(insertSql);
			} else {
				Map<String, Object> userInfoMap = userRes.get(0);
				userId = (Integer) userInfoMap.get("id");
				BigDecimal scoreBig = (BigDecimal) userInfoMap.get("score");
				score = scoreBig.intValue();
				String updateSql = "update tb_user set nickname='" + nickName + "',sex=" + sex + ",province='" + province + "',city='" + city + "',country='" + country + "',headimgurl='" + headimgurl + "',utime=now() where id=" + userId;
				updateSql = this.checkUpdateSql(updateSql);
				dao.ExecuteSql(updateSql);
			}
			/** 用户登陆信息存储redis */
			Map<String, String> userInfoMap = new HashMap<String, String>();
			userInfoMap.put(User.nickname.toString(), nickName);
			userInfoMap.put(User.sex.toString(),  sex + "");
			userInfoMap.put(User.province.toString(), province);
			userInfoMap.put(User.city.toString(), city);
			userInfoMap.put(User.country.toString(), country);
			userInfoMap.put(User.headimgurl.toString(), headimgurl);
			userInfoMap.put(User.openid.toString(), openId);
			userInfoMap.put(User.accessToken.toString(), accessToken);
			userInfoMap.put(User.score.toString(), score + "");
			redis.insertUserInfo(userId, userInfoMap);
		} catch (Exception e) {
			String path = "../../index.jsp?msg=weixin_userinfo_null";
			try {
				response.sendRedirect(path);
			} catch (IOException in) {
				in.printStackTrace();
			}
			return;
		}
		String encodeUserId = Base64Util.encode(userId);
		String path = "userinit.jsp?data=" + encodeUserId;
		try {
			response.sendRedirect(path);
		} catch (IOException in) {
			in.printStackTrace();
		}
	}

}
