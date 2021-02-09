package com.zhou.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component("redisUtil")
public class RedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    @Autowired
    private  RedisTemplate<String, Object> redisTemplate ;

    /**
     * 设置redisTemplate
     */
    @Resource
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    private JedisPool jedisPool ;

    @Autowired(required = true)
    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    //=============================common============================
    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    public  boolean expire(String key,long time){
        try {
            if(time>0){
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //============================String=============================
    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public  Object get(String key){
        return key==null?null:redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public  boolean set(String key,Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public  boolean set(String key,Object value,long time){
        try {
            if(time>0){
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            }else{
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized Jedis getJedis() {
        Jedis jedis = null;
        try {
            if(null !=jedisPool){
                jedis = jedisPool.getResource();
            }
        } catch (Exception e) {
            logger.error("getJedis{},获取Jedis实例异常:", e);
            e.printStackTrace();
            returnToPool(jedis);//释放资源
        }
        return jedis;
    }

    /***
     * @desc 分布式加锁，在超时时间内锁有效，超过超时时间，锁就会失效redis's set and del operation to lock
     * @param redisKey
     * @param timeout
     */
    //第一个为key，我们使用key来当锁，因为key是唯一的。
    //第二个为value，我们传的是requestId，很多童鞋可能不明白，有key作为锁不就够了吗，为什么还要用到value？原因就是我们在上面讲到可靠性时，分布式锁要满足第四个条件解铃还须系铃人，通过给value赋值为requestId，我们就知道这把锁是哪个请求加的了，在解锁的时候就可以有依据。requestId可以使用UUID.randomUUID().toString()方法生成。
    //第三个为nxxx，这个参数我们填的是NX，意思是SET IF NOT EXIST，即当key不存在时，我们进行set操作；若key已经存在，则不做任何操作；
    //第四个为expx，这个参数我们传的是PX，意思是我们要给这个key加一个过期的设置，具体时间由第五个参数决定。
    //第五个为time，与第四个参数相呼应，代表key的过期时间。
    //总的来说，执行上面的set()方法就只会导致两种结果：1. 当前没有锁（key不存在），那么就进行加锁操作，并对锁设置个有效期，同时value表示加锁的客户端。2. 已有锁存在，不做任何操作。

    public boolean addRedisLock(String redisKey,String value, long timeout) {
        Jedis jedis =null;

        try{
            jedis = getJedis();
            if(jedis == null){
                throw new NullPointerException("Jedis is Null");
            }
            if ("".equals(redisKey) || null == redisKey) {
                return false;
            }
            String result = jedis.set(redisKey, value, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, timeout);
            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
        }catch (Exception e) {
            returnToPool(jedis);//释放资源
            logger.error("addRedisLock key="+redisKey +" is error"+e.getMessage());
        }finally {
            logger.info("addRedisLock{}，释放资源="+redisKey);
            returnToPool(jedis);//释放资源
        }
        return false;
    }

    /**
     * 分布式释放锁
     * zhaolili
     * 2020年02月29日
     * @param redisKey
     */
    public void delRedisLock(String redisKey) {
        Jedis jedis =null;
        try{
            jedis= getJedis();
            if (!("".equals(redisKey))) {
                Long del = jedis.del(redisKey);
                logger.info("锁名："+ redisKey +", 释放锁成功：{}", redisKey, del);
            }
        } catch (Exception e) {
            logger.error("delRedisLock key="+redisKey +" is error"+e.getMessage());
            returnToPool(jedis);
        }
        finally {
            logger.info("delRedisLock{}，释放资源="+redisKey);
            returnToPool(jedis);
        }
    }
    /**
     * 功能描述: 回收Jedis对象资源
     */
    public synchronized void returnToPool(Jedis jedis) {
        try {
            if(jedis!=null){
                jedis.close();//替代方法
                logger.info("returnToPool success ");
            }else {
                logger.error("returnToPool jedis is null ");
            }
        } catch (Exception e) {
            logger.error("returnToPool error "+e.toString());
        }
    }
}
