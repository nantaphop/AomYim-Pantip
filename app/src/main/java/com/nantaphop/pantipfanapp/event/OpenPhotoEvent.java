package com.nantaphop.pantipfanapp.event;

/**
 * Created by nantaphop on 23-Jan-15.
 */
public class OpenPhotoEvent {
    String photoUrl;

    public OpenPhotoEvent(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
