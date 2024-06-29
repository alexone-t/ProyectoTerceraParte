package es.codeurjc.web.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "USERS")
public class User {

    public interface Basic {}


    @Id @JsonView(Basic.class)
    private Long userid;
    @JsonView(Basic.class)
    private String username;
    @JsonView(Basic.class)
    private String name;
    @JsonIgnore
    private String encodedPassword;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;


    public interface Posts{}
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @JsonView(Posts.class)
    private List<Post> listOfPosts = new ArrayList<>();


    public interface GroupClasses{}
    @JsonView(GroupClasses.class)
    @ManyToMany(cascade=CascadeType.MERGE)
    private List<GroupClass> listOfClasses = new ArrayList<>();


    public User(){}
    public User(String username, String name,String encodedPassword, String... roles) {
        this.name = name;
        this.username = username;
        this.encodedPassword = encodedPassword;
        this.roles = List.of(roles);
        this.listOfClasses = new ArrayList<>();
        this.listOfPosts = new ArrayList<>();
    }


    public Long getUserid(){return this.userid;}
    public void setUserid(Long userid){this.userid = userid;}


    public String getName(){return username;}
    public void setName(String name){this.username = name;}
    public String getUsername(){return this.username;}
    public void setUsername(String username){this.username = username;}

    public List<GroupClass> getListOfClasses(){
        return this.listOfClasses;
    }
    public void setListOfClasses(List<GroupClass> groupClasses){this.listOfClasses = groupClasses;}


    public List<Post> getListOfPosts(){return this.listOfPosts;}
    public void setListOfPosts(List<Post>posts){this.listOfPosts = posts;}

    public void addPost(Post post){this.listOfPosts.add(post);}
    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}