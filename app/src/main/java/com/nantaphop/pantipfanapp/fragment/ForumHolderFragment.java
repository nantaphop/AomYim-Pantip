package com.nantaphop.pantipfanapp.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.astuetz.PagerSlidingTabStrip;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
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
public class ForumHolderFragment extends Fragment {

    @App
    BaseApplication app;

    @ViewById
    ViewPager viewPager;

    @ViewById
    PagerSlidingTabStrip tabs;
    private List<ForumPagerItem> forumPagerItems;

    @AfterViews
    void init(){
        forumPagerItems = ForumPagerItem.getAll();
        viewPager.setAdapter(new ForumSlidePagerAdapter(getFragmentManager()));
        tabs.setViewPager(viewPager);
        tabs.setIndicatorColorResource(R.color.base_color_bright);
        app.getEventBus().register(this);
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


}
