package com.aosbank.lovetodotask.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

@Component
public class MysqlDao {
	
	@Autowired
	private DruidDataSource dataSource;
	@Autowired
	private DataSourceTransactionManager transactionManager;
	private static MysqlDao instance;
	
	public MysqlDao () {
		instance = this;
	}
	
	public static MysqlDao getInstance () {
		return instance;
	}

	public TransactionStatus setTransactionStart () {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);
		return status;
	}
	
	public void transRollback (TransactionStatus status) {
		transactionManager.rollback(status);
	}
	
	public void transCommit (TransactionStatus status) {
		transactionManager.commit(status);
	}
	
	public boolean ExecuteSql (String sql) {
		boolean res = false;
		DruidPooledConnection conn = null;
		Statement st = null;
		try {
			conn = dataSource.getConnection();
			st = conn.createStatement();
			int count  = st.executeUpdate(sql);
			if (count > 0) {
				res = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (st != null)
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
	        	try {
	        		conn.close();
	        	} catch (SQLException e) {
	        		e.printStackTrace();
	        	}
		}
		return res;
	}
	
	public List<Map<String, Object>> select (String sql) {
		List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
		DruidPooledConnection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			statement = conn.createStatement();
			rs = statement.executeQuery(sql);
			while (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				Map<String, Object> map = new HashMap<String, Object>();
				for (int index = 1; index <= count; index++) {
					String key = rsmd.getColumnLabel(index);
					Object value = rs.getObject(index);
					map.put(key, value);
				}
				resList.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) 
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        if (statement != null)
	        	try {
	        		statement.close();
	        	} catch (SQLException e) {
	        		e.printStackTrace();
	        	}
	        if (conn != null)
	        	try {
	        		conn.close();
	        	} catch (SQLException e) {
	        		e.printStackTrace();
	        	}
		}
		return resList;
	}
	
	public int insertAndGetId (String sql) {
		int insertId = 0;
		DruidPooledConnection conn = null;
		PreparedStatement insertStatement = null;
		ResultSet generatedKeys = null;
		try {
			conn = dataSource.getConnection();
			insertStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			insertStatement.execute();
			generatedKeys = insertStatement.getGeneratedKeys();
			if(generatedKeys.next()) {
				insertId = generatedKeys.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (generatedKeys != null) 
				try {
					generatedKeys.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        if (insertStatement != null)
	        	try {
	        		insertStatement.close();
	        	} catch (SQLException e) {
	        		e.printStackTrace();
	        	}
	        if (conn != null)
	        	try {
	        		conn.close();
	        	} catch (SQLException e) {
	        		e.printStackTrace();
	        	}
		}
		return insertId;
	}
	
	public Map<String, String> getConfMap () {
		Map<String, String> res = new HashMap<String, String>();
		DruidPooledConnection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			statement = conn.createStatement();
			rs = statement.executeQuery("select name,value from tb_conf");
			while (rs.next()) {
				String name = rs.getString(1);
				String value = rs.getString(2);
				res.put(name, value);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) 
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        if (statement != null)
	        	try {
	        		statement.close();
	        	} catch (SQLException e) {
	        		e.printStackTrace();
	        	}
	        if (conn != null)
	        	try {
	        		conn.close();
	        	} catch (SQLException e) {
	        		e.printStackTrace();
	        	}
		}
		return res;
	}
	

}
