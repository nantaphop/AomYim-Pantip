package com.nantaphop.pantipfanapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.ads.AdView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.DoEmoEvent;
import com.nantaphop.pantipfanapp.event.DoReplyEvent;
import com.nantaphop.pantipfanapp.event.DoVoteEvent;
import com.nantaphop.pantipfanapp.pref.UserPref_;
import com.nantaphop.pantipfanapp.response.Comment;
import com.nantaphop.pantipfanapp.response.CommentResponse;
import com.nantaphop.pantipfanapp.response.Comments;
import com.nantaphop.pantipfanapp.response.EmoResponse;
import com.nantaphop.pantipfanapp.response.Reply;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.response.TopicPost;
import com.nantaphop.pantipfanapp.response.VoteResponse;
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import com.nantaphop.pantipfanapp.utils.CommentComparator;
import com.nantaphop.pantipfanapp.utils.DeviceUtils;
import com.nantaphop.pantipfanapp.utils.RESTUtils;
import com.nantaphop.pantipfanapp.view.CommentDialogView;
import com.nantaphop.pantipfanapp.view.CommentDialogView_;
import com.nantaphop.pantipfanapp.view.CommentView;
import com.nantaphop.pantipfanapp.view.CommentView_;
import com.nantaphop.pantipfanapp.view.SimpleEmptyView;
import com.nantaphop.pantipfanapp.view.SimpleEmptyView_;
import com.nantaphop.pantipfanapp.view.TopicPostView;
import com.nantaphop.pantipfanapp.view.TopicPostView_;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by nantaphop on 08-Aug-14.
 */
@OptionsMenu(R.menu.menu_topic)
@EFragment(R.layout.fragment_topic)
public class TopicFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @FragmentArg
    Topic topic;

    @Pref
    UserPref_ userPref;

    @InstanceState
    int lastFirstVisibleItem;
    @InstanceState
    TopicPost topicPost;
    @InstanceState
    Comments comments;

    @SystemService
    InputMethodManager inputMethodManager;

    @DimensionPixelSizeRes(R.dimen.list_footer_height)
    int footerHeight;
    @ViewById
    AdView ads;
    @ViewById
    ObservableListView list;
    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;

    @ViewById
    EditText shortComment;
    @ViewById
    ImageButton comment;

    @ViewById
    FrameLayout root;
    @ViewById
    FloatingActionButton fab;

    @StringRes
    String comment_sort_type_title;
    @StringArrayRes
    String[] comment_sort_type;
    @StringRes(R.string.title_emo_dialog)
    String emoTitle;
    @StringArrayRes(R.array.emo_type)
    String[] emoType;


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
            toastAlert(getString(R.string.feedback_connection_failed));
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
            if (comments == null) {
                comments = newComments;
                if (newComments.getComments() != null) {
                    tmpCommentsList = (ArrayList<Comment>) newComments.getComments().clone();
                }
                if (comments.getComments() != null)
                    comments.getComments().clear();
            } else {
                if (tmpCommentsList == null) {
                    tmpCommentsList = new ArrayList<Comment>();
                }
                if (newComments.getComments() != null) {
                    tmpCommentsList.addAll(newComments.getComments());
                }
                comments.setPaging(newComments.getPaging());
            }
            currentCommentPage++;

            prepareComments();
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Object o) {
            toastAlert(getString(R.string.feedback_connection_failed));
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
            toastAlert(getString(R.string.feedback_connection_failed));
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
            if (!response.isError()) {
                lastFirstVisibleItem = list.getAdapter().getCount() - 1; // Force scroll to last after doComment
                comments = null;
                currentCommentPage = 1;
                loadNextComments();
                list.requestFocus();
                shortComment.setText("");
                toastInfo(getString(R.string.feedback_comment_success));
            } else {
                toastAlert(getString(R.string.feedback_comment_failed));
            }
            setRefreshComplete();

        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Object o) {
            toastAlert(getString(R.string.feedback_connection_failed));
        }

        @Override
        protected Object parseResponse(String s, boolean b) throws Throwable {
            return RESTUtils.parseCommentResp(s);
        }
    };

    CommentView tmpCommentView;
    Comment tmpComment;
    TopicPostView topicPostView;

    private BaseJsonHttpResponseHandler doVoteCallback = new BaseJsonHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, String s, Object o) {
            VoteResponse response = (VoteResponse) o;
            if (!response.isError()) {
                if (tmpCommentView != null) {
                    toastInfo(response.getVote_message());
                    tmpCommentView.setVote(response.getPoint());
                    tmpComment.setPoint(response.getPoint());
                    tmpCommentView = null;
                    tmpComment = null;
                } else {
                    toastInfo(response.getVote_message());
                    topicPostView.setVote();
                }
            } else {
                toastAlert(response.getError_message());
            }
            setRefreshComplete();

        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Object o) {
            toastAlert(getString(R.string.feedback_connection_failed));
        }

        @Override
        protected Object parseResponse(String s, boolean b) throws Throwable {
            return RESTUtils.parseVoteResp(s);
        }
    };

    private BaseJsonHttpResponseHandler doEmoCallback = new BaseJsonHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, String s, Object o) {
            EmoResponse response = (EmoResponse) o;
            if (response.getStatus().equals("ok")) {
                if (response.getEmotion().getType().equalsIgnoreCase("topic")) {
                    toastInfo(getString(R.string.feedback_emo_success));
                    topicPostView.setEmo();
                } else {
                    toastInfo(getString(R.string.feedback_emo_success));
                    tmpCommentView.setEmo(response);
                    tmpCommentView = null;
                }
            } else {
                toastAlert(getString(R.string.feedback_emo_failed));
            }
            setRefreshComplete();

        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Object o) {
            toastAlert(getString(R.string.feedback_connection_failed));
        }

        @Override
        protected Object parseResponse(String s, boolean b) throws Throwable {
            return RESTUtils.parseEmoResp(s);
        }
    };

    private boolean fabIsHiding;
    private SimpleEmptyView emptyView;
    private DoEmoEvent emoEvent;
    private int toolbarHeight;
    private boolean toolbarHiding;
    private MaterialDialog commentDialog;
    private CommentDialogView commentDialogView;

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
        initCommentDialog();
        getAttachedActivity().loadAd(ads);
        getAttachedActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        commentViewDefaultHeight = commentPane.getY();
        Log.d("topic", "init topic fragment " + topic.getId());
        // Prepare Adapter
        commentAdapter = new CommentAdapter(getAttachedActivity());
        list.setAdapter(commentAdapter);
        fab.attachToListView(list);
        emptyView = SimpleEmptyView_.build(getActivity());
        root.addView(emptyView);
        list.setEmptyView(emptyView);
