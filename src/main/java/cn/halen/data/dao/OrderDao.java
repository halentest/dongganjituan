//package cn.halen.data.dao;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
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
//import cn.halen.data.OrderStatus;
//import cn.halen.data.pojo.FenXiaoShang;
//import cn.halen.data.pojo.Order;
//import cn.halen.data.pojo.Template;
//
//@Repository
//public class OrderDao {
//	@Value("${list.order}")
//	private String sqlListOrder;
//	@Value("${list.order.by.fenxiaoshang}")
//	private String sqlListOrderByFenxiaoshang;
//	@Value("${get.order.by.id}")
//	private String sqlGetOrderById;
//	@Value("${get.order.by.orderid}")
//	private String sqlGetByOrderId;
//	@Value("${add.order}")
//	private String sqlAddOrder;
//	
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//	
//	public int addOrder(final Order order) {
//		int count =  jdbcTemplate.update(sqlAddOrder, new PreparedStatementSetter() {
//			@Override
//			public void setValues(PreparedStatement ps) throws SQLException {
//				ps.setLong(1, order.getOrder_id());
//				ps.setString(2, order.getName());
//				ps.setString(3, order.getPhone());
//				ps.setString(4, order.getAddress());
//				ps.setString(5, order.getPostcode());
//				ps.setString(6, order.getDelivery());
//				ps.setInt(7, order.getTemplate().getId());
//				ps.setInt(8, order.getFenxiaoshang().getId());
//			}
//		});
//		return count;
//	}
//	
//	public Order getById(final long id) {
//		return jdbcTemplate.query(new PreparedStatementCreator() {
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con)
//					throws SQLException {
//				PreparedStatement statement = con.prepareStatement(sqlGetOrderById);
//				statement.setLong(1, id);
//				return statement;
//			}
//		}, new OrderResultSetExtractor());
//	}
//	
//	public Order getByOrderId(final long orderId) {
//		
//		return jdbcTemplate.query(new PreparedStatementCreator() {
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con)
//					throws SQLException {
//				PreparedStatement statement = con.prepareStatement(sqlGetByOrderId);
//				statement.setLong(1, orderId);
//				return statement;
//			}
//		}, new OrderResultSetExtractor());
//	}
//	
//	public List<Order> listOrder(final int start, final int num) {
//		List<Order> list = jdbcTemplate.query(new PreparedStatementCreator() {
//
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con)
//					throws SQLException {
//				PreparedStatement statement = con.prepareStatement(sqlListOrder);
//				statement.setInt(1, start);
//				statement.setInt(2, num);
//				return statement;
//			}
//			
//		}, new OrderRowMapper());
//		return list;
//	}
//	
//	public List<Order> listOrderByFenXiaoShang(final int fenxiaoshangId, final int start, final int num) {
//		List<Order> list = jdbcTemplate.query(new PreparedStatementCreator() {
//
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con)
//					throws SQLException {
//				PreparedStatement statement = con.prepareStatement(sqlListOrder);
//				statement.setInt(1, fenxiaoshangId);
//				statement.setInt(2, start);
//				statement.setInt(3, num);
//				return statement;
//			}
//			
//		}, new OrderRowMapper());
//		return list;
//	}
//	
//	class OrderResultSetExtractor implements ResultSetExtractor<Order> {
//		@Override
//		public Order extractData(ResultSet rs) throws SQLException,
//				DataAccessException {
//			rs.next();
//			Order order = new Order();
//			order.setId(rs.getInt("id"));
//			order.setOrder_id(rs.getLong("order_id"));
//			order.setName(rs.getString("name"));
//			order.setPhone(rs.getString("phone"));
//			order.setAddress(rs.getString("address"));
//			order.setPostcode(rs.getString("postcode"));
//			order.setDelivery(rs.getString("delivery"));
//			order.setDeliveryMoney(rs.getInt("delivery_money"));
//			
//			Template template = new Template();
//			template.setId(rs.getInt("template_id"));
//			
//			order.setTemplate(template);
//			order.setTotal_weight(rs.getInt("total_weight"));
//			order.setHuokuan(rs.getInt("huokuan"));
//			order.setGoodsCount(rs.getInt("goods_count"));
//			
//			FenXiaoShang fenxiaoshang = new FenXiaoShang();
//			fenxiaoshang.setId(rs.getInt("fenxiaoshang_id"));
//			
//			order.setStatus(OrderStatus.toOrderStatus(rs.getInt("status")));
//			order.setCreated(rs.getTimestamp("created"));
//			order.setModified(rs.getTimestamp("modified"));
//			return order;
//		}
//	}
//
//	class OrderRowMapper implements RowMapper<Order> {
//
//		@Override
//		public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
//			
//			Order order = new Order();
//			order.setId(rs.getInt("id"));
//			order.setOrder_id(rs.getLong("order_id"));
//			order.setName(rs.getString("name"));
//			order.setPhone(rs.getString("phone"));
//			order.setAddress(rs.getString("address"));
//			order.setPostcode(rs.getString("postcode"));
//			order.setDelivery(rs.getString("delivery"));
//			order.setDeliveryMoney(rs.getInt("delivery_money"));
//			
//			Template template = new Template();
//			template.setId(rs.getInt("template_id"));
//			
//			order.setTemplate(template);
//			order.setTotal_weight(rs.getInt("total_weight"));
//			order.setHuokuan(rs.getInt("huokuan"));
//			order.setGoodsCount(rs.getInt("goods_count"));
//			
//			FenXiaoShang fenxiaoshang = new FenXiaoShang();
//			fenxiaoshang.setId(rs.getInt("fenxiaoshang_id"));
//			
//			order.setStatus(OrderStatus.toOrderStatus(rs.getInt("status")));
//			order.setCreated(rs.getTimestamp("created"));
//			order.setModified(rs.getTimestamp("modified"));
//			return order;
//		}
//	}
//}
