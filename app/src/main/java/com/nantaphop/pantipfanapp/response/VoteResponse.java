package com.nantaphop.pantipfanapp.response;

import android.text.Html;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 8/2/13
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class VoteResponse {

    boolean error;
    String error_message;


    int what_vote_type;
    boolean vote_success;
    String vote_message;

    public boolean isError() {
        return error;
    }

    public String getError_message() {
        return error_message;
    }

    public boolean isVote_success() {
        return vote_success;
    }

    public String getVote_message() {
        return Html.fromHtml(vote_message).toString();
    }
}
