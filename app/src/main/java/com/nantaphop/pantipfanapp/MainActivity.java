package com.nantaphop.pantipfanapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import at.markushi.ui.action.Action;
import at.markushi.ui.action.BackAction;
import at.markushi.ui.action.CloseAction;
import at.markushi.ui.action.DrawerAction;
import com.easyandroidanimations.library.TransferAnimation;
import com.nantaphop.pantipfanapp.event.*;
import com.nantaphop.pantipfanapp.fragment.*;
import com.nantaphop.pantipfanapp.view.ActionBarView;
import com.nantaphop.pantipfanapp.view.ActionBarView_;
import com.squareup.otto.Subscribe;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener, android.support.v4.app.FragmentManager.OnBackStackChangedListener {

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
    @StringRes
    String topic_sort_type_title;
    @StringArrayRes
    String[] topic_sort_type;

    private ActionBarView actionBarView;

    View.OnClickListener openDrawer = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawer_layout.openDrawer(Gravity.START);
            actionBarView.setAction(new CloseAction());
            actionBarView.setOnClickListener(closeDrawer);

        }
    };
    View.OnClickListener closeDrawer = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawer_layout.closeDrawer(Gravity.START);
            actionBarView.setAction(new DrawerAction());
            actionBarView.setOnClickListener(openDrawer);
        }
    };
    View.OnClickListener backAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };
    private Toolbar toolbar;

    boolean HomeAsUpEnabled = true;

    @OptionsItem()
    void action_1() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(!HomeAsUpEnabled);
        HomeAsUpEnabled = !HomeAsUpEnabled;
        Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
    }

    @OptionsItem()
    void action_2() {
        Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
    }

    @OptionsItem()
    void action_3() {
        Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( savedInstanceState == null ) {
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

    @AfterViews
    void init() {
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTitle = drawer_close;
        mDrawerTitle = drawer_open;

        actionBarView = ActionBarView_.build(this);
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer_layout,
                toolbar, R.string.drawer_open, R.string.drawer_close
        ) {

            Action tmpAction;
            View.OnClickListener tmpOnClick;

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mDrawerTitle = toolbar.getTitle();
                toolbar.setTitle(mTitle);
//                actionBarView.setOnClickListener(tmpOnClick);
//                actionBarView.setAction(tmpAction);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                tmpAction = actionBarView.getAction();
                tmpOnClick = actionBarView.getOnClickListener();
                super.onDrawerOpened(drawerView);
//                actionBarView.setOnClickListener(closeDrawer);
                mTitle = toolbar.getTitle();
//                actionBarView.setAction(new CloseAction());
//                actionBarView.setOnClickListener(closeDrawer);
                toolbar.setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };


        // Set the drawer toggle as the DrawerListener
        drawer_layout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
//        shouldDisplayHomeUp();
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

    @Override
    public void onBackStackChanged() {
//        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp() {
        //Enable Up button only  if there are entries in the back stack
        boolean canback = canBack();
        if ( canback ) {
//            mDrawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            drawer_layout.setDrawerListener(null);
        }
        else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            drawer_layout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();

//            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }

    }


    private boolean canBack() {return getSupportFragmentManager().getBackStackEntryCount() > 0;}

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
//        getSupportFragmentManager().popBackStack();
        onBackPressed();
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDrawerToggle.setDrawerIndicatorEnabled(true);
//        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
//            mDrawerToggle.setDrawerIndicatorEnabled(true);
//            actionBarView.setAction(new DrawerAction());
//            actionBarView.setOnClickListener(openDrawer);
//            Log.d("drawer", "main "+getSupportFragmentManager().getBackStackEntryCount());
//        }else{
//            mDrawerToggle.setDrawerIndicatorEnabled(false);
//            actionBarView.setAction(new BackAction());
//            actionBarView.setOnClickListener(backAction);
//
//            Log.d("drawer", "not main "+getSupportFragmentManager().getBackStackEntryCount());
//        }
    }

//    @OptionsItem(android.R.id.home)
//    void home(MenuItem item) {
//        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
//        if(canback){
//            onBackPressed();
//        }
//    }


    @Subscribe
    public void animateDialogShow(DialogShowEvent e) {
//        this.getWindow().getDecorView().animate().alpha(0.1f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    @Subscribe
    public void animateDialogDismiss(DialogDismissEvent e) {
//        this.getWindow().getDecorView().animate().alpha(1f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    @Subscribe
    public void openTopic(OpenTopicEvent e) {
        Intent i = new Intent(this, TopicActivity_.class);
        i.putExtra("topic", e.getTopic());
        startActivity(i);
        overridePendingTransition(R.anim.fragment_enter_slide_from_bottom, R.anim.scale_to_bottom_center);
        Log.d("fragment", "open topic");
        drawer_layout.closeDrawers();

    }

    @Subscribe
    public void openForumRearrange(OpenForumRearrangeEvent e) {
        Log.d("fragment", "rearrange");
//        mDrawerToggle.setDrawerIndicatorEnabled(false);
        getSupportFragmentManager().popBackStackImmediate(
                ForumRearrangeFragment.TAG,
                android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
        );
        openFragment(ForumRearrangeFragment_.builder().build(), ForumRearrangeFragment.TAG);
        actionBarView.setOnClickListener(backAction);
        actionBarView.setAction(new BackAction());
        drawer_layout.closeDrawers();
    }

    @Subscribe
    public void openLogin(OpenLoginScreenEvent e) {
        Log.d("fragment", "login");
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        getSupportFragmentManager().popBackStackImmediate(
                LoginFragment.TAG,
                android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
        );
        openFragment(LoginFragment_.builder().build(), LoginFragment.TAG);
        actionBarView.setOnClickListener(backAction);
        actionBarView.setAction(new BackAction());
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
        if ( drawer_layout.isDrawerOpen(Gravity.START) ) {
            drawer_layout.closeDrawers();
        }
        else {
            drawer_layout.openDrawer(Gravity.START);
        }
    }

    @Subscribe
    public void setTitle(SetTitleEvent e) {
        getSupportActionBar().setTitle(e.title);
    }
}
