package com.abukh.xkcdtextmode;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("XKCD") // should be same as the entity defined in parse dashboard
public class XKCD extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_COMIC_TITLE = "comic_title";
    public static final String KEY_COMIC_NUM = "comic_num";
    public static final String KEY_IS_FAVORITE = "isFavorite";


    public ParseUser getUser() { // parse has special class defined for User which we should use
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getComicTitle() {
        return getString(KEY_COMIC_TITLE); // get string is defined in the ParseObject class
    }

    public void setComicTitle(String comicTitle) {
        put(KEY_COMIC_TITLE, comicTitle);
    }

    public Integer getComicNum() {
        return getInt(KEY_COMIC_NUM); // get string is defined in the ParseObject class
    }

    public void setComicNum(Integer num) {
        put(KEY_COMIC_NUM, num);
    }

    public String getIsFavorite() {
        return getString(KEY_IS_FAVORITE);
    }

    public void setIsFavorite(String favs) {
        put(KEY_IS_FAVORITE, favs);
    }
}

