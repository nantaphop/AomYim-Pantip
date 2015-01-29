package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.DoEmoEvent;
import com.nantaphop.pantipfanapp.event.DoReplyEvent;
import com.nantaphop.pantipfanapp.event.DoVoteEvent;
import com.nantaphop.pantipfanapp.event.OpenUserEvent;
import com.nantaphop.pantipfanapp.response.Comment;
import com.nantaphop.pantipfanapp.response.EmoResponse;
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import com.nantaphop.pantipfanapp.utils.CircleTransform;
import com.nantaphop.pantipfanapp.utils.CustomLinkMovementMethod;
import com.nantaphop.pantipfanapp.utils.URLImageParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DrawableRes;

import java.util.Date;

/**
 * Created by nantaphop on 10-Aug-14.
 */
@EViewGroup(R.layout.listitem_comment)
public class CommentView extends RelativeLayout {

    @App
    BaseApplication app;
    @ViewById
    RelativeLayout root;
    @ViewById
    TextView author;
    @ViewById
    View replyIndicator;
    @ViewById
    TextView date;
    @ViewById
    TextView body;
    @ViewById
    Button reply;
    @ViewById
    Button votes;
    @ViewById
    Button emo;
    @ViewById
    Button loadMore;
    @ViewById
    ImageView authorPic;
    @ViewById
    TextView commentNo;


    @ColorRes(R.color.base_color_highlight)
    int highlightColor;
    @ColorRes(R.color.author_indicator)
    int authorIndicatorColor;
    @ColorRes(R.color.background)
    int bgColor;

    @DrawableRes(R.drawable.ic_action_thumb_up_highlight)
    Drawable thumbUpHighlight;
    @DrawableRes(R.drawable.ic_action_thumb_up)
    Drawable thumbsUp;
    @DrawableRes(R.drawable.ic_action_mood_small)
    Drawable emoNormal;
    @DrawableRes(R.drawable.ic_action_mood_small_highlight)
    Drawable emoHighlight;


    private Context context;


    private static DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .displayer(new RoundedBitmapDisplayer((int) 90f))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .showImageOnLoading(R.drawable.ic_image)
            .build();
    private OnClickListener onLoadMoreClick;
    private Comment comment;

    public CommentView(Context context) {
        super(context);
        this.context = context;
    }

    @AfterViews
    void setLinkMovement() {
        body.setMovementMethod(CustomLinkMovementMethod.getInstance(context));

    }

    @AfterViews
    void disableNonLoginUser() {
        if (!app.isUserLogin()) {
            reply.setVisibility(GONE);
        }
    }


    public void bind(Comment comment) {
        this.comment = comment;
        author.setText(comment.getUser().getName());
        if(comment.isOwner_topic()){
            root.setBackgroundColor(authorIndicatorColor);
        }else{
            root.setBackgroundColor(bgColor);
        }
        date.setText(
                DateUtils.getRelativeTimeSpanString(
                        comment.getDate().getTime(),
                        new Date().getTime(),
                        DateUtils.MINUTE_IN_MILLIS
                )
        );
        body.setText(Html.fromHtml(comment.getMessage(), new URLImageParser(body, context), null));
        votes.setText(comment.getPoint() > 0 ? comment.getPoint() + "" : "");
        if (comment.isVoted()) {
            votes.setCompoundDrawablesWithIntrinsicBounds(thumbUpHighlight, null, null, null);
        } else {
            votes.setCompoundDrawablesWithIntrinsicBounds(thumbsUp, null, null, null);
        }
        emo.setText(comment.getEmo_score() > 0 ? comment.getEmo_score() + "" : "");
        if (comment.getEmotion().isAlreadyAction()) {
            emo.setCompoundDrawablesWithIntrinsicBounds(emoHighlight, null, null, null);
        } else {
            emo.setCompoundDrawablesWithIntrinsicBounds(emoNormal, null, null, null);
        }
        reply.setText(comment.getReply_count() > 0 ? comment.getReply_count() + "" : "");
        if (comment.isReply()) {
            reply.setVisibility(GONE);
            replyIndicator.setVisibility(VISIBLE);
            commentNo.setText("#" + comment.getComment_no() + "-" + comment.getReply_no());
            if (comment.getReply_no() == comment.getParent().getLastReply() &&
                    comment.getParent().getReply_count() > comment.getReply_no()) {
                loadMore.setVisibility(VISIBLE);
            } else {
                loadMore.setVisibility(GONE);
            }
        } else {

            if (app.isUserLogin()) {
                reply.setVisibility(VISIBLE);
            } else {
                reply.setVisibility(GONE);
            }

            commentNo.setText("#" + comment.getComment_no());
            replyIndicator.setVisibility(GONE);
            loadMore.setVisibility(GONE);
        }

        Picasso.with(context).

                load(comment.getUser()

                                .

                                        getAvatar()

                                .

                                        getLarge()

                ).

                transform(new CircleTransform()

                ).

                into(authorPic);


    }

