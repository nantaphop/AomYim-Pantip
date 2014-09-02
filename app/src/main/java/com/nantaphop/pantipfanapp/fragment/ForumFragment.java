package com.nantaphop.pantipfanapp.fragment;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.*;
import com.nantaphop.pantipfanapp.model.ForumPagerItem;
import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.ForumPart;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.utils.RESTUtils;
import com.nantaphop.pantipfanapp.utils.ScrollDirectionListener;
import com.nantaphop.pantipfanapp.view.*;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.prototypes.SectionedCardAdapter;
import org.androidannotations.annotations.*;
import org.apache.http.Header;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import static com.nantaphop.pantipfanapp.service.PantipRestClient.ForumType;
import static com.nantaphop.pantipfanapp.service.PantipRestClient.TopicType;

/**
 * Created by nantaphop on 27-Jul-14.
 */
@OptionsMenu(R.menu.menu_forum)
@EFragment(R.layout.fragment_forum)
public class ForumFragment extends BaseFragment implements OnRefreshListener {

    @ViewById
    ListView list;
    //    @ViewById
//    CardListView cardList;
    @ViewById
    FloatingActionButton fab;


    @FragmentArg
    ForumPagerItem forumPagerItem;

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
    private ArrayList<TopicSectionCard> sections;
    private float fabDefaultY;
    private boolean fabIsHiding;
    private ArrayList<Card> cards;
    private TopicAdapter topicAdapter;
    @InstanceState
    public String[] recommendTopicTitle;
    @InstanceState
    public String[] recommendTopicUrl;

