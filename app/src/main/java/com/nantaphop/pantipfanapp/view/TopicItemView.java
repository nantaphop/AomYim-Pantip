package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.text.format.DateUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.Topic;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.w3c.dom.Text;

import java.util.Date;

/**
 * Created by nantaphop on 27-Jul-14.
 */
@EViewGroup(R.layout.itemview_topic)
public class TopicItemView extends RelativeLayout{

    @ViewById
    TextView title;

    @ViewById
    TextView author;

    @ViewById
    TextView comments;

    @ViewById
    TextView votes;

    @ViewById
    TextView date;

    public TopicItemView(Context context) {
        super(context);
    }

    public void bind(Topic topic){
        title.setText(topic.getTitle());
        author.setText(topic.getAuthor());
        comments.setText(""+topic.getComments());
        votes.setText(""+topic.getVotes());
        date.setText(DateUtils.getRelativeTimeSpanString(topic.getDate().getTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS));
//        date.setText(topic.getDate().hr);
    }
}
