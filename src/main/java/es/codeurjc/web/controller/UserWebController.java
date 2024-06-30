package es.codeurjc.web.controller;

import es.codeurjc.web.Model.GroupClass;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.Model.User;
import es.codeurjc.web.Service.GroupClassService;
import es.codeurjc.web.Service.UserService;
import es.codeurjc.web.Service.ValidateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class UserWebController {

    @Autowired
    private GroupClassService groupClassService;
    @Autowired
    private UserService userService;
    @Autowired
    private ValidateService validateService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private HttpSession session;
    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {

            model.addAttribute("logged", true);
            String name = principal.getName();
            Optional<User> userOptional = userService.findByUsername(name);
            User user = userOptional.get();
            model.addAttribute("user", user);
            model.addAttribute("admin", request.isUserInRole("ADMIN"));

        } else {
            model.addAttribute("logged", false);
        }
    }
    @GetMapping("/me")
    public String showUserInfo(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {

            String name = principal.getName();
            Optional<User> userOptional = userService.findByUsername(name);
            if(userOptional.isPresent()){
                User user = userOptional.get();
                model.addAttribute("user", user);
                model.addAttribute("admin", request.isUserInRole("ADMIN"));
                List<GroupClass> groupClasses = user.getListOfClasses();
                model.addAttribute("groupClasses", groupClasses);
                List<Post> posts = user.getListOfPosts();
                model.addAttribute("posts", posts);
                model.addAttribute("isLoggedUser", true);
            }
            return "user_info";
        }
        else
            return "redirect:/";
    }
    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable long id, Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            String name = principal.getName();
            Optional<User> userOptionalLogged = userService.findByUsername(name);
            if (userOptionalLogged.isPresent()) {
                User userLogged = userOptionalLogged.get();
                if ((userService.isUser(userLogged.getUserid(), id) || request.isUserInRole("ADMIN"))) {
                    Optional<User> userOptional = userService.findById(id);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        model.addAttribute("user", user);
                        return "editUser";
                    }
                } else {
                    return "redirect:/login";
                }
            } else {
                return "redirect:/login";
            }
        }
        return "redirect:/";
    }
    @PostMapping("/users/{id}/edit")
    public String editUser(@PathVariable long id, @ModelAttribute User user, @RequestParam Optional<String>password, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            String name = principal.getName();
            Optional<User> userOptionalLogged = userService.findByUsername(name);
            if (userOptionalLogged.isPresent()) {
                User userLogged = userOptionalLogged.get();
                if ((userService.isUser(userLogged.getUserid(), id) || request.isUserInRole("ADMIN"))){

                    User userToEdit = userService.findById(id).get();
                    userToEdit.setFirstName(validateService.cleanInput(user.getFirstName()));
                    userToEdit.setUsername(validateService.cleanInput(user.getUsername()));
                    System.out.println("PATATA" + password);
                    if(password.isPresent()){
                        userToEdit.setEncodedPassword(passwordEncoder.encode(password.get()));
                    }
                    userService.updateUser(userToEdit);

                    session.invalidate();
                    session = request.getSession(true);

                }
            }
        }
        return "redirect:/";
    }
    @GetMapping("/users")
    public String showUsers(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            String name = principal.getName();
            Optional<User> userOptional = userService.findByUsername(name);
            if (userOptional.isPresent() && request.isUserInRole("ADMIN")) {
                List<User> users = userService.findAll();
                model.addAttribute("users", users);
                return "users";
            } else {
                return "redirect:/login";
            }
        }
        return "redirect:/me";
    }
    @GetMapping("/users/{id}")
    public String getUser(@PathVariable long id, Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            String name = principal.getName();
            Optional<User> userOptionalLogged = userService.findByUsername(name);
            if (userOptionalLogged.isPresent()) {
                User userLogged = userOptionalLogged.get();
                if ((userService.isUser(userLogged.getUserid(), id) || request.isUserInRole("ADMIN"))) {

                    if((userService.isUser(userLogged.getUserid(), id))){
                        return "redirect:/me";
                    }
                    else if (request.isUserInRole("ADMIN")){
                        Optional<User> userOptional = userService.findById(id);
                        if (userOptional.isPresent()) {
                            User user = userOptional.get();
                            model.addAttribute("user", user);
                            List<GroupClass> groupClasses = user.getListOfClasses();
                            model.addAttribute("groupClasses", groupClasses);
                            List<Post> posts = user.getListOfPosts();
                            model.addAttribute("posts", posts);
                            return "user_info";
                        }
                    }

                } else {
                    return "redirect:/login";
                }
            } else {
                return "redirect:/login";
            }
        }
        return "redirect:/";
    }

    @GetMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable long id, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            String name = principal.getName();
            Optional<User> userOptionalLogged = userService.findByUsername(name);
            if (userOptionalLogged.isPresent()) {
                User userLogged = userOptionalLogged.get();
                if ((userService.isUser(userLogged.getUserid(), id) || request.isUserInRole("ADMIN"))) {
                    userService.delete(id);
                    return "redirect:/logout";
                } else {
                    return "redirect:/login";
                }
            } else {
                return "redirect:/login";
            }
        }
        return "redirect:/";
    }
}


