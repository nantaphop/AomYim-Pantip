package com.nantaphop.pantipfanapp.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.ads.AdView;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.ForumScrollDownEvent;
import com.nantaphop.pantipfanapp.event.ForumScrollUpEvent;
import com.nantaphop.pantipfanapp.event.ToggleDrawerEvent;
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import com.nantaphop.pantipfanapp.utils.CircleTransform;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

/**
 * Created by nantaphop on 27-Jul-14.
 */
@EFragment(R.layout.fragment_user)
public class UserFragment extends BaseFragment {

    public static final String TAG = "fragmentHolder";

    @ViewById
    ViewPager viewPager;
    @ViewById
    AdView ads;
    @ViewById
    Toolbar toolbar;
    @ViewById
    PagerSlidingTabStrip tabs;
    @ViewById
    LinearLayout topPanel;
    @ViewById
    ImageView userPhoto;
    @ViewById
    TextView username;

    @FragmentArg
    int userId;
    @FragmentArg
    String avatar;
    @FragmentArg
    String user;

    private int tabsHeight;
    private CharSequence actionBarTitle;
    private ForumSlidePagerAdapter pagerAdapter;
    private int currentPage;
    private ActionBar actionBar;
    private boolean toolbarHiding = false;
    private boolean navbarHiding = false;
    private float topDefaultY;

    @AfterViews
    void init() {
        getAttachedActivity().loadAd(ads);
        getAttachedActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pagerAdapter = new ForumSlidePagerAdapter(getFragmentManager());
        final DisplayMetrics displaymetrics = new DisplayMetrics();
        getAttachedActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.padding_default));
        tabs.setTabBackground(android.R.color.transparent);
        tabs.setViewPager(viewPager);
        Picasso.with(getAttachedActivity()).load(avatar).transform(new CircleTransform()).into(userPhoto);
        username.setText(user);
        tabs.setOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {


                    int sWidth = displaymetrics.widthPixels;

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        currentPage = position;
                        showTabs();
//                        View tab = tabs.getTab(position);
//                        Log.d("tabs", "widht = "+tab.getWidth());
//                        tabs.setScrollOffset((sWidth/2)-(tab.getWidth()/2), true);
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                }
        );
        tabs.setIndicatorColorResource(R.color.base_color_highlight);
        tabs.setDividerColorResource(android.R.color.transparent);
        tabs.setIndicatorHeight(getResources().getDimensionPixelOffset(R.dimen.tabs_indicator_height));

        topDefaultY = topPanel.getY();
    }

    @Override
    public void onResume() {
        super.onResume();
        app.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        app.getEventBus().unregister(this);

    }

    private class ForumSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ForumSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "กระทู้ที่ตั้ง";
                case 1:
                    return "กระทู้ที่ตอบ";
                case 2:
                    return "กระทู้โปรด";
            };
            return "";
        }


        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return ForumFragment_.builder().userId(userId).userTopicType(PantipRestClient.UserTopicType.Topic).noTabMargin(true).build();
                case 1: return ForumFragment_.builder().userId(userId).userTopicType(PantipRestClient.UserTopicType.Comment).noTabMargin(true).build();
                case 2: return ForumFragment_.builder().userId(userId).userTopicType(PantipRestClient.UserTopicType.Bookmarks).noTabMargin(true).build();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Subscribe
    public void hideTabs(ForumScrollDownEvent e) {
//        Log.d("event", "hideTabs");
//        if (!toolbarHiding) {
//            tabsHeight = toolbar.getHeight();
//            topPanel.animate().translationYBy(0 - tabsHeight).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//
////            actionBarTitle = actionBar.getTitle();
////            actionBar.setTitle(pagerAdapter.getPageTitle(currentPage));
////            app.getEventBus().post(new SetTitleEvent(pagerAdapter.getPageTitle(currentPage).toString()));
//            toolbarHiding = true;
//        } else if (!navbarHiding) {
//            tabsHeight = tabs.getHeight();
//            topPanel.animate().translationYBy(0 - tabsHeight).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//        }
    }


    @Subscribe
    public void showTabs(ForumScrollUpEvent e) {
        if (toolbarHiding) {
            showTabs();
        }
    }

    private void showTabs() {
        Log.d("event", "showTabs");


//        topPanel.animate().translationY(topDefaultY).setInterpolator(new AccelerateDecelerateInterpolator()).start();
////            app.getEventBus().post(new SetTitleEvent(getString(R.string.app_name)));
//        toolbarHiding = false;
    }


}
