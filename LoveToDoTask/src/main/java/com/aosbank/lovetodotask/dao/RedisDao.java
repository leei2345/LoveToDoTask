package com.aosbank.lovetodotask.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.aosbank.lovetodotask.pojo.BaseType;
import com.aosbank.lovetodotask.utils.Base64Util;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 
 * @author leei
 *
 */
@Component
public class RedisDao {

	private static RedisDao redis;
	
	private RedisDao () {
		redis = this;
	}
	
	public static RedisDao getInstance () {
		return redis;
	}
	
	@Autowired
	private ShardedJedisPool shardedJedisPool;
	@Autowired
	private MysqlDao dao;
	private static final String USERINFOKEY = "lovetask:userinfo:";
	
	public void insertUserInfo(int userId, Map<String, String> userInfoMap){
		ShardedJedis jedis = shardedJedisPool.getResource();
		if (userInfoMap == null) {
			String sql = "select nickname,sex,openid,province,city,country,headimgurl,score from tb_user where id=" + userId;
			List<Map<String, String>> userInfoList = dao.selectStringResult(sql);
			userInfoMap = userInfoList.get(0);
		}
		jedis.hmset(USERINFOKEY + userId, userInfoMap);
		jedis.expire(USERINFOKEY + userId, 36000);
		shardedJedisPool.returnResource(jedis);
	}
	
	public Map<String, String> getUserInfo(int userId){
		ShardedJedis jedis = shardedJedisPool.getResource();
		Map<String, String> userInfoMap = jedis.hgetAll(USERINFOKEY + userId);
		shardedJedisPool.returnResource(jedis);
		return userInfoMap;
	}
	
	public String getUserName(int userId){
		ShardedJedis jedis = shardedJedisPool.getResource();
		String uName = jedis.hget(USERINFOKEY + userId, "nickname");
		shardedJedisPool.returnResource(jedis);
		return uName;
	}
	
	public static void main(String[] args) {

		@SuppressWarnings("resource")
		ClassPathXmlApplicationContext application = new ClassPathXmlApplicationContext(new String[]{"database.xml"});
		application.start();
		RedisDao redis = (RedisDao) application.getBean("redisDao");
		redis.insertUserInfo(2, null);
		System.out.println(Base64Util.encode(2, BaseType.mcl13));
	}
	
	
}
