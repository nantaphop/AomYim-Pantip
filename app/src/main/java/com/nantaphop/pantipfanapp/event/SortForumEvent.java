package com.nantaphop.pantipfanapp.event;

import android.widget.BaseAdapter;
import com.nantaphop.pantipfanapp.response.Forum;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nantaphop on 10-Aug-14.
 */
public class SortForumEvent {
    Forum forum;
    CardArrayAdapter adapter;
    private List<Card> cardList;

    public SortForumEvent(Forum forum, CardArrayAdapter adapter, List<Card> cardList) {
        this.forum = forum;
        this.adapter = adapter;
        this.cardList = cardList;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(CardArrayAdapter adapter) {
        this.adapter = adapter;
    }
}
