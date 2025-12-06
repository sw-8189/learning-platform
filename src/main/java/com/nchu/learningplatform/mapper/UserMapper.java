// learning-platform/src/main/java/com/nchu/learningplatform/mapper/UserMapper.java
package com.nchu.learningplatform.mapper;

import com.nchu.learningplatform.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    int insert(User user);

    User findByUsername(@Param("username") String username);

    User findByPhone(@Param("phone") String phone);

    User findByEmail(@Param("email") String email);

    User findByUsernameOrPhone(@Param("usernameOrPhone") String usernameOrPhone);

    User findById(@Param("id") Long id);

    int updateUserInfo(User user);

    int updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);

    // 管理员功能：分页查询用户列表
    List<User> pageQueryUser(@Param("offset") int offset, 
                             @Param("size") int size, 
                             @Param("keyword") String keyword);

    // 管理员功能：统计用户总数
    long countUser(@Param("keyword") String keyword);

    // 管理员功能：删除用户
    int deleteById(@Param("id") Long id);

    // 管理员功能：更新用户状态
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
