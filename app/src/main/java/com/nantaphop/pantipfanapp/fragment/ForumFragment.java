package com.nantaphop.pantipfanapp.fragment;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.ads.AdView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.ForumScrollDownEvent;
import com.nantaphop.pantipfanapp.event.ForumScrollUpEvent;
import com.nantaphop.pantipfanapp.event.OpenForumEvent;
import com.nantaphop.pantipfanapp.event.OpenTopicEvent;
import com.nantaphop.pantipfanapp.event.ShowRecommendEvent;
import com.nantaphop.pantipfanapp.event.SortForumEvent;
import com.nantaphop.pantipfanapp.event.ToggleDrawerEvent;
import com.nantaphop.pantipfanapp.fragment.dialog.ListDialog;
import com.nantaphop.pantipfanapp.fragment.dialog.ListDialog_;
import com.nantaphop.pantipfanapp.model.ForumPagerItem;
import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.ForumPart;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.utils.DeviceUtils;
import com.nantaphop.pantipfanapp.utils.RESTUtils;
import com.nantaphop.pantipfanapp.utils.ScrollDirectionListener;
import com.nantaphop.pantipfanapp.utils.TopicComparator;
import com.nantaphop.pantipfanapp.utils.Utils;
import com.nantaphop.pantipfanapp.view.ForumEmptyView;
import com.nantaphop.pantipfanapp.view.ForumEmptyView_;
import com.nantaphop.pantipfanapp.view.LoadingItemView;
import com.nantaphop.pantipfanapp.view.LoadingItemView_;
import com.nantaphop.pantipfanapp.view.MyAnimationAdapter;
import com.nantaphop.pantipfanapp.view.RecommendCardView;
import com.nantaphop.pantipfanapp.view.RecommendCardView_;
import com.nantaphop.pantipfanapp.view.SimpleEmptyView;
import com.nantaphop.pantipfanapp.view.SimpleEmptyView_;
import com.nantaphop.pantipfanapp.view.TopicSectionView;
import com.nantaphop.pantipfanapp.view.TopicSectionView_;
import com.nantaphop.pantipfanapp.view.TopicThumbnailView;
import com.nantaphop.pantipfanapp.view.TopicThumbnailView_;
import com.nantaphop.pantipfanapp.view.TopicView;
import com.nantaphop.pantipfanapp.view.TopicView_;
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

import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Collections;

import it.gmariotti.cardslib.library.internal.Card;

import static com.nantaphop.pantipfanapp.service.PantipRestClient.ForumType;
import static com.nantaphop.pantipfanapp.service.PantipRestClient.TopicType;

/**
 * Created by nantaphop on 27-Jul-14.
 */
@EFragment(R.layout.fragment_forum)
public class ForumFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @ViewById
    ObservableListView list;
    //    @ViewById
//    CardListView cardList;
    @ViewById
    FloatingActionButton fab;
    @ViewById
    FrameLayout root;


    @FragmentArg
    ForumPagerItem forumPagerItem;

    @FragmentArg
    ForumType forumType;

    @FragmentArg
    boolean noTabMargin = false;


    @InstanceState
    String lastIdCurrentPage = "0";
    @InstanceState
    int currentPage = 0;
    @InstanceState
    int cardRenderCount = 0;

    @InstanceState
    Forum forum;
    @InstanceState
    ForumPart forumPart;

//    private CardArrayAdapter cardArrayAdapter;

