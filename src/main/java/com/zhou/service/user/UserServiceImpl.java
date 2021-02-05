package com.zhou.service.user;

import com.zhou.dao.user.UserDao;
import com.zhou.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    public User getLoginUser(String userCode) {
        return userDao.getLoginUser(userCode);
    }

    public List<User> getUserList(String userCode, int userRole) {



        return userDao.getUserList(userCode,userRole);
    }
}
