package cn.halen.data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import cn.halen.data.ConnectionPool;


public class ConnectionPoolTest {
	
	@Test
	public void test() throws SQLException {
		Connection conn = ConnectionPool.get(); 
	    Statement stmt = conn.createStatement();  
	    stmt.executeUpdate("DROP TABLE IF EXISTS my_table");  
	    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS my_table(name varchar(20))");  
	    conn.setAutoCommit(false);
	    try {
	    	
	    	stmt.executeUpdate("INSERT INTO my_table(name) VALUES('zhh')"); 
	    	throw new RuntimeException();
	    } catch (Exception e) {
	    	
	    	conn.rollback();
	    }
	    ResultSet rs = stmt.executeQuery("SELECT name FROM my_table");  
	    rs.next();  
	    System.out.println(rs.getString(1));  

	    stmt.close();  
	}
}
