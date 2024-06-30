package es.codeurjc.web.controller;
import es.codeurjc.web.Model.GroupClass;
import es.codeurjc.web.Model.User;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.Service.ImageService;
import es.codeurjc.web.Service.PostService;
import es.codeurjc.web.Service.UserService;
import es.codeurjc.web.Service.ValidateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Controller
public class BlogWebController {


    @Autowired
    private PostService postService;


    @Autowired
    private UserService userService;


    @Autowired
    private ImageService imageService;


    @Autowired
    private ValidateService validateService;


    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {

            model.addAttribute("logged", true);
            String name = principal.getName();
            System.out.println(name);
            Optional<User> userOptional = userService.findByUsername(name);
            User user = userOptional.get();
            model.addAttribute("user", user);
            model.addAttribute("admin", request.isUserInRole("ADMIN"));

        } else {
            model.addAttribute("logged", false);
        }
    }

    @GetMapping("/blog")
    public String showPosts(Model model) {

        model.addAttribute("Posts", postService.findAll());

        return "blog_index";
    }

    @GetMapping("/blog/{id}")
    public String showPost(Model model, @PathVariable long id, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        Optional<Post> post = postService.findById(id);

        if (principal != null) {
            String name = principal.getName();
            Optional<User> userOptionalLogged = userService.findByUsername(name);
            if (userOptionalLogged.isPresent()) {
                User userLogged = userOptionalLogged.get();
                model.addAttribute("ImagePresented", post.get().getImage());
                model.addAttribute("Posts", post.get());
                model.addAttribute("CreatorName", post.get().getCreatorName());
                if ((post.get().getCreator().equals(userLogged)|| request.isUserInRole("ADMIN"))) {
                    Optional<User> userOptional = userService.findById(id);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        model.addAttribute("iscreator", user);
                    }

                }
                return "show_Post";
            }
        }
        return "redirect:/login";
    }
    @GetMapping("/blog/new")
    public String newPost(Model model) {
        return "newPostPage";
    }

    @PostMapping("/blog/new")
    public String newPostProcess(Model model, String title, String text, MultipartFile imagefile, HttpServletRequest request) throws IOException {

        title = validateService.cleanInput(title);
        text = validateService.cleanInput(text);

        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            return "redirect:/login";
        }
        Optional<User> user = userService.findByUsername(principal.getName());

        Post post = new Post(title, text);

        if (validateService.validatePost(post) != null) {
            model.addAttribute("error", validateService.validatePost(post));
            model.addAttribute("post", post);
            return "newPostPage";
        }

        postService.save(post,imagefile);
        postService.addCreator(user.get().getUserid(),post.getId());



        return "redirect:/blog/" + post.getId();
    }

    @GetMapping("/blog/changePost/{id}")
    public String editPost(Model model, @PathVariable("id") long id, MultipartFile imagefile, HttpServletRequest request) throws IOException {

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return "redirect:/login";
        }
        String name = principal.getName();
        Optional<User> userOptional = userService.findByUsername(name);
        User user = userOptional.get();

        Post post = postService.findById(id).orElse(null);

        String creatorName = postService.findById(id).get().getCreatorName();
        Optional<User> userComment = userService.findByUsername(creatorName);
        User userC = userComment.get();
        if (userOptional.isPresent() && userComment.isPresent()
                && (userService.isUser(id, userC.getUserid()) || request.isUserInRole("ADMIN"))) {
            model.addAttribute("post", post);
            model.addAttribute("edit", true);
            return "newPostPage";
        } else {
            return "redirect:/blog";
        }


    }

    @PostMapping("/blog/changePost/{id}")
    public String editPostProcess(@PathVariable long id, Model model, Post post, MultipartFile imagefile
            , @RequestParam boolean deleteImage,HttpServletRequest request) throws IOException {

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return "redirect:/login";
        }
        String name = principal.getName();
        Optional<User> userOptional = userService.findByUsername(name);
        User user = userOptional.get();

        String creatorName = postService.findById(id).get().getCreatorName();
        Optional<User> userComment = userService.findByUsername(creatorName);
        User userC = userComment.get();
        if (userOptional.isPresent() && userComment.isPresent()
                && (userService.isUser(id, userC.getUserid()) || request.isUserInRole("ADMIN"))) {

            if (validateService.validatePost(post) != null) {
                model.addAttribute("error", validateService.validatePost(post));
                model.addAttribute("post", post);
                return "newPostPage";
            } else {
                if (deleteImage) {
                    postService.editPost(post, null, id);
                } else {
                    postService.editPost(post, imagefile, id);
                }
            }
            return "redirect:/blog/" + post.getId();
        }
        return "redirect:/blog/";
    }

    @GetMapping("/blog/deletePost/{id}")
    public String deletePost(Model model,@PathVariable long id,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return "redirect:/login";
        }
        String name = principal.getName();
        Optional<User> userOptional = userService.findByUsername(name);
        User user = userOptional.get();

        Post post = postService.findById(id).orElse(null);

        String creatorName = postService.findById(id).get().getCreatorName();
        Optional<User> userComment = userService.findByUsername(creatorName);
        User userC = userComment.get();

        if (userOptional.isPresent() && userComment.isPresent()
                && (userService.isUser(id, userC.getUserid()) || request.isUserInRole("ADMIN"))) {
            postService.delete(post.getId());
            return "deleted_post";
        } else {
            return "redirect:/blog";
        }

    }
    @GetMapping("/blog/{id}/image")
    public ResponseEntity<Resource> downloadImage(@PathVariable long id) throws SQLException {
        Optional<Post> optionalPost = postService.findById(id);
        if(optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Resource imageResource = imageService.getImage(post.getImage());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(imageResource);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }


}