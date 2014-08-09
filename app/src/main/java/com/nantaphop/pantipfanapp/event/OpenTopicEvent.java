package com.nantaphop.pantipfanapp.event;

import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.Topic;

/**
 * Created by nantaphop on 04-Aug-14.
 */
public class OpenTopicEvent {
    private Topic topic;

    public OpenTopicEvent(Topic topic) {
        this.topic = topic;
    }

    public Topic getTopic() {
        return topic;
    }
}
