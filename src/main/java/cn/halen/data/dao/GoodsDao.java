//package cn.halen.data.dao;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.dao.DataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.PreparedStatementSetter;
//import org.springframework.jdbc.core.ResultSetExtractor;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Repository;
//
//import cn.halen.controller.formbean.GoodsBase;
//import cn.halen.controller.formbean.GoodsStore;
//import cn.halen.data.pojo.Goods;
//import cn.halen.exception.UpdateZeroException;
//
//@Repository
//public class GoodsDao {
//	
//	@Value("${list.goods}")
//	private String sqlListGoods;
//	@Value("${get.goods.by.id}")
//	private String sqlGetGoodsById;
//	@Value("${add.goods}")
//	private String sqlAddGoods;
//	@Value("${update.goods.store}")
//	private String sqlUpdateGoodsStore;
//	@Value("${update.goods.base}")
//	private String sqlUpdateGoodsBase;
//	
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//	
//	public List<Goods> list() {
//		
//		List<Goods> list = jdbcTemplate.query(sqlListGoods, new GoodsRowMapper());
//		return list;
//	}
//	//@Transactional(rollbackFor=Exception.class)
//	public int addGoods(Goods goods) throws Exception {
//		int count = jdbcTemplate.update(sqlAddGoods, new Object[] {
//				goods.getHid(),
//				goods.getColor(),
//				goods.getWeight(),
//				goods.getPrice(),
//				goods.getThity_four(),
//				goods.getThity_five(),
//				goods.getThity_six(),
//				goods.getThity_seven(),
//				goods.getThity_eight(),
//				goods.getThity_nine(),
//				goods.getForty(),
//				goods.getForty_one(),
//				goods.getForty_two(),
//				goods.getForty_three(),
//				goods.getForty_four(),
//				goods.getForty_five(),
//				goods.getDiscount()
//		});
//		return count;
//	}
//	
//	public int updateStore(final GoodsStore goodsStore) throws UpdateZeroException {
//		int count =  jdbcTemplate.update(sqlUpdateGoodsStore, new PreparedStatementSetter() {
//
//			@Override
//			public void setValues(PreparedStatement ps) throws SQLException {
//				ps.setInt(1, goodsStore.getThity_eight());
//				ps.setInt(2, goodsStore.getThity_nine());
//				ps.setInt(3, goodsStore.getForty());
//				ps.setInt(4, goodsStore.getForty_one());
//				ps.setInt(5, goodsStore.getForty_two());
//				ps.setInt(6, goodsStore.getForty_three());
//				ps.setInt(7, goodsStore.getForty_four());
//				ps.setLong(8, goodsStore.getId());
//				ps.setObject(9, goodsStore.getModified());
//			}
//		});
//		if(count==0) {
//			throw new UpdateZeroException("");
//		}
//		return count;
//	}
//	
//	public int updateBase(GoodsBase goodsBase) throws UpdateZeroException {		
//		int count = jdbcTemplate.update(sqlUpdateGoodsBase, new Object[] {
//				goodsBase.getHid(), goodsBase.getColor(), goodsBase.getWeight(), goodsBase.getPrice(),
//				goodsBase.getId(), goodsBase.getModified()	
//		});
//		if(count==0) {
//			throw new UpdateZeroException("");
//		}
//		return count;
//	}
//	
//	public Goods get(final long id) {
//		Goods goods = jdbcTemplate.queryForObject(sqlGetGoodsById, new Object[] {id}, new GoodsRowMapper());
//		return goods;
//	}
//}
//
//class GoodsResultSetExtractor implements ResultSetExtractor<Goods> {
//	@Override
//	public Goods extractData(ResultSet rs) throws SQLException,
//			DataAccessException {
//		Goods goods = new Goods();
//		goods.setId(rs.getInt("id"));
//		goods.setHid(rs.getString("hid"));
//		goods.setColor(rs.getString("color"));
//		goods.setWeight(rs.getInt("weight"));
//		goods.setPrice(rs.getInt("price"));
//		goods.setThity_four(rs.getInt("thity_four"));
//		goods.setThity_five(rs.getInt("thity_five"));
//		goods.setThity_six(rs.getInt("thity_six"));
//		goods.setThity_seven(rs.getInt("thity_seven"));
//		goods.setThity_eight(rs.getInt("thity_eight"));
//		goods.setThity_nine(rs.getInt("thity_nine"));
//		goods.setForty(rs.getInt("forty"));
//		goods.setForty_one(rs.getInt("forty_one"));
//		goods.setForty_two(rs.getInt("forty_two"));
//		goods.setForty_three(rs.getInt("forty_three"));
//		goods.setForty_four(rs.getInt("forty_four"));
//		goods.setForty_five(rs.getInt("forty_five"));
//		goods.setDiscount(rs.getFloat("discount"));
//		goods.setCreated(rs.getTimestamp("created"));
//		goods.setModified(rs.getTimestamp("modified"));
//		return goods;
//	}
//}
//
//class GoodsRowMapper implements RowMapper<Goods> {
//
//	@Override
//	public Goods mapRow(ResultSet rs, int rowNum) throws SQLException {
//		
//		Goods goods = new Goods();
//		goods.setId(rs.getInt("id"));
//		goods.setHid(rs.getString("hid"));
//		goods.setColor(rs.getString("color"));
//		goods.setWeight(rs.getInt("weight"));
//		goods.setPrice(rs.getInt("price"));
//		goods.setThity_four(rs.getInt("thity_four"));
//		goods.setThity_five(rs.getInt("thity_five"));
//		goods.setThity_six(rs.getInt("thity_six"));
//		goods.setThity_seven(rs.getInt("thity_seven"));
//		goods.setThity_eight(rs.getInt("thity_eight"));
//		goods.setThity_nine(rs.getInt("thity_nine"));
//		goods.setForty(rs.getInt("forty"));
//		goods.setForty_one(rs.getInt("forty_one"));
//		goods.setForty_two(rs.getInt("forty_two"));
//		goods.setForty_three(rs.getInt("forty_three"));
//		goods.setForty_four(rs.getInt("forty_four"));
//		goods.setForty_five(rs.getInt("forty_five"));
//		goods.setDiscount(rs.getFloat("discount"));
//		goods.setCreated(rs.getTimestamp("created"));
//		goods.setModified(rs.getTimestamp("modified"));
//		return goods;
//	}
//	
//}
