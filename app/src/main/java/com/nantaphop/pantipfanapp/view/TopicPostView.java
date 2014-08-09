package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.response.TopicPost;
import com.nantaphop.pantipfanapp.utils.URLImageParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

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

    private static DisplayImageOptions displayImageOptions =  new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .displayer(new RoundedBitmapDisplayer((int) 60f))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .showImageOnLoading(R.drawable.ic_launcher)
            .build();

    public TopicPostView(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(TopicPost topicPost){
        title.setText(topicPost.getTitle());
        date.setText(topicPost.getDateString());
        body.setText(Html.fromHtml(topicPost.getBody(), new URLImageParser(body, context), null));
        votes.setText(topicPost.getVotes()+"");
        emo.setText(topicPost.getEmotions()+"");
        author.setText(topicPost.getAuthor());

    }
}
