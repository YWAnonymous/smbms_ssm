package com.zhou.dao.user;

import com.zhou.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {
    // 根据用户名获取登录用户
    public User getLoginUser(String userCode);
    // 查询所有用户
    public List<User> getUserList(@Param("userCode") String userCode,
                                  @Param("userRole") int userRole,
                                  @Param("currentPageNo")int currentPageNo,
                                  @Param("pageSize")int pageSize);


    public int getUserCount(@Param("userCode") String userCode,
                            @Param("userRole") int userRole);
}
