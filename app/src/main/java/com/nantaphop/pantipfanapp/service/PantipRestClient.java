package com.nantaphop.pantipfanapp.service;

import android.content.Context;
import android.util.Log;
import com.activeandroid.app.Application;
import com.loopj.android.http.*;
import com.nantaphop.pantipfanapp.response.ForumPart;
import org.androidannotations.annotations.*;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nantaphop on 26-Jul-14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class PantipRestClient {

    @RootContext
    Application app;

    private static final String TAG = "REST";

    private final String BASE_URL = "http://pantip.com/";
    private AsyncHttpClient client = null;

    public static enum ForumType {
        Room {
            @Override
            public String toString() {
                return "room";
            }
        },
        Club {
            @Override
            public String toString() {
                return "club";
            }
        },
        Tag {
            @Override
            public String toString() {
                return "tag";
            }
        }
    }

    ;

    public static enum TopicType {
        All_Except_Sell {
            @Override
            public String toString() {
                return "0";
            }
        },
        Chat {
            @Override
            public String toString() {
                return "1";
            }
        },
        Poll {
            @Override
            public String toString() {
                return "2";
            }
        },
        Question {
            @Override
            public String toString() {
                return "3";
            }
        },
        Review {
            @Override
            public String toString() {
                return "4";
            }
        },
        News {
            @Override
            public String toString() {
                return "5";
            }
        },
        Sell {
            @Override
            public String toString() {
                return "6";
            }
        }
    }

    ;

    public PantipRestClient(Context context) {
        client = new AsyncHttpClient();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        client.addHeader("origin", "http://pantip.com");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Accept-Encoding", "gzip,deflate,sdch");
        client.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");
        client.addHeader("Referer", "http://pantip.com/login?redirect=Lw==");
        client.addHeader("Accept-Language", "en-US,en;q=0.8,th;q=0.6");

    }


    private void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
        Log.d(TAG, "get - " + getAbsoluteUrl(url));

    }

    private void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
        Log.d(TAG, "post - " + getAbsoluteUrl(url));

    }

    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    @Trace
    @Background
    public void login(String username, String password, AsyncHttpResponseHandler cb) {
        RequestParams params = new RequestParams();
        params.put("member[email]", username);
        params.put("member[crypted_password]", password);
        params.put("action", "login");
        params.put("redirect", "");
        params.put("persistent[remember]", "1");
        post("login/authentication", params, cb);
    }

    @Trace
    @Background
    public void comment(String topicId, String msg, AsyncHttpResponseHandler cb) {
        RequestParams params = new RequestParams();
        params.put("topic_id", topicId);
        params.put("msg[raw]", msg);
        params.put("msg[disp]", "msg");
        params.put("type", "1");


        post("forum/topic/save_comment", params, cb);
    }

    @Trace
    @Background
    public void getForum(String forum, ForumType type, TopicType topicType, int currentPage, String lastTopicId, boolean thumbnailView, AsyncHttpResponseHandler cb) {
        int defaultType = topicType == TopicType.All_Except_Sell ? 1 : 0;

        String url;
        if (type == ForumType.Tag) {
            url = "forum/topic/ajax_json_all_topic_tag";
        } else if (type == ForumType.Room) {
            url = "forum/topic/ajax_json_all_topic_info_loadmore";
        } else {
            url = "forum/topic/ajax_json_all_topic_club";
            type = ForumType.Tag;
        }

        // ห้องไร้สังกัด
        if (forum == null) {
            forum = "undefined";
        }

        RequestParams params = new RequestParams();
        params.put(String.format("dataSend[%s]", type), forum);
        params.put("dataSend[topic_type][type]", topicType);
        params.put("dataSend[topic_type][default_type]", defaultType);
        params.put("thumbnailview", thumbnailView);
        params.put("current_page", currentPage);
        params.put("last_id_current_page", lastTopicId);

        client.addHeader("X-Requested-With", "XMLHttpRequest");
        client.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        post(url, params, cb);
        Log.d(TAG, "get forum - " + params.toString());

    }

    @Trace
    @Background
    public void getForumPart(String forumName, AsyncHttpResponseHandler cb) {
        get("forum/" + forumName, null, cb);
        Log.d(TAG, "get forum part - " + forumName);

    }

    @Trace
    @Background
    public void getTopicPost(String topicId, AsyncHttpResponseHandler cb) {
        get("topic/" + topicId, null, cb);
        Log.d(TAG, "get topic post " + topicId);

    }

    @Trace
    @Background
    public void getComments(String topicId, int page, boolean justOwner, AsyncHttpResponseHandler cb) {
        String url;
//        RequestParams params = new RequestParams();

        if (justOwner) {
//            params.add("param", "story");

            url = "forum/topic_mode/render_comments?tid="+topicId+"&type=1&page="+page+"&param=story"+page+"&_="+new Date().getTime();
        } else {
//            params.add("param", "page");
            url = "forum/topic/render_comments?tid="+topicId+"&type=1&page="+page+"&param=page"+page+"&_="+new Date().getTime();
//                url = "forum/topic/render_comments?tid="+topicId+"&param=&type=1&time=0.23428448126651347&_="+new Date().getTime();
        }

//        params.add("tid", topicId);
//        params.add("type", "1");
//        params.add("page", page+"");
//        params.add("_", new Date().getTime()+"");

//        get(url, params, cb);
        get(url, null, cb);
//        Log.d(TAG, "get comments - " + params.toString());
        Log.d(TAG, "get comments - " + url);

    }


}
