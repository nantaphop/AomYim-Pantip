package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.response.Comment;
import com.nantaphop.pantipfanapp.utils.URLImageParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.jsoup.Connection;

import java.util.Date;

/**
 * Created by nantaphop on 10-Aug-14.
 */
@EViewGroup(R.layout.listitem_comment)
public class CommentView extends RelativeLayout{
    @App
    BaseApplication app;
    @ViewById
    TextView author;
    @ViewById
    TextView date;
    @ViewById
    TextView body;
    @ViewById
    Button votes;
    @ViewById
    Button emo;
    @ViewById
    ImageView authorPic;
    @ViewById
    TextView commentNo;
    private Context context;

    private static DisplayImageOptions displayImageOptions =  new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .displayer(new RoundedBitmapDisplayer((int) 60f))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .showImageOnLoading(R.drawable.ic_launcher)
            .build();

    public CommentView(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(Comment comment){
        author.setText(comment.getUser().getName());
        date.setText(DateUtils.getRelativeTimeSpanString(comment.getDate().getTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS));
        body.setText(Html.fromHtml(comment.getMessage(), new URLImageParser(body, context), null));
        votes.setText(comment.getPoint()+"");
        emo.setText(comment.getEmo_score()+"");
        commentNo.setText("#"+comment.getComment_no());
        app.getImageLoader().displayImage(comment.getUser().getAvatar().getLarge(), authorPic, displayImageOptions);

    }
}
