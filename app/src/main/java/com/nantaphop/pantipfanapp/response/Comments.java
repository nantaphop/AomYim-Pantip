package com.nantaphop.pantipfanapp.response;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 7/23/13
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Comments implements Serializable {

    int count;
    Paging paging;
    ArrayList<Comment> comments;

    public void addComments(ArrayList<Comment> newComments){
        if(newComments != null)
            comments.addAll(newComments);

    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public int getCount() {
        return count;
    }

    public Paging getPaging() {
        return paging;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }
}
