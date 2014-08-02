package com.nantaphop.pantipfanapp.response;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 7/22/13
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tag implements Serializable {
    String tag;
    String url;
    boolean allow;


    public Tag(String tag, String url) {
        this.tag = tag;
        this.url = url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    @Override
    public String toString() {
        return tag+" - "+allow;
    }
}
