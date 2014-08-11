package com.nantaphop.pantipfanapp;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import com.nantaphop.pantipfanapp.event.*;
import com.nantaphop.pantipfanapp.fragment.*;
import com.nantaphop.pantipfanapp.fragment.dialog.ListDialog;
import com.nantaphop.pantipfanapp.fragment.dialog.ListDialog_;
import com.nantaphop.pantipfanapp.fragment.dialog.RecommendDialog;
import com.nantaphop.pantipfanapp.fragment.dialog.RecommendDialog_;
import com.nantaphop.pantipfanapp.response.Comment;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.utils.CommentComparator;
import com.nantaphop.pantipfanapp.utils.TopicCardComparator;
import com.nantaphop.pantipfanapp.utils.TopicComparator;
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



    @AfterViews
    void init() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new ForumHolderFragment_(), ForumHolderFragment.TAG);
        fragmentTransaction.setCustomAnimations(R.anim.enter_slide_from_bottom, 0, 0, R.anim.exit_slide_to_bottom );
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
        fragmentTransaction.setCustomAnimations(R.anim.fragment_enter_slide_from_bottom, 0, 0, R.anim.fragment_exit_slide_to_bottom);
        fragmentTransaction.add(R.id.content_frame, topicFragment);
        fragmentTransaction.addToBackStack(null);
        Log.d("fragment", "open topic");
        fragmentTransaction.commit();
    }

    @Subscribe
    public void sortForum(final SortForumEvent e){

        final ArrayList<Topic> topics = e.getForum().getTopics();
        final ListDialog listDialog = ListDialog_.builder().choices(topic_sort_type).title(topic_sort_type_title).listItemLayoutRes(android.R.layout.simple_list_item_1).build();
        listDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TopicCardComparator topicCardComparator;
                TopicComparator topicComparator;
                switch(i){
                    case 0:
                        topicCardComparator = new TopicCardComparator(TopicCardComparator.SortType.Comment);
                        topicComparator = new TopicComparator(TopicComparator.SortType.Comment);
                        break;
                    case 1:
                        topicCardComparator = new TopicCardComparator(TopicCardComparator.SortType.Vote);
                        topicComparator = new TopicComparator(TopicComparator.SortType.Vote);
                        break;
                    case 2:
                        topicCardComparator = new TopicCardComparator(TopicCardComparator.SortType.Time);
                        topicComparator = new TopicComparator(TopicComparator.SortType.Time);
                        break;
                    default:
                        topicCardComparator = new TopicCardComparator(TopicCardComparator.SortType.Time);
                        topicComparator = new TopicComparator(TopicComparator.SortType.Time);
                        break;
                }
                Collections.sort(e.getCardList(), topicCardComparator);
                Collections.sort(e.getForum().getTopics(), topicComparator);
                e.getAdapter().notifyDataSetChanged();
                listDialog.dismiss();
            }
        });
        listDialog.show(getFragmentManager(), "sort_topic");



    }

    @Subscribe
    public void sortComment(final SortCommentEvent e){

        final ArrayList<Comment> comments = e.getComments().getComments();
        final ListDialog listDialog = ListDialog_.builder().choices(comment_sort_type).title(comment_sort_type_title).listItemLayoutRes(android.R.layout.simple_list_item_1).build();
        listDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CommentComparator commentComparator;
                switch(i){
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
    public void openForumRearrange(OpenForumRearrangeEvent e){
        Log.d("fragment", "rearrange");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_enter_slide_from_bottom, 0, 0, R.anim.fragment_exit_slide_to_bottom);
        fragmentTransaction.add(R.id.content_frame, ForumRearrangeFragment_.builder().build(), "rearrangeForum");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        drawer_layout.closeDrawers();
    }

    @Subscribe
    public void updateForumList(UpdateForumListEvent e){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new ForumHolderFragment_(), ForumHolderFragment.TAG);
        fragmentTransaction.setCustomAnimations(R.anim.enter_slide_from_bottom, 0, 0, R.anim.exit_slide_to_bottom );
        fragmentTransaction.commit();
    }
}
