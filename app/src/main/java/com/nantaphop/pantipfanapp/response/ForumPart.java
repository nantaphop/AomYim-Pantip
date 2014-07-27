package com.nantaphop.pantipfanapp.response;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 8/8/13
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForumPart {

    ArrayList<String> recommendTopic;
    ArrayList<String> recommendUrl;
    ArrayList<String> tag;
    ArrayList<String> tagUrl;
    ArrayList<String> club;
    ArrayList<String> clubUrl;

    public ArrayList<String> getRecommendTopic() {
        return recommendTopic;
    }

    public void setRecommendTopic(ArrayList<String> recommendTopic) {
        this.recommendTopic = recommendTopic;
    }

    public ArrayList<String> getRecommendUrl() {
        return recommendUrl;
    }

    public void setRecommendUrl(ArrayList<String> recommendUrl) {
        this.recommendUrl = recommendUrl;
    }

    public ArrayList<String> getTag() {
        return tag;
    }

    public void setTag(ArrayList<String> tag) {
        this.tag = tag;
    }

    public ArrayList<String> getTagUrl() {
        return tagUrl;
    }

    public void setTagUrl(ArrayList<String> tagUrl) {
        this.tagUrl = tagUrl;
    }

    public ArrayList<String> getClub() {
        return club;
    }

    public void setClub(ArrayList<String> club) {
        this.club = club;
    }

    public ArrayList<String> getClubUrl() {
        return clubUrl;
    }

    public void setClubUrl(ArrayList<String> clubUrl) {
        this.clubUrl = clubUrl;
    }
}
