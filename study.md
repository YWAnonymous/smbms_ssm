# 开发中遇到的问题
```markdown
1、mybatis的动态sql的编写问题：
两表联查时，使用resultmap将两个表的字段取到；

2、使用trim进行动态sql操作：trim prefix="WHERE" prefixoverride="AND |OR"
prefix：前缀增加的内容
suffix：后缀增加的内容
prefixOverrides：前缀覆盖第一个判断的条件
suffixOverrides：后缀覆盖最后一个判断的条件

3、模糊查询：like CONCAT("%",#{userCode},"%")
模糊查询使用like关键字 
CONCAT("%",xxx,"%")这个关键字可以拼接字符串

4、请求的url中数据：user.do?method=query&queryname=admin&queryUserRole=3&pageIndex=1
在dao层中获取数据，使用@Param
controller中获取数据使用@RequestParam获取url拼接的数据

5、版本问题最为致命
```

### jdbc配置文件
```properties
url=jdbc:mysql://localhost:3306/smbms?useSSL=true&useUnicode=true&characterEncoding=utf8
```

### 上传文件
```markdown
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary8US3dx8lueWvf2Vi
使用@RequestParam("file") MultipartFile file获取前端上送的文件
```
### 加载配置文件
```markdown
当context:property-placeholder标签使用了多个，Spring容器只会加载一个。
当有多个外部文件要加载时使用 <context:property-placeholder location=“classpath:jdbc.properties,classpath:redis.properties” />
```

### 使用redis中问题汇总
<b>基于redis实现的分布式锁主要依赖redis的SETNX命令和DEL命令，SETNX相当于上锁，DEL相当于释放锁</b>
```markdown

1、报错nested exception is org.springframework.core.serializer.support.SerializationFailedException:
        Failed to serialize object using DefaultSerializer;
实体类要加上序列化

2、redis分布式锁
2.1、错误示例就是使用jedis.setnx()和jedis.expire()组合实现加锁
Long result = jedis.setnx(lockKey, requestId);
if (result == 1) {
    // 若在这里程序突然崩溃，则无法设置过期时间，将发生死锁
    jedis.expire(lockKey, expireTime);
}
setnx()方法作用就是SET IF NOT EXIST，expire()方法就是给锁加一个过期时间。不具有原子性
2.2、低版本的jedis不支持set多参数,变成高版本2.9.0

jedis.set(String key, String value, String nxxx, String expx, int time)，这个set()方法一共有五个形参：
第一个为key，我们使用key来当锁，因为key是唯一的。
第二个为value，我们传的是requestId，很多童鞋可能不明白，有key作为锁不就够了吗，为什么还要用到value？原因就是我们在上面讲到可靠性时，分布式锁要满足第四个条件解铃还须系铃人，通过给value赋值为requestId，我们就知道这把锁是哪个请求加的了，在解锁的时候就可以有依据。requestId可以使用UUID.randomUUID().toString()方法生成。
第三个为nxxx，这个参数我们填的是NX，意思是SET IF NOT EXIST，即当key不存在时，我们进行set操作；若key已经存在，则不做任何操作；
第四个为expx，这个参数我们传的是PX，意思是我们要给这个key加一个过期的设置，具体时间由第五个参数决定。
第五个为time，与第四个参数相呼应，代表key的过期时间。
总的来说，执行上面的set()方法就只会导致两种结果：1. 当前没有锁（key不存在），那么就进行加锁操作，并对锁设置个有效期，同时value表示加锁的客户端。2. 已有锁存在，不做任何操作。

2.3、删除key，使用lua脚本代码
String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";


```

### 使用redis中报错
```markdown
1、Could not get a resource from the pool
增加jedisPool连接池的配置
最大活动对象数
redis.maxTotal=1000
最大能够保持idel状态的对象数
redis.maxIdle=100
设置最小空闲数
redis.minIdle=60
```

