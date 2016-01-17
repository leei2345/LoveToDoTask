package com.aosbank.lovetodotask.pojo;

import java.util.ArrayList;
import java.util.List;

public class Receive extends Base {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1102700387946738918L;
	private String task_info;
	private String receive_info;
	private String receive_uinfo;
	private String receive_uname;
	private List<String> img_name_list = new ArrayList<String>();
	private String utime;
	private String app_name;
	private String search_key;
	private String comment_key;
	
	public String getTask_info() {
		return task_info;
	}
	public void setTask_info(String task_info) {
		this.task_info = task_info;
	}
	public String getReceive_info() {
		return receive_info;
	}
	public void setReceive_info(String receive_info) {
		this.receive_info = receive_info;
	}
	public String getReceive_uinfo() {
		return receive_uinfo;
	}
	public void setReceive_uinfo(String receive_uinfo) {
		this.receive_uinfo = receive_uinfo;
	}
	public String getReceive_uname() {
		return receive_uname;
	}
	public void setReceive_uname(String receive_uname) {
		this.receive_uname = receive_uname;
	}
	public List<String> getImg_name_list() {
		return img_name_list;
	}
	public void setImg_name_list(List<String> img_name_list) {
		this.img_name_list = img_name_list;
	}
	public String getUtime() {
		return utime;
	}
	public void setUtime(String utime) {
		this.utime = utime;
	}
	public String getApp_name() {
		return app_name;
	}
	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}
	public String getSearch_key() {
		return search_key;
	}
	public void setSearch_key(String search_key) {
		this.search_key = search_key;
	}
	public String getComment_key() {
		return comment_key;
	}
	public void setComment_key(String comment_key) {
		this.comment_key = comment_key;
	}
	
}
