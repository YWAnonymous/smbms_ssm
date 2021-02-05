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

    public List<User> getUserList(String userCode, int userRole,int currentPageNo, int pageSize) {
        int aa = (currentPageNo-1)*pageSize;
        return userDao.getUserList(userCode,userRole,aa,pageSize);
    }

    @Override
    public int getUserCount(String userCode, int userRole) {
        return userDao.getUserCount(userCode,userRole);
    }

    @Override
    public boolean addNewUser(User user) {
        int row = userDao.addUser(user);
        boolean flag = false;
        if(row > 0){
            flag = true;
        }
        return flag;
    }
}
