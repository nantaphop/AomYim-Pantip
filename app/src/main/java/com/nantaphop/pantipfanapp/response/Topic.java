package com.nantaphop.pantipfanapp.response;

import android.content.Context;
import com.google.gson.annotations.SerializedName;
import com.nantaphop.pantipfanapp.view.TopicCard;
import it.gmariotti.cardslib.library.internal.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 7/22/13
 * Time: 5:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class Topic implements Serializable {

    @SerializedName("cover_img")
    String coverImg;

    Boolean brightCover;


    @SerializedName("_id")
    int id;

    @SerializedName("topic_id")
    int id2;

    @SerializedName("disp_topic")
    String title;

    int comments;
    int votes;


    @SerializedName("utime")
    Date date;

    @SerializedName("topic_type")
    int topicType;

    @SerializedName("abbr_title")
    String dateString;

    String author;

    boolean read;

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    ArrayList<Tag> tags;

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public int getId() {
        // id2 is topic when operate with user topic like bookmark etc.
        return id2 > 0 ? id2 : id;
    }

    public Boolean isBrightCover() {
        return brightCover;
    }

    public void setBrightCover(Boolean brightCover) {
        this.brightCover = brightCover;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTopicType() {
        return topicType;
    }

    public void setTopicType(int topicType) {
        this.topicType = topicType;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return title + " - " + author;
    }

    public Card toCard(Context context) {
        return new TopicCard(context, this);
    }


}
