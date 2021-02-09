package com.zhou.service.user;

import com.alibaba.fastjson.JSON;
import com.zhou.dao.user.UserDao;
import com.zhou.pojo.User;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 *
 * 缓存机制说明：所有有缓存注解的查询结果都放进了缓存，也就是把MySQL查询的结果放到了redis中去，
 * 然后第二次发起该条查询时就可以从redis中去读取查询的结果，从而不与MySQL交互，从而达到优化的效果，
 * redis的查询速度之于MySQL的查询速度相当于 内存读写速度 /硬盘读写速度
 *
 * @Cacheable(value="xxx" key="zzz")注解：标注该方法查询的结果进入缓存，再次访问时直接读取缓存中的数据
 * 1.对于有参数的方法，指定value(缓存区间)和key(缓存的key)；
 * 	   对于无参数的方法，只需指定value,存到数据库中数据的key通过com.ssm.utils.RedisCacheConfig中重写的generate()方法生成。
 * 2.调用该注解标识的方法时，会根据value和key去redis缓存中查找数据，如果查找不到，则去数据库中查找，然后将查找到的数据存放入redis缓存中；
 * 3.向redis中填充的数据分为两部分：
 * 		1).用来记录xxx缓存区间中的缓存数据的key的xxx~keys(zset类型)
 * 		2).缓存的数据，key：数据的key；value：序列化后的从数据库中得到的数据
 * 4.第一次执行@Cacheable注解标识的方法，会在redis中新增上面两条数据
 * 5.非第一次执行@Cacheable注解标识的方法，若未从redis中查找到数据，则执行从数据库中查找，并：
 * 		1).新增从数据库中查找到的数据
 * 		2).在对应的zset类型的用来记录缓存区间中键的数据中新增一个值，新增的value为上一步新增的数据的key
 */


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisTemplate redisTemplate;

//    @Cacheable(value="aboutUser",key = "#root.methodName")
    public User getLoginUser(String userCode) {

//        redisTemplate.opsForValue().setIfAbsent()
        User user = userDao.getLoginUser(userCode);
        return user;
    }

    /**
     * 获取所有用户信息
     * 1.对数据一致性要求较高，所以在执行增删改操作后需要将redis中该数据的缓存清空，
     * 从数据库中获取最新数据。
     * 2.若缓存中没有所需的数据，则执行该方法后：
     * 	1).在redis缓存中新增一条数据
     * 		key：getAllUser  value：序列化后的List<User>
     * 		key的值通过com.ssm.utils.RedisCacheConfig中重写的generate()方法生成
     * 	2).在用来记录aboutUser缓存区间中的缓存数据的key的aboutUser~keys(zset类型)中新添加一个value，
     * 	        值为上面新增数据的key
     */

    public List<User> getUserList(String userCode, int userRole,int currentPageNo, int pageSize) {
        List<User> userList = userDao.getUserList(userCode, userRole, ((currentPageNo - 1) * pageSize), pageSize);

        return userList;
    }

    public int getUserCount(String userCode, int userRole) {
        return userDao.getUserCount(userCode,userRole);
    }

    /**
     * @CacheEvict()注解:移除指定缓存区间的一个或者多个缓存对象
     * @param  + key 或者 value + allEntries=true
     * 1.value + key 移除value缓存区间内的键为key的数据
     * 2.value + allEntries=true 移除value缓存区间内的所有数据
     */
    //@CacheEvict(value= "aboutUser", key="'user_'+#result.id")
    //@CacheEvict(value= "aboutUser", allEntries=true)
    public boolean addNewUser(User user) {
        int row = userDao.addUser(user);
        boolean flag = false;
        if(row > 0){
            flag = true;
        }
        return flag;
    }



}
