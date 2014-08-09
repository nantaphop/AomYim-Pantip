package com.nantaphop.pantipfanapp;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import com.nantaphop.pantipfanapp.event.DialogDismissEvent;
import com.nantaphop.pantipfanapp.event.DialogShowEvent;
import com.nantaphop.pantipfanapp.event.OpenTopicEvent;
import com.nantaphop.pantipfanapp.event.ShowRecommendEvent;
import com.nantaphop.pantipfanapp.fragment.ForumHolderFragment_;
import com.nantaphop.pantipfanapp.fragment.TopicFragment;
import com.nantaphop.pantipfanapp.fragment.TopicFragment_;
import com.nantaphop.pantipfanapp.fragment.dialog.RecommendDialog;
import com.nantaphop.pantipfanapp.fragment.dialog.RecommendDialog_;
import com.squareup.otto.Subscribe;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.StringRes;

@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity {

    @App
    BaseApplication app;

    @ViewById
    FrameLayout content_frame;

    @ViewById
    DrawerLayout drawer_layout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;

    @StringRes
    String drawer_open;
    @StringRes
    String drawer_close;


    @AfterViews
    void init() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new ForumHolderFragment_());
        fragmentTransaction.setCustomAnimations(R.anim.enter_slide_from_bottom, 0, 0, R.anim.exit_slide_to_bottom );
        fragmentTransaction.commit();

        app.getEventBus().register(this);

        mTitle = drawer_close;
        mDrawerTitle = drawer_open;


        mDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout,
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mDrawerTitle = getActionBar().getTitle();
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mTitle = getActionBar().getTitle();
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawer_layout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }

    @OptionsItem(android.R.id.home)
    void home(MenuItem item) {
        mDrawerToggle.onOptionsItemSelected(item);
    }

    @Subscribe
    public void showRecommend(final ShowRecommendEvent e) {
        RecommendDialog recommendDialog = RecommendDialog_.builder().topics(e.getRecommendTopics()).urls(e.getRecommendUrls()).build();
        recommendDialog.show(getFragmentManager(), "recommend");

    }

    @Subscribe
    public void animateDialogShow(DialogShowEvent e){
//        this.getWindow().getDecorView().animate().alpha(0.1f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    @Subscribe
    public void animateDialogDismiss(DialogDismissEvent e){
//        this.getWindow().getDecorView().animate().alpha(1f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    @Subscribe
    public void openTopic(OpenTopicEvent e){
        TopicFragment topicFragment = TopicFragment_.builder().topic(e.getTopic()).build();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_enter_slide_from_bottom, 0, 0, R.anim.fragment_exit_slide_to_bottom );
        fragmentTransaction.add(R.id.content_frame, topicFragment);
        fragmentTransaction.addToBackStack(null);
        Log.d("fragment", "open topic");
        fragmentTransaction.commit();
    }
}
