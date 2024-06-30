package es.codeurjc.web.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import es.codeurjc.web.Model.User;
import es.codeurjc.web.exceptions.ResourceNotFoundException;
import es.codeurjc.web.repository.PostRepository;
import es.codeurjc.web.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.Model.Post;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Example;

@Service
public class PostService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    private AtomicLong nextId = new AtomicLong(1L);


    public boolean exist(long id){return postRepository.existsById(id);}

    public Post findPostById(long id){ return postRepository.findById(id).orElseThrow();}

    public Optional<Post> findById(long id) {

        if(this.exist(id)) {
            return Optional.of(this.findPostById(id));
        }
        return Optional.empty();

    }

    public List<Post> findByIds(List<Long>ids){
        List<Post> posts = new ArrayList<>();
        for(long id : ids){
            posts.add(postRepository.findById(id).orElseThrow());
        }
        return posts;
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public List findAll(User user) {
        StringBuilder queryBuilder = new StringBuilder("SELECT p.*, u.name AS user_name FROM post p");

        if (user != null) {
            queryBuilder.append(" INNER JOIN class_user u ON p.user_id = u.id WHERE u.id = :userId");
        }

        Query query = entityManager.createNativeQuery(queryBuilder.toString(), Post.class);

        if (user != null) {
            query.setParameter("userId", user.getUserid());
        }

        return ((Query) query).getResultList();
    }

    public Post save(Post post, MultipartFile imageField) throws IOException {

        if (imageField != null && !imageField.isEmpty()) {
            post.setImage(imageField.getOriginalFilename());
            post.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
        }

        if (post.getImage() == null || post.getImage().isEmpty()) {
            post.setImage("no-image.png");
        }

        long id = nextId.getAndIncrement();
        post.setId(id);
        postRepository.save(post);
        return post;
    }

    public void delete(long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post no encontrado"));

        User creator = post.getCreator();
        if (creator != null) {
            creator.getListOfPosts().remove(post);
            userRepository.save(creator);
        }

        postRepository.delete(post);

    }

    public void editPost(Post post, MultipartFile imageField, long id) throws IOException{

        if (imageField != null && !imageField.isEmpty()) {
            post.setImage(imageField.getOriginalFilename());
            post.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
        }

        else if (post.getImage() == null || post.getImage().isEmpty()) {
            Post existingProduct = postRepository.findById(id).orElseThrow();
            post.setImageFile(existingProduct.getImageFile());
            post.setImage(String.valueOf(existingProduct.getImage()));
        }
        Post actualPost = postRepository.findById(id).orElseThrow();
        post.setCreator(actualPost.getCreator());
        postRepository.save(post);
    }

    public void addCreator(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post no encontrado"));
        if (post.getCreator() != null) {
            throw new IllegalArgumentException("El post ya tiene un creador asignado");
        }

        post.setCreator(user);
        user.getListOfPosts().add(post);

        postRepository.save(post);
        userRepository.save(user);
    }

}

