package sanggit999.example.admin_comic.model;

import org.bson.types.ObjectId;

import java.util.Date;

public class Comment {
    private ObjectId _id;
    private ObjectId comicId;
    private ObjectId userId;
    private String content;
    private long createdAt;


    private User user;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment() {

    }

    public Comment(ObjectId _id, ObjectId comicId, ObjectId userId, String content, long createdAt) {
        this._id = _id;
        this.comicId = comicId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public ObjectId getComicId() {
        return comicId;
    }

    public void setComicId(ObjectId comicId) {
        this.comicId = comicId;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }



}
