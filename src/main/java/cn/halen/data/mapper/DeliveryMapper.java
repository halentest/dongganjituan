package cn.halen.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cn.halen.data.pojo.Delivery;

public interface DeliveryMapper {
	@Select("select * from delivery")
	List<Delivery> list();
	
	@Select("select * from delivery where id=#{id}")
	Delivery selectById(@Param("id") int id);
}
