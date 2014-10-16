package com.nantaphop.pantipfanapp.event;

/**
 * Created by nantaphop on 16-Oct-14.
 */
public class DoReplyEvent {
    public int commentRefId;
    public int commentNo;
    public long commentTimestamp;

    public DoReplyEvent(int commentRefId, int commentNo, long commentTimestamp) {
        this.commentRefId = commentRefId;
        this.commentNo = commentNo;
        this.commentTimestamp = commentTimestamp;
    }
}
