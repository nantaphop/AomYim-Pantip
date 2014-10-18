package com.nantaphop.pantipfanapp.event;

import com.nantaphop.pantipfanapp.response.Comment;
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import com.nantaphop.pantipfanapp.view.CommentView;

/**
 * Created by nantaphop on 18-Oct-14.
 */
public class DoVoteEvent {

    public CommentView view;
    public Comment comment;



    public DoVoteEvent(CommentView view, Comment comment) {
        this.view = view;
        this.comment = comment;
    }
}
