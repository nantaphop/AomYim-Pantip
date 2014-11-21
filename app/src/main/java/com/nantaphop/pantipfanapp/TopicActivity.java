package com.nantaphop.pantipfanapp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.nantaphop.pantipfanapp.fragment.TopicFragment;
import com.nantaphop.pantipfanapp.fragment.TopicFragment_;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.view.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;

/**
 * Created by nantaphop on 24-Oct-14.
 */
@EActivity(R.layout.activity_fragment)
public class TopicActivity extends BaseActivity {


    @ViewById
    Toolbar toolbar;
    @ViewById
    FrameLayout contentFrame;
    @App
    BaseApplication app;

    @Extra
    Topic topic;

    @NonConfigurationInstance
    TopicFragment topicFragment;
    @ViewById
    View toolbarPadding;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @AfterViews
    void init() {
        toolbarPadding.setVisibility(View.GONE);
        if (topicFragment == null) {
            topicFragment = TopicFragment_.builder().topic(topic).build();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, topicFragment, null)
                    .commit();
        }
        setSupportActionBar(toolbar);

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
