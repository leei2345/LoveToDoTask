package com.aosbank.lovetodotask.pojo;

/**
 * 
 * @author leei
 *
 */
public class Task extends Base {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2200603322880906972L;
	
	private String uinfo;
	private String task_info;
	private String user_name;
	private String app_name;
	private String search_key;
	private int rank = 0;
	private String comment_key;
	private int task_count;
	private int receive_count;
	private int complate_count;
	private String ctime;
	
	public String getUinfo() {
		return uinfo;
	}
	public void setUinfo(String uinfo) {
		this.uinfo = uinfo;
	}
	public int getTask_count() {
		return task_count;
	}
	public void setTask_count(int task_count) {
		this.task_count = task_count;
	}
	public String getTask_info() {
		return task_info;
	}
	public void setTask_info(String task_info) {
		this.task_info = task_info;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
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
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getComment_key() {
		return comment_key;
	}
	public void setComment_key(String comment_key) {
		this.comment_key = comment_key;
	}
	public int getReceive_count() {
		return receive_count;
	}
	public void setReceive_count(int receive_count) {
		this.receive_count = receive_count;
	}
	public int getComplate_count() {
		return complate_count;
	}
	public void setComplate_count(int complate_count) {
		this.complate_count = complate_count;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

}
