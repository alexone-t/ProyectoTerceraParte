package es.codeurjc.web.Model;

import com.fasterxml.jackson.annotation.JsonView;

import java.sql.Blob;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ClassUser {

    public interface Basic {}


    @Id @JsonView(Basic.class)
    private Long userid;
    @JsonView(Basic.class)
    private String name;



    public interface Posts{}


    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @JsonView(Posts.class)
    private List<Post> listOfPosts = new ArrayList<>();


    public interface GroupClasses{}


    @JsonView(GroupClasses.class)
    @ManyToMany(cascade=CascadeType.MERGE)
    private List<GroupClass> listOfClasses = new ArrayList<>();


    public ClassUser(){}
    public ClassUser(String name){
        this.name = name;
        this.listOfClasses = new ArrayList<>();
        this.listOfPosts = new ArrayList<>();
    }


    public Long getUserid(){return this.userid;}
    public void setUserid(Long userid){this.userid = userid;}


    public String getName(){return name;}
    public void setName(String name){this.name = name;}


    public List<GroupClass> getListOfClasses(){
        return this.listOfClasses;
    }
    public void setListOfClasses(List<GroupClass> groupClasses){this.listOfClasses = groupClasses;}


    public List<Post> getListOfPosts(){return this.listOfPosts;}
    public void setListOfPosts(List<Post>posts){this.listOfPosts = posts;}

    public void addPost(Post post){this.listOfPosts.add(post);}

}