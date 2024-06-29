package es.codeurjc.web.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.*;

import java.sql.Blob;

@Entity
public class Post {
    public interface Basic{}

    @Id @JsonView(Basic.class)
    private long id;
    @JsonView(Basic.class)
    private String title;
    @JsonView(Basic.class)
    private String text;
    @JsonView(Basic.class)
    private String image;
    @Lob@JsonIgnore
    private Blob imageFile;
    public interface Creator{}

    @OneToOne@JsonView(Creator.class)
    private ClassUser creator;

    public Post() {

    }

    public Post(String title, String text) {
        super();
        this.title = title;
        this.text = text;
    }
    public Post(String creatorName, String title, String text){
        super();
        this.creator = new ClassUser(creatorName);
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
    public void setCreator(ClassUser creator){this.creator = creator;}
    public ClassUser getCreator(){return this.creator;}
    public void setCreatorName(String creatorName){this.creator.setName(creatorName);}
    public String getCreatorName(){return this.creator.getName();}
    public String getImage(){return this.image;}

    public void setImage(String image){ this.image = image; }
    public Blob getImageFile(){return this.imageFile;}
    public void setImageFile(Blob imageFile){this.imageFile = imageFile;}
    @Override
    public String toString() {
        return "Post [id=" + id + ", title=" + title + ", text=" + text + "]";
    }
}
