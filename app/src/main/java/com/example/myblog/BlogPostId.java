package com.example.myblog;

import com.google.firebase.database.Exclude;

import javax.annotation.Nonnull;

public class BlogPostId {

    @Exclude
    public String BlogPostId ;

    public <T extends BlogPostId> T withId(@Nonnull final String id) {
        this.BlogPostId = id ;
        return (T) this ;
    }
}
