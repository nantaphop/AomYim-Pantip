package com.nantaphop.pantipfanapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.model.ForumPagerItem;
import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.ForumPart;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import com.nantaphop.pantipfanapp.utils.RESTUtils;
import com.nantaphop.pantipfanapp.view.RecommendCard;
import com.nantaphop.pantipfanapp.view.TopicItemView;
import com.nantaphop.pantipfanapp.view.TopicItemView_;
import com.nantaphop.pantipfanapp.view.TopicSectionCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardSection;
import it.gmariotti.cardslib.library.prototypes.SectionedCardAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
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
@EFragment(R.layout.fragment_forum)
public class ForumFragment extends Fragment implements OnRefreshListener {

    @App
    BaseApplication app;

    @Bean
    PantipRestClient client;

    @ViewById
    ListView list;
    @ViewById
    CardListView cardList;


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

//    ForumAdapter adapter;

    private CardArrayAdapter cardArrayAdapter;
    private ForumPart forumPart;
    private SectionedCardAdapter sectionedCardAdapter;


    private boolean mIsScrollingUp;
    private int mLastFirstVisibleItem;

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

            joinForum();
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Object o) {
            Log.d("forum", "failed");

        }

        @Override
        protected Object parseResponse(String s, boolean b) throws Throwable {
            return RESTUtils.parseForum(s);
        }
    };
    AsyncHttpResponseHandler forumPartCallback = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int x, Header[] headers, byte[] bytes) {
            forumPart = RESTUtils.parseForumPart(new String(bytes));
            joinForum();
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

        }
    };


    void joinForum() {
        // Join Thread
        if (forumPart != null && forum != null) {
            int numPreview = forumPart.getRecommendTopic().size() > 3 ? 3 : forumPart.getRecommendTopic().size();

            if (currentPage == 2) { // Do just first load
                cardArrayAdapter.clear();
                // Add Recommend Topic
                for (int i = 0; i < numPreview; i++) {
                    Card card = new Card(getActivity());
                    card.setTitle(forumPart.getRecommendTopic().get(i));
                    card.setClickable(true);
                    card.setShadow(false);
                    card.setBackgroundResourceId(R.drawable.card_background);
                    cardArrayAdapter.add(card);
                }


            }

            // Add Topics
            cardArrayAdapter.addAll(forum.toCardList(getActivity(), cardRenderCount));
            cardRenderCount = forum.getTopics().size();

            if (currentPage == 2) { // Do just first load
                // Split Section
                ArrayList<TopicSectionCard> sections = new ArrayList<TopicSectionCard>();
                sections.add(new TopicSectionCard(0, app.getString(R.string.recomend_topic), app.getString(R.string.view_all), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(getActivity())
                                .setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.recommend_list_item, forumPart.getRecommendTopic()), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(getActivity(), forumPart.getRecommendUrl().get(i), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setTitle(app.getString(R.string.recomend_topic))
                                .show();
                    }
                }));
                sections.add(new TopicSectionCard(numPreview, "กระทู้ในห้อง", "", null));
                TopicSectionCard[] dummy = new TopicSectionCard[sections.size()];
                sectionedCardAdapter.setCardSections(sections.toArray(dummy));
            }

            // Update List
            cardArrayAdapter.notifyDataSetChanged();
            sectionedCardAdapter.notifyDataSetChanged();
        }
        pullToRefreshLayout.setRefreshComplete();
    }


    @AfterViews
    void init() {
        // Prepare Adapter
        cardArrayAdapter = new TopicCardAdapter(getActivity(), new ArrayList<Card>());
        cardArrayAdapter.setInnerViewTypeCount(2);
        sectionedCardAdapter = new TopicSectionedAdapter(getActivity(), cardArrayAdapter);
        cardList.setExternalAdapter(sectionedCardAdapter, cardArrayAdapter);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this.getActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set the OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(pullToRefreshLayout);

        // Load Initial Data
        loadMore();
        loadForumPart();
    }

    private void loadForumPart() {
        if (!pullToRefreshLayout.isRefreshing())
            pullToRefreshLayout.setRefreshing(true);
        client.getForumPart(forumPagerItem.url, forumPartCallback);
    }

    @Override
    public void onRefreshStarted(View view) {
        currentPage = 1;
        lastIdCurrentPage = "0";
        cardRenderCount = 0;
        forumPart = null;
        forum = null;
        // Load Initial Data
        loadMore();
        loadForumPart();
    }

    public class TopicSectionedAdapter extends SectionedCardAdapter {

        /*
         * Define your layout in the constructor
         */
        public TopicSectionedAdapter(Context context, CardArrayAdapter cardArrayAdapter) {
            super(context, R.layout.card_topic_section, cardArrayAdapter);
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

    private void loadMore() {
        if (!pullToRefreshLayout.isRefreshing())
            pullToRefreshLayout.setRefreshing(true);
        client.getForum(forumPagerItem.url, ForumType.Room, TopicType.All_Except_Sell, currentPage, lastIdCurrentPage, false, forumCallback);
    }

}
