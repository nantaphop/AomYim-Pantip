package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.OpenTopicEvent;
import com.nantaphop.pantipfanapp.response.Topic;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

/**
 * Created by nantaphop on 28-Oct-14.
 */
@EViewGroup(R.layout.card_recommend)
public class RecommendCardView extends CardView {
    @App
    BaseApplication app;
    @ViewById
    LinearLayout root;
    @ViewById
    CardView card;
    @ColorRes(R.color.base_color_bright)
    int rippleColor;

    public RecommendCardView(Context context) {
        super(context);
    }

    public void addItem(final String title, final String url){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.card_item_recommend, this, false);
        final TextView text = (TextView) itemView.findViewById(android.R.id.text1);
        text.setText(title);
        final Topic topic = new Topic();
        topic.setTitle(title);
        topic.setId(Integer.parseInt(url.split("/")[4]));
        text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                app.fireEvent(new OpenTopicEvent(topic));
            }
        });
        RippleDrawable.createRipple(itemView, rippleColor);
        root.addView(itemView);
    }

}
