package com.aosbank.lovetodotask.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.aosbank.lovetodotask.pojo.BaseType;
import com.aosbank.lovetodotask.pojo.Receive;
import com.aosbank.lovetodotask.pojo.Task;
import com.aosbank.lovetodotask.utils.Base64Util;
import com.aosbank.lovetodotask.utils.ConfigUtils;


@Controller
@RequestMapping("/task")
public class TaskInfoController extends BaseController {

	private static final String NEEDSCOREKEY = "EACHTASKNEEDSCORE";
	private static final int EACHPAGECOUNT = 20;
	private static FastDateFormat sim = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
	private static String imgPath;
	private static File loadFialImgFile = null;
	
	static {
		imgPath = ConfigUtils.getValue("img.path");
		File dir = new File(imgPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String path = TaskInfoController.class.getResource("").getPath() + "tempimg/localfail.jpg";
		loadFialImgFile = new File(path);
	}
	
	/**
	 * 完成微信授权并获取用户id
	 * @param request
	 * @param code
	 * @param response
	 */
	@RequestMapping(value="needScore", method=RequestMethod.GET)
	public void getUserInfo (@RequestParam("uinfo") String useridEncode, @RequestParam("taskcount") String taskCount, HttpServletResponse response){
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Object> resMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(useridEncode)) {
			String res = this.reorganizeRes(resMap, responseStatus.need_relogin);
			writer.write(res);
			writer.flush();
			writer.close();
			return;
		}
		String needScore = confMap.get(NEEDSCOREKEY);
		try {
			int eachScore = Integer.parseInt(needScore);
			int count = Integer.parseInt(taskCount);
			int holeScore = eachScore*count;
			resMap.put("need_score", holeScore + "");
		} catch (Exception e) {
			String res = this.reorganizeRes(resMap, responseStatus.system_error);
			writer.write(res);
			writer.flush();
			writer.close();
			return;
		}
		String res = this.reorganizeRes(resMap, responseStatus.succuess);
		writer.write(res);
		writer.flush();
		writer.close();
	}
	
	@RequestMapping(value="submitTask", method=RequestMethod.POST)
	public void submitTask (@RequestBody String reqBody, HttpServletResponse response) {
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Object> resMap = new HashMap<String, Object>();
		responseStatus status = responseStatus.succuess;
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
		int userid = Base64Util.decode(uinfo, BaseType.mcl13);
		if (userid == 0) {
			String res = this.reorganizeRes(resMap, responseStatus.need_relogin);
			writer.write(res);
			writer.flush();
			writer.close();
			return;
		}
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
			String res = this.reorganizeRes(resMap, responseStatus.need_relogin);
			writer.write(res);
			writer.flush();
			writer.close();
			return;
		}
		BigDecimal score = (BigDecimal) scoreRes.get(0).get("score");
		if (score.intValue() < needScore) {
			status = responseStatus.deficiency_of_integral;
		} else {
			TransactionStatus transStatus = dao.setTransactionStart();
			String insertSql = "insert into tb_task (user_id,app_name,search_key,rank,comment_key,task_count,need_score,ctime) values "
					+ "(" + userid + ",'" + appname + "','" + searchKeyword + "'," + rankNum + ",'" + comkeyword + "'," + taskCount + "," + needScore + ",now())";
			boolean insertRes = dao.ExecuteSql(insertSql);
			if (insertRes) {
				String cutDownScoreSql = "update tb_user set  score=(score-" + needScore + ") where id=" + userid;
				boolean cutDownScoreRes = dao.ExecuteSql(cutDownScoreSql);
				if (!cutDownScoreRes) {
					status = responseStatus.system_error;
					dao.transRollback(transStatus);
				} else {
					dao.transCommit(transStatus);
					redis.insertUserInfo(userid, null);
				}
			} else {
				status = responseStatus.system_error;
				dao.transRollback(transStatus);
			}
		}
		String res = this.reorganizeRes(resMap, status);
		writer.write(res);
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
		String selectSql = "select b.id as userid,a.id as taskid,b.nickname,a.app_name,a.search_key,a.rank,a.comment_key,a.task_count,a.receive_count,a.complate_count,a.ctime from tb_task a left join tb_user b on a.user_id=b.id and a.receive_count<a.task_count order by a.ctime desc limit " + start + "," + EACHPAGECOUNT;
		List<Map<String, Object>> selectRes = dao.select(selectSql);
		for (Map<String, Object> map : selectRes) {
			Task task = new Task();
			int userid = (Integer) map.get("userid");
			String eachUinfo = Base64Util.encode(userid, BaseType.mcl13);
			task.setUinfo(eachUinfo);
			int taskid = (Integer) map.get("taskid");
			String eachTaskinfo = Base64Util.encode(taskid, BaseType.mcl13);
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
		String res = this.reorganizeRes(resMap, responseStatus.succuess);
		writer.write(res);
		writer.flush();
		writer.close();
	}
	
