package com.zhou.service.role;

import com.zhou.dao.role.RoleDao;
import com.zhou.pojo.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    public List<Role> getRoleList() {
        return roleDao.getRoleList();
    }
}
