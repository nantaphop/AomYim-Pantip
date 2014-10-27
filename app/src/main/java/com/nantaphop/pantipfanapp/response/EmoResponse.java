package com.nantaphop.pantipfanapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 8/7/13
 * Time: 9:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmoResponse {

    @Expose
    private String status;
    @Expose
    private Emotion emotion;

    public String getStatus() {
        return status;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public class Emotion {

        @SerializedName("emo_type")
        @Expose
        private String emoType;
        @Expose
        private Boolean check;
        @Expose
        private String type;

        public String getEmoType() {
            return emoType;
        }

        public void setEmoType(String emoType) {
            this.emoType = emoType;
        }

        public Boolean getCheck() {
            return check;
        }

        public void setCheck(Boolean check) {
            this.check = check;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }
}

