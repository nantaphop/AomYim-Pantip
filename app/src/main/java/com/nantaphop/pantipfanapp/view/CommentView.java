package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.gcm.GoogleCloudMessaging;
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
    View replyIndicator;
    @ViewById
    TextView date;
    @ViewById
    TextView body;
    @ViewById
    Button repiles;
    @ViewById
    Button votes;
    @ViewById
    Button emo;
    @ViewById
    Button loadMore;
    @ViewById
    ImageView authorPic;
    @ViewById
    TextView commentNo;

    private Context context;



    private static DisplayImageOptions displayImageOptions =  new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .displayer(new RoundedBitmapDisplayer((int) 90f))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .showImageOnLoading(R.drawable.ic_launcher)
            .build();
    private OnClickListener onLoadMoreClick;
    private Comment comment;

    public CommentView(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(Comment comment){
        this.comment = comment;
        author.setText(comment.getUser().getName());
        date.setText(DateUtils.getRelativeTimeSpanString(comment.getDate().getTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS));
        body.setText(Html.fromHtml(comment.getMessage(), new URLImageParser(body, context), null));
        votes.setText(comment.getPoint()>0 ? comment.getPoint()+"" : "");
        emo.setText(comment.getEmo_score()>0 ? comment.getEmo_score()+"" : "");
        repiles.setText(comment.getReply_count()+"");
        if(comment.isReply()){
            repiles.setVisibility(GONE);
            replyIndicator.setVisibility(VISIBLE);
            commentNo.setText("#"+comment.getComment_no()+"-"+comment.getReply_no());
            if(comment.getReply_no() == comment.getParent().getLastReply() && comment.getParent().getReply_count() > comment.getReply_no()){
                loadMore.setVisibility(VISIBLE);
            }else {
                loadMore.setVisibility(GONE);
            }
        }else{
            if(comment.getReply_count() == 0){
                repiles.setVisibility(GONE);
            }else{
                repiles.setVisibility(VISIBLE);
            }
            commentNo.setText("#"+comment.getComment_no());
            replyIndicator.setVisibility(GONE);
            loadMore.setVisibility(GONE);
        }
        app.getImageLoader().displayImage(comment.getUser().getAvatar().getLarge(), authorPic, displayImageOptions);

    }

    public Comment getComment() {
        return comment;
    }

    public void setOnLoadMoreClick(OnClickListener onLoadMoreClick){
        this.onLoadMoreClick = onLoadMoreClick;
        loadMore.setOnClickListener(onLoadMoreClick);
    }

    public void disableLoadMore(){
        loadMore.setVisibility(GONE);
    }
}