//    private SectionedCardAdapter sectionedCardAdapter;

    @InstanceState
    int lastFirstVisibleItem;

    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;

    @StringRes
    String topic_sort_type_title;
    @StringArrayRes
    String[] topic_sort_type;
    @StringArrayRes
    String[] topic_type;


    private ForumEmptyView forumEmptyView;
    BaseJsonHttpResponseHandler forumCallback = new BaseJsonHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, String s, Object o) {
            Log.i("loadData " + forumPagerItem.title, "forumCallback success");
            Log.d("forum", "success");
            Forum newForum = (Forum) o;
//            if (currentPage == 1f) {
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
//            prepareTopicDone = true;
//            joinForum();
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
//            prepareRecommendDone = true;
//            joinForum();
        }
    };
    private ArrayList<Card> tmpRecommendCard;
    private ArrayList<Card> tmpTopicCard;
    private ScrollDirectionListener mOnScrollListener;
    private boolean fabAnimating;
    private Animator.AnimatorListener fabAnimListener;
    private boolean reInitFragment;
    private boolean prepareTopicDone;
    private boolean prepareRecommendDone;
    private float fabDefaultY;
    private boolean fabIsHiding;
    private ArrayList<Card> cards;
    private TopicAdapter topicAdapter;
    @InstanceState
    public String[] recommendTopicTitle;
    @InstanceState
    public String[] recommendTopicUrl;
    private SimpleEmptyView emptyView;
    private TopicType topicType;
    private LoadingItemView loadingItemView;

    @Trace
    @Background
    void prepareForumPart() {
        Log.i("loadData " + forumPagerItem.title, "prepareForumPart start");
        forumPart = RESTUtils.parseForumPart(new String(tmpForumPartBytes));
        if(forumType == ForumType.Room && forumPart.getRecommendTopic().get(0) == null){
            Log.e("forumPath", "CANT PARSE FORUMPATH\n"+new String(tmpForumPartBytes));
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

    private void showRecommendDialog() {

        MaterialDialog dialog = new MaterialDialog.Builder(getAttachedActivity())
                .title(R.string.recomend_topic)
                .adapter(new ArrayAdapter<String>(getAttachedActivity(), R.layout.listitem_recommend_dialog, forumPart.getRecommendTopic()))
//                .items(forumPart.getRecommendTopic().toArray(new String[forumPart.getRecommendTopic().size()]))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int i, CharSequence text) {

                    }
                }).build();
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                final Topic topic = new Topic();
                topic.setTitle(forumPart.getRecommendTopic().get(i));
                topic.setId(Integer.parseInt(forumPart.getRecommendUrl().get(i).split("/")[4]));
                app.getEventBus().post(new OpenTopicEvent(topic));
            }
        });
        dialog.show();

