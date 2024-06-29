/*package es.codeurjc.web.controller;
import es.codeurjc.web.Model.User;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.Service.ImageService;
import es.codeurjc.web.Service.PostService;
import es.codeurjc.web.Service.UserService;
import es.codeurjc.web.Service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.SQLException;
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


    @GetMapping("/blog")
    public String showPosts(Model model){

        model.addAttribute("Posts", postService.findAll());

        return "blog_index";
    }
    @GetMapping("/blog/{id}")
    public String showPost(Model model, @PathVariable long id){

        Optional<Post> post = postService.findById(id);
        if (post.isPresent()) {
            //model.addAttribute("ImagePresented", imageService.getImage(validateService.validateImage()));
            //model.addAttribute("ImagePresented", validateService.validateImage(imageService.getImage()));
            //model.addAttribute("ImagePresented",post.get().imagePresent());
            model.addAttribute("ImagePresented",post.get().getImage());
            model.addAttribute("Posts", post.get());
            model.addAttribute("CreatorName", post.get().getCreatorName());
            return "show_post";
        } else {
            return "blog_index";
        }

    }
    @GetMapping("/blog/new")
    public String newPost(Model model){
        return "newPostPage";
    }

    @PostMapping("/blog/new")
    public String newPostProcess(Model model, Post post, MultipartFile imagefile, String user) throws IOException {

        User classUser = new User(user);
        post.setCreator(classUser);

        if(validateService.validatePost(post) != null){
            model.addAttribute("error",validateService.validatePost(post));
            model.addAttribute("post",post);
            return "newPostPage";
        }else{
            userService.addPost(post, classUser.getUserid()); //error newpost
            classUser.addPost(post);
            userService.save(classUser);
            postService.save(post, imagefile);
        }

        return "redirect:/blog/" + post.getId();
    }

    @GetMapping("/blog/changePost/{id}")
    public String editPost(Model model, @PathVariable("id") long id, MultipartFile imagefile) throws IOException {

        Post post = postService.findById(id).orElse(null);

        model.addAttribute("post",post);
        model.addAttribute("edit",true);
        return "newPostPage";


    }

    @PostMapping("/blog/changePost/{id}")
    public String editPostProcess(@PathVariable long id, Model model, Post post, MultipartFile imagefile,
                                  String user,@RequestParam boolean deleteImage) throws IOException{

        User classUser = new User(user);
        post.setCreator(classUser);

        if(validateService.validatePost(post) != null){
            model.addAttribute("error",validateService.validatePost(post));
            model.addAttribute("post",post);
            return "newPostPage";
        }else{
            if(deleteImage){
                postService.editPost(post,null,id);
            }else{
                postService.editPost(post,imagefile,id);
            }
        }
        return "redirect:/blog/" + post.getId();
    }

    @GetMapping("/blog/deletePost/{id}")
    public String deletePost(Model model,@PathVariable long id){
        postService.delete(id);
        return "deleted_post";
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


}*/