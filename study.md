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
