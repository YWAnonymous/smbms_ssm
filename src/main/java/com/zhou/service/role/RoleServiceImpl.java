package com.zhou.service.role;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhou.dao.role.RoleDao;
import com.zhou.pojo.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RedisTemplate redisTemplate;

    /*
        实体类需要序列化，否则会报错
        nested exception is org.springframework.core.serializer.support.SerializationFailedException:
        Failed to serialize object using DefaultSerializer;
     */
    @Cacheable(value="aboutRole")
    public List<Role> getRoleList() {

//        String  role = (String) redisTemplate.opsForValue().get("roleList");
//
//        if(StringUtils.isEmpty(role)){
//            System.out.println("===============roleList查询数据库==================");
//            List<Role> roleList = roleDao.getRoleList();
//            String s = JSON.toJSONString(roleList);
//
//            redisTemplate.opsForValue().set("roleList",s);
//            return roleList;
//        }else{
//            System.out.println("================roleList使用redis缓存===================");
//            List roleList = JSON.parseObject(role, List.class);
//            return roleList;
//        }
        return roleDao.getRoleList();
    }
}
