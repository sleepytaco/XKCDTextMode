package com.abukh.xkcdtextmode;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Post") // should be same as the entity defined in parse dashboard
public class XKCDComicParse extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_COMICS_READ = "comics_read";

    public ParseUser getUser() { // parse has special class defined for User which we should use
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public List<Integer> getComicsRead() {
        return getList(KEY_COMICS_READ); // get string is defined in the ParseObject class
    }

    public void setComicsRead(List<Integer> comicsRead) {
        put(KEY_COMICS_READ, comicsRead);
    }
}

