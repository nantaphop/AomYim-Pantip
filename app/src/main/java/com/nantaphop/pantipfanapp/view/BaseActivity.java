package com.nantaphop.pantipfanapp.view;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.mrengineer13.snackbar.SnackBar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nantaphop.pantipfanapp.R;

/**
 * Created by nantaphop on 27-Oct-14.
 */
public class BaseActivity extends ActionBarActivity {

    private static final String SAVED_SNACKBAR = "snackbar";
    private SnackBar mSnackBar;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected CharSequence mTitle;
    protected CharSequence mDrawerTitle;
    private DrawerLayout drawerLayout;
    private String openTxt;
    private String closeTxt;
    private Toolbar toolbar;
    protected boolean noBackTransition;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!noBackTransition) {
            overridePendingTransition(R.anim.activity_scale_in_from_bottom_center, R.anim.activity_exit_slide_to_bottom);
        }else{
            overridePendingTransition(0,0);
        }
    }

    protected void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    public void setDrawerOpenText(String openTxt) {
        this.openTxt = openTxt;
    }

    public void setDrawerCloseText(String closeTxt) {
        this.closeTxt = closeTxt;
    }

    public void initNavDrawer(final Toolbar toolbar) {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mDrawerTitle = toolbar.getTitle();
                toolbar.setTitle(closeTxt);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mTitle = toolbar.getTitle();
                toolbar.setTitle(openTxt);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.syncState();

    }

    @Override
    protected void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
        // use this to save your snacks for later
//        saveState.putBundle(SAVED_SNACKBAR, mSnackBar.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle loadState) {
        super.onRestoreInstanceState(loadState);
        // use this to load your snacks for later
//        mSnackBar.onRestoreInstanceState(loadState.getBundle(SAVED_SNACKBAR));
    }

    protected void overrideAnimationBeforeStartActivity() {
        overridePendingTransition(R.anim.activity_enter_slide_from_bottom, R.anim.activity_scale_out_to_bottom_center);
    }

    public SnackBar getSnackBar() {
        if (mSnackBar == null) {
            mSnackBar = new SnackBar(this);
        }
        return mSnackBar;
    }

    void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        setToolbar(toolbar);
        super.setSupportActionBar(toolbar);
    }

    public void loadAd(AdView ads){
        AdRequest adRequest = new AdRequest.Builder().build();
        ads.loadAd(adRequest);
    }
}
