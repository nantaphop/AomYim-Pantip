package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.OpenTopicEvent;
import com.nantaphop.pantipfanapp.response.Topic;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

/**
 * Created by nantaphop on 02-Sep-14.
 */
@EViewGroup(R.layout.listitem_topic_recommend)
public class TopicRecommendView extends LinearLayout {

    @App
    BaseApplication app;

    @ViewById
    CardView card;
    @ViewById
    TextView title;
    @ViewById
    LinearLayout root;
    @ColorRes(R.color.base_color_bright)
    int rippleColor;

    public TopicRecommendView(Context context) {
        super(context);
    }

    @AfterViews
    void init(){
        RippleDrawable.createRipple(root, rippleColor);
    }

    public void bind(String title, String url) {
        Log.d("recommend", title + " - "+url);
        this.title.setText(title);

        final Topic topic = new Topic();
        topic.setTitle(title);
        topic.setId(Integer.parseInt(url.split("/")[4]));

        root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                app.getEventBus().post(new OpenTopicEvent(topic));
            }
        });
    }
}
