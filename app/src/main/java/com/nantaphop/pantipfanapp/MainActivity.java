package com.nantaphop.pantipfanapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import com.nantaphop.pantipfanapp.fragment.ForumHolderFragment_;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity {

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
    void init(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new ForumHolderFragment_());
        fragmentTransaction.commit();

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
    void home(MenuItem item){
        mDrawerToggle.onOptionsItemSelected(item);
    }

}
