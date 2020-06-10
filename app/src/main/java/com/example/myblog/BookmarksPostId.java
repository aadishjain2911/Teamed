package com.example.myblog;

import com.google.firebase.database.Exclude;

import javax.annotation.Nonnull;

public class BookmarksPostId {

    @Exclude
    public String BookmarksPostId ;

    public <T extends BookmarksPostId> T withId(@Nonnull final String id) {
        this.BookmarksPostId = id ;
        return (T) this ;
    }
}
