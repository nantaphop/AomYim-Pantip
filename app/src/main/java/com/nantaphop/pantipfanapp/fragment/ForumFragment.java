package com.nantaphop.pantipfanapp.fragment;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.*;
import com.nantaphop.pantipfanapp.fragment.dialog.ListDialog;
import com.nantaphop.pantipfanapp.fragment.dialog.ListDialog_;
import com.nantaphop.pantipfanapp.model.ForumPagerItem;
import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.ForumPart;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.utils.*;
import com.nantaphop.pantipfanapp.view.*;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.ListStyle;
import com.squareup.otto.Subscribe;
import it.gmariotti.cardslib.library.internal.Card;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;
import org.apache.http.Header;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

import static com.nantaphop.pantipfanapp.service.PantipRestClient.ForumType;
import static com.nantaphop.pantipfanapp.service.PantipRestClient.TopicType;

/**
 * Created by nantaphop on 27-Jul-14.
 */
@EFragment(R.layout.fragment_forum)
public class ForumFragment extends BaseFragment implements OnRefreshListener {

    @ViewById
    ListView list;
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
    int currentPage = 1;
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
    PullToRefreshLayout pullToRefreshLayout;

    @StringRes
    String topic_sort_type_title;
    @StringArrayRes
    String[] topic_sort_type;
    @StringArrayRes
    String[] topic_type;


