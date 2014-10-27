package com.nantaphop.pantipfanapp.response;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by nantaphop on 25-Oct-14.
 */
public class Emotion implements Serializable{

    @Expose
    private Integer sum;
    @Expose
    private Like like;
    @Expose
    private Laugh laugh;
    @Expose
    private Love love;
    @Expose
    private Impress impress;
    @Expose
    private Scary scary;
    @Expose
    private Surprised surprised;

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public Like getLike() {
        return like;
    }

    public void setLike(Like like) {
        this.like = like;
    }

    public Laugh getLaugh() {
        return laugh;
    }

    public void setLaugh(Laugh laugh) {
        this.laugh = laugh;
    }

    public Love getLove() {
        return love;
    }

    public void setLove(Love love) {
        this.love = love;
    }

    public Impress getImpress() {
        return impress;
    }

    public void setImpress(Impress impress) {
        this.impress = impress;
    }

    public Scary getScary() {
        return scary;
    }

    public void setScary(Scary scary) {
        this.scary = scary;
    }

    public Surprised getSurprised() {
        return surprised;
    }

    public void setSurprised(Surprised surprised) {
        this.surprised = surprised;
    }

    public boolean isAlreadyAction(){
        return (like.getStatus()!=null || laugh.getStatus()!=null || love.getStatus()!=null || impress.getStatus()!=null || scary.getStatus()!=null || surprised.getStatus()!=null);
    }

    public class Impress implements Serializable{

        @Expose
        private Integer count;
        @Expose
        private Boolean status;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

    }

    public class Laugh  implements Serializable{

        @Expose
        private Integer count;
        @Expose
        private Boolean status;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

    }

    public class Like implements Serializable {

        @Expose
        private Integer count;
        @Expose
        private Boolean status;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

    }

    public class Love  implements Serializable{

        @Expose
        private Integer count;
        @Expose
        private Boolean status;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

    }

    public class Scary  implements Serializable{

        @Expose
        private Integer count;
        @Expose
        private Boolean status;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

    }

    public class Surprised implements Serializable {

        @Expose
        private Integer count;
        @Expose
        private Boolean status;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

    }
}
