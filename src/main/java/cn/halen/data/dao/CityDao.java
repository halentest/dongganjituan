package cn.halen.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.halen.data.pojo.City;

@Repository
public class CityDao {
	@Value("${list.city.by.province.id}")
	private String sqlListByProvinceId;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<City> list(int pid) {
		
		List<City> list = jdbcTemplate.query(sqlListByProvinceId, new Object[] {pid}, new CityRowMapper());
		return list;
	}
}

class CityRowMapper implements RowMapper<City> {

	@Override
	public City mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		City city = new City();
		city.setId(rs.getInt("id"));
		city.setName(rs.getString("name"));
		return city;
	}
}
