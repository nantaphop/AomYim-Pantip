package com.nantaphop.pantipfanapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdView;
import com.nantaphop.pantipfanapp.event.OpenPhotoEvent;
import com.nantaphop.pantipfanapp.event.OpenUserEvent;
import com.nantaphop.pantipfanapp.fragment.PhotoViewFragment_;
import com.nantaphop.pantipfanapp.fragment.TopicFragment;
import com.nantaphop.pantipfanapp.fragment.TopicFragment_;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.utils.DeviceUtils;
import com.nantaphop.pantipfanapp.view.BaseActivity;
import com.squareup.otto.Subscribe;

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
    LinearLayout contentRoot;
    @ViewById
    FrameLayout contentFrame;
    @ViewById
    FrameLayout root;
    @ViewById
    AdView ads;
    @App
    BaseApplication app;


    @Extra
    Topic topic;
    @Extra
    int startingLocation;

    @NonConfigurationInstance
    TopicFragment topicFragment;
    @ViewById
    View toolbarPadding;




    private void startIntroAnimation() {
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(startingLocation);
        contentFrame.setTranslationY(100);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        contentFrame.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
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
        setTitle("");
        root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                root.getViewTreeObserver().removeOnPreDrawListener(this);
                startIntroAnimation();
                return true;
            }
        });
        loadAd(ads);
    }

    @Override
    public void onBackPressed() {
        noBackTransition = true;

        contentRoot.animate()
//                .translationY(DeviceUtils.getDisplayHeight(this))
                .scaleY(0f)
                .alpha(0)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        TopicActivity.super.onBackPressed();
                    }
                })
                .start();

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
    public void openUser(OpenUserEvent e) {
        UserActivity_.intent(this).userId(e.getUserId()).user(e.getUsername()).avatar(e.getAvatar()).start();
    }

    @Subscribe
    public void openPhoto(OpenPhotoEvent event){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.root, PhotoViewFragment_.builder().photoUrl(event.getPhotoUrl()).build(), null);
        ft.addToBackStack(null);
        ft.commit();
    }

}
