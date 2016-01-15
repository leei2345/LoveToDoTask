package com.aosbank.lovetodotask.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.aosbank.lovetodotask.utils.LoggerUtil;


public class HttpHandle {
	
	private HttpClient hc = new HttpClient();
	private static final String RequestCharset = "UTF-8";
	private Map<String, String> RequestHeaderMap = new HashMap<String, String>();
	private static Map<String, String> DefaultRequestHeaderMap = new HashMap<String, String>();
	
	static {
		DefaultRequestHeaderMap.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/41.0.2272.76 Chrome/41.0.2272.76 Safari/537.36");
		DefaultRequestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		DefaultRequestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		DefaultRequestHeaderMap.put("Connection", "keep-alive");
	}
	
	public HttpHandle () {
		hc.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
		hc.getHttpConnectionManager().getParams().setSoTimeout(5000);
	}
	
	public void SetTimeOut (int timeOut) {
		hc.getHttpConnectionManager().getParams().setConnectionTimeout(timeOut);
		hc.getHttpConnectionManager().getParams().setSoTimeout(10000);
	}
	
	public void AddHeader (String key, String value) {
		RequestHeaderMap.put(key, value);
	}
	
	public void InstallProxy (String host, int port) {
		hc.getHostConfiguration().setProxy(host, port);
	}
	
	public String httpGet (String url) {
		return this.httpGet(url, RequestCharset);
	}
	
	public String httpGet(String url, String charset) {
		String page = "";
		GetMethod get = null;
		int status = -1;
		StringBuilder logBuilder = new StringBuilder("[HttpHandle][Get][" + url + "]");
		try {
			get = new GetMethod(url);
			get.setFollowRedirects(false);
			if (RequestHeaderMap.size() == 0) {
				for (Entry<String, String> headers : DefaultRequestHeaderMap.entrySet()) {
					get.addRequestHeader(headers.getKey(), headers.getValue());
				}
			} else {
				for (Entry<String, String> headers : RequestHeaderMap.entrySet()) {
					get.addRequestHeader(headers.getKey(), headers.getValue());
				}
			}
			get.getParams().setContentCharset(RequestCharset);
			status = hc.executeMethod(get);
			InputStream is = get.getResponseBodyAsStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
			String line;
			while ((line = reader.readLine()) != null) {
				page += line + "\n";
			}
		} catch (java.io.IOException e) {
			logBuilder.append("[" + e.toString() + "]");
		} catch (Exception e) {
			logBuilder.append("[" + e.toString() + "]");
		} finally {
			try {
				get.releaseConnection();
			} catch (Exception e) {
				logBuilder.append("[" + e.toString() + "]");
			}
			hc.getHttpConnectionManager().closeIdleConnections(0);
			logBuilder.append("[" + status + "]");
			LoggerUtil.HttpInfoLog(logBuilder.toString());
		}
		if (status != 200) {
			page = "";
		}
		return page;
	}
	
	public String httpPost (String url, String body) {
		return this.httpPost(url, body, RequestCharset);
	}

	protected String httpPost(String url, String body, String charset) {
		String page = "";
		PostMethod post = null;
		int status = -1;
		StringBuilder logBuilder = new StringBuilder("[HttpHandle][Post][" + url + "]");
		try {
			post = new PostMethod(url);
			post.setFollowRedirects(false);
			if (RequestHeaderMap.size() == 0) {
				for (Entry<String, String> headers : DefaultRequestHeaderMap.entrySet()) {
					post.addRequestHeader(headers.getKey(), headers.getValue());
				}
			} else {
				for (Entry<String, String> headers : RequestHeaderMap.entrySet()) {
					post.addRequestHeader(headers.getKey(), headers.getValue());
				}
			}
			post.getParams().setContentCharset(RequestCharset);
			post.setRequestEntity(new StringRequestEntity(body,"application/x-www-form-urlencoded", null));
			status = hc.executeMethod(post);
			InputStream is = post.getResponseBodyAsStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
			String line;
			while ((line = reader.readLine()) != null) {
				page += line + "\n";
			}
		} catch (java.io.IOException e) {
			logBuilder.append("[" + e.toString() + "]");
		} catch (Exception e) {
			logBuilder.append("[" + e.toString() + "]");
		} finally {
			try {
				post.releaseConnection();
			} catch (Exception e) {
				logBuilder.append("[" + e.toString() + "]");
			}
			hc.getHttpConnectionManager().closeIdleConnections(0);
			logBuilder.append("[" + status + "]");
			LoggerUtil.HttpInfoLog(logBuilder.toString());
		}
		if (status != 200) {
			page = "";
		}
		return page;
	}
	
	
	public static void main(String[] args) {
		String url = "http://www.baidu.com";
		HttpHandle h = new HttpHandle();
		System.out.println(h.httpGet(url));
	}
	
}
