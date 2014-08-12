package com.nantaphop.pantipfanapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.ForumScrollDownEvent;
import com.nantaphop.pantipfanapp.event.ForumScrollUpEvent;
import com.nantaphop.pantipfanapp.event.SortCommentEvent;
import com.nantaphop.pantipfanapp.response.*;
import com.nantaphop.pantipfanapp.utils.RESTUtils;
import com.nantaphop.pantipfanapp.utils.ScrollDirectionListener;
import com.nantaphop.pantipfanapp.view.CommentView;
import com.nantaphop.pantipfanapp.view.CommentView_;
import com.nantaphop.pantipfanapp.view.TopicPostView;
import com.nantaphop.pantipfanapp.view.TopicPostView_;
import org.androidannotations.annotations.*;
import org.apache.http.Header;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by nantaphop on 08-Aug-14.
 */
@OptionsMenu(R.menu.menu_topic)
@EFragment(R.layout.fragment_topic)
public class TopicFragment extends BaseFragment implements OnRefreshListener {

    @FragmentArg
    Topic topic;
    @ViewById
    ListView list;
    @ViewById
    PullToRefreshLayout pullToRefreshLayout;
    @ViewById
    FloatingActionButton fab;

    @InstanceState
    int lastFirstVisibleItem;
    @InstanceState
    TopicPost topicPost;
    @InstanceState
    Comments comments;

    private byte[] tmpTopicPageHtml;
    private float fabDefaultY;
    @InstanceState
    int currentCommentPage = 1;
    private CommentAdapter commentAdapter;
    boolean prepareCommentsDone = false, prepareTopicPostDone = false;
    private ArrayList<Comment> tmpCommentsList;


