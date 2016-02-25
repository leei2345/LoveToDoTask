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
	.min{width:320px;height:auto;}
	</style>
	<title>love_to_task</title>
  	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/zepto_min.js"></script>
	<script type="text/javascript" src="js/touchslider.js"></script>
	<script type="text/javascript" src="js/cookie_tools.js"></script>
	
	<script type="text/javascript" src="js/jquery.mousewheel-3.0.2.pack.js"></script>
	<script type="text/javascript" src="js/jquery.fancybox-1.3.1.js"></script>
	<script type="text/javascript" src="js/pngobject.js"></script>
 	<link rel="stylesheet" href="css/style.css" type="text/css" />
	<link rel="stylesheet" href="css/jquery.fancybox-1.3.1.css" type="text/css" />
	
	
	<script type="text/javascript">
 	
 	var pagenum = 1;
	var uinfo = "";
	function getReceiveList() {
		var receiveListRes = $.ajax({url : "action/task/receiveList?page=" + pagenum + "&uinfo=" + uinfo,	async : false});
		var responseJson = eval("(" + receiveListRes.responseText + ")");
		var receiveListJson = responseJson.result;
		var receiveList = receiveListJson.receivelist;
		for (var index=0; index < receiveList.length; index++) {
			var receive = receiveList[index];
			var taskInfo = receive.task_info;
			var eachUinfo = receive.receive_uinfo;
			var receiveUname = receive.receive_uname;
			var eachReceiveInfo = receive.receive_info;
			var imgNameList = receive.img_name_list;
			var app_name = receive.app_name;
			var search_key = receive.search_key;
			var comment_key = receive.comment_key;
			var utime = receive.utime;
			var imgtdStr = "";
			for (var imgIndex = 0; imgIndex < imgNameList.length; imgIndex++) {
				var eachImgName = imgNameList[imgIndex];
				var eachImgTdStr = "<tr><td>图片" + (imgIndex + 1) + "</td><td><a id=\"receiveimg\" href=\"action/task/receiveimg?imgname=" + eachImgName + "&taskinfo=" + taskInfo + "\"><img class=\"min\" src=\"action/task/receiveimg?imgname=" + eachImgName + "&taskinfo=" + taskInfo + "\"/></a></td></tr>";		
				imgtdStr += eachImgTdStr;
			}
			var innerTableStr = "<div id=\"" + index + "\"  style=\"display:none\" class=\"newbox\"><table class=\"tasktable\" border=\"1\" bordercolor=\"red\" ><tbody>" +
													"<tr style=\"display:none\" id=\"" + taskInfo + "\"><td>" + eachUinfo + "</td><td>" + eachReceiveInfo + "</td></tr>" +
													"<tr><td>执行人</td><td>" + receiveUname + "</td></tr>" +
													"<tr><td>提交时间</td><td>" + utime + "</td></tr>" +
													"<tr><td>应用名称</td><td>" + app_name + "</td></tr>" +												
													"<tr><td>搜索关键词</td><td>" + search_key + "</td></tr>" +			
													"<tr><td>评论关键词</td><td>" + comment_key + "</td></tr>" +	
													imgtdStr +		
													"<tr><td><input type=\"button\" value=\"审核通过\" onclick=\"receiveQualified(" + index + ")\"/></td><td><input type=\"button\" value=\"审核不通过\" onclick=\"receiveUnqualified(" + index + ")\"/></td></tr>" +	
													"</tbody></table></div>";
			var outTaskStr = "<tr><td>" + app_name + "</td><td>" + search_key + "</td><td>" + comment_key + "</td><td>" + receiveUname + "</td><td><input type=\"button\" value=\"查看\" onclick=\"showReceiveDetail('" + index + "')\"/></td></tr>";
			$('tbody#tasklist').append(outTaskStr);
			$('div#eachTaskShow').append(innerTableStr);
		}
		pagenum++;
	}
	
	function showReceiveDetail (index) {
		$('div#eachTaskShow>div').attr("style","display:none");
		$('div#eachTaskShow>div#' + index).attr("style","display:block");
		$("a#receiveimg").fancybox({
			'showCloseButton':true,
			'hideOnOverlayClick':true,
			'overlayShow':true
		});
		$('body,html').animate({scrollTop:0},500); 
	}
	
	function receiveUnqualified (index) {
		var taskInfo = $('div#eachTaskShow>div#' + index + '>table>tbody>tr:eq(0)').attr("id");
		var receiceUInfo = $('div#eachTaskShow>div#' + index + '>table>tbody>tr:eq(0) > td:eq(0)').text();
		var receiveInfo = $('div#eachTaskShow>div#' + index + '>table>tbody>tr:eq(0) > td:eq(1)').text();
		var trueOrFalse = window.confirm("单击“确定”继续。单击“取消”停止。"); 
		if (trueOrFalse == true) {
			$.ajax({
				cache : true,
				type : "GET",
				url : "action/task/receivequalified?receiveinfo=" + receiveInfo + "&type=unqualified&uinfo=" + uinfo + "&receiveuinfo=" +receiceUInfo + "&taskinfo=" + taskInfo,
				async : false,
				error : function(request) {
					alert("Connection error");
					location.reload();
				},
				success : function(res) {
					var resData = res.result;
					var msg = resData.text;
					if (msg != "succ") {
						alert(resData);
					} else {
						alert("提交成功");
					}
					location.reload();
				}
			});
		}
		pagenum=1;
		location.reload();
	}
	
	function receiveQualified (index){
		var taskInfo = $('div#eachTaskShow>div#' + index + '>table>tbody>tr:eq(0)').attr("id");
		var receiceUInfo = $('div#eachTaskShow>div#' + index + '>table>tbody>tr:eq(0) > td:eq(0)').text();
		var receiveInfo = $('div#eachTaskShow>div#' + index + '>table>tbody>tr:eq(0) > td:eq(1)').text();
		var trueOrFalse = window.confirm("单击“确定”继续。单击“取消”停止。"); 
		if (trueOrFalse == true) {
			$.ajax({
				cache : true,
				type : "GET",
				url : "action/task/receivequalified?receiveinfo=" + receiveInfo + "&type=qualified&uinfo=" + uinfo + "&receiveuinfo=" +receiceUInfo + "&taskinfo=" + taskInfo,
				async : false,
				error : function(request) {
					alert("Connection error");
					location.reload();
				},
				success : function(res) {
					var resData = res.result;
					var msg = resData.text;
					if (msg != "succ") {
						alert(resData);
					} else {
						alert("提交成功");
					}
					location.reload();
				}
			});
		}
		pagenum=1;
		location.reload();
	}
	
	var idRe = new RegExp("\\d+"); 
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
	 	$("a#score").attr("href","scorehis.jsp");
		$("a#mytask").attr("href","mytask.jsp");
		getReceiveList();
		$("a#receiveimg").fancybox({
			'showCloseButton':true
		});
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
		<p>审核</p>
	</div>
	<div id="eachTaskShow" class="newrow" >
    </div>
	
	<div class="newrow">
    <div class="newbox" id="slider1">
    <div class="taskform">
  	  <br/>
               	<table class="tasktable" border="1" bordercolor="red" >
               	<tbody id="tasklist">
               		<tr><td><p>app</p></td><td><p>搜索关键词</p></td><td><p>评论关键词</p></td><td><p>领取用户</p></td><td><p>提交详情</p></td></tr>
               	</tbody>
               	</table>
    </div>
    <div class="newbox">
		<a onclick="getReceiveList()"><b>查看更多</b></a>
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