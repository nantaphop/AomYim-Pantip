package com.nantaphop.pantipfanapp.response;

/**
 * Created by nantaphop on 11-Sep-14.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentResponse {

    @Expose
    private String status;
    @SerializedName("comment_no")
    @Expose
    private String commentNo;
    @SerializedName("comment_id")
    @Expose
    private int commentId;
    @SerializedName("reply_no")
    @Expose
    private String replyNo;
    @SerializedName("reply_id")
    @Expose
    private int replyId;
    @SerializedName("topic_id")
    @Expose
    private String topicId;
    @SerializedName("error")
    @Expose
    private boolean error;
    @SerializedName("error_message")
    @Expose
    private String errorMessage;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCommentNo() {
        return commentNo;
    }

    public void setCommentNo(String commentNo) {
        this.commentNo = commentNo;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}