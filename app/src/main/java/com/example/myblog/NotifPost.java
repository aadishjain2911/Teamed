package com.example.myblog;

import java.util.Date;

public class NotifPost extends NotifPostId{

    public String sender_name,sender_image,notif_type,blogPostId,event_name ;
    public Date timestamp ;

    public NotifPost() {}

    public NotifPost(String sender_name,String sender_image, Date timestamp,String blogPostId,String notif_type,String event_name) {
        this.sender_name = sender_name;
        this.sender_image = sender_image ;
        this.timestamp = timestamp;
        this.blogPostId = blogPostId ;
        this.notif_type = notif_type ;
        this.event_name = event_name ;
    }

    public String getBlogPostId() {
        return blogPostId;
    }

    public String getNotif_type() {
        return notif_type;
    }

    public void setNotif_type(String notif_type) {
        this.notif_type = notif_type;
    }

    public void setBlogPostId(String blogPostId) {
        this.blogPostId = blogPostId;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getSender_image() {
        return sender_image;
    }

    public void setSender_image(String sender_image) {
        this.sender_image = sender_image;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }



}
