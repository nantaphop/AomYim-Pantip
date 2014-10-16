package com.nantaphop.pantipfanapp;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import at.markushi.ui.action.Action;
import at.markushi.ui.action.BackAction;
import at.markushi.ui.action.CloseAction;
import at.markushi.ui.action.DrawerAction;
import com.nantaphop.pantipfanapp.event.*;
import com.nantaphop.pantipfanapp.fragment.*;
import com.nantaphop.pantipfanapp.fragment.dialog.ListDialog;
import com.nantaphop.pantipfanapp.fragment.dialog.ListDialog_;
import com.nantaphop.pantipfanapp.response.Comment;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.utils.CommentComparator;
import com.nantaphop.pantipfanapp.utils.TopicComparator;
import com.nantaphop.pantipfanapp.view.ActionBarView;
import com.nantaphop.pantipfanapp.view.ActionBarView_;
import com.squareup.otto.Subscribe;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.Collections;

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
    @StringRes
    String topic_sort_type_title;
    @StringArrayRes
    String[] topic_sort_type;
    @StringRes
    String comment_sort_type_title;
    @StringArrayRes
    String[] comment_sort_type;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new ForumHolderFragment_(), ForumHolderFragment.TAG);
            fragmentTransaction.setCustomAnimations(R.anim.enter_slide_from_bottom, 0, 0, R.anim.exit_slide_to_bottom);
            fragmentTransaction.commit();
        }
    }

    @AfterViews
    void init() {


        mTitle = drawer_close;
        mDrawerTitle = drawer_open;
        actionBarView = ActionBarView_.build(this);
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        final ActionBar actionBar = getActionBar();

        mDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout,
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close) {

            Action tmpAction;
            View.OnClickListener tmpOnClick;

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mDrawerTitle = actionBar.getTitle();
                actionBar.setTitle(mTitle);
//                actionBarView.setOnClickListener(tmpOnClick);
//                actionBarView.setAction(tmpAction);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                tmpAction = actionBarView.getAction();
                tmpOnClick = actionBarView.getOnClickListener();
                super.onDrawerOpened(drawerView);
//                actionBarView.setOnClickListener(closeDrawer);
                mTitle = actionBar.getTitle();
//                actionBarView.setAction(new CloseAction());
//                actionBarView.setOnClickListener(closeDrawer);
                actionBar.setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };


        // Set the drawer toggle as the DrawerListener
        drawer_layout.setDrawerListener(mDrawerToggle);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
        actionBarView.setOnClickListener(openDrawer);

        actionBar.setCustomView(actionBarView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);



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
    public void onBackPressed() {
        super.onBackPressed();
        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            actionBarView.setAction(new DrawerAction());
            actionBarView.setOnClickListener(openDrawer);
            Log.d("drawer", "main "+getSupportFragmentManager().getBackStackEntryCount());
        }else{
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            actionBarView.setAction(new BackAction());
            actionBarView.setOnClickListener(backAction);

            Log.d("drawer", "not main "+getSupportFragmentManager().getBackStackEntryCount());
        }
    }

