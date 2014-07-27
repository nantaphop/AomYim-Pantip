package com.nantaphop.pantipfanapp.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 7/23/13
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class User implements Serializable{

    @SerializedName("mid")
    int id;
    String name;
    String link;
    Avatar avatar;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public class Avatar implements Serializable{
        public String getOriginal() {
            return original;
        }

        public String getLarge() {
            return large;
        }

        public String getMedium() {
            return medium;
        }

        public String getSmall() {
            return small;
        }

        String original;
        String large;
        String medium;
        String small;

    }

}
