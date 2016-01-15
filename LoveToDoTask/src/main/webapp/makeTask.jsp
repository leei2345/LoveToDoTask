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
	.tasktable{width:100%; height:100%; border-collapse:separate; border-spacing:10px;}
	</style>
	<title>love_to_task</title>
	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/zepto_min.js"></script>
	<script type="text/javascript" src="js/touchslider.js"></script>
	<script type="text/javascript" src="js/cookie_tools.js"></script>
	<script type="text/javascript">
 	
	var idRe = new RegExp("\\d+"); 
	var uinfo = "";
 	var score = "";
	$(document).ready(function(){
		var url = location.search;
	 	if (url.indexOf("data") != 0 && url != "") {
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
		var userInfoJson = eval("(" + userinfo.responseText + ")");
		headimgUrl = userInfoJson.headimgurl;
		nickName = userInfoJson.nickName;
		score = userInfoJson.score;
		$.cookie("uinfo", uinfo, { expires: 60 });
		$("img#headimg").attr("src", headimgUrl);
		$("b#nickname").text(nickName);
		$("b#score").text(score);
	 	$("a#score").attr("href","scorehis.jsp?uinfo=" + uinfo);
	});
	
	var needScore = "0";
	
	function checkNeedScore() {
		var taskCount = $("input#taskCount").val();
		var idRes = taskCount.match(idRe);
		if (idRes == null) {
			alert("任务数量请填写阿拉伯数字");
			return;
		}
		var needScoreRes = $.ajax({url : "action/task/needScore?uinfo=" + uinfo +"&taskcount=" + taskCount,	async : false});
		var needScoreJson = eval("(" + needScoreRes.responseText + ")");
		needScore = needScoreJson.need_score;
		$("input#needScore").val(needScore);
	}
	
	function submitTask () {
		var ownerScoreNum = Number(score);
		var needScoreNum = Number(needScore);
		if (needScoreNum > ownerScoreNum) {
			alert("当前积分不足，不能发布任务");
			return;
		}
		var searchkeyword = $("#searchkeyword").val();
		if (searchkeyword == "") {
			alert("请填写搜索关键词，否则不能发布任务");
			return;
		}
		var appname = $("#appname").val();
		if (appname == "") {
			alert("请填写App名称，否则不能发布任务");
			return;
		}
		var rank = $("#rank").val();
		var comkeyword = $("#comkeyword").val();
		if (comkeyword == "") {
			alert("请填写评论关键词，否则不能发布任务");
			return;
		}
		var taskCount = $("#taskCount").val();
		if (taskCount == "") {
			alert("任务数量为空，默认发布1份任务");
			taskCount = "1";
		}
		var push_data = "uinfo=" + uinfo +"&searchkeyword=" + searchkeyword+"&appname=" + appname +"&rank=" + rank +"&comkeyword=" + comkeyword +"&taskCount=" + taskCount;
		$.ajax({
			cache : true,
			type : "POST",
			url : "action/task/submitTask",
			data : push_data,
			async : false,
			error : function(request) {
				alert("Connection error");
				location.reload();
			},
			success : function(res) {
				var status = res.res_status;
				if (status == 1) {
					alert("分数不足，未能提交任务");
				} else if (status == 2) {
					alert("系统错误");
				} else if (status == 3) {
					alert("系统错误");
				} else {
					alert("任务提交成功");
					location.href = "./userinit.jsp";
				}
			}
		});
		
		
	}
	

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
			<li><span><a href=""><b id="nickname"></b><br />用户昵称</a></span></li>
			<li style="width: 34%"><span><a id="score" href=""><b id="score">0</b><br />当前积分</a></span></li>
		</ul>
	</div>
	<br />
	<br />
	<div class="newbox" id="slider4">
		<p>发任务</p>
	</div>
	<div class="newrow">
    <div class="newbox" id="slider1">
    <div class="taskform">
  	  <br/>
               	<table class="tasktable">
               		<tr><td><p>搜索关键词</p></td><td><input id="searchkeyword" type="text" width="20"></td></tr>
               		<tr><td><p>应用名称</p></td><td><input id="appname" type="text" width="20"></td></tr>
               		<tr><td><p>当前排名</p></td><td><input id="rank" type="text" width="20"></td></tr>
               		<tr><td><p>评论关键词</p></td><td><input id="comkeyword" type="text" width="20"></td></tr>
               		<tr><td><p>发布数量</p></td><td><input id="taskCount" type="text" width="10" onchange="checkNeedScore();"></td></tr>
               		<tr><td><p>需要积分</p></td><td><input id="needScore" type="text" width="10" disabled="disabled"></td></tr>
               	</table>
    </div>
    <br/>
    <br/>
    <div>
              	<input type="button" value="发布" onclick="submitTask()"/>
    </div>
    </div>

</div>
	<br />
	<br />
	<br />
	<br />
	<br />
	<div style="display: none;"></div>
	<div class="footer">
		<p>Copyright @ 2011-2015 aosbank.com</p>
	</div>
</body>
</html>