package com.nantaphop.pantipfanapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.OpenTopicEvent;
import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.ForumPart;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import com.nantaphop.pantipfanapp.view.RecommendCardView;
import com.nantaphop.pantipfanapp.view.RecommendCardView_;
import com.nantaphop.pantipfanapp.view.TopicSectionView;
import com.nantaphop.pantipfanapp.view.TopicSectionView_;

import java.util.ArrayList;

/**
 * Created by nantaphop on 18-Jan-15.
 */
public class ForumAdapter extends TopicAdapter {



    private String[] recommendTopicTitle;
    private Context context;
    private Forum forum;
    private ForumPart forumPart;
    private final PantipRestClient.ForumType forumType;
    private String[] recommendTopicUrl;

    public ForumAdapter(Context context, Forum forum, ForumPart forumPart, PantipRestClient.ForumType forumType, String[] recommendTopicTitle, String[] recommendTopicUrl) {
        super(context, new ArrayList<Topic>());
        this.context = context;
        this.forum = forum;
        this.forumPart = forumPart;
        this.forumType = forumType;
        this.recommendTopicTitle = recommendTopicTitle;
        this.recommendTopicUrl = recommendTopicUrl;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
        super.setTopics(forum.getTopics());
        notifyDataSetChanged();
    }

    public void setForumPart(ForumPart forumPart) {
        this.forumPart = forumPart;
        notifyDataSetChanged();
    }

    public void setData(Forum forum, ForumPart forumPart, String[] recommendTopicTitle, String[] recommendTopicUrl){
        this.forum = forum;
        super.setTopics(forum.getTopics());
        this.forumPart = forumPart;
        this.recommendTopicTitle = recommendTopicTitle;
        this.recommendTopicUrl = recommendTopicUrl;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (forumType == PantipRestClient.ForumType.Room){
            if (forum == null || forumPart == null)
                return 0;
            else
                return super.getCount() + 3;
        }else if(forumType == PantipRestClient.ForumType.Tag || forumType == PantipRestClient.ForumType.Club){
            if (forum == null)
                return 0;
            else
                return super.getCount();
        }
        else{
            return super.getCount();
        }
    }

    @Override
    public Object getItem(int i) {
        if (forumType == PantipRestClient.ForumType.Room) {
            if (recommendTopicTitle != null && recommendTopicTitle.length > 0)
                return super.getItem(i - 3);
            else
                return super.getItem(i);
        } else {
            return super.getItem(i);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getCount() > 3 && position == (getCount() - 1)) {
            return TYPE_TOPIC_LOADING;
        } else if (forumType == PantipRestClient.ForumType.Room && (position == 0 || position == 2)) {
            return TYPE_SECTION;
        } else if (forumType == PantipRestClient.ForumType.Room && (position == 1)) {
            return TYPE_RECOMMEND;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        switch (getItemViewType(position)) {

            case TYPE_SECTION:
                view = getViewTopicSection(position, convertView);
                break;
            case TYPE_RECOMMEND:
                view = getViewTopicRecommend(position, convertView);
                break;
            default: view = super.getView(position, convertView, parent);
        }
        return view;
    }

    private TopicSectionView getViewTopicSection(int position, View convertView) {
        final TopicSectionView topicSectionView;
        if (convertView != null) {
            topicSectionView = (TopicSectionView) convertView;
        } else {
            topicSectionView = TopicSectionView_.build(context);

        }
        if (position == 0) {
            topicSectionView.bind(
                    context.getString(R.string.recomend_topic), context.getString(R.string.view_all), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showRecommendDialog();
                        }
                    }
            );
        } else if (position == 2) {
            topicSectionView.bind(context.getString(R.string.topic_in_forum));
        }
        return topicSectionView;
    }

    private RecommendCardView getViewTopicRecommend(int position, View convertView) {
        final RecommendCardView recommendCardView;

        if (convertView != null) {
            recommendCardView = (RecommendCardView) convertView;
        } else {
            recommendCardView = RecommendCardView_.build(context);
        }
        if (recommendTopicTitle != null) {
            for (int i = 0; i < recommendTopicTitle.length; i++) {
                recommendCardView.addItem(recommendTopicTitle[i], recommendTopicUrl[i]);
            }
        } else {
            Log.e("recommend", "getViewTopicRecommend called even recommendTopicTitle is null");
        }
        return recommendCardView;
    }

    private void showRecommendDialog() {

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.recomend_topic)
                .adapter(new ArrayAdapter<String>(context, R.layout.listitem_recommend_dialog, forumPart.getRecommendTopic()))
//                .items(forumPart.getRecommendTopic().toArray(new String[forumPart.getRecommendTopic().size()]))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int i, CharSequence text) {

                    }
                }).build();
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                final Topic topic = new Topic();
                topic.setTitle(forumPart.getRecommendTopic().get(i));
                try {
                    topic.setId(Integer.parseInt(forumPart.getRecommendUrl().get(i).split("/")[4]));
                    ((BaseApplication)context.getApplicationContext()).getEventBus().post(new OpenTopicEvent(topic));
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
        });
        dialog.show();
    }
}
