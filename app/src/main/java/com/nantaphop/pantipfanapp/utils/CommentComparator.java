package com.nantaphop.pantipfanapp.utils;

import com.nantaphop.pantipfanapp.response.Comment;
import com.nantaphop.pantipfanapp.response.Topic;

import java.util.Comparator;

/**
 * Created by nantaphop on 10-Aug-14.
 */
public class CommentComparator implements Comparator<Comment> {


    public static enum SortType {Vote, Order, Emo}

    ;
    SortType sortBy;

    public CommentComparator(SortType sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public int compare(Comment c, Comment c2) {

            if (sortBy == SortType.Vote) {
                return c2.getPoint() - c.getPoint();
            } else if (sortBy == SortType.Emo) {
                return c2.getEmo_score() - c.getEmo_score();
            } else if (sortBy == SortType.Order) {
                if (c2.getComment_no() != c.getComment_no()) {
                    return c.getComment_no() - c2.getComment_no();
                }else{
                    return c.getReply_no() - c2.getReply_no();
                }
            }
            return 0;
    }
}

