package com.nantaphop.pantipfanapp.view;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.nantaphop.pantipfanapp.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by nantaphop on 14-Jan-15.
 */
@EViewGroup(R.layout.dialog_comment)
public class CommentDialogView extends LinearLayout{
    @ViewById
    EditText shortComment;
    private Context context;

    public CommentDialogView(Context context) {
        super(context);
        this.context = context;
    }

    public String getMessage(){
        return shortComment.getText().toString();
    }

    public void clear(){
        shortComment.setText("");

    }

    public void setText(String msg){
        shortComment.setText(msg);
    }

    public void replyTo(int commentNo){
        shortComment.setText(
                Html.fromHtml(
                        String.format(
                                context.getString(R.string.reply_comment_prefix),
                                commentNo
                        ) + " "
                )
        );
        shortComment.requestFocus();
        shortComment.setSelection(shortComment.getText().length());
    }


}
