package com.aosbank.lovetodotask.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.aosbank.lovetodotask.pojo.Task;
import com.aosbank.lovetodotask.utils.Base64Util;


@Controller
@RequestMapping("/task")
public class TaskInfoController extends BaseController {

	private static final String NEEDSCOREKEY = "EACHTASKNEEDSCORE";
	private static final int EACHPAGECOUNT = 20;
	private static FastDateFormat sim = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 完成微信授权并获取用户id
	 * @param request
	 * @param code
	 * @param response
	 */
	@RequestMapping(value="needScore", method=RequestMethod.GET)
	public void getUserInfo (@RequestParam("uinfo") String useridEncode, @RequestParam("taskcount") String taskCount, HttpServletResponse response){
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		if (StringUtils.isBlank(useridEncode)) {
			String path = "../../index.jsp?msg=need_relogin";
			try {
				response.sendRedirect(path);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			return;
		}
		String needScore = confMap.get(NEEDSCOREKEY);
		Map<String, String> resMap = new HashMap<String, String>();
		try {
			int eachScore = Integer.parseInt(needScore);
			int count = Integer.parseInt(taskCount);
			int holeScore = eachScore*count;
			resMap.put("need_score", holeScore + "");
		} catch (Exception e) {
			String path = "../../index.jsp?msg=system_error";
			try {
				response.sendRedirect(path);
			} catch (IOException in) {
				in.printStackTrace();
			} 
			return;
		}
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
	
	@RequestMapping(value="submitTask", method=RequestMethod.POST)
	public void submitTask (@RequestBody String reqBody, HttpServletResponse response) {
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		String[] reqParamsArr = reqBody.split("\\&");
		Map<String, String> reqMap = new HashMap<String, String>();
		for (String reqParam : reqParamsArr) {
			String[] paramArr = reqParam.split("=");
			if (paramArr.length == 1) {
				continue;
			}
			try {
				String value = URLDecoder.decode(paramArr[1], "UTf-8");
				reqMap.put(paramArr[0], value);
			} catch (UnsupportedEncodingException e) {
				continue;
			}
		}
		String uinfo = reqMap.get("uinfo");
		int userid = Base64Util.decode(uinfo);
		if (userid == 0) {
			String path = "../../index.jsp?msg=need_relogin";
			try {
				response.sendRedirect(path);
			} catch (IOException in) {
				in.printStackTrace();
			} 
			return;
		}
		Map<String, Object> resMap = new HashMap<String, Object>();
		int status = 0;
		String searchKeyword = reqMap.get("searchkeyword");
		String appname = reqMap.get("appname");
		int rankNum = 0;
		String rank = reqMap.get("rank");
		try {
			rankNum = Integer.parseInt(rank);
		} catch (Exception e) {
		}
		String comkeyword = reqMap.get("comkeyword");
		String taskCount = reqMap.get("taskCount");
		String eachTaskNeedScore = confMap.get(NEEDSCOREKEY);
		int needScore = Integer.parseInt(eachTaskNeedScore)*Integer.parseInt(taskCount);
		String selectScoreSql = "select score from tb_user where id=" + userid;
		List<Map<String, Object>> scoreRes = dao.select(selectScoreSql);
		if (scoreRes.size() == 0) {
			String path = "../../index.jsp?msg=need_relogin";
			try {
				response.sendRedirect(path);
			} catch (IOException in) {
				in.printStackTrace();
			} 
			return;
		}
		BigDecimal score = (BigDecimal) scoreRes.get(0).get("score");
		if (score.intValue() < needScore) {
			status = 1;
		} else {
			String insertSql = "insert into tb_task (user_id,app_name,search_key,rank,comment_key,task_count,need_score,ctime) values "
					+ "(" + userid + ",'" + appname + "','" + searchKeyword + "'," + rankNum + ",'" + comkeyword + "'," + taskCount + "," + needScore + ",now())";
			boolean insertRes = dao.ExecuteSql(insertSql);
			if (insertRes) {
				String cutDownScoreSql = "update tb_user set  score=(score-" + needScore + ") where id=" + userid;
				boolean cutDownScoreRes = dao.ExecuteSql(cutDownScoreSql);
				if (!cutDownScoreRes) {
					status = 3;
				}
			} else {
				status = 2;
			}
		}
		resMap.put("res_status", status);
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
	
	@RequestMapping(value="taskList", method=RequestMethod.GET)
	public void getTaskList (@RequestParam("page") String pageNum, HttpServletResponse response) {
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Object> resMap = new HashMap<String, Object>();
		List<Task> taskList = new ArrayList<Task>();
		int page = 1;
		try {
			page = Integer.parseInt(pageNum);
		} catch (Exception e) {
		}
		int start = (page -1)*EACHPAGECOUNT;
		String selectSql = "select a.id,b.nickname,a.app_name,a.search_key,a.rank,a.comment_key,a.task_count,a.receive_count,a.complate_count,a.ctime from tb_task a left join tb_user b on a.user_id=b.id and a.receive_count<a.task_count order by a.ctime desc limit " + start + "," + EACHPAGECOUNT;
		List<Map<String, Object>> selectRes = dao.select(selectSql);
		for (Map<String, Object> map : selectRes) {
			Task task = new Task();
			int taskid = (Integer) map.get("id");
			String eachTaskinfo = Base64Util.encode(taskid);
			task.setTask_info(eachTaskinfo);
			String uName = (String) map.get("nickname");
			if (StringUtils.isBlank(uName)) {
				continue;
			}
			task.setUser_name(uName);
			String appName = (String) map.get("app_name");
			task.setApp_name(appName);
			String searchKey = (String) map.get("search_key");
			task.setSearch_key(searchKey);
			int rank = (Integer) map.get("rank");
			task.setRank(rank);
			String commentKey = (String) map.get("comment_key");
			task.setComment_key(commentKey);
			int taskCount = (Integer) map.get("task_count");
			task.setTask_count(taskCount);
			int receiveCount = (Integer) map.get("receive_count");
			task.setReceive_count(receiveCount);
			int complateCount = (Integer) map.get("complate_count");
			task.setComplate_count(complateCount);
			Timestamp ctime = (Timestamp) map.get("ctime");
			String ctimeStr = sim.format(ctime);
			task.setCtime(ctimeStr);
			taskList.add(task);
		}
		resMap.put("tasklist", taskList);
		writer.write(JSON.toJSONString(resMap));
		writer.flush();
		writer.close();
	}
	
	@RequestMapping(value="receiveTask", method=RequestMethod.POST)
	public void receiveTask (@RequestBody String reqBody, HttpServletResponse response) {
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		String[] reqParamsArr = reqBody.split("\\&");
		Map<String, String> reqMap = new HashMap<String, String>();
		for (String reqParam : reqParamsArr) {
			String[] paramArr = reqParam.split("=");
			if (paramArr.length == 1) {
				continue;
			}
			try {
				String value = URLDecoder.decode(paramArr[1], "UTf-8");
				reqMap.put(paramArr[0], value);
			} catch (UnsupportedEncodingException e) {
				continue;
			}
		}
		String uinfo = reqMap.get("uinfo");
		int userid = Base64Util.decode(uinfo);
		if (userid == 0) {
			String path = "../../index.jsp?msg=need_relogin";
			try {
				response.sendRedirect(path);
			} catch (IOException in) {
				in.printStackTrace();
			} 
			return;
		}
		Map<String, Object> resMap = new HashMap<String, Object>();
		int status = 0;
		String taskInfo = reqMap.get("taskinfo");
		int taskId = Base64Util.decode(taskInfo);
		if (taskId == 0) {
			status = 1;
		} else {
			TransactionStatus transStatus = dao.setTransactionStart();
			String receiveTaskSql = "insert into tb_receive (task_id,receive_uid,ctime) values (" + taskId + "," + userid + ",now())";
			boolean receiveTaskRes = dao.ExecuteSql(receiveTaskSql);
			if (receiveTaskRes) {
				String updateTaskSql = "update tb_task set receive_count=(receive_count-1) where id=" + taskId;
				boolean updateTaskRes = dao.ExecuteSql(updateTaskSql);
				if (updateTaskRes) {
					dao.transCommit(transStatus);
				} else {
					dao.transRollback(transStatus);
					status = 2;
				}
			}
		}
		resMap.put("res_status", status);
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


