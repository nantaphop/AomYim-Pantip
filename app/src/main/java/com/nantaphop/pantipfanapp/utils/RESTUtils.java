package com.nantaphop.pantipfanapp.utils;

import android.util.Log;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.ForumPart;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Formatter;

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

}
