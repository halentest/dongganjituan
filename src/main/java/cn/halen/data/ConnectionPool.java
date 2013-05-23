package cn.halen.data;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPool {
	
	private static Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
	private static JdbcConnectionPool cp = null;
	
	static {
		cp = JdbcConnectionPool.create(
				"jdbc:h2:tcp://localhost:9092/mydb;JMX=TRUE", "sa", "");
	}
	
	public static Connection get() {
		
		try {
			return cp.getConnection();
		} catch (SQLException e) {
			logger.error("Error to get a connection : ", e);
		}
		return null;
	}
	        
}
