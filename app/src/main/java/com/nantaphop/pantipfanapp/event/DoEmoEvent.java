package com.nantaphop.pantipfanapp.event;

import com.nantaphop.pantipfanapp.response.Comment;
import com.nantaphop.pantipfanapp.response.TopicPost;
import com.nantaphop.pantipfanapp.view.CommentView;
import com.nantaphop.pantipfanapp.view.TopicPostView;

/**
 * Created by nantaphop on 18-Oct-14.
 */
public class DoEmoEvent {

    public CommentView view;
    public Comment comment;
    public TopicPostView topicPostView;
    public TopicPost topicPost;


    public DoEmoEvent(CommentView view, Comment comment) {
        this.view = view;
        this.comment = comment;
    }

    public DoEmoEvent(TopicPostView topicPostView, TopicPost topicPost) {
        this.topicPostView = topicPostView;
        this.topicPost = topicPost;
    }
}
