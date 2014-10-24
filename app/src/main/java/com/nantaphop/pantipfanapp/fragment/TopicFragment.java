package com.nantaphop.pantipfanapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.DoReplyEvent;
import com.nantaphop.pantipfanapp.event.DoVoteEvent;
import com.nantaphop.pantipfanapp.event.SortCommentEvent;
import com.nantaphop.pantipfanapp.fragment.dialog.ListDialog;
import com.nantaphop.pantipfanapp.fragment.dialog.ListDialog_;
import com.nantaphop.pantipfanapp.response.*;
import com.nantaphop.pantipfanapp.utils.CommentComparator;
import com.nantaphop.pantipfanapp.utils.RESTUtils;
import com.nantaphop.pantipfanapp.utils.ScrollDirectionListener;
import com.nantaphop.pantipfanapp.view.*;
import com.squareup.otto.Subscribe;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;
import org.apache.http.Header;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by nantaphop on 08-Aug-14.
 */
@OptionsMenu(R.menu.menu_topic)
@EFragment(R.layout.fragment_topic)
public class TopicFragment extends BaseFragment implements OnRefreshListener {

    @FragmentArg
    Topic topic;

    @InstanceState
    int lastFirstVisibleItem;
    @InstanceState
    TopicPost topicPost;
    @InstanceState
    Comments comments;

    @DimensionPixelSizeRes(R.dimen.list_footer_height)
    int footerHeight;
    @ViewById
    ListView list;
    @ViewById
    PullToRefreshLayout pullToRefreshLayout;
    @ViewById
    ImageButton expandMoreComment;
    @ViewById
    ImageButton expandLessComment;
    @ViewById
    LinearLayout commentTools;
    @ViewById
    View commentBarSeparator;
    @ViewById
    EditText shortComment;
    @ViewById
    ImageButton comment;
    @ViewById
    LinearLayout commentPane;
    @ViewById
    FrameLayout root;

    @StringRes
    String comment_sort_type_title;
    @StringArrayRes
    String[] comment_sort_type;


