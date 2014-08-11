package com.nantaphop.pantipfanapp.event;

import android.widget.BaseAdapter;
import com.nantaphop.pantipfanapp.response.Comments;
import com.nantaphop.pantipfanapp.response.Forum;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

import java.util.List;

/**
 * Created by nantaphop on 10-Aug-14.
 */
public class SortCommentEvent {
    Comments comments;
    BaseAdapter adapter;

    public SortCommentEvent(Comments comments, BaseAdapter adapter) {
        this.comments = comments;
        this.adapter = adapter;
    }

    public Comments getComments() {
        return comments;
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }
}
