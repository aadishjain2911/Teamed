package com.example.myblog;

import java.util.Date;

public class NotifPost extends NotifPostId{

    public String sender_user_id,type,blogPostId ;
    public Date timestamp ;

    public NotifPost() {}

    public NotifPost(String sender_user_id, Date timestamp,String blogPostId,String type) {
        this.sender_user_id = sender_user_id;
        this.timestamp = timestamp;
        this.blogPostId = blogPostId ;
        this.type = type ;
    }

    public String getBlogPostId() {
        return blogPostId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBlogPostId(String blogPostId) {
        this.blogPostId = blogPostId;
    }

    public String getSender_user_id() {
        return sender_user_id;
    }

    public void setSender_user_id(String sender_user_id) {
        this.sender_user_id = sender_user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }



}
