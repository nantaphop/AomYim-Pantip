package com.nantaphop.pantipfanapp.utils;

import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.view.TopicCard;
import it.gmariotti.cardslib.library.internal.Card;

import java.util.Comparator;

/**
 * Created by nantaphop on 10-Aug-14.
 */
public class TopicCardComparator implements Comparator<Card> {


    public static enum SortType {Comment, Vote, Time}

    ;
    SortType sortBy;

    public TopicCardComparator(SortType sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public int compare(Card card, Card card2) {
        if (card instanceof TopicCard && card2 instanceof TopicCard) {
            Topic topic = ((TopicCard) card).getTopic();
            Topic topic2 = ((TopicCard) card2).getTopic();
            if (sortBy == SortType.Comment) {
                return topic2.getComments() - topic.getComments();
            } else if (sortBy == SortType.Vote) {
                return topic2.getVotes() - topic.getVotes();
            } else if (sortBy == SortType.Time) {
                return topic2.getDate().compareTo(topic.getDate());
            }
            return 0;
        }
        return 0;
    }
}

