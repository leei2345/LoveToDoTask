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
	<link rel="stylesheet" type="text/css" href="css/weui.css" />
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
 	
	$(document).ready(function(){
		var url = location.search;
		var uinfo = "";
	 	if (url.indexOf("data") != 0 && url != "") {
	 		var str = url.substr(1);
	 		uinfo = str.substr(6);
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
		$("a#myreceive").attr("href","myreceive.jsp?uinfo=" + uinfo);
		
		
		
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
			<li><span><a class="weui_cell_bd weui_cell_primary">用户昵称<br/><b id="nickname"></b></a></span></li>
			<li><span><a class="weui_cell_bd weui_cell_primary">当前积分<br /><b id="score">0</b></a></span></li>
		</ul>
		<ul>
			<li><a id="myreceive" class="weui_cell_bd weui_cell_primary">领取的任务</a></li>
			<li><a id="mytask" class="weui_cell_bd weui_cell_primary">发布的任务</a></li>
			<li><a id="score" class="weui_cell_bd weui_cell_primary">积分历史</a></li>
		</ul>
	</div>
	<br />
	<br />
	<div class="newbox" id="slider4">
		<p>我申请的任务(未完成的)</p>
	</div>

	<div class="newrow">
    <div class="newbox" id="slider1">
    <div class="taskform">
  	  <br/>
               	<table class="tasktable" border="1" bordercolor="red" >
               	<tbody id="tasklist">
               		<tr><td><p>用户昵称</p></td><td><p>app</p></td><td><p>搜索关键词</p></td><td><p>评论关键词</p></td><td><p>领取时间</p></td><td><p>提交审核</p></td></tr>
               	</tbody>
               	</table>
    </div>
    <div class="newbox">
		 <a onclick="getTaskList()" class="weui_btn weui_btn_warn">查看更多</a>
	</div>
    </div>

</div>
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