package es.codeurjc.web.Model;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class GroupClass {

    public interface Basic{}

    @Id @JsonView(Basic.class)
    private Long classid;
    @JsonView(Basic.class)
    private String name;
    @Column(name = "dayOfWeek")
    @JsonView(Basic.class)
    private String day;
    @JsonView(Basic.class)
    private String time;
    @JsonView(Basic.class)
    private String instructor;
    @JsonView(Basic.class)
    private int maxCapacity;
    @JsonView(Basic.class)
    private int currentCapacity;

    public interface Users{}

    @ManyToMany(cascade=CascadeType.MERGE)
    @JsonView(Users.class)
    private List<User> usersList = new ArrayList<>();




    public GroupClass(){}

    public GroupClass(String name, String day, String time, String instructor, int maxCapacity) {
        super();
        this.name = name;
        this.day = day;
        this.time = time;
        this.instructor = instructor;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = 0;
        this.usersList = new ArrayList<>();
    }

    public Long getId() {return classid;}
    public void setId(long id) {this.classid = id;}

    public String getName() {
        return name;
    }
    public void setName(String name) {this.name = name;}
    public boolean sameName(String name){return this.name.equals(name);}
    ////////////////////////////////////////
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }


    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }


    public String getInstructor() {
        return instructor;
    }
    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }


    public int getMaxCapacity() {
        return maxCapacity;
    }
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }


    public int getCurrentCapacity() {
        return currentCapacity;
    }
    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }


    public boolean maxCapacityReached(){
        return this.currentCapacity >= this.maxCapacity;
    }

    public List<User> getClassUsers(){return this.usersList;}
    public void setClassUsers(List<User> users){this.usersList = users;}

    public void addClassUser(User user){
        this.usersList.add(user);
        this.setCurrentCapacity(this.getCurrentCapacity()+1);
    }
    public void removeClassUser(User user){
        this.usersList.remove(user);
        this.setCurrentCapacity(this.getCurrentCapacity()-1);
    }
    @Override
    public String toString() {
        return "Class [name=" + name + ", day=" + day + ", time=" + time + ", instructor=" + instructor + ", maxCapacity=" + maxCapacity + ", currentCapacity=" + currentCapacity + "]";
    }



}

