package es.codeurjc.web.Model;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Iterator;
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
    @JsonView(Basic.class)
    private boolean officialClass;
    private boolean alreadyJoined;

    public interface Users{}

    @ManyToMany(cascade=CascadeType.MERGE)
    @JsonView(Users.class)
    private List<ClassUser> usersList = new ArrayList<>();




    public GroupClass(){}

    public GroupClass(String name, String day, String time, String instructor, int maxCapacity,boolean officialClass) {
        super();
        this.name = name;
        this.day = day;
        this.time = time;
        this.instructor = instructor;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = 0;
        this.usersList = new ArrayList<>();
        this.alreadyJoined = false;
        this.officialClass = officialClass;
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


    public boolean getAlreadyJoined(){return this.alreadyJoined;}
    public void setAlreadyJoined(boolean joined){this.alreadyJoined = joined;}


    public boolean getOfficialClass(){return this.officialClass;}
    public void setOfficialClass(boolean officialClass){this.officialClass = officialClass;}


    public List<ClassUser> getClassUsers(){return this.usersList;}
    public void setClassUsers(List<ClassUser> classUsers){this.usersList = classUsers;}


    @Override
    public String toString() {
        return "Class [name=" + name + ", day=" + day + ", time=" + time + ", instructor=" + instructor + ", maxCapacity=" + maxCapacity + ", currentCapacity=" + currentCapacity + ", officialClass=" + officialClass + "]";
    }



}

