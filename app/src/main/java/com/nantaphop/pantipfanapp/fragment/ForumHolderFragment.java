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
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.google.android.gms.ads.AdView;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.ForumScrollDownEvent;
import com.nantaphop.pantipfanapp.event.ForumScrollUpEvent;
import com.nantaphop.pantipfanapp.event.OpenForumRearrangeEvent;
import com.nantaphop.pantipfanapp.event.ToggleDrawerEvent;
import com.nantaphop.pantipfanapp.model.ForumPagerItem;
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import com.nantaphop.pantipfanapp.utils.ShowcaseUtils;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.codechimp.apprater.AppRater;

import java.util.List;

/**
 * Created by nantaphop on 27-Jul-14.
 */
@EFragment(R.layout.fragment_forumholder)
public class ForumHolderFragment extends BaseFragment {

    public static final String TAG = "fragmentHolder";

    @ViewById
    ViewPager viewPager;
    @ViewById
    PagerSlidingTabStrip tabs;
    @ViewById
    Toolbar toolbar;
    @ViewById
    LinearLayout topPanel;

    private List<ForumPagerItem> forumPagerItems;
    private int tabsHeight;
    private CharSequence actionBarTitle;
    private ForumSlidePagerAdapter pagerAdapter;
    private int currentPage;
    private ActionBar actionBar;
    private boolean toolbarHiding = false;
    private boolean navbarHiding = false;
    private float topDefaultY;

    @AfterViews
    void init(){

        rateApp();

        getAttachedActivity().setSupportActionBar(toolbar);
        getAttachedActivity().initNavDrawer(toolbar);
        forumPagerItems = ForumPagerItem.getAll("enable");
        pagerAdapter = new ForumSlidePagerAdapter(getFragmentManager());
        final DisplayMetrics displaymetrics = new DisplayMetrics();
        getAttachedActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.padding_default));
        tabs.setTabBackground(android.R.color.transparent);
        tabs.setViewPager(viewPager);

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

        actionBar = ((ActionBarActivity)getAttachedActivity()).getSupportActionBar();
        topDefaultY = topPanel.getY();
        showcase();
    }

    private void showcase() {
        ShowcaseView sv1 = new ShowcaseView.Builder(getAttachedActivity())
                .setTarget(Target.NONE)
                .singleShot(ShowcaseUtils.FIRST_TIME_LAUNCH_ID)
                .setContentTitle("ยินดีต้อนรับเข้าสู่ \"อมยิ้ม\"")
                .setContentText("ประสบการณ์ การเล่น Pantip ที่แปลกใหม่!")
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        ShowcaseView sv2 = new ShowcaseView.Builder(getAttachedActivity())
                                .setContentTitle("คุณคงไม่ได้อ่าน Pantip ทุกห้อง")
                                .setContentText("เราจึงทำระบบให้คุณเลือกแสดง" +
                                        "\nและจัดอันดับห้องตามที่คุณชอบได้" +
                                        "\nลองไปทำกันเลย")
                                .setShowcaseEventListener(new OnShowcaseEventListener() {
                                    @Override
                                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                        BaseApplication.getEventBus().post(new OpenForumRearrangeEvent());
                                    }

                                    @Override
                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                    }

                                    @Override
                                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                    }
                                })
                                .build();
                        sv2.setButtonPosition(ShowcaseUtils.getCenterInParentLayoutParam());
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                    }
                })
                .build();
        sv1.setButtonPosition(ShowcaseUtils.getCenterInParentLayoutParam());
    }

    private void rateApp() {
        AppRater.app_launched(getAttachedActivity());
        AppRater.setLightTheme();
//        AppRater.setVersionCodeCheckEnabled(true);
        AppRater.setNumLaunchesForRemindLater(1);
//        AppRater.showRateDialog(getAttachedActivity());
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

    @Override
    void back() {
        app.getEventBus().post(new ToggleDrawerEvent());
    }

    private class ForumSlidePagerAdapter extends FragmentStatePagerAdapter{
        public ForumSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return forumPagerItems.get(position).title;
        }



        @Override
        public Fragment getItem(int position) {
            return ForumFragment_.builder().forumType(PantipRestClient.ForumType.Room).forumPagerItem(forumPagerItems.get(position)).build();
        }

        @Override
        public int getCount() {
            return forumPagerItems.size();
        }
    }

    @Subscribe
    public void hideTabs(ForumScrollDownEvent e){
//        Log.d("event", "hideTabs");
//        if (!toolbarHiding) {
//            tabsHeight = toolbar.getHeight();
//            topPanel.animate().translationYBy(0 - tabsHeight).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//
////            actionBarTitle = actionBar.getTitle();
////            actionBar.setTitle(pagerAdapter.getPageTitle(currentPage));
////            app.getEventBus().post(new SetTitleEvent(pagerAdapter.getPageTitle(currentPage).toString()));
//            toolbarHiding = true;
//        }else if(!navbarHiding){
//            tabsHeight = tabs.getHeight();
//            topPanel.animate().translationYBy(0 - tabsHeight).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//        }
    }



    @Subscribe
    public void showTabs(ForumScrollUpEvent e){
//        if (toolbarHiding) {
//            showTabs();
//        }
    }

    private void showTabs() {
        Log.d("event", "showTabs");


            topPanel.animate().translationY(topDefaultY).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//            app.getEventBus().post(new SetTitleEvent(getString(R.string.app_name)));
            toolbarHiding = false;
    }


}
