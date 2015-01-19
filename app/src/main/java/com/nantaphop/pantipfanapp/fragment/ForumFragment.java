package com.nantaphop.pantipfanapp.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.adapter.ForumAdapter;
import com.nantaphop.pantipfanapp.adapter.TopicAdapter;
import com.nantaphop.pantipfanapp.event.ForumScrollDownEvent;
import com.nantaphop.pantipfanapp.event.ForumScrollUpEvent;
import com.nantaphop.pantipfanapp.event.OpenForumEvent;
import com.nantaphop.pantipfanapp.event.SortForumEvent;
import com.nantaphop.pantipfanapp.event.ToggleDrawerEvent;
import com.nantaphop.pantipfanapp.model.ForumPagerItem;
import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.ForumPart;
import com.nantaphop.pantipfanapp.response.MyPage;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import com.nantaphop.pantipfanapp.utils.DeviceUtils;
import com.nantaphop.pantipfanapp.utils.RESTUtils;
import com.nantaphop.pantipfanapp.utils.TopicComparator;
import com.nantaphop.pantipfanapp.utils.Utils;
import com.nantaphop.pantipfanapp.view.ForumEmptyView;
import com.nantaphop.pantipfanapp.view.ForumEmptyView_;
import com.nantaphop.pantipfanapp.view.LoadingItemView;
import com.nantaphop.pantipfanapp.view.MyAnimationAdapter;
import com.nantaphop.pantipfanapp.view.SimpleEmptyView;
import com.nantaphop.pantipfanapp.view.SimpleEmptyView_;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Collections;

import static com.nantaphop.pantipfanapp.service.PantipRestClient.ForumType;
import static com.nantaphop.pantipfanapp.service.PantipRestClient.TopicType;

/**
 * Created by nantaphop on 27-Jul-14.
 */
