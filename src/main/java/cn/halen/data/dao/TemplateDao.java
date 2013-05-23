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

import cn.halen.data.pojo.City;
import cn.halen.data.pojo.Delivery;
import cn.halen.data.pojo.Template;

@Repository
public class TemplateDao {
	@Value("${get.template.by.city.delivery}")
	private String sqlGetByCityDelivery;
	@Value("${get.deliveris.by.city}")
	private String sqlGetDeliveriesByCity;
	@Value("${get.deliveris.by.id}")
	private String sqlGetById;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Integer> getDeliveriesByCity(int cityId) {
		return jdbcTemplate.queryForList(sqlGetDeliveriesByCity, Integer.class, new Object[] {cityId});
	}
	
	public Template getById(final int id) {
		Template template = jdbcTemplate.query(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement statement = con.prepareStatement(sqlGetById);
				statement.setInt(1, id);
				return statement;
			}
		}, new TemplateResultSetExtractor());
		return template;
	}
	
	public Template getTemplateByCityDelivery(final int cityId, final int deliveryId) {
		Template template = jdbcTemplate.query(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement statement = con.prepareStatement(sqlGetByCityDelivery);
				statement.setInt(1, cityId);
				statement.setInt(2, deliveryId);
				return statement;
			}
		}, new TemplateResultSetExtractor());
		return template;
	}
	
	class TemplateResultSetExtractor implements ResultSetExtractor<Template> {
		@Override
		public Template extractData(ResultSet rs) throws SQLException,
				DataAccessException {
			rs.next();
			Template template = new Template();
			template.setId(rs.getInt("id"));
			
			City city = new City();
			city.setId(rs.getInt("city_id"));
			
			template.setCity(city);
			
			Delivery delivery = new Delivery();
			delivery.setId(rs.getInt("delivery_id"));
			
			template.setDelivery(delivery);
			template.setBase(rs.getInt("base"));
			template.setPerAdd(rs.getInt("per_add"));
			return template;
		}
	}

	class TemplateRowMapper implements RowMapper<Template> {

		@Override
		public Template mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			Template template = new Template();
			template.setId(rs.getInt("id"));
			
			City city = new City();
			city.setId(rs.getInt("city_id"));
			
			template.setCity(city);
			
			Delivery delivery = new Delivery();
			delivery.setId(rs.getInt("delivery_id"));
			
			template.setDelivery(delivery);
			template.setBase(rs.getInt("base"));
			template.setPerAdd(rs.getInt("per_add"));
			return template;
		}
	}
}
