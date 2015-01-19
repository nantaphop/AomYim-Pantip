package com.nantaphop.pantipfanapp.service;

import android.content.Context;
import android.util.Log;

import com.activeandroid.app.Application;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.nantaphop.pantipfanapp.pref.UserPref_;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.http.client.params.ClientPNames;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by nantaphop on 26-Jul-14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class PantipRestClient {

    @RootContext
    Application app;

    @Pref
    UserPref_ userPref;

    private static final String TAG = "REST";

    private final String BASE_URL = "http://pantip.com/";
    private AsyncHttpClient client = null;
    private Context context;

    public static enum UserTopicType {
        Topic {
            @Override
            public String toString() {
                return "topic";
            }
        },
        Comment {
            @Override
            public String toString() {
                return "comment";
            }
        },
        Bookmarks {
            @Override
            public String toString() {
                return "bookmarks";
            }
        }
    }

    ;

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
        forumType, Tag {
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

    public static enum Emo {
        Like {
            @Override
            public String toString() {
                return "like";
            }
        },
        Laugh {
            @Override
            public String toString() {
                return "laugh";
            }
        },
        Love {
            @Override
            public String toString() {
                return "love";
            }
        },
        Impress {
            @Override
            public String toString() {
                return "impress";
            }
        },
        Scary {
            @Override
            public String toString() {
                return "scary";
            }
        },
        Surprised {
            @Override
            public String toString() {
                return "surprised";
            }
        }
    }

    ;

    public static enum VoteType {
        Topic {
            @Override
            public String toString() {
                return "1";
            }
        },
        Comment {
            @Override
            public String toString() {
                return "2";
            }
        },
        Reply {
            @Override
            public String toString() {
                return "3";
            }
        }
    }

    ;

    public PantipRestClient(Context context) {
        this.context = context;
        client = new AsyncHttpClient();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        client.setMaxRetriesAndTimeout(2, 10);
        client.addHeader("origin", "http://pantip.com");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("X-Requested-With", "XMLHttpRequest");
        client.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        client.addHeader("Accept-Encoding", "gzip,deflate,sdch");
        client.addHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36"
        );
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
        logout();
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        RequestParams params = new RequestParams();
        params.put("member[email]", username);
        params.put("member[crypted_password]", password);
        params.put("action", "login");
        params.put("redirect", "");
        params.put("persistent[remember]", "1");
        post("login/authentication", params, cb);
    }

    public void logout() {
        userPref.clear();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        myCookieStore.clear();
        client.setCookieStore(myCookieStore);
    }

    @Trace
    @Background
    public void reply(int topicId, int commentRefId, int commentNo, long commentTimestamp, String msg, AsyncHttpResponseHandler cb) {
        RequestParams params = new RequestParams();
        params.put("topic_id", topicId);
        params.put("msg[raw]", msg);
        params.put("msg[disp]", "msg");
        params.put("msg[time]", commentTimestamp);
        params.put("type", "1");
        params.put("msg[ref_id]", commentRefId + "pantip3g");
        params.put("msg[ref_comment]", "comment" + commentNo);


        post("forum/topic/save_reply", params, cb);
    }

    @Trace
    @Background
    public void comment(int topicId, String msg, AsyncHttpResponseHandler cb) {
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


        post(url, params, cb);
        Log.d(TAG, "get forum - " + params.toString());

    }

    @Trace
    @Background
    public void getForumPart(String forumName, ForumType type, AsyncHttpResponseHandler cb) {
        try {
            forumName = URLEncoder.encode(forumName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url;
        if (type == ForumType.Tag) {
            url = "tag/";
        } else if (type == ForumType.Room) {
            url = "forum/";
        } else {
            url = "club/";
            type = ForumType.Club;
        }
        get(url + forumName, null, cb);
        Log.d(TAG, "get forum part - " + forumName);

    }

    @Background
    public void getUserTopic(int userId, UserTopicType userTopicType, int page, long first_id, long last_id, AsyncHttpResponseHandler cb) {
        String url = String.format("profile/me/ajax_my_%s?type=%s&mid=%d&p=%d&ftid=%d&ltid=%d"
                , userTopicType.toString()
                , userTopicType.toString()
                , userId
                , page
                , first_id
                , last_id);
        get(url, null, cb);
        Log.d(TAG, "get user " + userId + " " + userTopicType);
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

        if (justOwner) {

            url = "forum/topic_mode/render_comments?tid=" + topicId + "&type=1&page=" + page + "&param=story" + page + "&_=" + new Date()
                    .getTime();
        } else {
            url = "forum/topic/render_comments?tid=" + topicId + "&type=1&page=" + page + "&param=page" + page + "&_=" + new Date()
                    .getTime();
        }
        get(url, null, cb);
        Log.d(TAG, "get comments - " + url);
    }

    @Trace
    @Background
    public void getReplies(int commentId, int lastReply, int maxReplyCount, int parentCommentUserId, AsyncHttpResponseHandler cb) {
        String url = "http://pantip.com/forum/topic/render_replys?last=" + lastReply + "&cid=" + commentId + "&c=" + maxReplyCount + "&ac=n&o=" + parentCommentUserId;
        get(url, null, cb);
        Log.d(TAG, "get repiles - " + commentId);
    }

    @Trace
    @Background
    public void voteComment(int topic_id, int comment_id, int commentNo, AsyncHttpResponseHandler cb) {
        client.addHeader("X-Requested-With", "XMLHttpRequest");
        RequestParams params = new RequestParams();
        params.add("vote_status", "1");
        params.add("vote_type", VoteType.Comment.toString());
        params.add("topic_id", topic_id + "");
        params.add("comment_id", comment_id + "");
        params.add("comment_no", commentNo + "");

        post("vote1/cal_like", params, cb);
    }

    @Trace
    @Background
    public void voteReply(int topic_id, int comment_id, int commentNo, int reply_id, int replyNo, AsyncHttpResponseHandler cb) {
        client.addHeader("X-Requested-With", "XMLHttpRequest");
        RequestParams params = new RequestParams();
        params.add("vote_status", "1");
        params.add("vote_type", VoteType.Reply.toString());
        params.add("topic_id", topic_id + "");
        params.add("cid", comment_id + "");
        params.add("comment_no", commentNo + "");
        params.add("rp_id", reply_id + "");
        params.add("rp_no", replyNo + "");

        post("vote1/cal_like", params, cb);
    }

    @Trace
    @Background
    public void voteTopic(int topic_id, AsyncHttpResponseHandler cb) {
        client.addHeader("X-Requested-With", "XMLHttpRequest");
        RequestParams params = new RequestParams();
        params.add("vote_status", "1");
        params.add("vote_type", VoteType.Topic.toString());
        params.add("topic_id", topic_id + "");
        post("vote1/cal_like", params, cb);
    }

    @Trace
    @Background
    public void emoTopic(int topic_id, Emo emo, AsyncHttpResponseHandler cb) {
        client.addHeader("X-Requested-With", "XMLHttpRequest");
        RequestParams params = new RequestParams();
        params.add("id", topic_id + "");
        params.add("topic_id", topic_id + "");
        params.add("type", "topic");
        params.add("emo", emo.toString());

        post("forum/topic/express_emotion", params, cb);
    }

    @Trace
    @Background
    public void emoComment(int topic_id, int comment_id, Emo emo, AsyncHttpResponseHandler cb) {
        client.addHeader("X-Requested-With", "XMLHttpRequest");
        RequestParams params = new RequestParams();
        params.add("id", comment_id + "");
        params.add("topic_id", topic_id + "");
        params.add("type", "comment");
        params.add("emo", emo.toString());

        post("forum/topic/express_emotion", params, cb);
    }

    @Trace
    @Background
    public void emoReply(int topic_id, int comment_id, int reply_id, int comment_no, int reply_no, Emo emo, AsyncHttpResponseHandler cb) {
        client.addHeader("X-Requested-With", "XMLHttpRequest");
        RequestParams params = new RequestParams();
        params.add("id", reply_id + "");
        params.add("rid", comment_id + "");
        params.add("topic_id", topic_id + "");
        params.add("type", "reply");
        params.add("emo", emo.toString());
        params.add("comment_no", comment_no + "");
        params.add("no", reply_no + "");

        post("forum/topic/express_emotion", params, cb);
    }

    @Trace
    @Background
    public void getRoomsTags(AsyncHttpResponseHandler cb) {
        RequestParams params = new RequestParams();
        post("forum/new_topic/get_rooms_tags", params, cb);
    }

    @Trace
    @Background
    public void saveTopic(AsyncHttpResponseHandler cb) {
        RequestParams params = new RequestParams();
        post("forum/new_topic/save", params, cb);
    }


}