//        View footer = new View(getActivity());
//        footer.setMinimumHeight(footerHeight);
//        list.addFooterView(footer);
        // Now setup the PullToRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.base_color);
        swipeRefreshLayout.setProgressViewOffset(false
                , getResources().getDimensionPixelSize(R.dimen.tabs_height)
                , DeviceUtils.getDisplayCenterPixel(getAttachedActivity()));
        getAttachedActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                    hideCommentPane();
                    hideToolbar();

                } else if (scrollState == ScrollState.DOWN) {
                    showCommentPane();
                    showToolbar();
                }
            }
        });
        if (topicPost == null && comments == null) {
            loadTopicPost();
            loadNextComments();
        } else {
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

    @AfterViews
    void disableNonLoginUser(){
        if(!app.isUserLogin()){
            fab.setVisibility(View.GONE);
        }
    }

    private void hideToolbar() {
        if (!toolbarHiding) {
            Toolbar toolbar = getAttachedActivity().getToolbar();
            toolbarHeight = toolbar.getHeight();
            toolbar.animate().translationYBy(0 - toolbarHeight).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            toolbarHiding = true;
        }
    }

    private void showToolbar() {
        if (toolbarHiding) {
            Toolbar toolbar = getAttachedActivity().getToolbar();
            toolbar.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            toolbarHiding = false;
        }
    }

    @FocusChange(R.id.shortComment)
    void commentFocused() {

        if (shortComment.isFocused()) {
            showCommentTools();
        } else {
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
            if (flattenComments != null) {
                comments.addComments(flattenComments);
            }
            tmpCommentsList.clear();
        }
        prepareCommentsDone = true;
        joinTopic();
    }

    @UiThread
    public void joinTopic() {
        if (prepareCommentsDone && prepareTopicPostDone) {

            topicPostView = TopicPostView_.build(getAttachedActivity());
            topicPostView.bind(topicPost);
            if (list.getHeaderViewsCount() == 0)
                list.addHeaderView(topicPostView);
            commentAdapter.notifyDataSetChanged();
            if (lastFirstVisibleItem != 0) {
                list.setSelection(lastFirstVisibleItem);
            }
            setRefreshComplete();
        }
    }

    @UiThread
    void setRefreshComplete() {
        Log.d("refresh", "stop");
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        lastFirstVisibleItem = list.getFirstVisiblePosition();

        super.onSaveInstanceState(outState);


    }

    public void loadReplies(Comment c, CommentView waitUpdateCommentView, int newRepliesPosition) {
        if (!swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(true);
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
        setRefreshComplete();
    }

    public void loadTopicPost() {
        if (!swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(true);
        prepareTopicPostDone = false;
        client.getTopicPost(topic.getId() + "", topicPostCallback);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_topic, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    void comment(String msg) {
        // Detect for reply or new comment
        boolean isReply = false;
        if (msg.startsWith("ตอบ คห.")) {
            String commentNo = msg.substring(0, msg.indexOf(">")).split(" ")[2];
            msg = msg.split(">")[1].trim();
            DoReplyEvent e = (DoReplyEvent) shortComment.getTag();
            client.reply(topic.getId(), e.commentRefId, e.commentNo, e.commentTimestamp, msg, doCommentCallback);

        } else if (msg.length() > 0) {
            swipeRefreshLayout.setRefreshing(true);
            hideCommentTools();
            client.comment(topic.getId(), msg, doCommentCallback);
        }

    }


    @OptionsItem(android.R.id.home)
    void backHome() {
        getAttachedActivity().onBackPressed();
    }

    @OptionsItem
    void action_sort_comment() {
        Log.d("menu", "sort comment");


        new MaterialDialog.Builder(getAttachedActivity())
                .title(comment_sort_type_title)
                .items(comment_sort_type)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int i, CharSequence text) {
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
                        Collections.sort(comments.getComments(), commentComparator);
                        commentAdapter.notifyDataSetChanged();
                    }
                })
                .show();

//        sortDialog = PostOfficeHelper.newSimpleListMailCancelable(
//                getAttachedActivity(),
//                comment_sort_type_title,
//                Design.MATERIAL_LIGHT,
//                comment_sort_type,
//                new ListStyle.OnItemAcceptedListener<CharSequence>() {
//                    @Override
//                    public void onItemAccepted(CharSequence charSequence, int i) {
//                        CommentComparator commentComparator;
//                        switch (i) {
//                            case 0:
//                                commentComparator = new CommentComparator(CommentComparator.SortType.Vote);
//                                break;
//                            case 1:
//                                commentComparator = new CommentComparator(CommentComparator.SortType.Emo);
//                                break;
//                            case 2:
//                                commentComparator = new CommentComparator(CommentComparator.SortType.Order);
//                                break;
//                            default:
//                                commentComparator = new CommentComparator(CommentComparator.SortType.Order);
//                                break;
//                        }
//                        Collections.sort(comments.getComments(), commentComparator);
//                        commentAdapter.notifyDataSetChanged();
//                    }
//                });
//        sortDialog.show(getFragmentManager());
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
    public void reply(DoReplyEvent e) {
        commentDialogView.clear();
        commentDialogView.replyTo(e.commentNo);
        commentDialogView.setTag(e);
        commentDialog.show();
        showKeyboard();
    }

    @Subscribe
    public void vote(DoVoteEvent e) {
        tmpCommentView = e.view;
        tmpComment = e.comment;

        if (e.comment != null) {
            if (e.comment.isReply()) {
                client.voteReply(topic.getId(), e.comment.getParent().getId(), e.comment.getComment_no(), e.comment.getReply_id(), e.comment.getReply_no(), doVoteCallback);
            } else {
                client.voteComment(topic.getId(), e.comment.getId(), e.comment.getComment_no(), doVoteCallback);
            }
        } else {
            client.voteTopic(topic.getId(), doVoteCallback);
        }
    }

    @Subscribe
    public void emo(final DoEmoEvent e) {
        Log.d("emo", "receive event");
        if (!e.equals(emoEvent)) {
            emoEvent = e;
        } else {
            return;
        }


        new MaterialDialog.Builder(getAttachedActivity())
                .title(emoTitle)
                .items(emoType)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int i, CharSequence text) {
                        tmpCommentView = emoEvent.view;
                        PantipRestClient.Emo emo = PantipRestClient.Emo.Like;
                        switch (i) {
                            case 0:
                                emo = PantipRestClient.Emo.Like;
                                break;
                            case 1:
                                emo = PantipRestClient.Emo.Laugh;
                                break;
                            case 2:
                                emo = PantipRestClient.Emo.Love;
                                break;
                            case 3:
                                emo = PantipRestClient.Emo.Impress;
                                break;
                            case 4:
                                emo = PantipRestClient.Emo.Scary;
                                break;
                            case 5:
                                emo = PantipRestClient.Emo.Surprised;
                                break;
                        }
                        if (emoEvent.comment != null) {
                            if (emoEvent.comment.isReply()) {
                                client.emoReply(
                                        topic.getId(),
                                        emoEvent.comment.getParent().getId(),
                                        emoEvent.comment.getReply_id(),
                                        emoEvent.comment.getComment_no(),
                                        emoEvent.comment.getReply_no(),
                                        emo,
                                        doEmoCallback);
                            } else {
                                client.emoComment(topic.getId(), emoEvent.comment.getId(), emo, doEmoCallback);
                            }
                        } else {
                            client.emoTopic(topic.getId(), emo, doEmoCallback);
                        }
                        emoEvent = null;
                    }
                })
                .show();


