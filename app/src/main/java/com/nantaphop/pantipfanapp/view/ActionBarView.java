package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.markushi.ui.ActionView;
import at.markushi.ui.action.Action;
import com.nantaphop.pantipfanapp.R;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by nantaphop on 16-Oct-14.
 */
@EViewGroup(R.layout.actionbar)
public class ActionBarView extends LinearLayout{
    @ViewById
    ActionView action;
    @ViewById
    TextView title;
    private OnClickListener onClickListener;
    private Action actionV;

    public ActionBarView(Context context) {
        super(context);
    }

    public void setAction(Action action){
        this.actionV = action;
        this.action.setAction(this.actionV, true);
    }

    public void setActionClick(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
        this.action.setOnClickListener(this.onClickListener);
    }

    public Action getAction() {
        return actionV;
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public String getTitle() {
        return title.getText().toString();
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }
}
