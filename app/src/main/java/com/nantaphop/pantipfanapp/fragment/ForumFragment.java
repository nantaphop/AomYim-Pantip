package com.nantaphop.pantipfanapp.fragment;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
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
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import com.nantaphop.pantipfanapp.utils.PostOfficeHelper;
import com.nantaphop.pantipfanapp.utils.RESTUtils;
import com.nantaphop.pantipfanapp.utils.ScrollDirectionListener;
import com.nantaphop.pantipfanapp.utils.TopicComparator;
import com.nantaphop.pantipfanapp.view.*;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.ListStyle;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.prototypes.SectionedCardAdapter;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;
import org.apache.http.Header;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
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
    @ViewById
    FrameLayout root;


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
            if ( currentPage == 1 ) {
                forum = newForum;
                lastIdCurrentPage = forum.getLastIdCurrentPage();
            }
            else {
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
    private TopicType forumType;

    @Trace
    @Background
    void prepareForumPart() {
        forumPart = RESTUtils.parseForumPart(new String(tmpForumPartBytes));
        Log.d(
                "forumPart",
                "prepareForumPart recommend size : " + forumPart.getRecommendUrl()
                                                                .size() + " - " + forumPart.getRecommendTopic().size()
        );
        for ( String s : forumPart.getRecommendTopic() ) {
            Log.d("recommend", forumPagerItem.title + " " + s);
        }
        prepareRecommendCard();


    }

    @Background
    public void prepareRecommendCard() {
        int numPreview = forumPart.getRecommendTopic().size() > 3 ? 3 : forumPart.getRecommendTopic().size();
        recommendTopicTitle = new String[numPreview];
        recommendTopicUrl = new String[numPreview];
        if ( currentPage == 2 ) { // Do just first load
//            // Add Recommend Topic
            for ( int i = 0; i < numPreview; i++ ) {
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
        if ( prepareRecommendDone && prepareTopicDone ) {
            // Update List
            topicAdapter.notifyDataSetChanged();
            setRefreshComplete();
            if ( lastFirstVisibleItem != 0 ) {
                list.setSelection(lastFirstVisibleItem);
            }
        }


    }

    @Override
    void back() {
        app.getEventBus().post(new ToggleDrawerEvent());
    }

    private void hideFab() {
        if ( !fabIsHiding ) {
            fab.animate()
               .translationY(fab.getHeight() * 3)
               .setInterpolator(new AccelerateDecelerateInterpolator())
               .start();
            fabIsHiding = true;
        }
    }

    private void showFab() {
        if ( fabIsHiding ) {
            fab.animate().translationY(fabDefaultY).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            fabIsHiding = false;
        }
    }

    @OptionsItem
    void action_sort_topic() {
        Log.d("menu", "sort - " + forumPagerItem.title);
//        app.getEventBus().post(new SortForumEvent(forum.getTopics(), topicAdapter));
        PostOfficeHelper.newSimpleListMailCancelable(
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
        ).show(getFragmentManager());
    }

    @OptionsItem
    void action_topic_type() {
//        app.getEventBus().post(new ChooseTopicType());
        PostOfficeHelper.newSimpleListMailCancelable(
                getAttachedActivity()
                , "เลือกประเภทกระทู้"
                , Design.MATERIAL_LIGHT
                , topic_type
                , new ListStyle.OnItemAcceptedListener<CharSequence>() {
                    @Override
                    public void onItemAccepted(CharSequence charSequence, int i) {
                        switch (i){
                            case 0: forumType = TopicType.All_Except_Sell; break;
                            case 1: forumType = TopicType.Question; break;
                            case 2: forumType = TopicType.Chat; break;
                            case 3: forumType = TopicType.Poll; break;
                            case 4: forumType = TopicType.Review; break;
                            case 5: forumType = TopicType.News; break;
                            case 6: forumType = TopicType.Sell; break;
                        }
                        refresh();
                    }
                }
        ).show(getFragmentManager());
    }


    @UiThread
    void setRefreshComplete() {
        pullToRefreshLayout.setRefreshComplete();
    }


    @AfterViews
    void
    init() {
        forumType = TopicType.All_Except_Sell;

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
        View blankHeader = new View(getAttachedActivity());
        blankHeader.setMinimumHeight(getResources().getDimensionPixelOffset(R.dimen.tabs_height));
        list.addHeaderView(blankHeader);

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
        if ( forum != null && forumPart != null ) {
            prepareRecommendCard();
            prepareTopic();
            return;
        }
        else {
            // Load Initial Data
            loadMore();
            loadForumPart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        if ( !pullToRefreshLayout.isRefreshing() )
            pullToRefreshLayout.setRefreshing(true);
        prepareRecommendDone = false;
        client.getForumPart(forumPagerItem.url, forumPartCallback);
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
        if ( !pullToRefreshLayout.isRefreshing() )
            pullToRefreshLayout.setRefreshing(true);
        prepareTopicDone = false;
        client.getForum(
                forumPagerItem.url,
                ForumType.Room,
                forumType,
                currentPage,
                lastIdCurrentPage,
                false,
                forumCallback
        );
    }

    private int getFabMarginBottom() {
        int marginBottom = 0;
        final ViewGroup.LayoutParams layoutParams = fab.getLayoutParams();
        if ( layoutParams instanceof ViewGroup.MarginLayoutParams ) {
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
            if ( forum == null )
                return 0;
            else return forum.getTopics().size() + 2 + recommendTopicTitle.length;
        }

        @Override
        public Object getItem(int i) {
            if ( recommendTopicTitle != null && recommendTopicTitle.length > 0 )
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
            return 4;
        }

        @Override
        public int getItemViewType(int position) {
            if ( recommendTopicTitle == null ) {
                return 0;
            }
            if ( position == 0 || position == recommendTopicTitle.length + 1 ) {
                return TYPE_SECTION;
            }
            else if ( position > 0 && position <= recommendTopicTitle.length ) {
                return TYPE_RECOMMEND;
            }
            else {
                Topic topic = null;
                try {
                    topic = (Topic) getItem(position);
                } catch (Exception e) {
                    return TYPE_TOPIC;
                }
                if ( topic.getCoverImg().length() > 0 )
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

        private TopicRecommendView getViewTopicRecommend(int position, View convertView) {
            final TopicRecommendView topicRecommendView;

            if ( convertView != null ) {
                topicRecommendView = (TopicRecommendView) convertView;
            }
            else {
                topicRecommendView = TopicRecommendView_.build(getAttachedActivity());

            }
            Log.d(
                    "recommend",
                    "get recommend view [ total:" + recommendTopicTitle.length + "]" + " get at " + (position - 1) + " " + recommendTopicTitle[position - 1] + " " + recommendTopicUrl[position - 1]
            );
            try {
                topicRecommendView.bind(recommendTopicTitle[position - 1], recommendTopicUrl[position - 1]);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return topicRecommendView;
        }

        private TopicSectionView getViewTopicSection(int position, View convertView) {
            final TopicSectionView topicSectionView;
            if ( convertView != null ) {
                topicSectionView = (TopicSectionView) convertView;
            }
            else {
                topicSectionView = TopicSectionView_.build(getAttachedActivity());

            }
            if ( position == 0 ) {
                topicSectionView.bind(
                        getString(R.string.recomend_topic), getString(R.string.view_all), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showRecommendDialog();
                            }
                        }
                );
            }
            else if ( position == recommendTopicTitle.length + 1 ) {
                topicSectionView.bind(getString(R.string.topic_in_forum));
            }
            return topicSectionView;
        }

        private TopicView getViewTopic(int position, View convertView) {
            final TopicView topicView;
            if ( position == getCount() - 5 ) {
                loadMore();
            }

            if ( convertView != null ) {
                topicView = (TopicView) convertView;
            }
            else {
                topicView = TopicView_.build(getAttachedActivity());

            }
            topicView.bind((Topic) getItem(position));
            return topicView;
        }

        private TopicThumbnailView getViewTopicThumbnail(int position, View convertView) {
            final TopicThumbnailView topicView;
            if ( position == getCount() - 5 ) {
                loadMore();
            }

            if ( convertView != null ) {
                topicView = (TopicThumbnailView) convertView;
            }
            else {
                topicView = TopicThumbnailView_.build(getAttachedActivity());

            }
            topicView.bind((Topic) getItem(position));
            return topicView;
        }


    }


}
