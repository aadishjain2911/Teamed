package com.example.myblog;


import java.sql.Timestamp;
import java.util.Date;

public class BlogPost extends BlogPostId{

    public String name , description , user_id ;
    public Date timestamp ;

    public BlogPost() {}

    public BlogPost(String name, String description, String user_id,Date timestamp) {
        this.name = name;
        this.description = description;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
