package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.OpenTopicEvent;
import com.nantaphop.pantipfanapp.response.Tag;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;

import java.util.Date;

/**
 * Created by nantaphop on 02-Aug-14.
 */
public class TopicCard extends Card {
    public static final int TYPE = 0;

    BaseApplication app;

    private static DisplayImageOptions displayImageOptions =  new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .showImageOnLoading(R.drawable.ic_image)
            .build();

    private final Topic topic;
    private final Context context;

    public TopicCard(Context context, final Topic topic) {
        super(context, R.layout.card_topic_content);
        app = (BaseApplication) context.getApplicationContext();
        this.topic = topic;
        this.context = context;

        CardHeader cardHeader = new CardHeader(context);

        cardHeader.setTitle(Html.fromHtml(topic.getTitle()).toString());
        addCardHeader(cardHeader);

        setShadow(false);

        if(topic.getCoverImg() != null && topic.getCoverImg().length()>0) {
            UniversalCardThumbnail cardThumbnail = new UniversalCardThumbnail(context);
            cardThumbnail.setExternalUsage(true);
            cardThumbnail.setUrlResource(topic.getCoverImg());
            addCardThumbnail(cardThumbnail);
        }

        setClickable(true);
        mOnClickListener = new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                app.getEventBus().post(new OpenTopicEvent(TopicCard.this.topic));
            }
        };
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView author = (TextView) parent.findViewById(R.id.author);
        TextView votes = (TextView) parent.findViewById(R.id.votes);
        TextView comments = (TextView) parent.findViewById(R.id.comments);
        TextView tags = (TextView) parent.findViewById(R.id.tags);
        author.setText(Html.fromHtml(topic.getAuthor())+" - "+ DateUtils.getRelativeTimeSpanString(topic.getDate().getTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS));
        comments.setText(topic.getComments()+" "+ getContext().getString(R.string.comment));
        votes.setText(topic.getVotes() + " " + getContext().getString(R.string.vote));
        StringBuilder sb = new StringBuilder();
        for (Tag tag: topic.getTags()){
            sb.append(tag.getTag()+", ");
        }
        try {
            tags.setText(sb.substring(0, sb.length()-2));
        } catch (Exception e) {
        }
    }

    public Topic getTopic() {
        return topic;
    }

    class UniversalCardThumbnail extends CardThumbnail {

        public UniversalCardThumbnail(Context context) {
            super(context, R.id.card_thumbnail_layout);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View viewImage) {
            app.getImageLoader().displayImage(getUrlResource(), (ImageView) viewImage,displayImageOptions);
        }
    }

}