	@RequestMapping(value="receiveTask", method=RequestMethod.POST)
	public void receiveTask (@RequestBody String reqBody, HttpServletResponse response) {
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Object> resMap = new HashMap<String, Object>();
		responseStatus status = responseStatus.succuess;
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
		int userid = Base64Util.decode(uinfo, BaseType.mcl13);
		if (userid == 0) {
			String res = this.reorganizeRes(resMap, responseStatus.need_relogin);
			writer.write(res);
			writer.flush();
			writer.close();
			return;
		}
		String taskInfo = reqMap.get("taskinfo");
		int taskId = Base64Util.decode(taskInfo, BaseType.mcl13);
		if (taskId == 0) {
			status = responseStatus.task_not_exist;
		} else {
			String selectReveiveTaskSql = "select id from tb_receive where receive_uid=" + userid +" and task_id=" + taskId;
			List<Map<String, Object>> selectReceiveTaskRes = dao.select(selectReveiveTaskSql);
			if (selectReceiveTaskRes.size() > 0) {
				status = responseStatus.task_exist;
			} else {
				TransactionStatus transStatus = dao.setTransactionStart();
				String receiveTaskSql = "insert into tb_receive (task_id,receive_uid,ctime) values (" + taskId + "," + userid + ",now())";
				boolean receiveTaskRes = dao.ExecuteSql(receiveTaskSql);
				if (receiveTaskRes) {
					String updateTaskSql = "update tb_task set receive_count=(receive_count+1) where id=" + taskId;
					boolean updateTaskRes = dao.ExecuteSql(updateTaskSql);
					if (updateTaskRes) {
						dao.transCommit(transStatus);
					} else {
						dao.transRollback(transStatus);
						status = responseStatus.system_error;
					}
				} else {
					dao.transRollback(transStatus);
					status = responseStatus.system_error;
				}
			}
		}
		String res = this.reorganizeRes(resMap, status);
		writer.write(res);
		writer.flush();
		writer.close();
	}

	@RequestMapping(value="receiveList", method=RequestMethod.GET)
	public void getReceiveList (@RequestParam("page") String pageNum, @RequestParam("uinfo") String uInfo, HttpServletResponse response) {
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Object> resMap = new HashMap<String, Object>();
		int userid = Base64Util.decode(uInfo, BaseType.mcl13);
		if (userid == 0) {
			String res = this.reorganizeRes(resMap, responseStatus.need_relogin);
			writer.write(res);
			writer.flush();
			writer.close();
			return;
		}
		List<Receive> receiveList = new ArrayList<Receive>();
		int page = 1;
		try {
			page = Integer.parseInt(pageNum);
		} catch (Exception e) {
		}
		int start = (page -1)*EACHPAGECOUNT;
		String selectSql = "select a.id,a.task_id,a.receive_uid,a.img_id_list,a.utime,b.app_name,b.search_key,b.comment_key from tb_receive a left join tb_task b on a.task_id=b.id and b.user_id=" + userid + " and a.img_id_list!=''  and a.audit_result='0' order by a.ctime limit " + start + "," + EACHPAGECOUNT;
		List<Map<String, Object>> selectRes = dao.select(selectSql);
		for (Map<String, Object> map : selectRes) {
			Receive receive = new Receive();
			int taskid = (Integer) map.get("task_id");
			String taskInfo = Base64Util.encode(taskid, BaseType.mcl13);
			receive.setTask_info(taskInfo);
			int receiveid = (Integer) map.get("id");
			String eachReceiveInfo = Base64Util.encode(receiveid, BaseType.mcl15);
			receive.setReceive_info(eachReceiveInfo);
			int receiveuid = (Integer) map.get("receive_uid");
			String receiveUinfo = Base64Util.encode(receiveuid, BaseType.mcl15);
			receive.setReceive_uinfo(receiveUinfo);
			String receiveUname = redis.getUserName(receiveuid);
			if (StringUtils.isBlank(receiveUname)) {
				redis.insertUserInfo(receiveuid, null);
				receiveUname = redis.getUserName(receiveuid);
			}
			receive.setReceive_uname(receiveUname);
			String imgIdListStr = (String) map.get("img_id_list");
			if (StringUtils.isBlank(imgIdListStr)) {
				continue;
			}
			String[] imgIdArr = imgIdListStr.split(",");
			List<String> imgNameList = Arrays.asList(imgIdArr);
			receive.setImg_name_list(imgNameList);
			String appName = (String) map.get("app_name");
			receive.setApp_name(appName);
			String searchKey = (String) map.get("search_key");
			receive.setSearch_key(searchKey);
			String commentKey = (String) map.get("comment_key");
			receive.setComment_key(commentKey);
			Timestamp utime = (Timestamp) map.get("utime");
			String utimeStr = sim.format(utime);
			receive.setUtime(utimeStr);
			receiveList.add(receive);
		}
		resMap.put("receivelist", receiveList);
		String res = this.reorganizeRes(resMap, responseStatus.succuess);
		writer.write(res);
		writer.flush();
		writer.close();
	}
	
