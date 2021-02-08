package com.vut.fit.gja2020.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class RedirectController {
    @RequestMapping("/")
    public String defaultAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ADMIN")) {
            return "redirect:/dashboard.xhtml";
        }
        return "redirect:/student.xhtml";
    }

}
