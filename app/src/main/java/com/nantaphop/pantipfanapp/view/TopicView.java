package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.OpenTopicEvent;
import com.nantaphop.pantipfanapp.model.ReadLog;
import com.nantaphop.pantipfanapp.response.Tag;
import com.nantaphop.pantipfanapp.response.Topic;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;

import java.util.Date;

/**
 * Created by nantaphop on 13-Aug-14.
 */
@EViewGroup(R.layout.listitem_topic)
public class TopicView extends RelativeLayout implements View.OnClickListener {

    @App
    BaseApplication app;

    @ViewById
    RelativeLayout root;

    @ViewById
    CardView card;
    @ViewById
    TextView title;
    @ViewById
    TextView author;
    @ViewById
    TextView date;
    @ViewById
    TextView tags;
    @ViewById
    TextView votes;
    @ViewById
    TextView comments;
    @ViewById
    View read;
    private Context context;
    private BaseAdapter adapter;

    @DimensionPixelSizeRes(R.dimen.padding_extra)
    int paddingExtra;
    @DimensionPixelSizeRes(R.dimen.padding_default)
    int paddingDefault;

    @ColorRes(R.color.text_color_title_primary_dark)
    int text_color_title_primary_dark;
    @ColorRes(R.color.text_color_title_secondary_dark)
    int text_color_title_secondary_dark;
    @ColorRes(R.color.card_header_bg_bright)
    int headerBgBright;
    @ColorRes(R.color.card_header_bg_dark)
    int headerBgDark;
    @ColorRes(android.R.color.transparent)
    int headerBgTransparent;
    @ColorRes(R.color.base_color_bright)
    int rippleColor;

    private Topic topic;


    public TopicView(Context context) {
        super(context);
        this.context = context;

    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
    }

    @AfterViews
    void init() {
//        RippleDrawable.createRipple(root, rippleColor);
        root.setOnClickListener(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            card.setElevation(context.getResources().getDimensionPixelSize(R.dimen.card_topic_elevation));
        }
    }

    @Trace(tag = "topicView")
    public void bind(final Topic topic) {
        this.topic = topic;

        title.setText(Html.fromHtml(topic.getTitle()));
        if (topic.isRead()) {
            read.setVisibility(VISIBLE);
        } else {
            read.setVisibility(GONE);
        }


        author.setText(Html.fromHtml(topic.getAuthor()));
        date.setText(DateUtils.getRelativeTimeSpanString(topic.getDate().getTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS));
        comments.setText(topic.getComments() + " " + getContext().getString(R.string.comment));
        votes.setText(topic.getVotes() + " " + getContext().getString(R.string.vote));

        Resources resources = context.getResources();
        // Color indicate
        if (topic.getComments() == 0)
            comments.setTextColor(resources.getColor(R.color.text_color_score_0));
        else if (topic.getComments() < 10)
            comments.setTextColor(resources.getColor(R.color.text_color_score_1));
        else if (topic.getComments() < 20)
            comments.setTextColor(resources.getColor(R.color.text_color_score_2));
        else
            comments.setTextColor(resources.getColor(R.color.text_color_score_3));

        // Color indicate
        if (topic.getVotes() == 0)
            votes.setTextColor(resources.getColor(R.color.text_color_score_0));
        else if (topic.getVotes() < 10)
            votes.setTextColor(resources.getColor(R.color.text_color_score_1));
        else if (topic.getVotes() < 20)
            votes.setTextColor(resources.getColor(R.color.text_color_score_2));
        else
            votes.setTextColor(resources.getColor(R.color.text_color_score_3));


        StringBuilder sb = new StringBuilder();
        for (Tag tag : topic.getTags()) {
            sb.append(tag.getTag() + ", ");
        }
        try {
            tags.setText(sb.substring(0, sb.length() - 2));
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View view) {
        topic.setRead(true);
        adapter.notifyDataSetChanged();
        app.getEventBus().post(new OpenTopicEvent(topic, this));
    }
}
