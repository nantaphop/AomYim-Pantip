package com.nantaphop.pantipfanapp.event;

import com.nantaphop.pantipfanapp.service.PantipRestClient;

/**
 * Created by nantaphop on 19-Jan-15.
 */
public class OpenUserEvent {
    int userId;
    String username;
    String avatar;

    public OpenUserEvent(int userId, String username, String avatar) {
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
