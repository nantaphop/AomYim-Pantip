package com.nantaphop.pantipfanapp.utils;

/**
 * Created by nantaphop on 27-Oct-14.
 */
public class Utils {

    public static String getForumPath(String fullUrl){
        String[] strings = fullUrl.split("/");
        return strings[4];
    }
}
