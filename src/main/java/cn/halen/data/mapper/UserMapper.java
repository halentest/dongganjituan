package cn.halen.data.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cn.halen.data.pojo.User;

public interface UserMapper {
	@Select("SELECT * FROM user1 WHERE username = #{username}")
	User getUserByUsername(@Param("username") String username);
}
