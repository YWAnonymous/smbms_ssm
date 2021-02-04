package com.zhou.service.user;

import com.zhou.pojo.User;

import java.util.List;

public interface UserService {

    //根据用户名获取登录用户
    public User getLoginUser(String userCode);
    // 查询所有用户
    public List<User> getUserList(String userCode, int userRole);
}
