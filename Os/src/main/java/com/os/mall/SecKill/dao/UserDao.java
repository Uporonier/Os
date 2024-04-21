package com.os.mall.SecKill.dao;

import com.os.mall.SecKill.entity.User1;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserDao {
    @Select("select * from sk_user where id = #{id}")
    public User1 getById(@Param("id") Long id);
    @Select("select * from sk_user where nickname = #{nickname}")
    public User1 getByNickName(@Param("nickname") String nickname);
    @Update("update sk_user set password =#{password} where id = #{id}")
    public void update(User1 newUser1);
}
