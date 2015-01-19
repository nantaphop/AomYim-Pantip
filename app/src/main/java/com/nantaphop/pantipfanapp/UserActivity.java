package com.nantaphop.pantipfanapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.nantaphop.pantipfanapp.fragment.UserFragment;
import com.nantaphop.pantipfanapp.fragment.UserFragment_;
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
public class UserActivity extends BaseActivity {


    @ViewById
    Toolbar toolbar;
    @ViewById
    FrameLayout contentFrame;
    @App
    BaseApplication app;

    @Extra
    int userId;
    @Extra
    String avatar;
    @Extra
    String user;

    @NonConfigurationInstance
    UserFragment userFragment;
    @ViewById
    View toolbarPadding;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        toolbarPadding.setVisibility(View.GONE);
        if (userFragment == null) {
            userFragment = UserFragment_.builder().userId(userId).avatar(avatar).user(user).build();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, userFragment, null)
                    .commit();
        }
        toolbar.setBackgroundColor(Color.parseColor("#00000000"));
        setTitle("");
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
