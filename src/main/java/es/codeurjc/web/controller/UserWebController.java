package es.codeurjc.web.controller;

import es.codeurjc.web.Service.GroupClassService;
import es.codeurjc.web.Service.UserService;
import es.codeurjc.web.Service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class UserWebController {

    @Autowired
    private GroupClassService groupClassService;
    @Autowired
    private UserService userService;
    @Autowired
    private ValidateService validateService;

    @GetMapping("/me")
    public String showUserInfo(Model model) {
        return "user_info";
    }
    //@GetMapping("/me/edit")
    //@PostMapping("/me/edit")
    //@GetMapping("/me/delete")
    //@PostMapping("/me/delete")
    }

