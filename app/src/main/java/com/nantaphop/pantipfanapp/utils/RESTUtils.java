package com.nantaphop.pantipfanapp.utils;

import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.response.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by nantaphop on 27-Jul-14.
 */
public class RESTUtils {

    public static Forum parseForum(String resp){
        try {
            return BaseApplication.getGson().fromJson(new JSONObject(resp).get("item").toString(), Forum.class);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ForumPart parseForumPart(String pageHtml){
        Document doc = Jsoup.parse(pageHtml);
        ForumPart forumPart = new ForumPart();


        // get Recommend
        ArrayList<String> recList = new ArrayList<String>();
        ArrayList<String> recUrl = new ArrayList<String>();

        Elements temp = doc.select("div.best-item a");
        for(Element e: temp){
            recUrl.add("http://pantip.com"+e.attr("href"));
            recList.add(e.text());
        }

        forumPart.setRecommendTopic(recList);
        forumPart.setRecommendUrl(recUrl);

        // get Tag
        ArrayList<String> tagList = new ArrayList<String>();
        ArrayList<String> tagUrl = new ArrayList<String>();

        temp = doc.select("#tag-filter-container-topic div.tag-item a.tag-title");

        for (Element e: temp){
            tagList.add(e.text());
            tagUrl.add("http://pantip.com"+e.attr("href"));
        }

        forumPart.setTag(tagList);
        forumPart.setTagUrl(tagUrl);

        // get Club

        ArrayList<String> clubList = new ArrayList<String>();
        ArrayList<String> clubUrl = new ArrayList<String>();

        temp = doc.select("div.section-club div.item_club a");
        for (Element e: temp){
            clubList.add(e.text());
            clubUrl.add("http://pantip.com"+e.attr("href"));
        }

        forumPart.setClub(clubList);
        forumPart.setClubUrl(clubUrl);

        return forumPart;
    }

    public static String escapeUnicode(String input) {

        long start = System.currentTimeMillis();

        StringBuilder b = new StringBuilder(input.length());
        Formatter f = new Formatter(b);
        for (char c : input.toCharArray()) {
            if (c < 128) {
                b.append(c);
            } else {
                f.format("\\u0e[0-9a-z][0-9a-z]", (int) c);
            }
        }

        long duration = (System.currentTimeMillis()-start);
        return b.toString();
    }

    public static TopicPost parseTopicPost(String topicPageHtml){
        TopicPost topicPost = new TopicPost();
        Document doc = Jsoup.parse(topicPageHtml);

        Elements title = doc.select("h2.display-post-title");
        topicPost.setTitle(title.get(0).text());

        Element body = doc.select("div.display-post-story").get(0);
        body.select("script").remove();

        // Add Image Link
        Elements imgs = body.select("img.img-in-post");
        for(Element img: imgs){
            img.before("<br/>");
            img.after("<br/><a href=\""+ img.attr("src") +"\">ดูภาพใหญ่</a>");
        }

        // Add Youtube Link
        imgs = body.select("a.play_btn");
        for(Element a: imgs){
            a.before("<br/>");
            a.after("<br/><a href=\""+ a.attr("href") +"\">ดู Video</a>");
        }


        topicPost.setBody(body.html());
        topicPost.setVotes(Integer.parseInt(doc.select("span.like-score ").get(0).text()));
        topicPost.setEmotions(Integer.parseInt(doc.select("span.emotion-score").get(0).text()));
        topicPost.setAuthor(doc.select("a.display-post-name").text());

        Element dateEle = doc.select("abbr.timeago").get(0);
        try {
            topicPost.setDate(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH).parse(dateEle.attr("data-utime")));
        } catch (ParseException e) {
            topicPost.setDate(new Date());
        }
        topicPost.setDateString(dateEle.attr("title"));

        return topicPost;
    }

    public static Comments parseComments(String resp){
        return BaseApplication.getGson().fromJson(resp, Comments.class);
    }

    public static Reply parseReplies(String resp){
        return BaseApplication.getGson().fromJson(resp, Reply.class);
    }

    public static void processComment(Comment c) {
        Document doc = Jsoup.parse(c.getMessage());

        Elements imgs = doc.select("img.img-in-post");
        for(Element img: imgs){
            img.before("<br/>");
            img.after("<br/><a href=\""+ img.attr("src") +"\">ดูภาพใหญ่</a>");
        }

        // Add Youtube Link
        imgs = doc.select("a.play_btn");
        for(Element a: imgs){
            a.before("<br/>");
            a.after("<br/><a href=\""+ a.attr("href") +"\">ดู Video</a>");
        }

        c.setMessage(doc.html());
    }

}
