package es.codeurjc.web.Service;

import es.codeurjc.web.Model.User;
import es.codeurjc.web.Model.GroupClass;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.repository.UserRepository;
import es.codeurjc.web.repository.GroupClassRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private EntityManager entityManager;


    @Autowired
    private PostService postService;


    @Autowired
    private GroupClassService groupClassService;


    @Autowired
    private GroupClassRepository groupClassRepository;



    private AtomicLong nextId = new AtomicLong(1L);
    private Long classUserId;


    public List<User>findAll(){return userRepository.findAll();}


    @SuppressWarnings("unchecked")
    public Iterable<User>findAll(String name){
        String query = "SELECT * FROM classUser";
        if(name != null){
            query+=" WHERE name= " + name;
        }
        if(!query.startsWith("SELECT")){
            query = query.substring(5);
        }
        return (List<User>) entityManager.createNativeQuery(query, User.class).getResultList();
    }


    public User findUserById(long id){return userRepository.findById(id).orElseThrow();}


    public boolean exist(long id){return userRepository.existsById(id);}


    public Optional<User> findById(long id){

        if(this.exist(id)){
            return Optional.of(this.findUserById(id));
        }
        return Optional.empty();

    }


    public User save(User user){

        long id = nextId.getAndIncrement();
        user.setUserid(id);
        userRepository.save(user);
        return user;

    }
    public User updateUser(User user){
        userRepository.save(user);
        return user;
    }


    public void delete(long id){
        userRepository.deleteById(id);}


    public void addPost(Post post, Long UserId){

        User user = userRepository.findById(UserId).orElseThrow(); //error en newpost
        List <Post> posts = user.getListOfPosts();
        posts.add(post);
        user.setListOfPosts(posts);
        userRepository.save(user);

    }


    public void removePost(long postId, long classUserId){

        User user = userRepository.findById(classUserId).orElseThrow();
        List <Post> posts = user.getListOfPosts();
        posts.remove(postService.findById(postId).get());
        user.setListOfPosts(posts);
        userRepository.save(user);

    }


    public void addGroupClass(GroupClass groupClass, long classUserId){

        User user = userRepository.findById(classUserId).orElseThrow();
        List <GroupClass> groupClasses = user.getListOfClasses();
        groupClasses.add(groupClass);
        user.setListOfClasses(groupClasses);
        userRepository.save(user);

    }


    public void removeGroupClass(long groupClassId, long classUserId){

        User user = userRepository.findById(classUserId).orElseThrow();
        List <GroupClass> groupClasses = user.getListOfClasses();
        groupClasses.remove(groupClassService.findById(groupClassId).get());
        user.setListOfClasses(groupClasses);
        userRepository.save(user);

    }
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean isUser(long id, long id2){return id==id2;}

}
