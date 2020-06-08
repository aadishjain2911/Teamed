package com.example.myblog;

import java.sql.Timestamp;

public class BlogPost {

    public String name , image_uri , description , user_id , image_thumb ;
    public Timestamp timestamp ;

    public BlogPost() {}

    public BlogPost(String name, String image_uri, String description, String user_id, String image_thumb, Timestamp timestamp) {
        this.name = name;
        this.image_uri = image_uri;
        this.description = description;
        this.user_id = user_id;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
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

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }



}
