package com.nantaphop.pantipfanapp.fragment;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.response.Comments;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.response.TopicPost;
import com.nantaphop.pantipfanapp.utils.RESTUtils;
import com.nantaphop.pantipfanapp.view.CommentView;
import com.nantaphop.pantipfanapp.view.CommentView_;
import com.nantaphop.pantipfanapp.view.TopicPostView;
import com.nantaphop.pantipfanapp.view.TopicPostView_;
import org.androidannotations.annotations.*;
import org.apache.http.Header;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by nantaphop on 08-Aug-14.
 */
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

    private byte[] tmpTopicPageHtml;
    private TopicPost topicPost;
    private float fabDefaultY;
    int currentCommentPage;
    Comments comments;
    private CommentAdapter commentAdapter;

    boolean prepareCommentsDone = false, prepareTopicPostDone = false;


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
            } else {
                comments.addComments(newComments.getComments());
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


    @AfterViews
    void init() {
        fabDefaultY = fab.getY();
        Log.d("topic", "init topic fragment " + topic.getId());
        // Prepare Adapter
        commentAdapter = new CommentAdapter(getActivity());
        list.setAdapter(commentAdapter);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this.getActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set the OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(pullToRefreshLayout);
        pullToRefreshLayout.setRefreshing(true);
        loadTopicPost();
        loadNextComments();
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

        prepareCommentsDone = true;
        joinTopic();
    }

    @UiThread
    public void joinTopic() {
        if (prepareCommentsDone && prepareTopicPostDone) {

            TopicPostView topicPostView = TopicPostView_.build(getActivity());
            topicPostView.bind(topicPost);
            if (list.getHeaderViewsCount() == 0)
                list.addHeaderView(topicPostView);

            commentAdapter.notifyDataSetChanged();

            pullToRefreshLayout.setRefreshComplete();
        }
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

    private void loadNextComments() {
        if (!pullToRefreshLayout.isRefreshing())
            pullToRefreshLayout.setRefreshing(true);
        client.getComments(topic.getId() + "", currentCommentPage, false, commentsCallback);
    }

    class CommentAdapter extends BaseAdapter {

        public CommentAdapter(Context context) {
        }

        @Override
        public int getCount() {
            if (comments!=null) {
                if (comments.getComments() != null) {
                    return comments.getComments().size();
                }
                else
                    return 0;
            }else{
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
        public View getView(int position, View convertView, ViewGroup parent) {
            CommentView commentView;
//            if (position == getCount() - 5) {
//                Log.d("", "Do Loadmore");
//                loadNextComments();
//            }

            if(convertView != null){
                commentView = (CommentView) convertView;
            }else{
                commentView = CommentView_.build(getActivity());
            }
            commentView.bind(comments.getComments().get(position));
            return commentView;
        }


    }
}
