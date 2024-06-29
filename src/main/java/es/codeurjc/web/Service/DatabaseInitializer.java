package es.codeurjc.web.Service;

import java.io.IOException;

// import org.hibernate.engine.jdbc.BlobProxy;
import es.codeurjc.web.Model.User;
import es.codeurjc.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.codeurjc.web.Model.GroupClass;
import es.codeurjc.web.Model.Post;
import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {
    @Autowired
    private GroupClassService groupClass;
    @Autowired
    private UserService users;
    @Autowired
    private PostService posts;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init()throws IOException{
        users.save(new User("Manolo","user1",passwordEncoder.encode("pass1"), "USER"));
        users.save(new User("Paco","user2",passwordEncoder.encode("pass2"), "USER"));
        users.save(new User("Miguel","admin",passwordEncoder.encode("adminpass"), "USER","ADMIN"));

        //Create group class
        GroupClass class1 = new GroupClass("Advanced yoga", "Monday", "10:00", "Professor A", 20);
        GroupClass class2 = new GroupClass("Pilates", "Tuesday", "15:00", "Professor B", 15);
        GroupClass class3 = new GroupClass("CrossFit", "Wednesday", "18:00", "Professor C", 25);
        GroupClass class4 = new GroupClass("Zumba", "Thursday", "12:00", "Professor D", 30);
        GroupClass class5 = new GroupClass("Spinning", "Friday", "17:00", "Professor E", 20);
        GroupClass class6 = new GroupClass("Aerobics", "Saturday", "09:00", "Professor F", 25);
        groupClass.save(class1);
        groupClass.save(class2);
        groupClass.save(class3);
        groupClass.save(class4);
        groupClass.save(class5);
        groupClass.save(class6);

        //Create posts
        Post post1 = new Post("The beggining of a new week", "Hi everyone! It's Monday and and I'm excited to start a new week full of challenges.");
        Post post2 = new Post("Mi experience in the Pilates class", "Hello! Today I want to share my experience in yesterday's Pilates class. It was great!");
        Post post3 = new Post("Today's CrossFit trainning", "Hi guys! I just finished my CrossFit workout today and I'm completely exhausted but happy.");
        Post post4 = new Post("Zumba to stay in shape", "Hello everyone! I want to talk to you about my Zumba class today. It was a great way to stay fit and have fun!");
        Post post5 = new Post("My thoughts  after Spinning", "Hello friends! After my Spinning class today, I feel incredibly energized and ready to take on any challenge.");
        Post post6 = new Post("Benefits of Aerobics", "Good morning! Today I want to share with you some of the benefits of doing aerobics regularly. I hope you find it interesting!");

        post1.setImage("example1.jpeg");
        post2.setImage("example2.jpeg");
        post3.setImage("example3.jpeg");
        post4.setImage("example4.jpeg");


        posts.save(post1,null);
        posts.save(post2,null);
        posts.save(post3,null);
        posts.save(post4,null);
        posts.save(post5,null);
        posts.save(post6,null);


    }
}