//        emoDialog = PostOfficeHelper.newSimpleListMailCancelable(
//                getAttachedActivity(),
//                emoTitle,
//                Design.MATERIAL_LIGHT,
//                emoType,
//                new ListStyle.OnItemAcceptedListener<CharSequence>() {
//                    @Override
//                    public void onItemAccepted(CharSequence charSequence, int i) {
//
//                        tmpCommentView = emoEvent.view;
//                        PantipRestClient.Emo emo = PantipRestClient.Emo.Like;
//                        switch (i) {
//                            case 0:
//                                emo = PantipRestClient.Emo.Like;
//                                break;
//                            case 1:
//                                emo = PantipRestClient.Emo.Laugh;
//                                break;
//                            case 2:
//                                emo = PantipRestClient.Emo.Love;
//                                break;
//                            case 3:
//                                emo = PantipRestClient.Emo.Impress;
//                                break;
//                            case 4:
//                                emo = PantipRestClient.Emo.Scary;
//                                break;
//                            case 5:
//                                emo = PantipRestClient.Emo.Surprised;
//                                break;
//                        }
//                        if (emoEvent.comment != null) {
//                            if (emoEvent.comment.isReply()) {
//                                client.emoReply(
//                                        topic.getId(),
//                                        emoEvent.comment.getParent().getId(),
//                                        emoEvent.comment.getReply_id(),
//                                        emoEvent.comment.getComment_no(),
//                                        emoEvent.comment.getReply_no(),
//                                        emo,
//                                        doEmoCallback);
//                            } else {
//                                client.emoComment(topic.getId(), emoEvent.comment.getId(), emo, doEmoCallback);
//                            }
//                        } else {
//                            client.emoTopic(topic.getId(), emo, doEmoCallback);
//                        }
//                        emoEvent = null;
//                    }
//                });
//        emoDialog.show(getFragmentManager());
    }

    private void loadNextComments() {
        if (!swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(true);
        prepareCommentsDone = false;
        client.getComments(topic.getId() + "", currentCommentPage, false, commentsCallback);
    }

    private void updateNewComment(int commentNo) {
        if (!swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(true);
        prepareCommentsDone = false;
        client.getComments(topic.getId() + "", currentCommentPage, false, commentsCallback);
    }


    private void hideCommentPane() {
//        if (!fabIsHiding) {
//            commentBarSeparator.animate()
//                    .translationY(commentPane.getHeight() * 3)
//                    .setInterpolator(new AccelerateDecelerateInterpolator())
//                    .start();
//            commentPane.animate()
//                    .translationY(commentPane.getHeight() * 3)
//                    .setInterpolator(new AccelerateDecelerateInterpolator())
//                    .start();
//            fabIsHiding = true;
//        }
    }

    @Click
    void fab(){
        commentDialogView.clear();
        commentDialog.show();
        showKeyboard();
    }

    void showKeyboard(){
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private CommentDialogView initCommentDialog() {
        if (commentDialog == null) {
            commentDialogView = CommentDialogView_.build(getAttachedActivity());
            commentDialog = new MaterialDialog.Builder(getAttachedActivity())
                    .title("แสดงความเห็น")
                    .customView(commentDialogView)
                    .positiveText("ส่งความเห็น")
                    .positiveColor(getResources().getColor(R.color.base_color))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            comment(commentDialogView.getMessage());
                        }
                    })
                    .build();
        }
        return commentDialogView;
    }

    private void showCommentPane() {
//        if (fabIsHiding) {
//            commentPane.animate()
//                    .translationY(commentViewDefaultHeight)
//                    .setInterpolator(new AccelerateDecelerateInterpolator())
//                    .start();
//            commentBarSeparator.animate()
//                    .translationY(commentViewDefaultHeight)
//                    .setInterpolator(new AccelerateDecelerateInterpolator())
//                    .start();
//            fabIsHiding = false;
//        }
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

    @Override
    public void onRefresh() {
        topicPost = null;
        comments = null;
        currentCommentPage = 1;
        loadNextComments();
        loadTopicPost();
    }

    class CommentAdapter extends BaseAdapter {
        boolean noComment = false;

        public CommentAdapter(Context context) {
        }

        @Override
        public int getCount() {
            if (comments != null) {
                if (comments.getComments() != null) {
                    noComment = false;
                    return comments.getComments().size();
                } else {
                    noComment = true;
                    return 1;
                }
            } else {
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

            if (noComment) {
                //                NoCommentView noCommentView = NoCommentView_.build(getActivity());
                //                noCommentView.bind();

                return new View(getActivity());
            }
            final CommentView commentView;
            if (position == getCount() - 5) {
                if (comments.getCount() > comments.getPaging().getLimit() * comments.getPaging().getPage()) {
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