    private CommentView waitUpdateCommentView;
    private int newRepliesPosition;
    private Reply tmpReplies;
    @InstanceState
    int currentCommentPage = 1;
    boolean prepareCommentsDone = false, prepareTopicPostDone = false;
    private byte[] tmpTopicPageHtml;
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
    private float commentViewDefaultHeight;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> tmpCommentsList;
    private BaseJsonHttpResponseHandler commentsCallback = new BaseJsonHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, String s, Object o) {
            Log.d("forum", "success");
            Comments newComments = (Comments) o;
            if ( comments == null ) {
                comments = newComments;
                if ( newComments.getComments() != null ) {
                    tmpCommentsList = (ArrayList<Comment>) newComments.getComments().clone();
                }
                if ( comments.getComments() != null )
                    comments.getComments().clear();
            }
            else {
                if ( tmpCommentsList == null ) {
                    tmpCommentsList = new ArrayList<Comment>();
                }
                if ( newComments.getComments() != null ) {
                    tmpCommentsList.addAll(newComments.getComments());
                }
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
            if ( tmpReplies != null ) {
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

    private BaseJsonHttpResponseHandler doCommentCallback = new BaseJsonHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, String s, Object o) {
            CommentResponse response = (CommentResponse) o;
            if ( !response.isError() ) {
                lastFirstVisibleItem = list.getAdapter().getCount() - 1; // Force scroll to last after doComment
                comments = null;
                currentCommentPage = 1;
                loadNextComments();
                list.requestFocus();
                shortComment.setText("");
                Crouton.makeText(
                        getActivity(),
                        getActivity().getString(R.string.feedback_comment_success),
                        Style.CONFIRM
                ).show();
            }
            else {
                Crouton.makeText(getActivity(), getActivity().getString(R.string.feedback_comment_failed), Style.ALERT)
                       .show();
            }
            pullToRefreshLayout.setRefreshComplete();

        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Object o) {

        }

        @Override
        protected Object parseResponse(String s, boolean b) throws Throwable {
            return RESTUtils.parseCommentResp(s);
        }
    };

    CommentView tmpCommentView;
    Comment tmpComment;
    private BaseJsonHttpResponseHandler doVoteCallback = new BaseJsonHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, String s, Object o) {
            VoteResponse response = (VoteResponse) o;
            if ( !response.isError() ) {
                Crouton.makeText(
                        getActivity(),
                        response.getVote_message(),
                        Style.CONFIRM
                ).show();
                tmpCommentView.setVote(response.getPoint());
                tmpComment.setPoint(response.getPoint());
                tmpCommentView = null;
                tmpComment = null;
            }
            else {
                Crouton.makeText(getActivity(), response.getError_message(), Style.ALERT)
                       .show();
            }
            pullToRefreshLayout.setRefreshComplete();

        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Object o) {

        }

        @Override
        protected Object parseResponse(String s, boolean b) throws Throwable {
            return RESTUtils.parseVoteResp(s);
        }
    };

    private boolean fabIsHiding;
    private SimpleEmptyView emptyView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("topic", " on attached");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // update the actionbar to show the up carat/affordance
    }

    @AfterViews
    void init() {
        getAttachedActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        commentViewDefaultHeight = commentPane.getY();
        Log.d("topic", "init topic fragment " + topic.getId());
        // Prepare Adapter
        commentAdapter = new CommentAdapter(getAttachedActivity());
        list.setAdapter(commentAdapter);
        emptyView = SimpleEmptyView_.build(getActivity());
        root.addView(emptyView);
        list.setEmptyView(emptyView);
        View footer = new View(getActivity());
        footer.setMinimumHeight(footerHeight);
        list.addFooterView(footer);
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

        list.setOnScrollListener(
                new ScrollDirectionListener(
                        lastFirstVisibleItem, new ScrollDirectionListener.OnScrollUp() {
                    @Override
                    public void onScrollUp() {
                        showCommentPane();
                    }
                }, new ScrollDirectionListener.OnScrollDown() {
                    @Override
                    public void onScrollDown() {
                        hideCommentPane();
                    }
                }, new ScrollDirectionListener.OnBottomReach() {
                    @Override
                    public void onBottomReach() {
                        showCommentPane();
                    }
                }
                )
        );

        if ( topicPost == null && comments == null ) {
            loadTopicPost();
            loadNextComments();
        }
        else {
            prepareTopicPostDone = true;
            prepareComments();
        }
        list.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        list.requestFocus();
                        return false;
                    }
                }
        );
    }

    @FocusChange(R.id.shortComment)
    void commentFocused() {

        if ( shortComment.isFocused() ) {
            showCommentTools();
        }
        else {
            hideCommentTools();
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
        if ( tmpCommentsList != null ) {
            ArrayList<Comment> flattenComments = new ArrayList<Comment>();
            for ( Comment c : tmpCommentsList ) {
                RESTUtils.processComment(c);
                ArrayList<Comment> replies = c.getReplies();
                flattenComments.add(c);
                Iterator<Comment> it = replies.iterator();
                while ( it.hasNext() ) {
                    Comment r = it.next();
                    r.setReply(true);
                    r.setParent(c);
                    RESTUtils.processComment(r);
                    flattenComments.add(r);
                    it.remove();
                    c.setLastReply(r.getReply_no());
                }
            }
            if ( flattenComments != null ) {
                comments.addComments(flattenComments);
            }
            tmpCommentsList.clear();
        }
        prepareCommentsDone = true;
        joinTopic();
    }

    @UiThread
    public void joinTopic() {
        if ( prepareCommentsDone && prepareTopicPostDone ) {

            TopicPostView topicPostView = TopicPostView_.build(getAttachedActivity());
            topicPostView.bind(topicPost);
            if ( list.getHeaderViewsCount() == 0 )
                list.addHeaderView(topicPostView);
            commentAdapter.notifyDataSetChanged();
            if ( lastFirstVisibleItem != 0 ) {
                list.setSelection(lastFirstVisibleItem);
            }
            pullToRefreshLayout.setRefreshComplete();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        lastFirstVisibleItem = list.getFirstVisiblePosition();

        super.onSaveInstanceState(outState);


    }

    public void loadReplies(Comment c, CommentView waitUpdateCommentView, int newRepliesPosition) {
        if ( !pullToRefreshLayout.isRefreshing() )
            pullToRefreshLayout.setRefreshing(true);
        this.waitUpdateCommentView = waitUpdateCommentView;
        this.newRepliesPosition = newRepliesPosition;
        Comment p = c.getParent();
        client.getReplies(p.getId(), p.getLastReply(), p.getReply_count(), p.getUser().getId(), repliesCallback);
    }

    @UiThread
    void addReplies() {
        Comment parent = comments.getComments().get(newRepliesPosition).getParent();
        for ( Comment comment : tmpReplies.getReplies() ) {
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
        topicPost = null;
        comments = null;
        currentCommentPage = 1;
        loadNextComments();
        loadTopicPost();
    }

    public void loadTopicPost() {
        if ( !pullToRefreshLayout.isRefreshing() )
            pullToRefreshLayout.setRefreshing(true);
        prepareTopicPostDone = false;
        client.getTopicPost(topic.getId() + "", topicPostCallback);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_topic, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Click
    void comment() {
        String msg = shortComment.getText().toString();
        // Detect for reply or new comment
        boolean isReply = false;
        if ( msg.startsWith("ตอบ คห.") ) {
            String commentNo = msg.substring(0, msg.indexOf(">")).split(" ")[2];
            msg = msg.split(">")[1].trim();
            DoReplyEvent e = (DoReplyEvent)shortComment.getTag();
            client.reply(topic.getId(), e.commentRefId, e.commentNo, e.commentTimestamp, msg, doCommentCallback);

        }else if ( msg.length() > 0 ) {
            pullToRefreshLayout.setRefreshing(true);
            hideCommentTools();
            client.comment(topic.getId(), msg, doCommentCallback);
        }

    }


    @OptionsItem(android.R.id.home)
    void backHome(){
        getAttachedActivity().onBackPressed();
    }

    @OptionsItem
    void action_sort_comment() {
        Log.d("menu", "sort comment");
        app.getEventBus().post(new SortCommentEvent(comments, commentAdapter));
    }

    @OptionsItem
    void action_open_browser() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://pantip.com/topic/" + topic.getId()));
        startActivity(i);

    }

    @OptionsItem
    public void action_share() {

        try {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            intent.putExtra(Intent.EXTRA_SUBJECT, app.getString(R.string.share_title));
            intent.putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.share_text, topic.getTitle(), "http://pantip.com/topic/" + topic.getId())
            );

            startActivity(Intent.createChooser(intent, app.getString(R.string.share_prompt)));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        app.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        app.getEventBus().unregister(this);
    }

    @Subscribe
    public void sortComment(final SortCommentEvent e) {

        final ArrayList<Comment> comments = e.getComments().getComments();
        final ListDialog listDialog = ListDialog_.builder()
                                                 .choices(comment_sort_type)
                                                 .title(comment_sort_type_title)
                                                 .listItemLayoutRes(android.R.layout.simple_list_item_1)
                                                 .build();
        listDialog.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
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
                }
        );
        listDialog.show(getAttachedActivity().getFragmentManager(), "sort_topic");
    }

    @Subscribe
    public void reply(DoReplyEvent e) {
        shortComment.setText(
                Html.fromHtml(
                        String.format(
                                app.getString(R.string.reply_comment_prefix),
                                e.commentNo
                        ) + " "
                )
        );
        showCommentPane();
        shortComment.requestFocus();
        shortComment.setSelection(shortComment.getText().length());
        shortComment.setTag(e);
    }

    @Subscribe
    public void vote(DoVoteEvent e){
        tmpCommentView = e.view;
        tmpComment = e.comment;

        if ( e.comment.isReply() ) {
            client.voteReply(topic.getId(), e.comment.getParent().getId(), e.comment.getComment_no(), e.comment.getReply_id(), e.comment.getReply_no(), doVoteCallback);
        }
        else {
            client.voteComment(topic.getId(), e.comment.getId(), e.comment.getComment_no(), doVoteCallback);
        }
    }

    private void loadNextComments() {
        if ( !pullToRefreshLayout.isRefreshing() )
            pullToRefreshLayout.setRefreshing(true);
        prepareCommentsDone = false;
        client.getComments(topic.getId() + "", currentCommentPage, false, commentsCallback);
    }

    private void updateNewComment(int commentNo) {
        if ( !pullToRefreshLayout.isRefreshing() )
            pullToRefreshLayout.setRefreshing(true);
        prepareCommentsDone = false;
        client.getComments(topic.getId() + "", currentCommentPage, false, commentsCallback);
    }


    private void hideCommentPane() {
        if ( !fabIsHiding ) {
            commentBarSeparator.animate()
                               .translationY(commentPane.getHeight() * 3)
                               .setInterpolator(new AccelerateDecelerateInterpolator())
                               .start();
            commentPane.animate()
                       .translationY(commentPane.getHeight() * 3)
                       .setInterpolator(new AccelerateDecelerateInterpolator())
                       .start();
            fabIsHiding = true;
        }
    }

    private void showCommentPane() {
        if ( fabIsHiding ) {
            commentPane.animate()
                       .translationY(commentViewDefaultHeight)
                       .setInterpolator(new AccelerateDecelerateInterpolator())
                       .start();
            commentBarSeparator.animate()
                               .translationY(commentViewDefaultHeight)
                               .setInterpolator(new AccelerateDecelerateInterpolator())
                               .start();
            fabIsHiding = false;
        }
    }

    void showCommentTools() {
//        commentTools.setVisibility(View.VISIBLE);
//        commentTools.animate().alpha(1).setListener(null).setDuration(200).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    void hideCommentTools() {
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(shortComment.getWindowToken(), 0);
//        commentTools.animate().alpha(0).setDuration(200).setInterpolator(new AccelerateDecelerateInterpolator())
//                .setListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animator) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animator) {
//                        commentTools.setVisibility(View.INVISIBLE);
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animator) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animator) {
//
//                    }
//                }).start();
    }

    class CommentAdapter extends BaseAdapter {
        boolean noComment = false;

        public CommentAdapter(Context context) {
        }

        @Override
        public int getCount() {
            if ( comments != null ) {
                if ( comments.getComments() != null ) {
                    noComment = false;
                    return comments.getComments().size();
                }
                else {
                    noComment = true;
                    return 1;
                }
            }
            else {
                noComment = true;
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

            if ( noComment ) {
//                NoCommentView noCommentView = NoCommentView_.build(getActivity());
//                noCommentView.bind();

                return new View(getActivity());
            }
            final CommentView commentView;
            if ( position == getCount() - 5 ) {
                if ( comments.getCount() > comments.getPaging().getLimit() * comments.getPaging().getPage() ) {
                    Log.d("", "Do Loadmore");
                    loadNextComments();
                }
            }

            if ( convertView != null ) {
                commentView = (CommentView) convertView;
            }
            else {
                commentView = CommentView_.build(getAttachedActivity());

            }
            commentView.bind(comments.getComments().get(position));
            commentView.setOnLoadMoreClick(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Comment comment = commentView.getComment();
                            loadReplies(comment, commentView, position);

                        }
                    }
            );

            return commentView;
        }


    }
}
