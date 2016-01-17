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
	</style>
	<title>love_to_task</title>
	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/zepto_min.js"></script>
	<script type="text/javascript" src="js/touchslider.js"></script>
	<script type="text/javascript" src="js/cookie_tools.js"></script>
	<script type="text/javascript">
 	
	$(document).ready(function(){
		var url = location.search;
		var uinfo = "";
	 	if (url.indexOf("data") != 0 && url != "") {
	 		var str = url.substr(1);
	 		uinfo = str.substr(5);
	 	}
	 	var headimgUrl = "";
	 	var nickName = "";
	 	var score = "";
	 	if (uinfo == "") {
	 		uinfo =  $.cookie("uinfo");
	 		if (uinfo == "" || uinfo == null) {
	 			location.href = "./index.jsp?msg=need_relogin";
	 		}
	 	}
		var userinfo = $.ajax({url : "action/user/simpleinfo?uinfo=" + uinfo,	async : false});
		var responseJson = eval("(" + userinfo.responseText + ")");
		var status = responseJson.status;
		var msg = responseJson.msg;
		if (status != 0) {
 			location.href = "./index.jsp?msg=" + msg;
		}
		var userInfoJson = responseJson.result;
		headimgUrl = userInfoJson.headimgurl;
		nickName = userInfoJson.nickname;
		score = userInfoJson.score;
		$.cookie("uinfo", uinfo, { expires: 60 });
		$("img#headimg").attr("src", headimgUrl);
		$("b#nickname").text(nickName);
		$("b#score").text(score);
		$("a#score").attr("href","scorehis.jsp?uinfo=" + uinfo);
		$("a#mytask").attr("href","mytask.jsp?uinfo=" + uinfo);
	});

</script>
</head>
<body>
	<div class="top">
		<h2>
			love to do task
		</h2>
	</div>
	<div class="menu">
		<ul>
			<li><span><img id="headimg" src="img/head.jpg" /></span></li>
			<li><span><a id="mytask">用户昵称<br/><b id="nickname"></b><br /><b>我的任务</b></a></span></li>
			<li style="width: 34%"><span><a id="score" href="">当前积分<br /><b id="score">0</b><br/><b>积分历史</b></a></span></li>
		</ul>
	</div>
	<br />
	<br />
	<br />
	<br />
	<br />
	<div class="newbox" id="slider1">
		<a href="makeScore.jsp" class="go_btn">赚积分</a>
	</div>
	<br />
	<br />
	<br />
	<br />
	<br />

	<div class="newbox" id="slider4">
		<a href="makeTask.jsp" class="go_btn">发任务</a>
	</div>
	<br />
	<br />
	<br />
	<br />
	<br />
	<div class="newbox" id="slider4">
		<a href="checkTask.jsp" class="go_btn">审核</a>
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