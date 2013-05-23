package cn.halen.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import cn.halen.data.pojo.User;

@Repository
public class UserDao {
	@Value("${get.user.by.username}")
	private String sqlGetByUsername;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public User getUserByUsername(final String userName) {
		User user = jdbcTemplate.query(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement statement = con.prepareStatement(sqlGetByUsername);
				statement.setString(1, userName);
				return statement;
			}
		}, new UserResultSetExtractor());
		return user;
	}
	
	class UserResultSetExtractor implements ResultSetExtractor<User> {
		@Override
		public User extractData(ResultSet rs) throws SQLException,
				DataAccessException {
			rs.next();
			User user = new User();
			user.setId(rs.getInt("id"));
			return user;
		}
	}
}
