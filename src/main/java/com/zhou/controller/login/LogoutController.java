package com.zhou.controller.login;


import com.zhou.util.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/jsp")
public class LogoutController {


    @RequestMapping("/logout.do")
    public ModelAndView logout(HttpSession session){

        ModelAndView mv = new ModelAndView();
        Object o = session.getAttribute(Constants.USER_SESSION);

        if(o != null){
            session.removeAttribute(Constants.USER_SESSION);
            mv.setViewName("redirect:/login.jsp");
        }else {
            mv.addObject("error","请重新登录");
            mv.setViewName("forward:/login.jsp");
        }
        return mv;
    }
}
