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
            if(original.startsWith("/images")){
                return "http://pantip.com/"+original;
            }
            return original;
        }

        public String getLarge() {
            if(large.startsWith("/images")){
                return "http://pantip.com/"+large;
            }
            return large;
        }

        public String getMedium() {
            if(medium.startsWith("/images")){
                return "http://pantip.com/"+medium;
            }
            return medium;
        }

        public String getSmall() {
            if(small.startsWith("/images")){
                return "http://pantip.com/"+small;
            }
            return small;
        }

        String original;
        String large;
        String medium;
        String small;

    }

}