    private AsyncHttpResponseHandler topicPostCallback = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            tmpTopicPageHtml = bytes;
            prepareTopicPost();
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

        }
    };

    private BaseJsonHttpResponseHandler commentsCallback = new BaseJsonHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, String s, Object o) {
            Log.d("forum", "success");
            Comments newComments = (Comments) o;
            if (comments == null) {
                comments = newComments;
                if (newComments.getComments() != null) {
                    tmpCommentsList = (ArrayList<Comment>) newComments.getComments().clone();
                }
                if (comments.getComments() != null)
                    comments.getComments().clear();
            } else {
                if(tmpCommentsList == null){
                    tmpCommentsList = new ArrayList<Comment>();
                }
                tmpCommentsList.addAll(newComments.getComments());
                comments.setPaging(newComments.getPaging());
            }
            currentCommentPage++;

            prepareComments();
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Object o) {

        }

        @Override
        protected Object parseResponse(String s, boolean b) throws Throwable {
            return RESTUtils.parseComments(s);
        }
    };

    private BaseJsonHttpResponseHandler repliesCallback = new BaseJsonHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, String s, Object o) {
            tmpReplies = (Reply) o;
            if (tmpReplies != null) {
                addReplies();
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Object o) {

        }

        @Override
        protected Object parseResponse(String s, boolean b) throws Throwable {
            return RESTUtils.parseReplies(s);
        }
    };
    private CommentView waitUpdateCommentView;
    private int newRepliesPosition;
    private Reply tmpReplies;
    private boolean fabIsHiding;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("topic", " on attached");
    }

    @AfterViews
    void init() {
        fabDefaultY = fab.getY();
        Log.d("topic", "init topic fragment " + topic.getId());
        // Prepare Adapter
        commentAdapter = new CommentAdapter(getAttachedActivity());
        list.setAdapter(commentAdapter);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this.getAttachedActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set the OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(pullToRefreshLayout);
        pullToRefreshLayout.setRefreshing(true);

        // Attach scroll listener
        Log.d("forum", "init : lastFirstVisibleItem -> " + lastFirstVisibleItem);

        list.setOnScrollListener(new ScrollDirectionListener(lastFirstVisibleItem, new ScrollDirectionListener.OnScrollUp() {
            @Override
            public void onScrollUp() {
                showFab();
            }
        }, new ScrollDirectionListener.OnScrollDown() {
            @Override
            public void onScrollDown() {
                hideFab();
            }
        }));

        if(topicPost == null && comments == null) {
            loadTopicPost();
            loadNextComments();
        }else{
            prepareTopicPostDone = true;
            prepareComments();
        }
    }

    @Background
    public void prepareTopicPost() {
        prepareTopicPostDone = false;
        topicPost = RESTUtils.parseTopicPost(new String(tmpTopicPageHtml));
        prepareTopicPostDone = true;
        joinTopic();
    }

    @Background
    public void prepareComments() {
        prepareCommentsDone = false;
        // Flatten Comment and Replies
        if (tmpCommentsList != null) {
            ArrayList<Comment> flattenComments = new ArrayList<Comment>();
            for (Comment c : tmpCommentsList) {
                RESTUtils.processComment(c);
                ArrayList<Comment> replies = c.getReplies();
                flattenComments.add(c);
                Iterator<Comment> it = replies.iterator();
                while (it.hasNext()) {
                    Comment r = it.next();
                    r.setReply(true);
                    r.setParent(c);
                    RESTUtils.processComment(r);
                    flattenComments.add(r);
                    it.remove();
                    c.setLastReply(r.getReply_no());
                }
            }
            comments.addComments(flattenComments);
            tmpCommentsList.clear();
        }
        prepareCommentsDone = true;
        joinTopic();
    }

    @UiThread
    public void joinTopic() {
        if (prepareCommentsDone && prepareTopicPostDone) {

            TopicPostView topicPostView = TopicPostView_.build(getAttachedActivity());
            topicPostView.bind(topicPost);
            if (list.getHeaderViewsCount() == 0)
                list.addHeaderView(topicPostView);

            commentAdapter.notifyDataSetChanged();

            if (lastFirstVisibleItem != 0) {
                list.setSelection(lastFirstVisibleItem);
            }
        }
        pullToRefreshLayout.setRefreshComplete();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        lastFirstVisibleItem = list.getFirstVisiblePosition();

        super.onSaveInstanceState(outState);


    }

    public void loadReplies(Comment c, CommentView waitUpdateCommentView, int newRepliesPosition) {
        if (!pullToRefreshLayout.isRefreshing())
            pullToRefreshLayout.setRefreshing(true);
        this.waitUpdateCommentView = waitUpdateCommentView;
        this.newRepliesPosition = newRepliesPosition;
        Comment p = c.getParent();
        client.getReplies(p.getId(), p.getLastReply(), p.getReply_count(), p.getUser().getId(), repliesCallback);
    }

    @UiThread
    void addReplies() {
        Comment parent = comments.getComments().get(newRepliesPosition).getParent();
        for (Comment comment : tmpReplies.getReplies()) {
            comment.setReply(true);
            comment.setParent(waitUpdateCommentView.getComment().getParent());
            parent.setLastReply(comment.getReply_no());
        }
        comments.getComments().addAll(newRepliesPosition + 1, tmpReplies.getReplies());
        waitUpdateCommentView.disableLoadMore();
        tmpReplies = null;
        prepareComments();
        pullToRefreshLayout.setRefreshComplete();
    }

    @Override
    public void onRefreshStarted(View view) {
        loadNextComments();
    }

    public void loadTopicPost() {
        if (!pullToRefreshLayout.isRefreshing())
            pullToRefreshLayout.setRefreshing(true);
        client.getTopicPost(topic.getId() + "", topicPostCallback);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(com.nantaphop.pantipfanapp.R.menu.menu_topic, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OptionsItem
    void action_sort_comment() {
        Log.d("menu", "sort comment");
        app.getEventBus().post(new SortCommentEvent(comments, commentAdapter));
    }

    private void loadNextComments() {
        if (!pullToRefreshLayout.isRefreshing())
            pullToRefreshLayout.setRefreshing(true);
        client.getComments(topic.getId() + "", currentCommentPage, false, commentsCallback);
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

    class CommentAdapter extends BaseAdapter {

        public CommentAdapter(Context context) {
        }

        @Override
        public int getCount() {
            if (comments != null) {
                if (comments.getComments() != null) {
                    return comments.getComments().size();
                } else
                    return 0;
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            return comments.getComments().get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final CommentView commentView;
            if (position == getCount() - 5) {
                if (comments.getCount() > comments.getPaging().getLimit()*comments.getPaging().getPage()) {
                    Log.d("", "Do Loadmore");
                    loadNextComments();
                }
            }

            if (convertView != null) {
                commentView = (CommentView) convertView;
            } else {
                commentView = CommentView_.build(getAttachedActivity());

            }
            commentView.bind(comments.getComments().get(position));
            commentView.setOnLoadMoreClick(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Comment comment = commentView.getComment();
                    loadReplies(comment, commentView, position);

                }
            });

            return commentView;
        }


    }
}
