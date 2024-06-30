package es.codeurjc.web.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.*;

import java.sql.Blob;

@Entity
public class Post {
    public interface Basic{}

    @Id @JsonView(Basic.class)
    private Long id;
    @JsonView(Basic.class)
    private String title;
    @JsonView(Basic.class)
    private String text;
    @JsonView(Basic.class)
    private String image;
    @Lob@JsonIgnore
    private Blob imageFile;
    public interface Creator{}

    @ManyToOne@JsonView(Creator.class)
    private User creator;

    public Post() {

    }

    public Post(String title, String text) {
        super();
        this.title = title;
        this.text = text;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setCreator(User creator){this.creator = creator;}
    public User getCreator(){return this.creator;}
    public void setCreatorName(String creatorName){this.creator.setUsername(creatorName);}
    public String getCreatorName(){return this.creator.getUsername();}
    public String getImage(){return this.image;}
    public void setImage(String image){ this.image = image; }
    public Blob getImageFile(){return this.imageFile;}
    public void setImageFile(Blob imageFile){this.imageFile = imageFile;}
    public boolean hasImage(){return this.image.equals("no-image.png");}
    @Override
    public String toString() {
        return "Post [id=" + id + ", title=" + title + ", text=" + text + "]";
    }
}