//    @OptionsItem(android.R.id.home)
//    void home(MenuItem item) {
//        mDrawerToggle.onOptionsItemSelected(item);
//    }

    @Subscribe
    public void showRecommend(final ShowRecommendEvent e) {
//        RecommendDialog recommendDialog = RecommendDialog_.builder().topics(e.getRecommendTopics()).urls(e.getRecommendUrls()).build();
//        recommendDialog.show(getFragmentManager(), "recommend");
        final ListDialog listDialog = ListDialog_.builder().choicesArrayList(e.getRecommendTopics()).title(getString(R.string.recomend_topic)).listItemLayoutRes(R.layout.listitem_recommend_dialog).build();
        listDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Topic topic = new Topic();
                topic.setTitle(e.getRecommendTopics().get(i));
                topic.setId(Integer.parseInt(e.getRecommendUrls().get(i).split("/")[4]));
                app.getEventBus().post(new OpenTopicEvent(topic));
                listDialog.dismiss();

            }
        });
        listDialog.show(getFragmentManager(), null);
    }

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
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        TopicFragment topicFragment = TopicFragment_.builder().topic(e.getTopic()).build();
        openFragment(topicFragment, null);
        Log.d("fragment", "open topic");
        drawer_layout.closeDrawers();
        actionBarView.setOnClickListener(backAction);
        actionBarView.setAction(new BackAction());

    }

    @Subscribe
    public void sortForum(final SortForumEvent e) {

        final ArrayList<Topic> topics = e.getTopics();
        final ListDialog listDialog = ListDialog_.builder().choices(topic_sort_type).title(topic_sort_type_title).listItemLayoutRes(android.R.layout.simple_list_item_1).build();
        listDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TopicComparator topicComparator;
                switch (i) {
                    case 0:
                        topicComparator = new TopicComparator(TopicComparator.SortType.Comment);
                        break;
                    case 1:
                        topicComparator = new TopicComparator(TopicComparator.SortType.Vote);
                        break;
                    case 2:
                        topicComparator = new TopicComparator(TopicComparator.SortType.Time);
                        break;
                    default:
                        topicComparator = new TopicComparator(TopicComparator.SortType.Time);
                        break;
                }
                Collections.sort(topics, topicComparator);
                e.getAdapter().notifyDataSetChanged();
                listDialog.dismiss();
            }
        });
        listDialog.show(getFragmentManager(), "sort_topic");
    }

    @Subscribe
    public void sortComment(final SortCommentEvent e) {

        final ArrayList<Comment> comments = e.getComments().getComments();
        final ListDialog listDialog = ListDialog_.builder().choices(comment_sort_type).title(comment_sort_type_title).listItemLayoutRes(android.R.layout.simple_list_item_1).build();
        listDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CommentComparator commentComparator;
                switch (i) {
                    case 0:
                        commentComparator = new CommentComparator(CommentComparator.SortType.Vote);
                        break;
                    case 1:
                        commentComparator = new CommentComparator(CommentComparator.SortType.Emo);
                        break;
                    case 2:
                        commentComparator = new CommentComparator(CommentComparator.SortType.Order);
                        break;
                    default:
                        commentComparator = new CommentComparator(CommentComparator.SortType.Order);
                        break;
                }
                Collections.sort(comments, commentComparator);
                e.getAdapter().notifyDataSetChanged();
                listDialog.dismiss();
            }
        });
        listDialog.show(getFragmentManager(), "sort_topic");
    }




    @Subscribe
    public void openForumRearrange(OpenForumRearrangeEvent e) {
        Log.d("fragment", "rearrange");
//        mDrawerToggle.setDrawerIndicatorEnabled(false);
        getSupportFragmentManager().popBackStackImmediate(ForumRearrangeFragment.TAG, android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        openFragment(ForumRearrangeFragment_.builder().build(), ForumRearrangeFragment.TAG);
        actionBarView.setOnClickListener(backAction);
        actionBarView.setAction(new BackAction());
        drawer_layout.closeDrawers();
    }

    @Subscribe
    public void openLogin(OpenLoginScreenEvent e) {
        Log.d("fragment", "login");
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        getSupportFragmentManager().popBackStackImmediate(LoginFragment.TAG, android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        openFragment(LoginFragment_.builder().build(), LoginFragment.TAG);
        actionBarView.setOnClickListener(backAction);
        actionBarView.setAction(new BackAction());
        drawer_layout.closeDrawers();

    }


    private void openFragment(android.support.v4.app.Fragment f, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_enter_slide_from_bottom, 0, 0, R.anim.fragment_exit_slide_to_bottom);
        fragmentTransaction.add(R.id.content_frame, f, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    @Subscribe
    public void updateForumList(UpdateForumListEvent e) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new ForumHolderFragment_(), ForumHolderFragment.TAG);
        fragmentTransaction.setCustomAnimations(R.anim.enter_slide_from_bottom, 0, 0, R.anim.exit_slide_to_bottom);
        fragmentTransaction.commit();
    }

    @Subscribe
    public void toggleDrawer(ToggleDrawerEvent e){
        if(drawer_layout.isDrawerOpen(Gravity.START)){
           drawer_layout.closeDrawers();
        }else{
            drawer_layout.openDrawer(Gravity.START);
        }
    }

    @Subscribe
    public void setTitle(SetTitleEvent e){
        actionBarView.setTitle(e.title);
    }
}
