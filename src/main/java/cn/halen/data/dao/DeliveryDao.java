package cn.halen.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.halen.data.pojo.Delivery;

@Repository
public class DeliveryDao {
	@Value("${list.delivery}")
	private String sqlListDelivery;
	@Value("${get.delivery.by.id}")
	private String sqlGetById;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public Delivery getById(final int id) {
		
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement statement = con.prepareStatement(sqlGetById);
				statement.setLong(1, id);
				return statement;
			}
		}, new DeliveryResultSetExtractor());
	}
	
	public List<Delivery> list(List<Integer> ids) {
		if(ids.isEmpty()) 
			return null;
		
		if(ids.size()==1) {
			return jdbcTemplate.query("select * from delivery where id=" + ids.get(0), new DeliveryRowMapper());
		}
		
		StringBuilder builder = new StringBuilder("(");
		int count = 0;
		for(int id : ids) {
			builder.append(id);
			if(count==ids.size()-1) {
				builder.append(")");
			} else {
				builder.append(",");
			}
			count++;
		}
		String sql = "select * from delivery where id in" + builder.toString();
		return jdbcTemplate.query(sql, new DeliveryRowMapper());
	}
}

class DeliveryResultSetExtractor implements ResultSetExtractor<Delivery> {

	@Override
	public Delivery extractData(ResultSet rs) throws SQLException,
			DataAccessException {
		rs.next();
		Delivery delivery = new Delivery();
		delivery.setId(rs.getInt("id"));
		delivery.setName(rs.getString("name"));
		return delivery;
	}
}

class DeliveryRowMapper implements RowMapper<Delivery> {

	@Override
	public Delivery mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Delivery delivery = new Delivery();
		delivery.setId(rs.getInt("id"));
		delivery.setName(rs.getString("name"));
		return delivery;
	}
}
