package com.aosbank.lovetodotask.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.aosbank.lovetodotask.pojo.User;


@Controller
@RequestMapping("/user")
public class UserInfoController extends BaseController {

	/**
	 * 完成微信授权并获取用户id
	 * @param request
	 * @param code
	 * @param response
	 */
	@RequestMapping(value="simpleinfo", method=RequestMethod.GET)
	public void getUserInfo (HttpServletRequest request, @RequestParam("uinfo") String useridEncode, HttpServletResponse response){
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		Map<String, Object> resMap = new HashMap<String, Object>();
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (StringUtils.isBlank(useridEncode)) {
			String res = this.reorganizeRes(resMap, responseStatus.need_relogin);
			writer.write(res);
			writer.flush();
			writer.close();
			return;
		}
		Map<String, String> userInfo = this.checkLoginStatus(useridEncode);
		if (userInfo == null || userInfo.size() == 0) {
			String res = this.reorganizeRes(resMap, responseStatus.need_relogin);
			writer.write(res);
			writer.flush();
			writer.close();
			return;
		} 
		resMap.put(User.headimgurl.toString(), userInfo.get(User.headimgurl.toString()));
		resMap.put(User.nickname.toString(), userInfo.get(User.nickname.toString()));
		resMap.put(User.score.toString(), userInfo.get(User.score.toString()));
		String res = this.reorganizeRes(resMap, responseStatus.succuess);
		writer.write(res);
		writer.flush();
		writer.close();
	}

}
