package com.zhou.controller.user;

import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.StringUtils;
import com.zhou.pojo.Role;
import com.zhou.pojo.User;
import com.zhou.service.role.RoleService;
import com.zhou.service.user.UserService;
import com.zhou.util.Constants;
import com.zhou.util.PageSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/jsp")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @RequestMapping("/user.do")
    public ModelAndView query(@RequestParam("method") String method,
                              @RequestParam(value = "queryname", required = false) String temp,
                              @RequestParam(value = "queryUserRole", required = false) String userRole,
                              @RequestParam(value = "pageIndex", required = false) String pageIndex) {

        ModelAndView mv = new ModelAndView();
        //============准备数据===================
        String queryname = null;
        if (!StringUtils.isNullOrEmpty(temp)) {
            queryname = temp;
        }
        int queryUserRole = 0;//queryUserRole
        if (!StringUtils.isNullOrEmpty(userRole)) {
            queryUserRole = Integer.parseInt(userRole);
        }

        // 查询用户总条数
        int totalCount = userService.getUserCount(queryname, queryUserRole);
        //=================分页==============================
        int pageSize = Constants.PageSize;
        //当前页码
        int currentPageNo = 1;
        if (pageIndex != null) {
            try {
                currentPageNo = Integer.valueOf(pageIndex);
            } catch (NumberFormatException e) {
                mv.setViewName("forward:/error.jsp");
            }
        }
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        int totalPageCount = pages.getTotalPageCount();

        List<User> userList = userService.getUserList(queryname, queryUserRole, currentPageNo, pageSize);


        mv.addObject("totalPageCount", totalPageCount);
        mv.addObject("totalCount", totalCount);
        mv.addObject("currentPageNo", currentPageNo);

        List<Role> roleList = roleService.getRoleList();
        mv.addObject("queryUserName", queryname);
        mv.addObject("queryUserRole", queryUserRole);
        mv.addObject("userList", userList);
        mv.addObject("roleList", roleList);
        mv.setViewName("userlist");
        return mv;
    }


    @RequestMapping("/useradd.do")
    @ResponseBody
    public String userAdd(@RequestParam("method") String method,
                          @RequestParam(value = "userCode", required = false) String userCode) {

        String result = null;
        if ("getrolelist".equals(method)) {
            List<Role> roleList = roleService.getRoleList();

            result = JSON.toJSONString(roleList);
        } else if ("ucexist".equals(method)) {
            User user = userService.getLoginUser(userCode);
            Map map = new HashMap();
            if (user != null) {
                map.put("userCode", "exist");
            }else{
                map.put("userCode",userCode);
            }
            result = JSON.toJSONString(map);
        }
        return result;
    }


    @RequestMapping("/adduser.do")
    public void addUser(User user, HttpServletRequest request, HttpServletResponse response, Model model,
                        @RequestParam("file") MultipartFile file) throws IOException, ServletException {


        System.out.println("========user========"+user);
        System.out.println("========file========="+file);

        //保存数据库的路径
        String sqlPath = null;
        //定义文件保存的本地路径
        //String localPath="D:\\File\\";
        //上传路径保存设置
        String localPath = request.getServletContext().getRealPath("/upload");
        File realPath = new File(localPath);
        if (!realPath.exists()){
            realPath.mkdir();
        }
        //上传文件地址
        System.out.println("上传文件保存地址："+realPath);
        //定义 文件名
        String filename=null;
        if(!file.isEmpty()){
            //生成uuid作为文件名称
            String uuid = UUID.randomUUID().toString().replaceAll("-","");
            //获得文件类型（可以判断如果不是图片，禁止上传）
            String contentType=file.getContentType();
            //获得文件后缀名
            String suffixName=contentType.substring(contentType.indexOf("/")+1);
            //得到 文件名
            filename=uuid+"."+suffixName;
            System.out.println(filename);
            //文件保存路径
            file.transferTo(new File(realPath+"/"+filename));
        }
        //把图片的相对路径保存至数据库
        sqlPath = "/upload/"+filename;
        System.out.println("=============sqlPath=========="+sqlPath);
        user.setUserImage(sqlPath);
        userService.addNewUser(user);
        response.sendRedirect(request.getContextPath()+"/jsp/user.do?method=query");
    }

    /*
    测试redis分布式锁
     */
    @RequestMapping("/getRoleList.do")
    @ResponseBody
    public List<Role> findRoleList(){
        return roleService.getRoleList();
    }

}
