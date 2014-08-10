package com.nantaphop.pantipfanapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
//            Intent i = new Intent(this, TopicActivity_.class);
//            i.putExtra(TopicActivity.EXTRA_URL, uri.toString());
//            startActivity(i);
            return;
        }
        else if(view.equals("forum") || view.equals("tag")){
//            Intent i = new Intent(this, MainActivity_.class);
//            i.putExtra(TopicActivity.EXTRA_URL, uri.toString());
//            startActivity(i);
            return;
        }


    }
}
