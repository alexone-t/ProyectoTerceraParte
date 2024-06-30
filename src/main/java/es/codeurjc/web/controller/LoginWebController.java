package es.codeurjc.web.controller;

import es.codeurjc.web.Model.User;
import es.codeurjc.web.Service.UserService;
import es.codeurjc.web.Service.ValidateService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.modeler.BaseAttributeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class LoginWebController {


    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ValidateService validateService;

    @RequestMapping("/loginerror")
    public String loginerror() {
        return "loginerror";
    }
    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
            model.addAttribute("token", token.getToken());
            return "login";
        } else {
            return "redirect:/me";
        }
    }
    @GetMapping("/signup")
    public String register() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return "signup";
        } else {
            return "redirect:/me";
        }
    }
    @PostMapping("/signup")
    public String processRegister(Model model, @ModelAttribute User user) {

        if (validateService.validateUser(user) != null) {
            model.addAttribute("error", validateService.validateUser(user));
            model.addAttribute("user", user);
            return "signup";
        }

        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
        userService.saveUser(user);

        return "redirect:/";

    }

}
