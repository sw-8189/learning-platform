// learning-platform/src/main/java/com/nchu/learningplatform/mapper/UserMapper.java
package com.nchu.learningplatform.mapper;

import com.nchu.learningplatform.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    int insert(User user);

    User findByUsername(@Param("username") String username);

    User findByPhone(@Param("phone") String phone);

    User findByUsernameOrPhone(@Param("usernameOrPhone") String usernameOrPhone);

    User findById(@Param("id") Long id);

    int updateUserInfo(User user);

    int updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);
}