    BaseJsonHttpResponseHandler forumCallback = new BaseJsonHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, String s, Object o) {
            Log.d("forum", "success");
            Forum newForum = (Forum) o;
            if (currentPage == 1) {
                forum = newForum;
                lastIdCurrentPage = forum.getLastIdCurrentPage();
            } else {
                forum.getTopics().addAll(newForum.getTopics());
                lastIdCurrentPage = newForum.getLastIdCurrentPage();
            }
            currentPage++;

            prepareTopic();
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Object o) {
            Log.d("forum", "failed load forum");
            loadMore();
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
            tmpForumPartBytes = bytes;
            prepareForumPart();
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Log.d("forum", "failed load forum part");
            loadForumPart();
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
    private Delivery recommendDialog;
    private TopicType topicType;
    private Delivery sortDialog;

    @Trace
    @Background
    void prepareForumPart() {
        forumPart = RESTUtils.parseForumPart(new String(tmpForumPartBytes));
        Log.d(
                "forumPart",
                "prepareForumPart recommend size : " + forumPart.getRecommendUrl()
                        .size() + " - " + forumPart.getRecommendTopic().size()
        );
        for (String s : forumPart.getRecommendTopic()) {
            Log.d("recommend", forumPagerItem.title + " " + s);
        }
        prepareRecommendCard();


    }

    @Background
    public void prepareRecommendCard() {
        int numPreview = forumPart.getRecommendTopic().size() > 3 ? 3 : forumPart.getRecommendTopic().size();
        recommendTopicTitle = new String[numPreview];
        recommendTopicUrl = new String[numPreview];
        if (currentPage == 2) { // Do just first load
//            // Add Recommend Topic
            for (int i = 0; i < numPreview; i++) {
                Log.d("recommend", "prepareRecommendCard " + i + " : " + forumPart.getRecommendTopic().get(i));
                recommendTopicTitle[i] = forumPart.getRecommendTopic().get(i);
                recommendTopicUrl[i] = forumPart.getRecommendUrl().get(i);
            }
        }
        tmpForumPartBytes = null;
        prepareRecommendDone = true;
        joinForum();
    }

    private void showRecommendDialog() {
        recommendDialog = PostOfficeHelper.newSimpleListMailCancelable(
                getAttachedActivity()
                , getString(R.string.recomend_topic)
                , Design.MATERIAL_LIGHT
                , forumPart.getRecommendTopic().toArray(new String[forumPart.getRecommendTopic().size()])
                , new ListStyle.OnItemAcceptedListener<CharSequence>() {
                    @Override
                    public void onItemAccepted(CharSequence charSequence, int i) {
                        final Topic topic = new Topic();
                        topic.setTitle(forumPart.getRecommendTopic().get(i));
                        topic.setId(Integer.parseInt(forumPart.getRecommendUrl().get(i).split("/")[4]));
                        app.getEventBus().post(new OpenTopicEvent(topic));

                    }
                }
        );
        recommendDialog.show(getFragmentManager());
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
        prepareTopicDone = true;
        joinForum();
    }

//    @Background
//    void prepareTopicFromInstanceState() {
//        prepareTopicDone = true;
//        joinForum();
//    }

    @Trace(tag = "joinForum")
    @UiThread
    void joinForum() {
        // Join Thread
        if (prepareRecommendDone && prepareTopicDone) {
            // Update List
            topicAdapter.notifyDataSetChanged();
            setRefreshComplete();
            if (lastFirstVisibleItem != 0) {
                list.setSelection(lastFirstVisibleItem);
            }
        }


    }

    @Override
    void back() {
        app.getEventBus().post(new ToggleDrawerEvent());
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
        if (recommendDialog != null) {
            recommendDialog.dismiss();
        }
        if (sortDialog != null) {
            sortDialog.dismiss();
        }
        app.getEventBus().unregister(this);
        super.onPause();

    }

    @Subscribe
    public void showRecommend(final ShowRecommendEvent e) {
//        RecommendDialog recommendDialog = RecommendDialog_.builder().topics(e.getRecommendTopics()).urls(e.getRecommendUrls()).build();
//        recommendDialog.show(getFragmentManager(), "recommend");
        final ListDialog listDialog = ListDialog_.builder()
                .choicesArrayList(e.getRecommendTopics())
                .title(getString(R.string.recomend_topic))
                .listItemLayoutRes(R.layout.listitem_recommend_dialog)
                .build();
        listDialog.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final Topic topic = new Topic();
                        topic.setTitle(e.getRecommendTopics().get(i));
                        topic.setId(Integer.parseInt(e.getRecommendUrls().get(i).split("/")[4]));
                        app.getEventBus().post(new OpenTopicEvent(topic));
                        listDialog.dismiss();

                    }
                }
        );
        listDialog.show(getAttachedActivity().getFragmentManager(), null);
    }

    @Subscribe
    public void sortForum(final SortForumEvent e) {

        final ArrayList<Topic> topics = e.getTopics();
        final ListDialog listDialog = ListDialog_.builder()
                .choices(topic_sort_type)
                .title(topic_sort_type_title)
                .listItemLayoutRes(android.R.layout.simple_list_item_1)
                .build();
        listDialog.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
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
                }
        );
        listDialog.show(getAttachedActivity().getFragmentManager(), "sort_topic");
    }

    @OptionsItem
    void action_sort_topic() {
        Log.d("menu", "sort - " + forumPagerItem.title);
//        app.getEventBus().post(new SortForumEvent(forum.getTopics(), topicAdapter));
        sortDialog = PostOfficeHelper.newSimpleListMailCancelable(
                getAttachedActivity()
                , topic_sort_type_title
                , Design.MATERIAL_LIGHT
                , topic_sort_type
                , new ListStyle.OnItemAcceptedListener<CharSequence>() {
                    @Override
                    public void onItemAccepted(CharSequence charSequence, int i) {
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
//                        listDialog.dismiss();
                    }
                }
        );
        sortDialog.show(getFragmentManager());
    }

    @OptionsItem
    void action_topic_type() {
//        app.getEventBus().post(new ChooseTopicType());
        PostOfficeHelper.newSimpleListMailCancelable(
                getAttachedActivity()
                , app.getString(R.string.title_topic_type_dialog)
                , Design.MATERIAL_LIGHT
                , topic_type
                , new ListStyle.OnItemAcceptedListener<CharSequence>() {
                    @Override
                    public void onItemAccepted(CharSequence charSequence, int i) {
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
                }
        ).show(getFragmentManager());
    }

    @OptionsItem
    void action_view_tag() {
        if (forumPart!=null) {
            PostOfficeHelper.newSimpleListMailCancelable(
                    getAttachedActivity()
                    , app.getString(R.string.title_tags_dialog)
                    , Design.MATERIAL_LIGHT
                    , forumPart.getTag().toArray(new String[forumPart.getTag().size()])
                    , new ListStyle.OnItemAcceptedListener<CharSequence>() {
                        @Override
                        public void onItemAccepted(CharSequence charSequence, int i) {
                            ForumPagerItem forumPagerItem = new ForumPagerItem(forumPart.getTag().get(i), Utils.getForumPath(forumPart.getTagUrl().get(i)));
                            app.fireEvent(new OpenForumEvent(forumPagerItem, ForumType.Tag));
                        }
                    }
            ).show(getFragmentManager());
        } else {
            toastAlert(app.getString(R.string.feecback_waiting_forumpart_load));
        }
    }

    @OptionsItem
    void action_view_club() {
        if (forumPart!=null) {
            PostOfficeHelper.newSimpleListMailCancelable(
                    getAttachedActivity()
                    , app.getString(R.string.title_club_dialog)
                    , Design.MATERIAL_LIGHT
                    , forumPart.getClub().toArray(new String[forumPart.getClub().size()])
                    , new ListStyle.OnItemAcceptedListener<CharSequence>() {
                        @Override
                        public void onItemAccepted(CharSequence charSequence, int i) {
                            ForumPagerItem forumPagerItem = new ForumPagerItem(forumPart.getClub().get(i), Utils.getForumPath(forumPart.getClubUrl().get(i)));
                            app.fireEvent(new OpenForumEvent(forumPagerItem, ForumType.Club));
                        }
                    }
            ).show(getFragmentManager());
        } else {
            toastAlert(app.getString(R.string.feecback_waiting_forumpart_load));
        }
    }


    @UiThread
    void setRefreshComplete() {
        pullToRefreshLayout.setRefreshComplete();
    }


    @AfterViews
    void
    init() {
        topicType = TopicType.All_Except_Sell;

        fabDefaultY = fab.getY();
        Log.d("forum", "init forum fragment " + forumPagerItem.title);

        topicAdapter = new TopicAdapter(getActivity());
        MyAnimationAdapter animationAdapter = new MyAnimationAdapter(topicAdapter);
        animationAdapter.setAbsListView(list);
        emptyView = SimpleEmptyView_.build(getActivity());
        root.addView(emptyView);

        list.setAdapter(animationAdapter);
        list.setEmptyView(emptyView);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this.getAttachedActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set the OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(pullToRefreshLayout);

        // Add Blank Margin on top height = Tab's height
        if (!noTabMargin) {
            View blankHeader = new View(getAttachedActivity());
            blankHeader.setMinimumHeight(getResources().getDimensionPixelOffset(R.dimen.tabs_height));
            list.addHeaderView(blankHeader);
        }else{
            getAttachedActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Attach scroll listener
        Log.d("forum", "init : lastFirstVisibleItem -> " + lastFirstVisibleItem);

        list.setOnScrollListener(
                new ScrollDirectionListener(
                        lastFirstVisibleItem, new ScrollDirectionListener.OnScrollUp() {
                    @Override
                    public void onScrollUp() {
                        app.getEventBus().post(new ForumScrollUpEvent());
                        showFab();
                    }
                }, new ScrollDirectionListener.OnScrollDown() {
                    @Override
                    public void onScrollDown() {
                        app.getEventBus().post(new ForumScrollDownEvent());
                        hideFab();
                    }
                }
                )
        );

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
        if (!pullToRefreshLayout.isRefreshing())
            pullToRefreshLayout.setRefreshing(true);
        prepareRecommendDone = false;
        client.getForumPart(forumPagerItem.url, forumType, forumPartCallback);
    }

    @Override
    public void onRefreshStarted(View view) {
        refresh();
    }

    private void refresh() {
        prepareRecommendDone = false;
        prepareTopicDone = false;
        currentPage = 1;
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
        if (!pullToRefreshLayout.isRefreshing())
            pullToRefreshLayout.setRefreshing(true);
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

    public class TopicAdapter extends BaseAdapter {

        final int TYPE_SECTION = 2;
        final int TYPE_RECOMMEND = 1;
        final int TYPE_TOPIC = 0;
        final int TYPE_TOPIC_THUMBNAIL = 3;

        public TopicAdapter(Context context) {
        }


        @Override
        public int getCount() {
            if (forum == null)
                return 0;
            else return forum.getTopics().size() + 2 + recommendTopicTitle.length;
        }

        @Override
        public Object getItem(int i) {
            if (forumType == ForumType.Room) {
                if (recommendTopicTitle != null && recommendTopicTitle.length > 0)
                    return forum.getTopics().get(i -3);
                else
                    return -1;
            } else {
                return forum.getTopics().get(i);
            }
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 4;
        }

        @Override
        public int getItemViewType(int position) {
            if (recommendTopicTitle == null) {
                return 0;
            }
            if (forumType == ForumType.Room && (position == 0 || position == 2) ) {
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
            }
            return view;
        }

        private RecommendCardView getViewTopicRecommend(int position, View convertView) {
            final RecommendCardView recommendCardView;

            if (convertView != null) {
                recommendCardView = (RecommendCardView) convertView;
            } else {
                recommendCardView = RecommendCardView_.build(getAttachedActivity());
                for(int i=0;i< recommendTopicTitle.length; i++) {
                    recommendCardView.addItem(recommendTopicTitle[i], recommendTopicUrl[i]);
                }

            }
//            try {
//                topicRecommendView.bind(recommendTopicTitle[position - 1], recommendTopicUrl[position - 1]);
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
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
                loadMore();
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
