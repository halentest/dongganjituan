//package cn.halen.data.dao;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Date;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.dao.DataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.PreparedStatementCreator;
//import org.springframework.jdbc.core.PreparedStatementSetter;
//import org.springframework.jdbc.core.ResultSetExtractor;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Repository;
//
//import cn.halen.data.pojo.Goods;
//import cn.halen.data.pojo.Order;
//import cn.halen.data.pojo.OrderDetail;
//import cn.halen.exception.UpdateZeroException;
//
//@Repository
//public class OrderDetailDao {
//	@Value("${list.orderdetail}")
//	private String sqlListOrderDetail;
//	@Value("${add.orderdetail}")
//	private String sqlAddOrderDetail;
//	@Value("${update.orderdetail.status.by.id}")
//	private String sqlUpdateStatus;
//	@Value("${get.orderdetail.by.id}")
//	private String sqlGetById;
//	
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//	
//	public OrderDetail get(final long id) {
//		return jdbcTemplate.query(new PreparedStatementCreator() {
//
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con)
//					throws SQLException {
//				PreparedStatement statement = con.prepareStatement(sqlGetById);
//				statement.setLong(1, id);
//				return statement;
//			}
//			
//		}, new OrderDetailResultSetExtractor());
//	}
//	
//	public List<OrderDetail> list(final int orderId) {
//		List<OrderDetail> list = jdbcTemplate.query(new PreparedStatementCreator() {
//
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con)
//					throws SQLException {
//				PreparedStatement statement = con.prepareStatement(sqlListOrderDetail);
//				statement.setInt(1, orderId);
//				return statement;
//			}
//		}, new OrderDetailRowMapper());
//		return list;
//	}
//	
//	public int updateStatus(final long id, final int status, final Date modified) throws UpdateZeroException {
//		int count = jdbcTemplate.update(sqlUpdateStatus, new PreparedStatementSetter() {
//
//			@Override
//			public void setValues(PreparedStatement ps) throws SQLException {
//				ps.setInt(1, status);
//				ps.setLong(2, id);
//				ps.setObject(3, modified);
//			}
//			
//		});
//		if(count==0) {
//			throw new UpdateZeroException("");
//		}
//		return count;
//	}
//	
//	public int addOrderDetail(final OrderDetail detail) {
//		int count = jdbcTemplate.update(sqlAddOrderDetail, new PreparedStatementSetter() {
//
//			@Override
//			public void setValues(PreparedStatement ps) throws SQLException {
//				ps.setLong(1, detail.getOrder().getId());
//				ps.setLong(2, detail.getGoods().getId());
//				ps.setInt(3, detail.getThity_eight());
//				ps.setInt(4, detail.getThity_nine());
//				ps.setInt(5, detail.getForty());
//				ps.setInt(6, detail.getForty_one());
//				ps.setInt(7, detail.getForty_two());
//				ps.setInt(8, detail.getForty_three());
//				ps.setInt(9, detail.getForty_four());
//				ps.setFloat(10, detail.getDiscount());
//				ps.setInt(11, detail.getPrice());
//				ps.setInt(12, detail.getHuokuan());
//			}
//		});
//		return count;
//	}
//	
//	class OrderDetailResultSetExtractor implements ResultSetExtractor<OrderDetail> {
//		@Override
//		public OrderDetail extractData(ResultSet rs) throws SQLException,
//				DataAccessException {
//			rs.next();
//			OrderDetail orderDetail = new OrderDetail();
//			orderDetail.setId(rs.getInt("id"));
//			
//			Order order = new Order();
//			order.setId(rs.getLong("order_id"));
//			orderDetail.setOrder(order);
//			
//			Goods goods = new Goods();
//			goods.setId(rs.getInt("goods_id"));
//			orderDetail.setGoods(goods);
//			
//			orderDetail.setThity_four(rs.getInt("thity_four"));
//			orderDetail.setThity_five(rs.getInt("thity_five"));
//			orderDetail.setThity_six(rs.getInt("thity_six"));
//			orderDetail.setThity_seven(rs.getInt("thity_seven"));
//			orderDetail.setThity_eight(rs.getInt("thity_eight"));
//			orderDetail.setThity_nine(rs.getInt("thity_nine"));
//			orderDetail.setForty(rs.getInt("forty"));
//			orderDetail.setForty_one(rs.getInt("forty_one"));
//			orderDetail.setForty_two(rs.getInt("forty_two"));
//			orderDetail.setForty_three(rs.getInt("forty_three"));
//			orderDetail.setForty_four(rs.getInt("forty_four"));
//			orderDetail.setForty_five(rs.getInt("forty_five"));
//			orderDetail.setDiscount(rs.getFloat("discount"));
//			orderDetail.setPrice(rs.getInt("price"));
//			orderDetail.setHuokuan(rs.getInt("huokuan"));
//			orderDetail.setStatus(rs.getInt("status"));
//			orderDetail.setCreated(rs.getTimestamp("created"));
//			orderDetail.setModified(rs.getTimestamp("modified"));
//			return orderDetail;
//		}
//	}
//
//	class OrderDetailRowMapper implements RowMapper<OrderDetail> {
//
//		@Override
//		public OrderDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
//			
//			OrderDetail orderDetail = new OrderDetail();
//			orderDetail.setId(rs.getInt("id"));
//			
//			Order order = new Order();
//			order.setId(rs.getLong("order_id"));
//			orderDetail.setOrder(order);
//			
//			Goods goods = new Goods();
//			goods.setId(rs.getInt("goods_id"));
//			orderDetail.setGoods(goods);
//			
//			orderDetail.setThity_four(rs.getInt("thity_four"));
//			orderDetail.setThity_five(rs.getInt("thity_five"));
//			orderDetail.setThity_six(rs.getInt("thity_six"));
//			orderDetail.setThity_seven(rs.getInt("thity_seven"));
//			orderDetail.setThity_eight(rs.getInt("thity_eight"));
//			orderDetail.setThity_nine(rs.getInt("thity_nine"));
//			orderDetail.setForty(rs.getInt("forty"));
//			orderDetail.setForty_one(rs.getInt("forty_one"));
//			orderDetail.setForty_two(rs.getInt("forty_two"));
//			orderDetail.setForty_three(rs.getInt("forty_three"));
//			orderDetail.setForty_four(rs.getInt("forty_four"));
//			orderDetail.setForty_five(rs.getInt("forty_five"));
//			orderDetail.setDiscount(rs.getFloat("discount"));
//			orderDetail.setPrice(rs.getInt("price"));
//			orderDetail.setHuokuan(rs.getInt("huokuan"));
//			orderDetail.setStatus(rs.getInt("status"));
//			orderDetail.setCreated(rs.getTimestamp("created"));
//			orderDetail.setModified(rs.getTimestamp("modified"));
//			return orderDetail;
//		}
//	}
//}
