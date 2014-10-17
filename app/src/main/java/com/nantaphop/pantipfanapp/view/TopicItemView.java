package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.Topic;
import com.r0adkll.postoffice.widgets.RippleView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.w3c.dom.Text;

import java.util.Date;

/**
 * Created by nantaphop on 27-Jul-14.
 */
@EViewGroup(R.layout.itemview_topic)
public class TopicItemView extends RelativeLayout {
    @ViewById
    RelativeLayout root;

    @ViewById
    TextView title;

    @ViewById
    TextView author;

    @ViewById
    TextView comments;

    @ViewById
    TextView votes;

    @ColorRes(R.color.base_color_bright)
    int rippleColor;

    public TopicItemView(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        RippleDrawable.createRipple(root, rippleColor);
    }

    public void bind(Topic topic) {
        title.setText(Html.fromHtml(topic.getTitle()));
        author.setText(
                Html.fromHtml(topic.getAuthor()) + " - " + DateUtils.getRelativeTimeSpanString(
                        topic.getDate()
                             .getTime(),
                        new Date().getTime(),
                        DateUtils.MINUTE_IN_MILLIS
                )
        );
        comments.setText(topic.getComments() + " " + getContext().getString(R.string.comment));
        votes.setText(topic.getVotes() + " " + getContext().getString(R.string.vote));
//        date.setText(DateUtils.getRelativeTimeSpanString(topic.getDate().getTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS));
//        date.setText(topic.getDate().hr);
    }
}
