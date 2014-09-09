package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.widget.LinearLayout;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.response.Topic;
import org.androidannotations.annotations.EViewGroup;

/**
 * Created by nantaphop on 09-Sep-14.
 */
@EViewGroup(R.layout.view_add_comment)
public class AddCommentView extends LinearLayout {
    private Topic topic;

    public AddCommentView(Context context) {
        super(context);
    }
    public void bind(Topic topic){

        this.topic = topic;
    }
}
