package es.codeurjc.web.controller;

import es.codeurjc.web.Model.GroupClass;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.Model.User;
import es.codeurjc.web.Service.GroupClassService;
import es.codeurjc.web.Service.UserService;
import es.codeurjc.web.Service.ValidateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/signup")
    public String processRegister(Model model, @ModelAttribute User user) {
        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
        userService.save(user);

        return "redirect:/";
        //--> Falta validar el usuario <--

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
    public String editUser(@PathVariable long id, @ModelAttribute User user, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            String name = principal.getName();
            Optional<User> userOptionalLogged = userService.findByUsername(name);
            if (userOptionalLogged.isPresent()) {
                User userLogged = userOptionalLogged.get();
                if ((userService.isUser(userLogged.getUserid(), id) || request.isUserInRole("ADMIN"))){

                    User userToEdit = userService.findById(id).get();
                    userToEdit.setName(validateService.cleanInput(user.getName()));
                    userToEdit.setUsername(validateService.cleanInput(user.getUsername()));
                    userToEdit.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
                    userService.updateUser(userToEdit);
                    return "redirect:/users/" + id;

                } else {
                    return "redirect:/login";
                }
            } else {
                return "redirect:/login";
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

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

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
            }

        } }
}


