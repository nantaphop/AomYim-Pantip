package com.nantaphop.pantipfanapp.utils;

import android.util.Log;

import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.pref.UserPref_;
import com.nantaphop.pantipfanapp.response.Comment;
import com.nantaphop.pantipfanapp.response.CommentResponse;
import com.nantaphop.pantipfanapp.response.Comments;
import com.nantaphop.pantipfanapp.response.EmoResponse;
import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.ForumPart;
import com.nantaphop.pantipfanapp.response.MyPage;
import com.nantaphop.pantipfanapp.response.Reply;
import com.nantaphop.pantipfanapp.response.TopicPost;
import com.nantaphop.pantipfanapp.response.VoteResponse;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

/**
 * Created by nantaphop on 27-Jul-14.
 */
public class RESTUtils {

    public static Forum parseForum(String resp) {
        try {
            return BaseApplication.getGson().fromJson(new JSONObject(resp).get("item").toString(), Forum.class);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MyPage parseUserForum(String resp) {
        try {
            return BaseApplication.getGson().fromJson(new JSONObject(resp).toString(), MyPage.class);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ForumPart parseForumPart(String pageHtml) {
        Document doc = Jsoup.parse(pageHtml);
        ForumPart forumPart = new ForumPart();


        // get Recommend
        ArrayList<String> recList = new ArrayList<String>();
        ArrayList<String> recUrl = new ArrayList<String>();

        Elements temp = doc.select("div.best-item");

        for (Element e : temp) {
            Elements a = e.select("a");
            if(a.size() > 0) {
                Element recommendTopic = a.get(0);
                recUrl.add("http://pantip.com" + recommendTopic.attr("href"));
                recList.add(recommendTopic.text());
            }else{
                recList.add(e.text());
                recUrl.add("");
            }
        }

        forumPart.setRecommendTopic(recList);
        forumPart.setRecommendUrl(recUrl);

        // get Tag
        ArrayList<String> tagList = new ArrayList<String>();
        ArrayList<String> tagUrl = new ArrayList<String>();

        temp = doc.select("#tag-filter-container-topic div.tag-item a.tag-title");

        for (Element e : temp) {
            tagList.add(e.text());
            tagUrl.add("http://pantip.com" + e.attr("href"));
        }

        forumPart.setTag(tagList);
        forumPart.setTagUrl(tagUrl);

        // get Club

        ArrayList<String> clubList = new ArrayList<String>();
        ArrayList<String> clubUrl = new ArrayList<String>();

        temp = doc.select("div.section-club div.item_club a");
        for (Element e : temp) {
            clubList.add(e.text());
            clubUrl.add("http://pantip.com" + e.attr("href"));
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

        long duration = (System.currentTimeMillis() - start);
        return b.toString();
    }

    public static TopicPost parseTopicPost(String topicPageHtml) {
        TopicPost topicPost = new TopicPost();
        Document doc = Jsoup.parse(topicPageHtml);

        Elements title = doc.select("h2.display-post-title");
        topicPost.setTitle(title.get(0).text());

        Element body = doc.select("div.display-post-story").get(0);
        body.select("script").remove();

        // Add Image Link
        Elements imgs = body.select("img.img-in-post");
        for (Element img : imgs) {
            img.before("<a href=\"" + img.attr("src") + "\">ดูภาพใหญ่<br/>");
            img.after("</a>");

        }

        // Add Youtube Link
        imgs = body.select("a.play_btn");
        for (Element a : imgs) {
            a.before("<br/>");
            a.after("<br/><a href=\"" + a.attr("href") + "\">ดู Video</a>");
        }


        topicPost.setBody(body.html());
        topicPost.setVotes(Integer.parseInt(doc.select("span.like-score ").get(0).text()));
        topicPost.setEmotions(Integer.parseInt(doc.select("span.emotion-score").get(0).text()));
        topicPost.setAuthor(doc.select("a.display-post-name").get(0).text());
        topicPost.setUserId(Integer.parseInt(doc.select("div.display-post-avatar a").get(0).attr("href").split("/")[4]));
        topicPost.setVoted(doc.select("a.icon-heart-like.i-vote").size() > 0);
        topicPost.setEmoted(doc.select("a.emotion-choice-icon.i-vote").size() > 0);
        String avatar = doc.select("div.display-post-avatar a img").get(0).attr("src");
        if (avatar.startsWith("/images"))
            avatar = "http://pantip.com" + avatar;
        topicPost.setAuthorPic(avatar);

        Element dateEle = doc.select("abbr.timeago").get(0);
        try {
            topicPost.setDate(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH).parse(dateEle.attr("data-utime")));
        } catch (ParseException e) {
            topicPost.setDate(new Date());
        }
        topicPost.setDateString(dateEle.attr("title"));

        return topicPost;
    }

    public static Comments parseComments(String resp) {
        return BaseApplication.getGson().fromJson(resp, Comments.class);
    }

    public static Reply parseReplies(String resp) {
        return BaseApplication.getGson().fromJson(resp, Reply.class);
    }

    public static CommentResponse parseCommentResp(String resp) {
        Log.d("resp", resp);
        return BaseApplication.getGson().fromJson(resp, CommentResponse.class);
    }

    public static VoteResponse parseVoteResp(String resp) {
        Log.d("resp", resp);
        return BaseApplication.getGson().fromJson(resp, VoteResponse.class);
    }

    public static EmoResponse parseEmoResp(String resp) {
        Log.d("resp", resp);
        return BaseApplication.getGson().fromJson(resp, EmoResponse.class);
    }

    public static void processComment(Comment c) {
        Document doc = Jsoup.parse(c.getMessage());

        Elements imgs = doc.select("img.img-in-post");
        for (Element img : imgs) {
            img.before("<br/>");
            img.after("<br/><a href=\"" + img.attr("src") + "\">ดูภาพใหญ่</a>");
        }

        // Add Youtube Link
        imgs = doc.select("a.play_btn");
        for (Element a : imgs) {
            a.before("<br/>");
            a.after("<br/><a href=\"" + a.attr("href") + "\">ดู Video</a>");
        }

        c.setMessage(doc.html());
    }

    public static boolean isLogin(Header[] headers) {
        for (Header h : headers) {
            if (h.getName().equalsIgnoreCase("Connection") &&
                    h.getValue().equalsIgnoreCase("keep-alive")) {
                return true;
            }
        }
        return false;
    }

    public static boolean parseUserInfo(byte[] httpBody, UserPref_ userPref) {
        try {
            Document doc = Jsoup.parse(new String(httpBody, "utf-8"));
            String title = doc.select("title").get(0).text().replace("หน้าของ ", "").replace(" - Pantip", "");
            String avatar = doc.select("img.big-avatar").get(0).attr("src");
            int userId = Integer.parseInt(doc.select("div.profile-follow").get(0).attr("id").substring(2));
            if (avatar.startsWith("/images"))
                avatar += "http://pantip.com" + avatar;
            Log.d("login", "User login - " + title + " : " + avatar);
            userPref.edit().username().put(title).avatar().put(avatar).userId().put(userId).apply();
            return true;
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }
}
