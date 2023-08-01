package sanggit999.example.admin_comic.model;


import org.bson.types.ObjectId;

import java.io.Serializable;

public class User implements Serializable {
    private ObjectId _id;
    private String username;
    private String password;
    private String email;
    private String fullname;
    private int role;

    public User() {
    }


    public User(ObjectId _id, String username, String password, String email, String fullname, int role) {
        this._id = _id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.role = role;
    }

    public User(ObjectId _id, String fullname) {
        this._id = _id;
        this.fullname = fullname;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
