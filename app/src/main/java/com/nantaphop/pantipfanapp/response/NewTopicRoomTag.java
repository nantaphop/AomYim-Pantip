package com.nantaphop.pantipfanapp.response;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nantaphop on 18-Oct-14.
 */
public class NewTopicRoomTag {
    private List<Room> rooms = new ArrayList<Room>();
    @Expose
    private List<RoomTag> tags = new ArrayList<RoomTag>();

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<RoomTag> getTags() {
        return tags;
    }

    public void setTags(List<RoomTag> tags) {
        this.tags = tags;
    }
}
