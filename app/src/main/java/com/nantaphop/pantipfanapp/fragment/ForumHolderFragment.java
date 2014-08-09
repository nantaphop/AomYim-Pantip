package com.nantaphop.pantipfanapp.fragment;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.astuetz.PagerSlidingTabStrip;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.ForumScrollDownEvent;
import com.nantaphop.pantipfanapp.event.ForumScrollUpEvent;
import com.nantaphop.pantipfanapp.model.ForumPagerItem;
import com.squareup.otto.Subscribe;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by nantaphop on 27-Jul-14.
 */
@EFragment(R.layout.fragment_forumholder)
public class ForumHolderFragment extends BaseFragment {
    @ViewById
    ViewPager viewPager;
    @ViewById
    PagerSlidingTabStrip tabs;
    private List<ForumPagerItem> forumPagerItems;
    private int tabsHeight;
    private CharSequence actionBarTitle;
    private ForumSlidePagerAdapter pagerAdapter;
    private int currentPage;
    private ActionBar actionBar;
    private boolean tabHiding = false;
    private float tabsDefaultY;

    @AfterViews
    void init(){
        forumPagerItems = ForumPagerItem.getAll();
        pagerAdapter = new ForumSlidePagerAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabs.setViewPager(viewPager);
        tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPage = position;
                showTabs();
            }
        });
        tabs.setIndicatorColorResource(R.color.base_color_highlight);
        tabs.setDividerColorResource(android.R.color.transparent);
        tabs.setIndicatorHeight(getResources().getDimensionPixelOffset(R.dimen.tabs_indicator_height));
        app.getEventBus().register(this);
        actionBar = getActivity().getActionBar();
        tabsDefaultY = tabs.getY();

    }

    private class ForumSlidePagerAdapter extends FragmentPagerAdapter {
        public ForumSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return forumPagerItems.get(position).title;
        }

        @Override
        public Fragment getItem(int position) {
            return ForumFragment_.builder().forumPagerItem(forumPagerItems.get(position)).build();
        }

        @Override
        public int getCount() {
            return forumPagerItems.size();
        }
    }

    @Subscribe
    public void hideTabs(ForumScrollDownEvent e){
        hideTabs();
    }

    private void hideTabs() {
        Log.d("scroll","isHiding - "+ tabHiding);
        if (!tabHiding) {
            Log.d("scroll","hide");
            tabsHeight = tabs.getHeight();
            tabs.animate().translationYBy(0 - tabsHeight).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            actionBarTitle = actionBar.getTitle();
            actionBar.setTitle(pagerAdapter.getPageTitle(currentPage));
            tabHiding = true;
        }
    }

    @Subscribe
    public void showTabs(ForumScrollUpEvent e){
        showTabs();
    }

    private void showTabs() {
        Log.d("scroll","isHiding - "+ tabHiding);

        if (tabHiding) {
            Log.d("scroll", "show");
            tabs.animate().translationY(tabsDefaultY).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            actionBar.setTitle(actionBarTitle);
            tabHiding = false;
        }
    }


}
