package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;

import com.nantaphop.pantipfanapp.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by nantaphop on 30-Oct-14.
 */
@EViewGroup(R.layout.forum_empty)
public class ForumEmptyView extends LinearLayout {
    @ViewById
    Button retry;

    public ForumEmptyView(Context context) {
        super(context);
    }

    public void setOnRetry(OnClickListener onRetry){
        retry.setOnClickListener(onRetry);
    }



}
