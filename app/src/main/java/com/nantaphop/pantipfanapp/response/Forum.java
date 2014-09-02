package com.nantaphop.pantipfanapp.response;

import android.content.Context;
import com.google.gson.annotations.SerializedName;
import it.gmariotti.cardslib.library.internal.Card;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 7/22/13
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Forum implements Serializable{

    @SerializedName("last_id_current_page")
    String lastIdCurrentPage;

    String more;

    @SerializedName("topic")
    ArrayList<Topic> topics;

    boolean logged_in;

//    ArrayList<String> tagIn;
//    ArrayList<String> tagOut;



    public void addTopics(ArrayList<Topic> newTopics){
        topics.addAll(newTopics);
    }

    public String getLastIdCurrentPage() {
        return lastIdCurrentPage;
    }

    public String getMore() {
        return more;
    }

    public ArrayList<Topic> getTopics() {
        return topics;
    }

    public void setMore(String more) {
        this.more = more;
    }

    public void setLastIdCurrentPage(String lastIdCurrentPage) {
        this.lastIdCurrentPage = lastIdCurrentPage;
    }

    public void setTopics(ArrayList<Topic> topics) {
        this.topics = topics;
    }

}
