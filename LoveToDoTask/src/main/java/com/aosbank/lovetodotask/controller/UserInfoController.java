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

import com.alibaba.fastjson.JSON;
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
		if (StringUtils.isBlank(useridEncode)) {
			String path = "index.jsp?msg=need_relogin";
			try {
				response.sendRedirect(path);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			return;
		}
		Map<String, String> userInfo = this.checkLoginStatus(useridEncode);
		if (userInfo == null) {
			String path = "../../index.jsp?msg=need_relogin";
			try {
				response.sendRedirect(path);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			return;
		} 
		Map<String, String> resMap = new HashMap<String, String>();
		resMap.put(User.headimgurl.toString(), userInfo.get(User.headimgurl.toString()));
		resMap.put(User.nickName.toString(), userInfo.get(User.nickName.toString()));
		resMap.put(User.score.toString(), userInfo.get(User.score.toString()));
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.write(JSON.toJSONString(resMap));
		writer.flush();
		writer.close();
	}

}
