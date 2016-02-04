<!DOCTYPE HTML>
<%@ page language="java" import="java.util.*" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta
	content="initial-scale=1.0,user-scalable=no,maximum-scale=1,width=device-width"
	name="viewport" />
<meta content="initial-scale=1.0,user-scalable=no,maximum-scale=1"  name="viewport">
	<meta content="yes" name="apple-mobile-web-app-capable" />
	<meta content="black" name="apple-mobile-web-app-status-bar-style" />
	<meta content="telephone=no" name="format-detection" />
	<link rel="stylesheet" type="text/css" href="css/touch.css" />
 	<style> 
	#headimg{width:60px; height:60px} 
	.taskform{MARGIN-RIGHT: auto;MARGIN-LEFT: auto;vertical-align:middle;}
	.tasktable{width:100%; height:100%; border-collapse:collapse;border:1px solid #F00; border-spacing:5px;}
	</style>
	<title>love_to_task</title>
	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/zepto_min.js"></script>
	<script type="text/javascript" src="js/touchslider.js"></script>
	<script type="text/javascript" src="js/cookie_tools.js"></script>
	<script type="text/javascript">
 	
 	var mytasknum = 1;
 	var myreveicenum = 1;
	function getTaskList() {
		var taskListRes = $.ajax({url : "action/task/taskList?page=" + pagenum,	async : false});
		var responseJson = eval("(" + taskListRes.responseText + ")");
		var taskListJson = responseJson.result;
		var taskList = taskListJson.tasklist;
		for (var index=0; index<taskList.length; index++) {
			var task = taskList[index];
			var eachUinfo = task.uinfo;
			var eachTaskinfo = task.task_info;
			var uName = task.user_name;
			var app_name = task.app_name;
			var search_key = task.search_key;
			var rank = task.rank;
			var comment_key = task.comment_key;
			var task_count = task.task_count;
			var receive_count = task.receive_count;
			var complate_count = task.complate_count;
			var ctime = task.ctime;
			var innerTableStr = "<div id=\"" + index + "\"  style=\"display:none\" class=\"newbox\"><table class=\"tasktable\" border=\"1\" bordercolor=\"red\" ><tbody>" +
													"<tr style=\"display:none\"><td>" + eachUinfo + "</td><td>" + eachTaskinfo + "</td></tr>" +
													"<tr><td>发起人</td><td>" + uName + "</td></tr>" +
													"<tr><td>应用名称</td><td>" + app_name + "</td></tr>" +												
													"<tr><td>搜索关键词</td><td>" + search_key + "</td></tr>" +			
													"<tr><td>当前排名</td><td>" + rank + "</td></tr>" +		
													"<tr><td>评论关键词</td><td>" + comment_key + "</td></tr>" +		
													"<tr><td>发布任务数</td><td>" + task_count + "</td></tr>" +		
													"<tr><td>已领取数量</td><td>" + receive_count + "</td></tr>" +		
													"<tr><td>已完成数量</td><td>" + complate_count + "</td></tr>" +		
													"<tr><td>发布日期</td><td>" + ctime + "</td></tr>" +	
													"<tr><td><input type=\"button\" value=\"领取任务\" onclick=\"receiveTask(" + index + ")\"/></td><td><input type=\"button\" value=\"关闭\" onclick=\"shutDownTaskDetail()\"/></td></tr>" +	
													"</tbody></table></div>";
			var outTaskStr = "<tr><td>" + uName + "</td><td>" + app_name + "</td><td>" + search_key + "</td><td>" + task_count + "</td><td>" + receive_count + "</td><td><input type=\"button\" value=\"查看\" onclick=\"showTaskDetail('" + index + "')\"/></td></tr>";
			$('tbody#tasklist').append(outTaskStr);
			$('div#eachTaskShow').append(innerTableStr);
		}
		pagenum++;
	}
	
	function showTaskDetail (index) {
		$('div#eachTaskShow>div').attr("style","display:none");
		$('div#eachTaskShow>div#' + index).attr("style","display:block");
		$('body,html').animate({scrollTop:0},500); 
	}
	
	function shutDownTaskDetail () {
		$('div#eachTaskShow>div').attr("style","display:none");
	}
	
	function receiveTask (index){
		var taskUinfo = $('div#eachTaskShow>div#' + index + '>table>tbody>tr:eq(0)>td:eq(0)').text();
		if (taskUinfo == uinfo) {
			alert("不能领取自己提交的任务");
			return;
		}
		var taskInfo = $('div#eachTaskShow>div#' + index + '>table>tbody>tr:eq(0)>td:eq(1)').text();
		var push_data = "uinfo=" + uinfo +"&taskinfo=" + taskInfo;
		$.ajax({
			cache : true,
			type : "POST",
			url : "action/task/receiveTask",
			data : push_data,
			async : false,
			error : function(request) {
				alert("Connection error");
				location.reload();
			},
			success : function(res) {
				var status = res.status;
				var msg = res.msg;
				if (status == 1) {
					location.href = "./index.jsp?msg=" + msg;
				} else if (status != 0) {
					alert(msg);
				} else {
					alert("任务领取成功");
					location.href = "./userinit.jsp";
					
				}
			}
		});
		
	}
	
	var idRe = new RegExp("\\d+"); 
	var uinfo = "";
	$(document).ready(function(){
		var url = location.search;
	 	if (url.indexOf("uinfo") != 0 && url != "") {
	 		var str = url.substr(1);
	 		uinfo = str.substr(5);
	 	}
	 	var headimgUrl = "";
	 	var nickName = "";
	 	if (uinfo == "") {
	 		uinfo =  $.cookie("uinfo");
	 		if (uinfo == "" || uinfo == null) {
	 			location.href = "./index.jsp?msg=need_relogin";
	 		}
	 	}
		var userinfo = $.ajax({url : "action/user/simpleinfo?uinfo=" + uinfo,	async : false});
		var responseJson = eval("(" + userinfo.responseText + ")");
		var userInfoJson = responseJson.result;
		var status = responseJson.status;
		var msg = responseJson.msg;
		if (status != 0) {
 			location.href = "./index.jsp?msg=" + msg;
		}
		headimgUrl = userInfoJson.headimgurl;
		nickName = userInfoJson.nickname;
		score = userInfoJson.score;
		$("img#headimg").attr("src", headimgUrl);
		$("b#nickname").text(nickName);
		$("b#score").text(score);
	 	$("a#score").attr("href","scorehis.jsp?uinfo=" + uinfo);
		$("a#mytask").attr("href","mytask.jsp?uinfo=" + uinfo);
		getTaskList();
	});

</script>
</head>
<body>
	<div class="top">
	<div class="logo"><a href="userinit.jsp">返回首页</a></div>
		<h2>
			love to do task
		</h2>
	</div>
	<div class="menu">
		<ul>
			<li><span><img id="headimg" src="img/head.jpg" /></span></li>
			<li><span><a id="mytask" href="">用户昵称<br/><b id="nickname"></b><br /><b>我的任务</b></a></span></li>
			<li style="width: 34%"><span><a id="score" href="">当前积分<br/><b id="score">0</b><br /><b>积分历史</b></a></span></li>
		</ul>
	</div>
	<br />
	<br />
	<div class="newbox" id="slider4">
		<p>我发起的任务</p>
	</div>
	<div id="eachTaskShow" class="newrow" >
    </div>
	
	<div class="newrow">
    <div class="newbox" id="slider1">
    <div class="taskform">
  	  <br/>
               	<table class="tasktable" border="1" bordercolor="red" >
               	<tbody id="tasklist">
               		<tr><td><p>用户昵称</p></td><td><p>app</p></td><td><p>搜索关键词</p></td><td><p>任务数量</p></td><td><p>已领取数量</p></td><td><p>查看详情</p></td></tr>
               	</tbody>
               	</table>
    </div>
    <div class="newbox">
		<a onclick="getTaskList()"><b>查看更多</b></a>
	</div>
    </div>
</div>
	<br />
	<br />
	<div class="newbox" id="slider4">
		<p>我领取的任务</p>
	</div>
	<div id="eachTaskShow" class="newrow" >
    </div>
	
	<div class="newrow">
    <div class="newbox" id="slider1">
    <div class="taskform">
  	  <br/>
               	<table class="tasktable" border="1" bordercolor="red" >
               	<tbody id="tasklist">
               		<tr><td><p>用户昵称</p></td><td><p>app</p></td><td><p>搜索关键词</p></td><td><p>任务数量</p></td><td><p>已领取数量</p></td><td><p>查看详情</p></td></tr>
               	</tbody>
               	</table>
    </div>
    <div class="newbox">
		<a onclick="getTaskList()"><b>查看更多</b></a>
	</div>
    </div>
</div>
	<br />
	<br />
	<br />
	<div style="display: none;"></div>
	<div class="footer">
		<p>Copyright @ 2011-2015 aosbank.com</p>
	</div>
</body>
</html>