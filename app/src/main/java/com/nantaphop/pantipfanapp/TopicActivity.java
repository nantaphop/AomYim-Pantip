package com.nantaphop.pantipfanapp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import com.nantaphop.pantipfanapp.event.SortCommentEvent;
import com.nantaphop.pantipfanapp.fragment.TopicFragment_;
import com.nantaphop.pantipfanapp.fragment.dialog.ListDialog;
import com.nantaphop.pantipfanapp.fragment.dialog.ListDialog_;
import com.nantaphop.pantipfanapp.response.Comment;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.utils.CommentComparator;
import com.squareup.otto.Subscribe;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nantaphop on 24-Oct-14.
 */
@EActivity(R.layout.activity_fragment)
public class TopicActivity extends ActionBarActivity {

    @ViewById
    Toolbar toolbar;
    @ViewById
    FrameLayout contentFrame;
    @App
    BaseApplication app;

    @Extra
    Topic topic;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.scale_from_bottom_center, R.anim.fragment_exit_slide_to_bottom);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.content_frame, TopicFragment_.builder().topic(topic).build(), null)
                                   .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.getEventBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        app.getEventBus().unregister(this);
    }


}