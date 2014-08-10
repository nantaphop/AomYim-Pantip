package com.nantaphop.pantipfanapp.utils;

import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.view.TopicCard;
import it.gmariotti.cardslib.library.internal.Card;

import java.util.Comparator;

/**
 * Created by nantaphop on 10-Aug-14.
 */
public class TopicComparator implements Comparator<Topic> {


    public static enum SortType {Comment, Vote, Time}

    ;
    SortType sortBy;

    public TopicComparator(SortType sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public int compare(Topic topic, Topic topic2) {

            if (sortBy == SortType.Comment) {
                return topic2.getComments() - topic.getComments();
            } else if (sortBy == SortType.Vote) {
                return topic2.getVotes() - topic.getVotes();
            } else if (sortBy == SortType.Time) {
                return topic2.getDate().compareTo(topic.getDate());
            }
            return 0;
    }
}

