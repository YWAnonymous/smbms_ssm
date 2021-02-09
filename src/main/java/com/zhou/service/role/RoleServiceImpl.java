package com.zhou.service.role;

import com.alibaba.fastjson.JSON;
import com.zhou.dao.role.RoleDao;
import com.zhou.pojo.Role;
import com.zhou.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RedisUtil redisUtil;

    private final String lock_key ="lock";


    public List<Role> getRoleList() {

        boolean flag = redisUtil.addRedisLock(lock_key, "lock", 120000);
        if (flag) {

            String role = (String) redisTemplate.opsForValue().get("roleList");
            List<Role> roleList= null;
            if (StringUtils.isEmpty(role)) {
                System.out.println("===============roleList查询数据库==================");
                roleList = roleDao.getRoleList();

                redisTemplate.opsForValue().set("roleList", JSON.toJSONString(roleList));

            } else {
                System.out.println("================roleList使用redis缓存===================");
                roleList = JSON.parseObject(role, List.class);

            }
            redisUtil.delRedisLock(lock_key);
            return roleList;
        } else {
            System.out.println("获取分布式锁失败...等待重试");
            try{
                Thread.sleep(200);
            }catch (Exception e){
            }
            return getRoleList();
        }
    }
}
