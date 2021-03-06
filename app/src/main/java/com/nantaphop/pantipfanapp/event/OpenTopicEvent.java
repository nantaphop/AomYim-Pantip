package com.nantaphop.pantipfanapp.event;

import android.view.View;
import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.Topic;

/**
 * Created by nantaphop on 04-Aug-14.
 */
public class OpenTopicEvent {
    private Topic topic;
    private View animatedView;
    private int startingLocation;

    public OpenTopicEvent(Topic topic) {
        this.topic = topic;
    }

    public OpenTopicEvent(Topic topic, int startingLocation) {
        this.topic = topic;
        this.startingLocation = startingLocation;
    }

    public OpenTopicEvent(Topic topic, View animatedView) {
        this.topic = topic;
        this.animatedView = animatedView;
    }

    public Topic getTopic() {
        return topic;
    }

    public View getAnimatedView() {
        return animatedView;
    }

    public int getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(int startingLocation) {
        this.startingLocation = startingLocation;
    }
}
