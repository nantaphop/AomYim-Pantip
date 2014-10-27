package com.nantaphop.pantipfanapp.event;

import com.nantaphop.pantipfanapp.model.ForumPagerItem;
import com.nantaphop.pantipfanapp.service.PantipRestClient;

/**
 * Created by nantaphop on 27-Oct-14.
 */
public class OpenForumEvent {

    public ForumPagerItem forumPagerItem;
    public PantipRestClient.ForumType forumType;

    public OpenForumEvent(ForumPagerItem forumPagerItem, PantipRestClient.ForumType forumType) {
        this.forumPagerItem = forumPagerItem;
        this.forumType = forumType;
    }
}
