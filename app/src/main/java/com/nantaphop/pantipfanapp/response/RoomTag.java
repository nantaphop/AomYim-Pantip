package com.nantaphop.pantipfanapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nantaphop on 18-Oct-14.
 */
public class RoomTag {

    @SerializedName("tag_id")
    @Expose
    private Integer tagId;
    @SerializedName("tag_name")
    @Expose
    private String tagName;
    @SerializedName("room_id")
    @Expose
    private Integer roomId;
    @Expose
    private String parent;

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

}