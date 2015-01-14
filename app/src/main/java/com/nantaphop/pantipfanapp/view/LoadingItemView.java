package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.widget.LinearLayout;

import com.nantaphop.pantipfanapp.R;

import org.androidannotations.annotations.EViewGroup;

/**
 * Created by nantaphop on 14-Jan-15.
 */
@EViewGroup(R.layout.listitem_loading)
public class LoadingItemView extends LinearLayout{
    private Context context;

    public LoadingItemView(Context context) {
        super(context);
        this.context = context;
    }
}
