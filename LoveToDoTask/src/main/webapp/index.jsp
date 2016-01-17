<!DOCTYPE html>
<html lang="en">
<head>
		<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript">
		var url = location.search;
	 	if (url.indexOf("msg") != 0 && url != "") {
	 		var str = url.substr(1);
	 		var msg = str.substr(4);
	 		msg = decodeURI(msg);
			alert(msg);
	 	}
		window.location.href="https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=http%3a%2f%2fwww.asobank.com%2flovetodotask%2fweinxin%2flogin&response_type=code&scope=lovetodotask&state=STATE#wechat_redirect";
	</script>
    <title>爱做任务</title>
</head>
<body>
</body>
</html>