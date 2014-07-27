package com.nantaphop.pantipfanapp.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 7/23/13
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Paging implements Serializable{

    int page;
    int limit;
    int limit_reply;

    public void setPage(int page) {
        this.page = page;
    }

    int topic_status;

    @SerializedName("max_comments")
    int maxComments;

    @SerializedName("topic_id")
    String topicId;

//    @SerializedName("chk_best_answer")
//    ArrayList<Integer> chkBestAnswers;

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public int getLimit_reply() {
        return limit_reply;
    }

    public int getTopic_status() {
        return topic_status;
    }

    public int getMaxComments() {
        return maxComments;
    }

    public String getTopicId() {
        return topicId;
    }

//    public int getChkBestAnswers() {
//        return chkBestAnswers;
//    }
}