    public Comment getComment() {
        return comment;
    }

    public void setOnLoadMoreClick(OnClickListener onLoadMoreClick) {
        this.onLoadMoreClick = onLoadMoreClick;
        loadMore.setOnClickListener(onLoadMoreClick);
    }

    public void disableLoadMore() {
        loadMore.setVisibility(GONE);
    }

    @Click
    public void reply() {
        if (app.isUserLogin()) {
            app.getEventBus()
                    .post(new DoReplyEvent(comment.getComment_id(), comment.getComment_no(), comment.getDate().getTime()));
        }
    }

    @Click
    public void votes() {

        if (app.isUserLogin()) {
            if (!comment.isVoted()) {

                app.getEventBus().post(new DoVoteEvent(this, comment));

            }
        }
    }

    @Click
    public void emo() {
        if (app.isUserLogin()) {
            Log.d("emo", "send event");
            app.getEventBus().post(new DoEmoEvent(this, comment));
        }
    }

    public void setVote(int point) {
        comment.setPoint(point);
        comment.setVoted();
        bind(comment);
    }

    public void setEmo(EmoResponse emoResponse) {
        if (comment.getEmotion().getLike().getStatus() == null &&
                comment.getEmotion().getLove().getStatus() == null &&
                comment.getEmotion().getLaugh().getStatus() == null &&
                comment.getEmotion().getScary().getStatus() == null &&
                comment.getEmotion().getImpress().getStatus() == null &&
                comment.getEmotion().getSurprised().getStatus() == null
                )
            comment.setEmo_score(comment.getEmo_score() + 1);

        if (emoResponse.getEmotion().getEmoType().equalsIgnoreCase(String.valueOf(PantipRestClient.Emo.Like))) {
            if (comment.getEmotion().getLike().getStatus() == null) {
                comment.getEmotion().getLike().setStatus(true);
            }
        } else if (emoResponse.getEmotion().getEmoType().equalsIgnoreCase(String.valueOf(PantipRestClient.Emo.Love))) {
            if (comment.getEmotion().getLove().getStatus() == null) {
                comment.getEmotion().getLove().setStatus(true);
            }
        } else if (emoResponse.getEmotion().getEmoType().equalsIgnoreCase(String.valueOf(PantipRestClient.Emo.Laugh))) {
            if (comment.getEmotion().getLaugh().getStatus() == null) {
                comment.getEmotion().getLaugh().setStatus(true);
            }
        } else if (emoResponse.getEmotion().getEmoType().equalsIgnoreCase(String.valueOf(PantipRestClient.Emo.Scary))) {
            if (comment.getEmotion().getScary().getStatus() == null) {
                comment.getEmotion().getScary().setStatus(true);
            }
        } else if (emoResponse.getEmotion().getEmoType().equalsIgnoreCase(String.valueOf(PantipRestClient.Emo.Impress))) {
            if (comment.getEmotion().getImpress().getStatus() == null) {
                comment.getEmotion().getImpress().setStatus(true);
            }
        } else if (emoResponse.getEmotion().getEmoType().equalsIgnoreCase(String.valueOf(PantipRestClient.Emo.Surprised))) {
            if (comment.getEmotion().getSurprised().getStatus() == null) {
                comment.getEmotion().getSurprised().setStatus(true);
            }
        }


        bind(comment);
    }

    @Click
    void authorPic(){
        BaseApplication.getEventBus().post(new OpenUserEvent(comment.getUser().getId(), comment.getUser().getName(), comment.getUser().getAvatar().getLarge()));
    }

    public void setGone(){
        root.setVisibility(GONE);
    }

    public void setVisible(){
        root.setVisibility(VISIBLE);

    }


}
