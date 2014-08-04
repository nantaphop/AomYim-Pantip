package com.nantaphop.pantipfanapp.event;

import java.util.ArrayList;

/**
 * Created by nantaphop on 04-Aug-14.
 */
public class ShowRecommendEvent {
    ArrayList<String> recommendTopics;
    ArrayList<String> recommendUrls;

    public ShowRecommendEvent(ArrayList<String> recommendTopics, ArrayList<String> recommendUrls) {
        this.recommendTopics = recommendTopics;
        this.recommendUrls = recommendUrls;
    }

    public ArrayList<String> getRecommendTopics() {
        return recommendTopics;
    }

    public ArrayList<String> getRecommendUrls() {
        return recommendUrls;
    }
}
