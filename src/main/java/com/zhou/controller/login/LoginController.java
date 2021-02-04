package com.zhou.controller.login;


import com.zhou.pojo.User;
import com.zhou.service.user.UserService;
import com.zhou.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping("/login.do")
    public ModelAndView login(String userCode, String userPassword, HttpSession session) {

        ModelAndView mv = new ModelAndView();
        User user = userService.getLoginUser(userCode);
        if (user != null && user.getUserPassword().equals(userPassword)) {
            session.setAttribute(Constants.USER_SESSION, user);
            mv.setViewName("frame");
        } else {
            mv.addObject("error", "用户名或密码不正确");
            // 重定向  redirect  数据丢
            // 转发    forward   数据不会丢
            mv.setViewName("forward:/login.jsp");
        }
        return mv;
    }
}
