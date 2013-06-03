package cn.halen.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.halen.data.pojo.Distributor;
import cn.halen.data.pojo.User;

@Repository
public class DistributorDao {
	@Value("${list.fenxiaoshang}")
	private String sqlListFenXiaoShang;
	@Value("${get.fenxiaoshang.by.id}")
	private String sqlGetFenXiaoShangById;
	@Value("${get.fenxiaoshang.by.userid}")
	private String sqlGetByUserId;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Distributor> list() {
		
		List<Distributor> list = jdbcTemplate.query(sqlListFenXiaoShang, new FenXiaoShangRowMapper());
		return list;
	}
	
	public Distributor get(final long id) {
		Distributor fenXiaoShang = jdbcTemplate.queryForObject(sqlGetFenXiaoShangById, new Object[] {id}, new FenXiaoShangRowMapper());
		return fenXiaoShang;
	}
	
	public Distributor getByUserId(final int userId) {
		Distributor fenXiaoShang = jdbcTemplate.queryForObject(sqlGetByUserId, new Object[] {userId}, new FenXiaoShangRowMapper());
		return fenXiaoShang;
	}
}

class FenXiaoShangRowMapper implements RowMapper<Distributor> {

	@Override
	public Distributor mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Distributor fenXiaoShang = new Distributor();
		fenXiaoShang.setId(rs.getInt("id"));
		
		User user = new User();
		user.setId(rs.getInt("user_id"));
		
		fenXiaoShang.setUser(user);
		fenXiaoShang.setDeposit(rs.getLong("deposit"));
		fenXiaoShang.setDiscount(rs.getFloat("discount"));
		fenXiaoShang.setCreated(rs.getTimestamp("created"));
		fenXiaoShang.setModified(rs.getTimestamp("modified"));
		return fenXiaoShang;
	}
}
