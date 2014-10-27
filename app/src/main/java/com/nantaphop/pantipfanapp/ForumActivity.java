package com.nantaphop.pantipfanapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;
import com.nantaphop.pantipfanapp.event.OpenTopicEvent;
import com.nantaphop.pantipfanapp.fragment.ForumFragment;
import com.nantaphop.pantipfanapp.fragment.ForumFragment_;
import com.nantaphop.pantipfanapp.fragment.TopicFragment;
import com.nantaphop.pantipfanapp.fragment.TopicFragment_;
import com.nantaphop.pantipfanapp.model.ForumPagerItem;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import com.nantaphop.pantipfanapp.view.BaseActivity;
import com.squareup.otto.Subscribe;
import org.androidannotations.annotations.*;

/**
 * Created by nantaphop on 24-Oct-14.
 */
@EActivity(R.layout.activity_fragment)
public class ForumActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;
    @ViewById
    FrameLayout contentFrame;
    @App
    BaseApplication app;

    @Extra
    ForumPagerItem forumPagerItem;
    @Extra
    PantipRestClient.ForumType forumType;

    @NonConfigurationInstance
    ForumFragment forumFragment;



    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @AfterViews
    void init() {

        if (forumFragment == null) {
            forumFragment = ForumFragment_.builder().forumPagerItem(forumPagerItem).forumType(forumType).noTabMargin(true).build();
        }
        setSupportActionBar(toolbar);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_frame, forumFragment, null)
                .commit();
    }

    @OptionsItem(android.R.id.home)
    void backHome() {
        onBackPressed();
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

    @Subscribe
    public void openTopic(OpenTopicEvent e) {
        Intent i = new Intent(this, TopicActivity_.class);
        i.putExtra("topic", e.getTopic());
        startActivity(i);
        overrideAnimationBeforeStartActivity();
        Log.d("fragment", "open topic");

    }


}
