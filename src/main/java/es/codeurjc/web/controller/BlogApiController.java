/*package es.codeurjc.web.controller;

import com.fasterxml.jackson.annotation.JsonView;
import es.codeurjc.web.Model.User;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.Service.ImageService;
import es.codeurjc.web.Service.PostService;
import es.codeurjc.web.Service.UserService;
import es.codeurjc.web.Service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.core.io.Resource;
import java.io.IOException;
import java.net.URI;
import java.sql.Blob;
import java.util.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("api/posts")
public class BlogApiController {

    //private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ValidateService validateService;
    interface PostsDetails extends Post.Basic, Post.Creator, User.Basic{}

    /////////////////GET(all)/////////////////
    @JsonView(Post.Basic.class)
    @GetMapping("/")
    public Collection<Post> getPosts() {
        return postService.findAll();
    }

    /////////////////GET(byId)/////////////////
    @JsonView(PostsDetails.class)
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Post>> getPost(@PathVariable long id) {
        Post postExample = new Post();
        postExample.setId(id);

        Optional<Post> post = postService.findByExample(postExample);

        if (post.isPresent()) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /////////////////POST/////////////////
    @JsonView(PostsDetails.class)
    @PostMapping("/")
    public ResponseEntity<?> createPost(@RequestBody Post post,MultipartFile imageField) throws IOException {

        String error = validateService.validatePost(post);
        //Sanitizar la entrada

        if (error != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", error);
            response.put("post", post);
            return ResponseEntity.badRequest().body(response);
        } else {
            userService.save(post.getCreator());
            postService.save(post, imageField);
            URI location = fromCurrentRequest().path("{id}").buildAndExpand(post.getId()).toUri();
            return ResponseEntity.created(location).body(post);
        }
    }

    /////////////////DELETE/////////////////
    @JsonView(Post.Basic.class)
    @DeleteMapping("/{id}")
    public ResponseEntity<Optional<Post>> deletePost(@PathVariable long id) throws IOException {

        Optional <Post> post = postService.findById(id);

        if (post.isPresent()) {
            postService.delete(id);

            if(post.get().getImage() != null) {
                this.imageService.deleteImage(String.valueOf(post.get().getImage()));
            }

            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /////////////////PUT/////////////////
    @JsonView(Post.Basic.class)
    @PutMapping("/{id}")
    public ResponseEntity<?> replacePost(@PathVariable long id, @RequestBody Post newPost) throws IOException {

        String error = validateService.validatePost(newPost);

        if (error != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", error);
            response.put("post", newPost);
            return ResponseEntity.badRequest().body(response);
        } else {
            Optional<Post> postOptional = postService.findById(id);
            if(postOptional.isPresent()){
                newPost.setId(id);
                Blob newimage = newPost.getImageFile();
                postService.editPost(newPost, (MultipartFile) newimage,id);
                //apiposts.editPost(newPost,newPost.getImage(),id);
                //apiposts.editPost(newPost,id,newPost.getImage());
                return ResponseEntity.ok(newPost);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    /////////////////POST(img)/////////////////
    @PostMapping("/{id}/image")
    public ResponseEntity<Object> uploadImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
            throws IOException {

        Optional <Post> post = postService.findById(id);

        if (post.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        validateService.cleanInput(imageFile.getName());
        // Validate filename
        String originalFileName = imageFile.getOriginalFilename();
        if (!validateService.isValidFileName(originalFileName)) {
            return ResponseEntity.badRequest().body("Invalid image name");
        }

        // Save original file
        URI location = fromCurrentRequest().build().toUri();
        post.get().setImage(originalFileName);
        postService.editPost(post.get(), imageFile, id);

        // Save with original name
        String fileName = imageService.createImage(imageFile);
        return ResponseEntity.created(location).body(fileName);
    }

    /////////////////GET(img)/////////////////
    @GetMapping("/{id}/image")
    public ResponseEntity<Object> getImage(@PathVariable long id) {

        Optional <Post> post = postService.findById(id);
        //////NEW /////
        if (post.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        String imageName = post.get().getImage();
        if (imageName == null || imageName.isEmpty()) {
            return ResponseEntity.badRequest().body("Image name is required");
        }
        // Validates the file name
        if (!validateService.isValidFileName(imageName)) {
            return ResponseEntity.badRequest().body("Invalid image name");
        }
        ///////END NEW ///////
        try {
            Resource file = imageService.getImage(imageName);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(file);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not get image", e);
        }
    }

    /////////////////DELETE(img)/////////////////
    @DeleteMapping("/{id}/image")
    public ResponseEntity<String> deleteImage(@PathVariable long id) throws IOException {
        try {
            Optional <Post> post = postService.findById(id);
            //NEW
            if (post.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String imageName = post.get().getImage();
            if (imageName == null || imageName.isEmpty()) {
                return ResponseEntity.badRequest().body("Image name is required");
            }

            // Validate the file name
            if (!validateService.isValidFileName(imageName)) {
                return ResponseEntity.badRequest().body("Invalid image name");
            }

            imageService.deleteImage(imageName);
            postService.editPost(post.get(), null, id);
            //END NEW

            return ResponseEntity.ok().body("Image deleted successfully");
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }
}*/