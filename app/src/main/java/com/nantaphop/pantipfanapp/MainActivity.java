package com.nantaphop.pantipfanapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.nantaphop.pantipfanapp.event.OpenForumEvent;
import com.nantaphop.pantipfanapp.event.OpenForumRearrangeEvent;
import com.nantaphop.pantipfanapp.event.OpenLoginScreenEvent;
import com.nantaphop.pantipfanapp.event.OpenTopicEvent;
import com.nantaphop.pantipfanapp.event.SetTitleEvent;
import com.nantaphop.pantipfanapp.event.ToggleDrawerEvent;
import com.nantaphop.pantipfanapp.event.UpdateForumListEvent;
import com.nantaphop.pantipfanapp.fragment.ForumHolderFragment;
import com.nantaphop.pantipfanapp.fragment.ForumHolderFragment_;
import com.nantaphop.pantipfanapp.view.BaseActivity;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @App
    BaseApplication app;

    @ViewById
    FrameLayout content_frame;
    @ViewById
    DrawerLayout drawer_layout;

    @StringRes
    String drawer_open;
    @StringRes
    String drawer_close;
    @StringRes
    String topic_sort_type_title;
    @StringArrayRes
    String[] topic_sort_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new ForumHolderFragment_(), ForumHolderFragment.TAG);
            fragmentTransaction.setCustomAnimations(
                    R.anim.enter_slide_from_bottom,
                    0,
                    0,
                    R.anim.scale_to_bottom_center
            );
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.print(item.toString());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(Gravity.START))
            drawer_layout.closeDrawers();
        else
            super.onBackPressed();
    }

    @AfterViews
    void init() {
        setDrawerLayout(drawer_layout);
        setDrawerCloseText(drawer_close);
        setDrawerOpenText(drawer_open);

        // Set the drawer toggle as the DrawerListener
        drawer_layout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        overridePendingTransition(R.anim.fragment_enter_slide_from_bottom, R.anim.fragment_exit_slide_to_bottom);

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
    public void openTopic(final OpenTopicEvent e) {
        final Intent i = new Intent(MainActivity.this, TopicActivity_.class);
        i.putExtra("topic", e.getTopic());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                startActivity(i);
                overrideAnimationBeforeStartActivity();
                Log.d("fragment", "open topic");
                drawer_layout.closeDrawers();
            }
        }.execute();


    }

    @Subscribe
    public void openForum(OpenForumEvent e) {
        Intent i = new Intent(this, ForumActivity_.class);
        i.putExtra("forumPagerItem", e.forumPagerItem);
        i.putExtra("forumType", e.forumType);
        startActivity(i);
        overrideAnimationBeforeStartActivity();
        Log.d("fragment", "open forum");
        drawer_layout.closeDrawers();
    }


    @Subscribe
    public void openForumRearrange(OpenForumRearrangeEvent e) {
        Log.d("fragment", "rearrange");
        Intent i = new Intent(this, RoomArrangementActivity_.class);
        startActivity(i);
        drawer_layout.closeDrawers();
    }

    @Subscribe
    public void openLogin(OpenLoginScreenEvent e) {
        Intent i = new Intent(this, LoginActivity_.class);
        startActivity(i);
        drawer_layout.closeDrawers();

    }

    private void openFragment(android.support.v4.app.Fragment f, String tag) {
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.fragment_enter_slide_from_bottom,
                0,
                0,
                R.anim.fragment_exit_slide_to_bottom
        );
        fragmentTransaction.add(R.id.content_frame, f, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    @Subscribe
    public void updateForumList(UpdateForumListEvent e) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new ForumHolderFragment_(), ForumHolderFragment.TAG);
        fragmentTransaction.setCustomAnimations(R.anim.enter_slide_from_bottom, 0, 0, R.anim.scale_to_bottom_center);
        fragmentTransaction.commit();
    }

    @Subscribe
    public void toggleDrawer(ToggleDrawerEvent e) {
        if (drawer_layout.isDrawerOpen(Gravity.START)) {
            drawer_layout.closeDrawers();
        } else {
            drawer_layout.openDrawer(Gravity.START);
        }
    }

    @Subscribe
    public void setTitle(SetTitleEvent e) {
        getSupportActionBar().setTitle(e.title);
    }


}
