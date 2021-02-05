package com.zhou.controller.user;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.mysql.jdbc.StringUtils;
import com.zhou.pojo.Role;
import com.zhou.pojo.User;
import com.zhou.service.role.RoleService;
import com.zhou.service.user.UserService;
import com.zhou.util.Constants;
import com.zhou.util.PageSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/jsp")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @RequestMapping("/user.do")
    public ModelAndView query(@RequestParam("method") String method,
                              @RequestParam(value = "queryname",required = false) String temp,
                              @RequestParam(value = "queryUserRole",required = false) String userRole,
                              @RequestParam(value = "pageIndex",required = false) String pageIndex) {

        ModelAndView mv = new ModelAndView();
        //============准备数据===================
        String queryname = null;
        if(!StringUtils.isNullOrEmpty(temp)){
            queryname = temp;
        }
        int queryUserRole = 0;//queryUserRole
        if(!StringUtils.isNullOrEmpty(userRole)){
            queryUserRole = Integer.parseInt(userRole);
        }

        System.out.println("=======method="+method+"=====queryname="+queryname+"=====queryUserRole="+queryUserRole);
        // 查询用户总条数
        int totalCount = userService.getUserCount(queryname, queryUserRole);
        //=================分页==============================
        int pageSize = Constants.PageSize;
        //当前页码
        int currentPageNo = 1;
        if(pageIndex != null){
            try{
                currentPageNo = Integer.valueOf(pageIndex);
            }catch(NumberFormatException e){
                mv.setViewName("forward:/error.jsp");
            }
        }
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        int totalPageCount = pages.getTotalPageCount();

        List<User> userList = userService.getUserList(queryname, queryUserRole,currentPageNo,pageSize);


        mv.addObject("totalPageCount", totalPageCount);
        mv.addObject("totalCount", totalCount);
        mv.addObject("currentPageNo", currentPageNo);

        List<Role> roleList = roleService.getRoleList();
        mv.addObject("queryUserName",queryname);
        mv.addObject("queryUserRole",queryUserRole);
        mv.addObject("userList",userList);
        mv.addObject("roleList",roleList);
        mv.setViewName("userlist");
        return mv;
    }


}
