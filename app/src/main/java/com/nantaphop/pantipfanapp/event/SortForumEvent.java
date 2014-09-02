package com.nantaphop.pantipfanapp.event;

import android.widget.BaseAdapter;
import com.nantaphop.pantipfanapp.fragment.ForumFragment;
import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.Topic;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nantaphop on 10-Aug-14.
 */
public class SortForumEvent {
    private ArrayList<Topic> topics;
    private BaseAdapter adapter;

    public SortForumEvent(ArrayList<Topic> topics, BaseAdapter adapter) {
        this.topics = topics;
        this.adapter = adapter;
    }

    public ArrayList<Topic> getTopics() {
        return topics;
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }
}
