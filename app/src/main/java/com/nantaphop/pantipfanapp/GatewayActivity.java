package com.nantaphop.pantipfanapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.nantaphop.pantipfanapp.model.ForumPagerItem;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import com.nantaphop.pantipfanapp.utils.AnalyticsUtils;
import com.nantaphop.pantipfanapp.utils.Utils;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;


import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 8/14/13
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
@EActivity
public class GatewayActivity extends Activity {

    public static final String TAG = "PantipFanApp";

    @App
    public BaseApplication app;

    @Override
    public void onStart() {
        super.onStart();
//        EasyTracker.getInstance().activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
//        EasyTracker.getInstance().activityStop(this); // Add this method.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        List<String> pathSegments = uri.getPathSegments();

//        app.getTracker().sendEvent("user_action", "open_from_external", uri.toString(), null);



        // First Segment is { tag, forum, topic}

        // Second Segment is {tagName, forumName, topicId}
        String view = pathSegments.get(0);
        String detail = pathSegments.get(1);

        if(view.equals("topic")){
            AnalyticsUtils.sendEvent(AnalyticsUtils.CATEGORY_USER_ACTION, AnalyticsUtils.ACTION_IN_LINK, "Topic");
            final Intent i = new Intent(this, TopicActivity_.class);
            Topic topic = new Topic();
            topic.setId(Integer.parseInt(uri.toString().split("/")[4]));
            topic.setTitle(uri.toString());
            i.putExtra("topic", topic);
            startActivity(i);
            Log.d("fragment", "open topic");
            return;
        }
        else if(view.equals("forum") || view.equals("tag")){
            AnalyticsUtils.sendEvent(AnalyticsUtils.CATEGORY_USER_ACTION, AnalyticsUtils.ACTION_IN_LINK, "Forum");
            Intent i = new Intent(this, ForumActivity_.class);
            ForumPagerItem forumPagerItem = new ForumPagerItem(uri.toString(), Utils.getForumPath(uri.toString()));
            i.putExtra("forumPagerItem", forumPagerItem);
            if(view.equals("forum"))
                i.putExtra("forumType", PantipRestClient.ForumType.Room);
            else if(view.equals("tag"))
                i.putExtra("forumType", PantipRestClient.ForumType.Tag);
            startActivity(i);
            return;
        }


    }
}