	@RequestMapping(value="receivequalified", method=RequestMethod.GET)
	public void getReceiveQualified (@RequestParam("taskinfo") String taskinfo, @RequestParam("uinfo") String uInfo, @RequestParam("type") String type,@RequestParam("receiveinfo") String receiveinfo,@RequestParam("receiveuinfo") String receiveuinfo,HttpServletResponse response) {
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Object> resMap = new HashMap<String, Object>();
		int userid = Base64Util.decode(uInfo, BaseType.mcl13);
		if (userid == 0) {
			String res = this.reorganizeRes(resMap, responseStatus.need_relogin);
			writer.write(res);
			writer.flush();
			writer.close();
			return;
		}
		String text = "";
		if (StringUtils.equals("qualified", type)) {
			int receiveid = Base64Util.decode(receiveinfo, BaseType.mcl15);
			int receiveuid = Base64Util.decode(receiveuinfo, BaseType.mcl15);
			int taskid = Base64Util.decode(taskinfo, BaseType.mcl13);
			String sql = "select id from tb_receive where receive_uid=" + receiveuid + " and task_id=" + taskid;
			List<Map<String, Object>> selectRes = dao.select(sql);
			if (selectRes.size() < 1) {
				text = "没有这个任务";
			} else {
				int id = (Integer) selectRes.get(0).get("id");
				sql = "select user_id from tb_task where id=" + taskid;
				List<Map<String, Object>> selectTaskRes = dao.select(sql);
				int checkuid = 0;
				if (selectTaskRes.size() > 0) {
					int user_id =  (Integer) selectTaskRes.get(0).get("selectTaskRes");
				}
				if (id != receiveid || checkuid != userid) {
					text = "没有这个任务";
				} else {
					TransactionStatus status = dao.setTransactionStart();
					sql = "update audit_result ";
					
				}
			}
		} else if (StringUtils.equals("unqualified", type)) {
			
		} else {
			text = "参数写错了，亲，别再做抓取了，没什么前途的，多研究研究大数据吧";
		}
		
		resMap.put("receivelist", receiveList);
		String res = this.reorganizeRes(resMap, responseStatus.succuess);
		writer.write(res);
		writer.flush();
		writer.close();
	}
	
	@RequestMapping(value="receiveimg", method=RequestMethod.GET)
	public void showImg (@RequestParam("imgname") String imgName, @RequestParam("taskinfo") String taskInfo, HttpServletResponse response) {
        response.setContentType("image/jpeg");
        ServletOutputStream out = null;
        try {
			out = response.getOutputStream();
			int taskid = Base64Util.decode(taskInfo, BaseType.mcl13);
			if (taskid == 0) {
				FileInputStream is = new FileInputStream(loadFialImgFile);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				is.close();
				out.flush();
				return;
				}
				String thisImgPath = imgPath + "task_" +  taskid + "/" + imgName;
				File imgFile = new File(thisImgPath);
				FileInputStream is = new FileInputStream(imgFile);
				byte[] buffer = new byte[1024];
		        int len = 0;
		        while ((len = is.read(buffer)) != -1) {
		            out.write(buffer, 0, len);
		        }
		        is.close();
		        out.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
}


