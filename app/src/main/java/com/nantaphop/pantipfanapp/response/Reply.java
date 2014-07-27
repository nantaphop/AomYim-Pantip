package com.nantaphop.pantipfanapp.response;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 7/31/13
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class Reply {
    int last;
    int comment_id;
    int reply_count;

    ArrayList<Comment> replies;


    public int getLast() {
        return last;
    }

    public ArrayList<Comment> getReplies() {
        return replies;
    }
}
