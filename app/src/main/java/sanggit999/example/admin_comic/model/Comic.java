package sanggit999.example.admin_comic.model;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

public class Comic implements Serializable {
    private ObjectId _id;
    private String title;
    private String description;
    private String author;
    private String year;
    private String coverImage;
    private List<String> images;

    public Comic() {
    }

    public Comic(ObjectId _id, String title, String description, String author, String year, String coverImage, List<String> images) {
        this._id = _id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.year = year;
        this.coverImage = coverImage;
        this.images = images;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
