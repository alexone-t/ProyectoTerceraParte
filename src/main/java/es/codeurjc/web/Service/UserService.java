package es.codeurjc.web.Service;

import es.codeurjc.web.Model.ClassUser;
import es.codeurjc.web.Model.GroupClass;
import es.codeurjc.web.Model.Post;
import es.codeurjc.web.repository.ClassUserRepository;
import es.codeurjc.web.repository.GroupClassRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

    @Autowired
    private ClassUserRepository classUserRepository;


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


    public List<ClassUser>findAll(){return classUserRepository.findAll();}


    @SuppressWarnings("unchecked")
    public Iterable<ClassUser>findAll(String name){
        String query = "SELECT * FROM classUser";
        if(name != null){
            query+=" WHERE name= " + name;
        }
        if(!query.startsWith("SELECT")){
            query = query.substring(5);
        }
        return (List<ClassUser>) entityManager.createNativeQuery(query, ClassUser.class).getResultList();
    }


    public ClassUser findUserById(long id){return classUserRepository.findById(id).orElseThrow();}


    public boolean exist(long id){return classUserRepository.existsById(id);}


    public Optional<ClassUser> findById(long id){

        if(this.exist(id)){
            return Optional.of(this.findUserById(id));
        }
        return Optional.empty();

    }


    public ClassUser save(ClassUser classUser){

        long id = nextId.getAndIncrement();
        classUser.setUserid(id);
        classUserRepository.save(classUser);
        return classUser;

    }


    public void delete(long id){classUserRepository.deleteById(id);}


    public void addPost(Post post, Long UserId){

        ClassUser classUser = classUserRepository.findById(UserId).orElseThrow(); //error en newpost
        List <Post> posts = classUser.getListOfPosts();
        posts.add(post);
        classUser.setListOfPosts(posts);
        classUserRepository.save(classUser);

    }


    public void removePost(long postId, long classUserId){

        ClassUser classUser = classUserRepository.findById(classUserId).orElseThrow();
        List <Post> posts = classUser.getListOfPosts();
        posts.remove(postService.findById(postId).get());
        classUser.setListOfPosts(posts);
        classUserRepository.save(classUser);

    }


    public void addGroupClass(GroupClass groupClass, long classUserId){

        ClassUser classUser = classUserRepository.findById(classUserId).orElseThrow();
        List <GroupClass> groupClasses = classUser.getListOfClasses();
        groupClasses.add(groupClass);
        classUser.setListOfClasses(groupClasses);
        classUserRepository.save(classUser);

    }


    public void removeGroupClass(long groupClassId, long classUserId){

        ClassUser classUser = classUserRepository.findById(classUserId).orElseThrow();
        List <GroupClass> groupClasses = classUser.getListOfClasses();
        groupClasses.remove(groupClassService.findById(groupClassId).get());
        classUser.setListOfClasses(groupClasses);
        classUserRepository.save(classUser);

    }


}
