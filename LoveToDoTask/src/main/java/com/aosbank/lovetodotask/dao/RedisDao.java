package com.aosbank.lovetodotask.dao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	
	private static final String USERINFOKEY = "lovetask:userinfo:";
	
	public void insertUserInfo(int userId, Map<String, String> userInfoMap){
		ShardedJedis jedis = shardedJedisPool.getResource();
		jedis.hmset(USERINFOKEY + userId, userInfoMap);
		jedis.expire(USERINFOKEY + userId, 3600);
		shardedJedisPool.returnResource(jedis);
	}
	
	public Map<String, String> getUserInfo(int userId){
		ShardedJedis jedis = shardedJedisPool.getResource();
		Map<String, String> userInfoMap = jedis.hgetAll(USERINFOKEY + userId);
		shardedJedisPool.returnResource(jedis);
		return userInfoMap;
	}
	
}
