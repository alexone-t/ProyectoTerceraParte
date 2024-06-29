package es.codeurjc.web.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

// import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.web.Model.ClassUser;
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

    @PostConstruct
    public void init()throws IOException{
        //Create class users
        ClassUser user1 = new ClassUser("Juan");
        ClassUser user2 = new ClassUser("María");
        ClassUser user3 = new ClassUser("Pedro");
        users.save(user1);
        users.save(user2);
        users.save(user3);

        //Create group class
        GroupClass class1 = new GroupClass("Advanced yoga", "Monday", "10:00", "Professor A", 20,true);
        GroupClass class2 = new GroupClass("Pilates", "Tuesday", "15:00", "Professor B", 15,true);
        GroupClass class3 = new GroupClass("CrossFit", "Wednesday", "18:00", "Professor C", 25,true);
        GroupClass class4 = new GroupClass("Zumba", "Thursday", "12:00", "Professor D", 30,true);
        GroupClass class5 = new GroupClass("Spinning", "Friday", "17:00", "Professor E", 20,true);
        GroupClass class6 = new GroupClass("Aerobics", "Saturday", "09:00", "Professor F", 25,true);
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

        //Relate users to classes
        user1.getListOfClasses().add(class1);
        user1.getListOfClasses().add(class2);
        user1.getListOfClasses().add(class3);
        user1.getListOfPosts().add(post1);
        user1.getListOfPosts().add(post2);
        user1.getListOfPosts().add(post3);

        user2.getListOfClasses().add(class2);
        user2.getListOfClasses().add(class4);
        user2.getListOfPosts().add(post4);

        user3.getListOfClasses().add(class3);
        user3.getListOfClasses().add(class5);
        user3.getListOfPosts().add(post5);
        user3.getListOfPosts().add(post6);

    //Relate classes to users
        class1.getClassUsers().add(user1);
        class2.getClassUsers().add(user1);
        class3.getClassUsers().add(user1);

        class2.getClassUsers().add(user2);
        class4.getClassUsers().add(user2);

        class3.getClassUsers().add(user3);
        class5.getClassUsers().add(user3);

    //Relate posts to users
        post1.setCreator(user1);
        post2.setCreator(user1);
        post3.setCreator(user1);
        post4.setCreator(user2);
        post5.setCreator(user3);
        post6.setCreator(user3);
    }
}
