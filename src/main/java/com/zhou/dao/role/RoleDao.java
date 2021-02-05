package com.zhou.dao.role;

import com.zhou.pojo.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleDao {

    public List<Role> getRoleList();
}