@EFragment(R.layout.fragment_forum)
public class ForumFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @ViewById
    ObservableListView list;
    @ViewById
    FloatingActionButton fab;
    @ViewById
    FrameLayout root;


    // Forum Args
    @FragmentArg
    ForumPagerItem forumPagerItem;

    @FragmentArg
    ForumType forumType;

    @FragmentArg
    boolean noTabMargin = false;

    // User Args
    @FragmentArg
    int userId;
    @FragmentArg
    PantipRestClient.UserTopicType userTopicType;


    @InstanceState
    String lastIdCurrentPage = "0";
    @InstanceState
    int currentPage = 0;
    @InstanceState
    int cardRenderCount = 0;
    @InstanceState
    long lastFirstId = 0;
    @InstanceState
    long lastLastId = 0;

    @InstanceState
    Forum forum;
    @InstanceState
    ForumPart forumPart;
    @InstanceState
    MyPage myPage;

    @InstanceState
    int lastFirstVisibleItem;
    @InstanceState
    public String[] recommendTopicTitle;
    @InstanceState
    public String[] recommendTopicUrl;

    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;

    @StringRes
    String topic_sort_type_title;
    @StringArrayRes
    String[] topic_sort_type;
    @StringArrayRes
    String[] topic_type;

    private ForumEmptyView forumEmptyView;
    private boolean prepareTopicDone;
    private boolean prepareRecommendDone;
    private float fabDefaultY;
    private boolean fabIsHiding;
    private TopicAdapter forumAdapter;
    private SimpleEmptyView emptyView;

    private TopicType topicType;
    private LoadingItemView loadingItemView;


    BaseJsonHttpResponseHandler userForumCallback = new BaseJsonHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, String s, Object o) {
            Log.i("loadData " + userId, " userForumCallback success");
            MyPage newMyPage = (MyPage) o;
            if (myPage == null) {
                myPage = newMyPage;

            } else {
                myPage.getResult().addAll(myPage.getResult());
            }
            lastFirstId = myPage.getFirst_id();
            lastLastId = myPage.getLast_id();

            prepareTopic();
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Object o) {
            Log.d("forum", "failed load forum");
            Log.i("loadData", "forumCallback failed");
            showErrorScreen();
            currentPage--;
        }

        @Override
        protected Object parseResponse(String s, boolean b) throws Throwable {
            return RESTUtils.parseUserForum(s);
        }
    };
    BaseJsonHttpResponseHandler forumCallback = new BaseJsonHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, String s, Object o) {
            Log.i("loadData " + forumPagerItem.title, "forumCallback success");
            Log.d("forum", "success");
            Forum newForum = (Forum) o;
            if (forum == null) {
                forum = newForum;
                lastIdCurrentPage = forum.getLastIdCurrentPage();
            } else {
                forum.getTopics().addAll(newForum.getTopics());
                lastIdCurrentPage = newForum.getLastIdCurrentPage();
            }

            prepareTopic();
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Object o) {
            Log.d("forum", "failed load forum");
            Log.i("loadData " + forumPagerItem.title, "forumCallback failed");
            showErrorScreen();
            currentPage--;
        }

        @Override
        protected Object parseResponse(String s, boolean b) throws Throwable {
            return RESTUtils.parseForum(s);
        }
    };
    private byte[] tmpForumPartBytes;
    AsyncHttpResponseHandler forumPartCallback = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int x, Header[] headers, byte[] bytes) {
            Log.i("loadData " + forumPagerItem.title, "forumPartCallback success");
            tmpForumPartBytes = bytes;
            prepareForumPart();
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Log.d("forum", "failed load forum part");
            Log.i("loadData " + forumPagerItem.title, "forumPartCallback failed");

            showErrorScreen();
        }
    };

    @Trace
    @Background
    void prepareForumPart() {
        Log.i("loadData " + forumPagerItem.title, "prepareForumPart start");
        forumPart = RESTUtils.parseForumPart(new String(tmpForumPartBytes));
        if (forumType == ForumType.Room && forumPart.getRecommendTopic().get(0) == null) {
            Log.e("forumPath", "CANT PARSE FORUMPATH\n" + new String(tmpForumPartBytes));
        }
        Log.d(
                "forumPart",
                "prepareForumPart recommend size : " + forumPart.getRecommendUrl()
                        .size() + " - " + forumPart.getRecommendTopic().size()
        );
        for (String s : forumPart.getRecommendTopic()) {
            Log.d("recommend", forumPagerItem.title + " " + s);
        }
        Log.i("loadData " + forumPagerItem.title, "prepareForumPart finish");
        prepareRecommendCard();
    }

    @Background
    public void prepareRecommendCard() {
        Log.i("loadData " + forumPagerItem.title, "prepareRecommendCard starts");

        int numPreview = forumPart.getRecommendTopic().size() > 3 ? 3 : forumPart.getRecommendTopic().size();

        if (recommendTopicTitle == null) { // Do just first load
            recommendTopicTitle = new String[numPreview];
            recommendTopicUrl = new String[numPreview];
//            // Add Recommend Topic
            for (int i = 0; i < numPreview; i++) {
                Log.d("recommend", "prepareRecommendCard " + i + " : " + forumPart.getRecommendTopic().get(i));
                recommendTopicTitle[i] = forumPart.getRecommendTopic().get(i);
                recommendTopicUrl[i] = forumPart.getRecommendUrl().get(i);
            }
        }
        tmpForumPartBytes = null;
        prepareRecommendDone = true;
        Log.i("loadData " + forumPagerItem.title, "prepareRecommendCard finish");
        joinForum();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (forumPagerItem != null) {
            if (noTabMargin) {
                inflater.inflate(R.menu.menu_forum_tag, menu);
            } else {
                inflater.inflate(R.menu.menu_forum, menu);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Trace
    @Background
    void prepareTopic() {
        Log.i("loadData ", "prepareTopic start");

        prepareTopicDone = true;
        joinForum();
        Log.i("loadData ", "prepareTopic finish");

    }

    @Trace(tag = "joinForum")
    @UiThread
    void joinForum() {
        if (forumType != null) {
            Log.i("loadData " + forumPagerItem.title, "joinForum start");
        } else {
            Log.i("loadData " + userTopicType, " joinForum start");
        }

        // Join Thread
        if (prepareRecommendDone && prepareTopicDone) {
            // Update List
            forumAdapter.notifyDataSetChanged();
            if (forumType != null) {
                ((ForumAdapter) forumAdapter).setData(forum, forumPart, recommendTopicTitle, recommendTopicUrl);
            } else {
                forumAdapter.setTopics(myPage.getResult());
            }
            setRefreshComplete();
            if (lastFirstVisibleItem != 0) {
                list.setSelection(lastFirstVisibleItem);
            }
            Log.i("loadData", "joinForum finish");
        } else {
            Log.i("loadData", "joinForum postpone");
        }


    }



    void showErrorScreen() {
        setRefreshComplete();
        if (emptyView != null && emptyView.getParent() != null) {
            root.removeView(emptyView);
        }
        if (forumEmptyView == null) {
            forumEmptyView = ForumEmptyView_.build(getAttachedActivity());
            forumEmptyView.setOnRetry(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refresh();
                }
            });
        }
        if (forumEmptyView.getParent() == null) {
            root.addView(forumEmptyView);
        }
        list.setEmptyView(forumEmptyView);

    }

    void showLoadingScreen() {
        if (forumEmptyView != null && forumEmptyView.getParent() != null) {
            root.removeView(forumEmptyView);
        }
        if (emptyView == null) {
            emptyView = SimpleEmptyView_.build(getActivity());
        }
        if (emptyView.getParent() == null) {
            root.addView(emptyView);
        }
        list.setEmptyView(emptyView);

    }

    private void hideFab() {
        if (!fabIsHiding) {
            fab.animate()
                    .translationY(fab.getHeight() * 3)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
            fabIsHiding = true;
        }
    }

    private void showFab() {
        if (fabIsHiding) {
            fab.animate().translationY(fabDefaultY).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            fabIsHiding = false;
        }
    }


    @Override
    public void onPause() {
        app.getEventBus().unregister(this);
        super.onPause();
    }

    @Subscribe
    public void sortForum(final SortForumEvent e) {

        final ArrayList<Topic> topics = e.getTopics();

        new MaterialDialog.Builder(getAttachedActivity())
                .title(topic_sort_type_title)
                .items(topic_sort_type)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int i, CharSequence text) {
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
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @OptionsItem
    void action_sort_topic() {
        Log.d("menu", "sort");
        new MaterialDialog.Builder(getAttachedActivity())
                .title(topic_sort_type_title)
                .items(topic_sort_type)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int i, CharSequence text) {
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
                        Collections.sort(forum.getTopics(), topicComparator);
                        forumAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @OptionsItem
    void action_topic_type() {
        new MaterialDialog.Builder(getAttachedActivity())
                .title(R.string.title_topic_type_dialog)
                .items(topic_type)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int i, CharSequence text) {
                        switch (i) {
                            case 0:
                                topicType = TopicType.All_Except_Sell;
                                break;
                            case 1:
                                topicType = TopicType.Question;
                                break;
                            case 2:
                                topicType = TopicType.Chat;
                                break;
                            case 3:
                                topicType = TopicType.Poll;
                                break;
                            case 4:
                                topicType = TopicType.Review;
                                break;
                            case 5:
                                topicType = TopicType.News;
                                break;
                            case 6:
                                topicType = TopicType.Sell;
                                break;
                        }
                        refresh();
                    }
                })
                .show();
    }

    @OptionsItem
    void action_view_tag() {
        if (forumPart != null) {
            new MaterialDialog.Builder(getAttachedActivity())
                    .title(R.string.title_tags_dialog)
                    .items(forumPart.getTag().toArray(new String[forumPart.getTag().size()]))
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int i, CharSequence text) {
                            ForumPagerItem forumPagerItem = new ForumPagerItem(forumPart.getTag().get(i), Utils.getForumPath(forumPart.getTagUrl().get(i)));
                            app.fireEvent(new OpenForumEvent(forumPagerItem, ForumType.Tag));
                        }
                    })
                    .show();
        } else {
            toastAlert(app.getString(R.string.feecback_waiting_forumpart_load));
        }
    }

    @OptionsItem
    void action_view_club() {
        if (forumPart != null) {

            new MaterialDialog.Builder(getAttachedActivity())
                    .title(R.string.title_club_dialog)
                    .items(forumPart.getClub().toArray(new String[forumPart.getClub().size()]))
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int i, CharSequence text) {
                            ForumPagerItem forumPagerItem = new ForumPagerItem(forumPart.getClub().get(i), Utils.getForumPath(forumPart.getClubUrl().get(i)));
                            app.fireEvent(new OpenForumEvent(forumPagerItem, ForumType.Club));
                        }
                    })
                    .show();
        } else {
            toastAlert(app.getString(R.string.feecback_waiting_forumpart_load));
        }
    }


    @UiThread
    void setRefreshComplete() {
        Log.d("refresh", "stop");
        swipeRefreshLayout.setRefreshing(false);
        if (loadingItemView != null) {
            loadingItemView.setVisibility(View.GONE);
        }
    }

    @AfterViews
    void
    init() {
        topicType = TopicType.All_Except_Sell;

        fabDefaultY = fab.getY();
        if (forumType != null) {
            Log.d("forum", "init forum fragment " + forumPagerItem.title);
        } else {
            Log.d("forum", "init forum fragment " + userTopicType);
        }
        int toolbarAndNavSize = getResources().getDimensionPixelSize(R.dimen.tabs_height) + getResources().getDimensionPixelSize(R.dimen.toolbar_size);


        if (forumType != null && userTopicType == null) {
            forumAdapter = new ForumAdapter(getAttachedActivity(), forum, forumPart, forumType, recommendTopicTitle, recommendTopicUrl);
        } else if (forumType == null && userTopicType != null) {
            forumAdapter = new TopicAdapter(getAttachedActivity(), new ArrayList<Topic>());
        }
        forumAdapter.setLoadMoreListener(new TopicAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMore();
            }
        });
        loadingItemView = forumAdapter.getLoadingItemView();

        MyAnimationAdapter animationAdapter = new MyAnimationAdapter(forumAdapter);
        animationAdapter.setAbsListView(list);
        showLoadingScreen();

        list.setAdapter(animationAdapter);
        list.setEmptyView(emptyView);

        // Add Blank Margin on top height = Tab's height
        if (!noTabMargin) {
            View blankHeader = new View(getAttachedActivity());
            blankHeader.setMinimumHeight(toolbarAndNavSize);
            list.addHeaderView(blankHeader);
        } else {
            getAttachedActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        swipeRefreshLayout.setProgressViewOffset(false
                , getResources().getDimensionPixelSize(R.dimen.toolbar_size)
                , DeviceUtils.getDisplayCenterPixel(getAttachedActivity()));

        // Now setup the PullToRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this);
        Log.d("refresh", toolbarAndNavSize + "");
        swipeRefreshLayout.setColorSchemeResources(R.color.base_color);

        // Attach scroll listener
        Log.d("forum", "init : lastFirstVisibleItem -> " + lastFirstVisibleItem);
        list.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int i, boolean b, boolean b2) {

            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {
                if (scrollState == ScrollState.UP) {
                    app.getEventBus().post(new ForumScrollDownEvent());
                    hideFab();
                } else if (scrollState == ScrollState.DOWN) {
                    app.getEventBus().post(new ForumScrollUpEvent());
                    showFab();
                }
            }
        });


        // If from saved
        if (forum != null && forumPart != null) {
            prepareRecommendCard();
            prepareTopic();
            return;
        } else {
            // Load Initial Data
            loadMore();
            loadForumPart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        app.getEventBus().register(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        lastFirstVisibleItem = list.getFirstVisiblePosition();

        super.onSaveInstanceState(outState);


    }

    private void loadForumPart() {
        if (!swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(true);
        if (forumType == ForumType.Room) {
            prepareRecommendDone = false;
            client.getForumPart(forumPagerItem.url, forumType, forumPartCallback);
        } else {
            prepareRecommendDone = true;
            joinForum();
        }
    }

    private void refresh() {
        showLoadingScreen();
        prepareRecommendDone = false;
        prepareTopicDone = false;
        currentPage = 0;
        lastIdCurrentPage = "0";
        lastFirstVisibleItem = 0;
        cardRenderCount = 0;
        forumPart = null;
        forum = null;
        myPage = null;
        lastFirstId = 0;
        lastLastId = 0;
        // Load Initial Data
        forumAdapter.notifyDataSetChanged();
        loadMore();
        loadForumPart();
    }

    private void loadMore() {
        loadMore(true);
    }

    private void loadMore(boolean showSwipeRefresh) {
        currentPage++;

        if (loadingItemView != null) {
            loadingItemView.setVisibility(View.VISIBLE);
        }
//        if (!swipeRefreshLayout.isRefreshing())
//            swipeRefreshLayout.setRefreshing(showSwipeRefresh);
        prepareTopicDone = false;

        if (forumType != null) {
            client.getForum(
                    forumPagerItem.url,
                    forumType,
                    topicType,
                    currentPage,
                    lastIdCurrentPage,
                    false,
                    forumCallback
            );
        } else if (userTopicType != null) {
            Log.d("Load User Topic ", userTopicType.toString());
            if (myPage == null || currentPage < myPage.getMax_page())
                client.getUserTopic(userId, userTopicType, currentPage, lastFirstId, lastLastId, userForumCallback);
            else
                prepareTopic();

        }
    }

    @Override
    public void onRefresh() {
        refresh();
    }


}
