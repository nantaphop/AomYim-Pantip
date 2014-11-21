package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nantaphop.pantipfanapp.R;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

/**
 * Created by nantaphop on 02-Sep-14.
 */
@EViewGroup(R.layout.listitem_topic_section)
public class TopicSectionView extends LinearLayout{
    @ViewById
    LinearLayout root;
    @ViewById
    TextView title;
    @ViewById
    TextView button;
    @ColorRes(R.color.base_color_bright)
    int rippleColor;

    public TopicSectionView(Context context) {
        super(context);
    }

    @AfterViews
    void init(){
    }

    public void bind(String title){
        bind(title, null, null);
    }
    public void bind(String title, String buttonTxt, OnClickListener onClickListener){
        this.title.setText(title);
        if(buttonTxt  != null){
            this.button.setVisibility(VISIBLE);
            this.button.setText(buttonTxt);
            this.root.setOnClickListener(onClickListener);
        }else{
            this.button.setVisibility(GONE);
            this.root.setOnClickListener(null);
        }
    }
}
