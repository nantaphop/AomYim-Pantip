package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nantaphop.pantipfanapp.R;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by nantaphop on 09-Sep-14.
 */
@EViewGroup(R.layout.simple_empty)
public class SimpleEmptyView extends LinearLayout{
    @ViewById
    ProgressBar progressBar;
    @ViewById
    TextView text;
    @ViewById
    LinearLayout empty;

    public SimpleEmptyView(Context context) {
        super(context);
    }

    @AfterViews
    void init(){
        text.setVisibility(GONE);
    }

    public void setText(String text){
        this.text.setText(text);
        this.text.setVisibility(VISIBLE);
    }

    public void clearText(){
        text.setText("");
        text.setVisibility(GONE);
    }
}
