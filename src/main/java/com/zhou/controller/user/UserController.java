package com.zhou.controller.user;

import com.mysql.jdbc.StringUtils;
import com.zhou.pojo.User;
import com.zhou.service.user.UserService;
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

    @RequestMapping("/user.do")
    public ModelAndView query(@RequestParam("method") String method,
                              @RequestParam(value = "queryname",required = false) String queryname,
                              @RequestParam(value = "queryUserRole",required = false) String queryUserRole) {

        ModelAndView mv = new ModelAndView();

        int userRole = 0;
        if(!StringUtils.isNullOrEmpty(queryUserRole)){
            userRole = Integer.parseInt(queryUserRole);
        }

        System.out.println("=======method="+method+"=====userCode="+queryname+"=====userRole="+userRole);

        List<User> userList = userService.getUserList(queryname, userRole);
        mv.addObject("userList",userList);
        mv.setViewName("userlist");
        return mv;
    }


}
