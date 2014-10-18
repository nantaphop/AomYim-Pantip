package com.nantaphop.pantipfanapp.response;

/**
 * Created by nantaphop on 18-Oct-14.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Room {

    @SerializedName("room_id")
    @Expose
    private Integer roomId;
    @SerializedName("room_name")
    @Expose
    private String roomName;

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

}