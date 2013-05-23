package cn.halen.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.halen.data.pojo.Province;

@Repository
public class ProvinceDao {
	@Value("${list.province}")
	private String sqlListProvince;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Province> list() {
		
		List<Province> list = jdbcTemplate.query(sqlListProvince, new ProvinceRowMapper());
		return list;
	}
}

class ProvinceRowMapper implements RowMapper<Province> {

	@Override
	public Province mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Province province = new Province();
		province.setId(rs.getInt("id"));
		province.setName(rs.getString("name"));
		return province;
	}
}
