package com.nantaphop.pantipfanapp.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.nantaphop.pantipfanapp.response.Topic;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by nantaphop on 20-Jan-15.
 */
@Table(name = "ReadLogs")
public class ReadLog extends Model implements Serializable {

    @Column
    public String coverImg;
    @Column(name = "topicid")
    public int topic_id;
    @Column
    public String title;
    @Column
    public long date;
    @Column
    public String sdate;
    @Column
    public int topicType;
    @Column
    public String author;
    @Column
    public long readTime;

    public ReadLog() {
    }

    public ReadLog(int topic_id, String title, long readTime) {
        this.topic_id = topic_id;
        this.title = title;
        this.readTime = readTime;
    }

    public ReadLog(Topic t, long readTime){
        topic_id = t.getId();
        coverImg = t.getCoverImg();
        title = t.getTitle();
        Date d = t.getDate();
        if(d!=null){
            date = d.getTime();
        }
        sdate = t.getDateString();
        topicType = t.getTopicType();
        author = t.getAuthor();
        this.readTime = readTime;
    }

    public static boolean isRead(int topic_id){
        Model model = new Select().from(ReadLog.class).where("topicid = ?", topic_id).executeSingle();
        return model != null;
    }
}
