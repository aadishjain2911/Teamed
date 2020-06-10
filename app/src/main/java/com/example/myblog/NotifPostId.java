package com.example.myblog;

import com.google.firebase.database.Exclude;

import javax.annotation.Nonnull;

public class NotifPostId {

    @Exclude
    public String NotifPostId ;

    public <T extends NotifPostId> T withId(@Nonnull final String id) {
        this.NotifPostId = id ;
        return (T) this ;
    }
}