//        recommendDialog = PostOfficeHelper.newSimpleListMailCancelable(
//                getAttachedActivity()
//                , getString(R.string.recomend_topic)
//                , Design.MATERIAL_LIGHT
//                , forumPart.getRecommendTopic().toArray(new String[forumPart.getRecommendTopic().size()])
//                , new ListStyle.OnItemAcceptedListener<CharSequence>() {
//                    @Override
//                    public void onItemAccepted(CharSequence charSequence, int i) {
//                        final Topic topic = new Topic();
//                        topic.setTitle(forumPart.getRecommendTopic().get(i));
//                        topic.setId(Integer.parseInt(forumPart.getRecommendUrl().get(i).split("/")[4]));
//                        app.getEventBus().post(new OpenTopicEvent(topic));
//
//                    }
//                }
//        );
//        recommendDialog.show(getFragmentManager());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(noTabMargin){
            inflater.inflate(R.menu.menu_forum_tag, menu);
        }else{
            inflater.inflate(R.menu.menu_forum, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Trace
    @Background
    void prepareTopic() {
//        tmpTopicCard = forum.toCardList(getAttachedActivity(), cardRenderCount);
        Log.i("loadData " + forumPagerItem.title, "prepareTopic start");

        prepareTopicDone = true;
        joinForum();
        Log.i("loadData " + forumPagerItem.title, "prepareTopic finish");

    }

//    @Background
//    void prepareTopicFromInstanceState() {
//        prepareTopicDone = true;
//        joinForum();
//    }

    @Trace(tag = "joinForum")
    @UiThread
    void joinForum() {
        Log.i("loadData " + forumPagerItem.title, "joinForum start");

        // Join Thread
        if (prepareRecommendDone && prepareTopicDone) {
            // Update List
            topicAdapter.notifyDataSetChanged();
            setRefreshComplete();
            if (lastFirstVisibleItem != 0) {
                list.setSelection(lastFirstVisibleItem);
            }
            Log.i("loadData " + forumPagerItem.title, "joinForum finish");
        }else{
            Log.i("loadData " + forumPagerItem.title, "joinForum postpone");
        }



    }

    @Override
    void back() {
        app.getEventBus().post(new ToggleDrawerEvent());
    }

    void showErrorScreen(){
        setRefreshComplete();
        if(emptyView != null && emptyView.getParent() != null){
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

    void showLoadingScreen(){
        if(forumEmptyView!= null && forumEmptyView.getParent() != null){
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
//        if (recommendDialog != null) {
//            recommendDialog.dismiss();
//        }
//        if (sortDialog != null) {
//            sortDialog.dismiss();
//        }
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



//        final ListDialog listDialog = ListDialog_.builder()
//                .choices(topic_sort_type)
//                .title(topic_sort_type_title)
//                .listItemLayoutRes(android.R.layout.simple_list_item_1)
//                .build();
//        listDialog.setOnItemClickListener(
//                new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        TopicComparator topicComparator;
//                        switch (i) {
//                            case 0:
//                                topicComparator = new TopicComparator(TopicComparator.SortType.Comment);
//                                break;
//                            case 1:
//                                topicComparator = new TopicComparator(TopicComparator.SortType.Vote);
//                                break;
//                            case 2:
//                                topicComparator = new TopicComparator(TopicComparator.SortType.Time);
//                                break;
//                            default:
//                                topicComparator = new TopicComparator(TopicComparator.SortType.Time);
//                                break;
//                        }
//                        Collections.sort(topics, topicComparator);
//                        e.getAdapter().notifyDataSetChanged();
//                        listDialog.dismiss();
//                    }
//                }
//        );
//        listDialog.show(getAttachedActivity().getFragmentManager(), "sort_topic");
    }

    @OptionsItem
    void action_sort_topic() {
        Log.d("menu", "sort - " + forumPagerItem.title);
//        app.getEventBus().post(new SortForumEvent(forum.getTopics(), topicAdapter));

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
                        topicAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .show();

//        sortDialog = PostOfficeHelper.newSimpleListMailCancelable(
//                getAttachedActivity()
//                , topic_sort_type_title
//                , Design.MATERIAL_LIGHT
//                , topic_sort_type
//                , new ListStyle.OnItemAcceptedListener<CharSequence>() {
//                    @Override
//                    public void onItemAccepted(CharSequence charSequence, int i) {
//                        TopicComparator topicComparator;
//                        switch (i) {
//                            case 0:
//                                topicComparator = new TopicComparator(TopicComparator.SortType.Comment);
//                                break;
//                            case 1:
//                                topicComparator = new TopicComparator(TopicComparator.SortType.Vote);
//                                break;
//                            case 2:
//                                topicComparator = new TopicComparator(TopicComparator.SortType.Time);
//                                break;
//                            default:
//                                topicComparator = new TopicComparator(TopicComparator.SortType.Time);
//                                break;
//                        }
//                        Collections.sort(forum.getTopics(), topicComparator);
//                        topicAdapter.notifyDataSetChanged();
////                        listDialog.dismiss();
//                    }
//                }
//        );
//        sortDialog.show(getFragmentManager());
    }

    @OptionsItem
    void action_topic_type() {
//        app.getEventBus().post(new ChooseTopicType());

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

//        PostOfficeHelper.newSimpleListMailCancelable(
//                getAttachedActivity()
//                , app.getString(R.string.title_topic_type_dialog)
//                , Design.MATERIAL_LIGHT
//                , topic_type
//                , new ListStyle.OnItemAcceptedListener<CharSequence>() {
//                    @Override
//                    public void onItemAccepted(CharSequence charSequence, int i) {
//                        switch (i) {
//                            case 0:
//                                topicType = TopicType.All_Except_Sell;
//                                break;
//                            case 1:
//                                topicType = TopicType.Question;
//                                break;
//                            case 2:
//                                topicType = TopicType.Chat;
//                                break;
//                            case 3:
//                                topicType = TopicType.Poll;
//                                break;
//                            case 4:
//                                topicType = TopicType.Review;
//                                break;
//                            case 5:
//                                topicType = TopicType.News;
//                                break;
//                            case 6:
//                                topicType = TopicType.Sell;
//                                break;
//                        }
//                        refresh();
//                    }
//                }
//        ).show(getFragmentManager());
    }

    @OptionsItem
    void action_view_tag() {
        if (forumPart!=null) {

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

//            PostOfficeHelper.newSimpleListMailCancelable(
//                    getAttachedActivity()
//                    , app.getString(R.string.title_tags_dialog)
//                    , Design.MATERIAL_LIGHT
//                    , forumPart.getTag().toArray(new String[forumPart.getTag().size()])
//                    , new ListStyle.OnItemAcceptedListener<CharSequence>() {
//                        @Override
//                        public void onItemAccepted(CharSequence charSequence, int i) {
//                            ForumPagerItem forumPagerItem = new ForumPagerItem(forumPart.getTag().get(i), Utils.getForumPath(forumPart.getTagUrl().get(i)));
//                            app.fireEvent(new OpenForumEvent(forumPagerItem, ForumType.Tag));
//                        }
//                    }
//            ).show(getFragmentManager());
        } else {
            toastAlert(app.getString(R.string.feecback_waiting_forumpart_load));
        }
    }

    @OptionsItem
    void action_view_club() {
        if (forumPart!=null) {

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

//            PostOfficeHelper.newSimpleListMailCancelable(
//                    getAttachedActivity()
//                    , app.getString(R.string.title_club_dialog)
//                    , Design.MATERIAL_LIGHT
//                    , forumPart.getClub().toArray(new String[forumPart.getClub().size()])
//                    , new ListStyle.OnItemAcceptedListener<CharSequence>() {
//                        @Override
//                        public void onItemAccepted(CharSequence charSequence, int i) {
//                            ForumPagerItem forumPagerItem = new ForumPagerItem(forumPart.getClub().get(i), Utils.getForumPath(forumPart.getClubUrl().get(i)));
//                            app.fireEvent(new OpenForumEvent(forumPagerItem, ForumType.Club));
//                        }
//                    }
//            ).show(getFragmentManager());
        } else {
            toastAlert(app.getString(R.string.feecback_waiting_forumpart_load));
        }
    }


    @UiThread
    void setRefreshComplete() {
        Log.d("refresh", "stop");
        swipeRefreshLayout.setRefreshing(false);
        if(loadingItemView != null){
            loadingItemView.setVisibility(View.GONE);
        }
    }



    @AfterViews
    void
    init() {
        topicType = TopicType.All_Except_Sell;

        fabDefaultY = fab.getY();
        Log.d("forum", "init forum fragment " + forumPagerItem.title);
        int toolbarAndNavSize = getResources().getDimensionPixelSize(R.dimen.tabs_height) + getResources().getDimensionPixelSize(R.dimen.toolbar_size);



        topicAdapter = new TopicAdapter();
        MyAnimationAdapter animationAdapter = new MyAnimationAdapter(topicAdapter);
        animationAdapter.setAbsListView(list);
        showLoadingScreen();

        list.setAdapter(animationAdapter);
        list.setEmptyView(emptyView);


        // Add Blank Margin on top height = Tab's height
        if (!noTabMargin) {
            View blankHeader = new View(getAttachedActivity());
            blankHeader.setMinimumHeight(toolbarAndNavSize);
            list.addHeaderView(blankHeader);
        }else{
            getAttachedActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        swipeRefreshLayout.setProgressViewOffset(false
                , getResources().getDimensionPixelSize(R.dimen.toolbar_size)
                , DeviceUtils.getDisplayCenterPixel(getAttachedActivity()));

        // Now setup the PullToRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this);
        Log.d("refresh", toolbarAndNavSize+"");
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
                if(scrollState == ScrollState.UP){
                    app.getEventBus().post(new ForumScrollDownEvent());
                    hideFab();
                }else if (scrollState == ScrollState.DOWN){
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


    public ScrollDirectionListener getOnScrollListener() {
        return mOnScrollListener;
    }

    private void loadForumPart() {
        if (!swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(true);
        prepareRecommendDone = false;
        client.getForumPart(forumPagerItem.url, forumType, forumPartCallback);
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
        // Load Initial Data
        topicAdapter.notifyDataSetChanged();
        loadMore();
        loadForumPart();
    }

    private void loadMore() {
        loadMore(true);
    }

    private void loadMore(boolean showSwipeRefresh) {
        currentPage++;
        if(loadingItemView!= null){
            loadingItemView.setVisibility(View.VISIBLE);
        }
//        if (!swipeRefreshLayout.isRefreshing())
//            swipeRefreshLayout.setRefreshing(showSwipeRefresh);
        prepareTopicDone = false;
        client.getForum(
                forumPagerItem.url,
                forumType,
                topicType,
                currentPage,
                lastIdCurrentPage,
                false,
                forumCallback
        );
    }

    private int getFabMarginBottom() {
        int marginBottom = 0;
        final ViewGroup.LayoutParams layoutParams = fab.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        }
        return marginBottom;
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    public class TopicAdapter extends BaseAdapter {

        final int TYPE_SECTION = 2;
        final int TYPE_RECOMMEND = 1;
        final int TYPE_TOPIC = 0;
        final int TYPE_TOPIC_THUMBNAIL = 3;
        final int TYPE_TOPIC_LOADING = 4;


        @Override
        public int getCount() {
            if (forum == null || forumPart == null)
                return 0;
            else return forum.getTopics().size() + 3;
        }

        @Override
        public Object getItem(int i) {
            if (forumType == ForumType.Room) {
                if (recommendTopicTitle != null && recommendTopicTitle.length > 0)
                    return forum.getTopics().get(i -3);
                else
                    return forum.getTopics().get(i);
            } else {
                return forum.getTopics().get(i);
            }
        }

        @Override
        public void notifyDataSetChanged() {
            Log.i("loadData "+forumPagerItem.title, "notifyDataSetChanged");
            super.notifyDataSetChanged();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 5;
        }

        @Override
        public int getItemViewType(int position) {
//            if (recommendTopicTitle == null) {
//                return 0;
//            }
            if(getCount()>3 && position == (getCount()-1)){
                return TYPE_TOPIC_LOADING;
            }else if (forumType == ForumType.Room && (position == 0 || position == 2) ) {
                return TYPE_SECTION;
            } else if (forumType == ForumType.Room && (position == 1) ) {
                return TYPE_RECOMMEND;
            } else {
                Topic topic = null;
                try {
                    topic = (Topic) getItem(position);
                } catch (Exception e) {
                    return TYPE_TOPIC;
                }
                if (topic.getCoverImg().length() > 0)
                    return TYPE_TOPIC_THUMBNAIL;
                else
                    return TYPE_TOPIC;
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = null;
            switch (getItemViewType(position)) {
                case TYPE_TOPIC:
                    view = getViewTopic(position, convertView);
                    break;
                case TYPE_SECTION:
                    view = getViewTopicSection(position, convertView);
                    break;
                case TYPE_RECOMMEND:
                    view = getViewTopicRecommend(position, convertView);
                    break;
                case TYPE_TOPIC_THUMBNAIL:
                    view = getViewTopicThumbnail(position, convertView);
                    break;
                case TYPE_TOPIC_LOADING:
                    view = getViewLoading(position, convertView);
            }
            return view;
        }

        private LoadingItemView getViewLoading(int position, View convertView) {

            if (convertView != null) {
                loadingItemView = (LoadingItemView) convertView;
            } else {
                loadingItemView = LoadingItemView_.build(getAttachedActivity());
            }

            return loadingItemView;
        }

        private RecommendCardView getViewTopicRecommend(int position, View convertView) {
            final RecommendCardView recommendCardView;

            if (convertView != null) {
                recommendCardView = (RecommendCardView) convertView;
            } else {
                recommendCardView = RecommendCardView_.build(getAttachedActivity());
            }
            if (recommendTopicTitle!=null) {
                for(int i=0;i< recommendTopicTitle.length; i++) {
                    recommendCardView.addItem(recommendTopicTitle[i], recommendTopicUrl[i]);
                }
            }else{
                Log.e("recommend", "getViewTopicRecommend called even recommendTopicTitle is null");
            }
            return recommendCardView;
        }

        private TopicSectionView getViewTopicSection(int position, View convertView) {
            final TopicSectionView topicSectionView;
            if (convertView != null) {
                topicSectionView = (TopicSectionView) convertView;
            } else {
                topicSectionView = TopicSectionView_.build(getAttachedActivity());

            }
            if (position == 0) {
                topicSectionView.bind(
                        getString(R.string.recomend_topic), getString(R.string.view_all), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showRecommendDialog();
                            }
                        }
                );
            } else if (position == 2) {
                topicSectionView.bind(getString(R.string.topic_in_forum));
            }
            return topicSectionView;
        }

        private TopicView getViewTopic(int position, View convertView) {
            final TopicView topicView;
            if (position == getCount() - 5) {
                loadMore();
            }

            if (convertView != null) {
                topicView = (TopicView) convertView;
            } else {
                topicView = TopicView_.build(getAttachedActivity());

            }
            topicView.bind((Topic) getItem(position));
            return topicView;
        }

        private TopicThumbnailView getViewTopicThumbnail(int position, View convertView) {
            final TopicThumbnailView topicView;
            if (position == getCount() - 5) {
                loadMore(false);
            }

            if (convertView != null) {
                topicView = (TopicThumbnailView) convertView;
            } else {
                topicView = TopicThumbnailView_.build(getAttachedActivity());

            }
            topicView.bind((Topic) getItem(position));
            return topicView;
        }


    }


}
