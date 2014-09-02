package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.devddagnet.bright.lib.Bright;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.OpenTopicEvent;
import com.nantaphop.pantipfanapp.response.Tag;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;

import java.util.Date;

/**
 * Created by nantaphop on 13-Aug-14.
 */
@EViewGroup(R.layout.listitem_topic_thumbnail)
public class TopicThumbnailView extends RelativeLayout implements View.OnClickListener {

    @App
    BaseApplication app;

    @ViewById
    ImageView thumbnail;
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
    private Context context;

    @DimensionPixelSizeRes(R.dimen.padding_extra)
    int paddingExtra;
    @DimensionPixelSizeRes(R.dimen.padding_default)
    int paddingDefault;

    @ColorRes(R.color.text_color_title_dark)
    int textDark;
    @ColorRes(R.color.text_color_title_bright)
    int textBright;
    @ColorRes(R.color.card_header_bg_bright)
    int headerBgBright;
    @ColorRes(R.color.card_header_bg_dark)
    int headerBgDark;
    @ColorRes(android.R.color.transparent)
    int headerBgTransparent;


    private static DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .cacheInMemory(true)
            .cacheOnDisk(true)
//            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .showImageOnLoading(R.drawable.ic_image)
            .build();
    private Topic topic;

    public TopicThumbnailView(Context context) {
        super(context);
        this.context = context;
        setOnClickListener(this);

    }

    @AfterViews
    void init() {
        thumbnail.setDrawingCacheEnabled(true);
    }

    @Trace(tag = "topicView")
    public void bind(final Topic topic) {
        this.topic = topic;

        title.setText(Html.fromHtml(topic.getTitle()));
//        title.setTextColor(textDark);
//        title.setBackgroundColor(headerBgTransparent);

        app.getImageLoader().displayImage(topic.getCoverImg(), thumbnail, displayImageOptions);


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

    @Background
    void calculateTitleColor(Bitmap loadedImage) {
        boolean isBright = Bright.setup(Bright.Config.RELATIVE | Bright.Config.PERFORMANCE).isBright(loadedImage);
        topic.setBrightCover(isBright);
        updateTitleColor(isBright);

    }


    @UiThread
    void updateTitleColor(boolean isBright) {
        if (isBright) {
            title.setTextColor(textDark);
            title.setBackgroundColor(headerBgBright);
        } else {
            title.setTextColor(textBright);
            title.setBackgroundColor(headerBgDark);

        }
    }


    @Override
    public void onClick(View view) {
        app.getEventBus().post(new OpenTopicEvent(topic));
    }
}