    @Trace
    @Background
    void prepareForumPart() {
        forumPart = RESTUtils.parseForumPart(new String(tmpForumPartBytes));

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
                recommendTopicTitle[i] = forumPart.getRecommendTopic().get(i);
                recommendTopicUrl[i] = forumPart.getRecommendUrl().get(i);
            }
        }
        tmpForumPartBytes = null;
        prepareRecommendDone = true;
        joinForum();
    }

    @Trace
    @Background
    void prepareTopic() {
//        tmpTopicCard = forum.toCardList(getAttachedActivity(), cardRenderCount);
        prepareTopicDone = true;
        joinForum();
    }

    @Background
    void prepareTopicFromInstanceState() {
        tmpTopicCard = forum.toCardList(getAttachedActivity(), 0);
        prepareTopicDone = true;
        joinForum();
    }

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

    private void hideFab() {
        if (!fabIsHiding) {
            fab.animate().translationY(fab.getHeight() * 3).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            fabIsHiding = true;
        }
    }

    private void showFab() {
        if (fabIsHiding) {
            fab.animate().translationY(fabDefaultY).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            fabIsHiding = false;
        }
    }

    @OptionsItem
    void action_sort_topic() {
        Log.d("menu", "sort - " + forumPagerItem.title);
        app.getEventBus().post(new SortForumEvent(forum.getTopics(), topicAdapter));
    }


    @UiThread
    void setRefreshComplete() {
        pullToRefreshLayout.setRefreshComplete();
    }


    @AfterViews
    void
    init() {
        fabDefaultY = fab.getY();
        Log.d("forum", "init forum fragment " + forumPagerItem.title);
        // Prepare Adapter
//        cards = new ArrayList<Card>();
//        cardArrayAdapter = new TopicCardAdapter(getAttachedActivity(), cards);
//        cardArrayAdapter.setInnerViewTypeCount(2);
//        sections = new ArrayList<TopicSectionCard>();
//        sectionedCardAdapter = new TopicSectionedAdapter(getAttachedActivity(), cardArrayAdapter);
//        cardList.setExternalAdapter(sectionedCardAdapter, cardArrayAdapter);
        topicAdapter = new TopicAdapter(getActivity());
        list.setAdapter(topicAdapter);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this.getAttachedActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set the OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(pullToRefreshLayout);

        // Add Blank Margin on top height = Tab's height
        View blankHeader = new View(getAttachedActivity());
        blankHeader.setMinimumHeight(getResources().getDimensionPixelOffset(R.dimen.tabs_height));
        list.addHeaderView(blankHeader);

        // Attach scroll listener
        Log.d("forum", "init : lastFirstVisibleItem -> " + lastFirstVisibleItem);

        list.setOnScrollListener(new ScrollDirectionListener(lastFirstVisibleItem, new ScrollDirectionListener.OnScrollUp() {
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
        }));

        // If from saved
        if (forum != null && forumPart != null) {
            prepareRecommendCard();
            prepareTopicFromInstanceState();
            return;
        } else {
            // Load Initial Data
            loadMore();
            loadForumPart();
        }
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
        client.getForumPart(forumPagerItem.url, forumPartCallback);
    }

    @Override
    public void onRefreshStarted(View view) {
        prepareRecommendDone = false;
        prepareTopicDone = false;
        currentPage = 1;
        lastIdCurrentPage = "0";
        cardRenderCount = 0;
        forumPart = null;
        forum = null;
        // Load Initial Data
        loadMore();
        loadForumPart();
    }

    private void loadMore() {
        if (!pullToRefreshLayout.isRefreshing())
            pullToRefreshLayout.setRefreshing(true);
        prepareTopicDone = false;
        client.getForum(forumPagerItem.url, ForumType.Room, TopicType.All_Except_Sell, currentPage, lastIdCurrentPage, false, forumCallback);
    }

    private int getFabMarginBottom() {
        int marginBottom = 0;
        final ViewGroup.LayoutParams layoutParams = fab.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        }
        return marginBottom;
    }

    public class TopicSectionedAdapter extends SectionedCardAdapter {

        /*
         * Define your layout in the constructor
         */
        public TopicSectionedAdapter(Context context, CardArrayAdapter cardArrayAdapter) {
            super(context, R.layout.listitem_topic_section, cardArrayAdapter);
        }

        /*
         * Override this method to customize your layout
         */
        @Override
        protected View getSectionView(int position, View view, ViewGroup parent) {

            //Override this method to customize your section's view

            //Get the section
            TopicSectionCard section = (TopicSectionCard) getCardSections().get(position);

            if (section != null) {
                //Set the title
                TextView title = (TextView) view.findViewById(R.id.title);
                if (title != null)
                    title.setText(section.getTitle());

                //Set the button
                TextView buttonMore = (TextView) view.findViewById(R.id.button);
                if (section.getButtonText().length() > 0) {

                    buttonMore.setVisibility(View.VISIBLE);
                    if (buttonMore != null) {
                        buttonMore.setText(section.getButtonText());
                        if (section.getOnClickListener() != null) {
                            buttonMore.setOnClickListener(section.getOnClickListener());
                        }
                    }
                } else
                    buttonMore.setVisibility(View.GONE);

            }

            return view;
        }
    }

    class TopicCardAdapter extends CardArrayAdapter {

        public TopicCardAdapter(Context context, List<Card> cards) {
            super(context, cards);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position == getCount() - 5) {
                Log.d("", "Do Loadmore");
                loadMore();
            }
            return super.getView(position, convertView, parent);
        }
    }

    public class TopicAdapter extends BaseAdapter {

        final int TYPE_SECTION = 2;
        final int TYPE_RECOMMEND = 1;
        final int TYPE_TOPIC = 0;

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
            if (recommendTopicTitle != null && recommendTopicTitle.length > 0)
                return forum.getTopics().get(i - 2 - recommendTopicTitle.length);
            else
                return -1;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            if (recommendTopicTitle == null) {
                return 0;
            }
            if (position == 0 || position == recommendTopicTitle.length + 1) {
                return TYPE_SECTION;
            } else if (position > 0 && position <= recommendTopicTitle.length) {
                return TYPE_RECOMMEND;
            } else {
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
            }
            return view;
        }

        private TopicRecommendView getViewTopicRecommend(int position, View convertView) {
            final TopicRecommendView topicRecommendView;

            if (convertView != null) {
                topicRecommendView = (TopicRecommendView) convertView;
            } else {
                topicRecommendView = TopicRecommendView_.build(getAttachedActivity());

            }
            topicRecommendView.bind(recommendTopicTitle[position - 1], recommendTopicUrl[position - 1]);
            return topicRecommendView;
        }

        private TopicSectionView getViewTopicSection(int position, View convertView) {
            final TopicSectionView topicSectionView;
            if (convertView != null) {
                topicSectionView = (TopicSectionView) convertView;
            } else {
                topicSectionView = TopicSectionView_.build(getAttachedActivity());

            }
            if (position == 0) {
                topicSectionView.bind(getString(R.string.recomend_topic), getString(R.string.view_all), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        app.getEventBus().post(new ShowRecommendEvent(forumPart.getRecommendTopic(), forumPart.getRecommendUrl()));
                    }
                });
            } else if (position == recommendTopicTitle.length + 1) {
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


    }


}
