package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.DoEmoEvent;
import com.nantaphop.pantipfanapp.event.DoVoteEvent;
import com.nantaphop.pantipfanapp.response.EmoResponse;
import com.nantaphop.pantipfanapp.response.TopicPost;
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
import org.androidannotations.annotations.res.DrawableRes;

/**
 * Created by nantaphop on 09-Aug-14.
 */
@EViewGroup(R.layout.listheader_topic_post)
public class TopicPostView extends RelativeLayout{
    @App
    BaseApplication app;
    @ViewById
    TextView title;
    @ViewById
    TextView date;
    @ViewById
    TextView body;
    @ViewById
    Button votes;
    @ViewById
    Button emo;
    @ViewById
    TextView author;
    @ViewById
    ImageView authorPic;
    private Context context;
    private TopicPost topicPost;

    @DrawableRes(R.drawable.ic_action_thumb_up_highlight)
    Drawable thumbUpHighlight;
    @DrawableRes(R.drawable.ic_action_thumb_up)
    Drawable thumbsUp;
    @DrawableRes(R.drawable.ic_action_mood_small)
    Drawable emoNormal;
    @DrawableRes(R.drawable.ic_action_mood_small_highlight)
    Drawable emoHighlight;




    private static DisplayImageOptions displayImageOptions =  new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .displayer(new RoundedBitmapDisplayer((int) 60f))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .showImageOnLoading(R.drawable.ic_launcher_pantip)
            .build();

    public TopicPostView(Context context) {
        super(context);
        this.context = context;
    }

    @AfterViews
    void setLinkMovement(){
        body.setMovementMethod(CustomLinkMovementMethod.getInstance(context));

    }

    public void bind(TopicPost topicPost){
        this.topicPost = topicPost;
        title.setText(topicPost.getTitle());
        date.setText(topicPost.getDateString());
        body.setText(Html.fromHtml(topicPost.getBody(), new URLImageParser(body, context), null));
        votes.setText(topicPost.getVotes()+"");
        emo.setText(topicPost.getEmotions()+"");
        author.setText(topicPost.getAuthor());
        Picasso.with(context).load(topicPost.getAuthorPic()).transform(new CircleTransform()).into(authorPic);


        if (topicPost.isVoted()) {
            votes.setCompoundDrawablesWithIntrinsicBounds(thumbUpHighlight, null, null, null);
        } else {
            votes.setCompoundDrawablesWithIntrinsicBounds(thumbsUp, null, null, null);
        }
        if (topicPost.isEmoted()) {
            emo.setCompoundDrawablesWithIntrinsicBounds(emoHighlight, null, null, null);
        } else {
            emo.setCompoundDrawablesWithIntrinsicBounds(emoNormal, null, null, null);
        }
    }

    @Click
    void emo(){
        app.getEventBus().post(new DoEmoEvent(this, topicPost));
    }

    @Click
    void votes(){
        app.getEventBus().post(new DoVoteEvent(this, topicPost));
    }

    public void setEmo() {
        topicPost.setEmotions(topicPost.getEmotions()+1);
        topicPost.setEmoted(true);
        bind(topicPost);
    }

    public void setVote() {
        topicPost.setVotes(topicPost.getVotes()+1);
        topicPost.setVoted(true);
        bind(topicPost);
    }
}
