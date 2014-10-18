package com.nantaphop.pantipfanapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 7/23/13
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Comment implements Serializable {

    public void setUser(User user) {
        this.user = user;
    }

    @SerializedName("_id")
    int id;
    int comment_id;
    int comment_parent_id;
    Comment parent;
    int comment_no;
    int reply_no;
    int reply_id;

    public int getReply_id() {
        return reply_id;
    }

    @SerializedName("good_bad_vote")
    @Expose
    private GoodBadVote goodBadVote;
    int emo_score;
    int point;
    String message; // TODO: Have to remove javascript
    int status;
    boolean owner_topic;
    boolean reply;
    int lastReply;
    boolean showLoadMore;
    @SerializedName("data_utime")
    Date date;
    ArrayList<Comment> replies;
    int reply_count;
    User user;
    private int parent_max_reply;

    public int getComment_id() {
        return comment_id;
    }

    public boolean isShowLoadMore() {
        return showLoadMore;
    }

    public void setShowLoadMore(boolean showLoadMore) {
        this.showLoadMore = showLoadMore;
    }

    public int getId() {
        return id;
    }

    public int getComment_no() {
        return comment_no;
    }

    public int getEmo_score() {
        return emo_score;
    }

    public void setEmo_score(int emo_score) {
        this.emo_score = emo_score;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public boolean isOwner_topic() {
        return owner_topic;
    }

    public Date getDate() {
        return date;
    }

    public boolean isReply() {
        return reply;
    }

    public void setReply(boolean reply) {
        this.reply = reply;
    }

    public ArrayList<Comment> getReplies() {
        return replies;
    }

    public int getReply_count() {
        return reply_count;
    }

    public User getUser() {
        return user;
    }

    public int getLastReply() {
        return lastReply;
    }

    public void setLastReply(int lastReply) {
        this.lastReply = lastReply;
    }

    public int getParent_max_reply() {
        return parent_max_reply;
    }

    public void setParent_max_reply(int parent_max_reply) {
        this.parent_max_reply = parent_max_reply;
    }

    public int getComment_parent_id() {
        return comment_parent_id;
    }

    public void setComment_parent_id(int comment_parent_id) {
        this.comment_parent_id = comment_parent_id;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public Reply addReplies(Reply reply) {
        for ( Comment comment : reply.getReplies() ) {
            comment.setReply(true);
            comment.parent = this;
            comment.setComment_parent_id(id);
            comment.setParent_max_reply(reply_count);
        }


        replies.addAll(reply.getReplies());
        this.setLastReply(replies.get(replies.size() - 1).getReply_no());

        return reply;

    }

    public boolean isVoted() {
        return this.getGoodBadVote().getGoodVoted().equals(GoodBadVote.I_VOTE);
    }

    public void setVoted() {
        this.getGoodBadVote().setGoodVoted(GoodBadVote.I_VOTE);
    }

    public void initReplies() {
        for ( Comment comment : getReplies() ) {
            comment.setReply(true);
        }
    }

    public GoodBadVote getGoodBadVote() {
        return goodBadVote;
    }

    public void setGoodBadVote(GoodBadVote goodBadVote) {
        this.goodBadVote = goodBadVote;
    }

    public int getReply_no() {
        return reply_no;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class GoodBadVote {

        public static final String I_VOTE = "i-vote";

        @SerializedName("good_voted")
        @Expose
        private String goodVoted;
        @SerializedName("bad_voted")
        @Expose
        private String badVoted;
        @Expose
        private Integer point;
        @SerializedName("class_score")
        @Expose
        private String classScore;

        public String getGoodVoted() {
            return goodVoted;
        }

        public void setGoodVoted(String goodVoted) {
            this.goodVoted = goodVoted;
        }

        public String getBadVoted() {
            return badVoted;
        }

        public void setBadVoted(String badVoted) {
            this.badVoted = badVoted;
        }

        public Integer getPoint() {
            return point;
        }

        public void setPoint(Integer point) {
            this.point = point;
        }

        public String getClassScore() {
            return classScore;
        }

        public void setClassScore(String classScore) {
            this.classScore = classScore;
        }

    }
}
