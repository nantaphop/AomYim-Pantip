package com.nantaphop.pantipfanapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.view.LoadingItemView;
import com.nantaphop.pantipfanapp.view.LoadingItemView_;
import com.nantaphop.pantipfanapp.view.TopicThumbnailView;
import com.nantaphop.pantipfanapp.view.TopicThumbnailView_;
import com.nantaphop.pantipfanapp.view.TopicView;
import com.nantaphop.pantipfanapp.view.TopicView_;

import java.util.List;

/**
 * Created by nantaphop on 18-Jan-15.
 */
public class TopicAdapter extends BaseAdapter {


    final int TYPE_TOPIC = 0;
    final int TYPE_TOPIC_THUMBNAIL = 1;
    final int TYPE_TOPIC_LOADING = 2;
    final int TYPE_SECTION = 3;
    final int TYPE_RECOMMEND = 4;
    private Context context;
    private List<Topic> topics;
    private LoadingItemView loadingItemView;
    LoadMoreListener loadMoreListener;

    public TopicAdapter(Context context, List<Topic> topics) {
        this.context = context;
        this.topics = topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
        notifyDataSetChanged();
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public LoadingItemView getLoadingItemView() {
        return loadingItemView;
    }

    @Override
    public int getCount() {
        return topics.size();
    }

    @Override
    public Object getItem(int i) {
        return topics.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        Topic topic;
        try {
            topic = (Topic) getItem(position);
        } catch (Exception e) {
            return TYPE_TOPIC;
        }
        if (topic.getCoverImg() != null && topic.getCoverImg().length() > 0)
            return TYPE_TOPIC_THUMBNAIL;
        else
            return TYPE_TOPIC;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        switch (getItemViewType(position)) {
            case TYPE_TOPIC:
                view = getViewTopic(position, convertView);
                break;
            case TYPE_TOPIC_THUMBNAIL:
                view = getViewTopicThumbnail(position, convertView);
                break;
            case TYPE_TOPIC_LOADING:
                view = getViewLoading(position, convertView);
        }
        return view;
    }

    private LoadingItemView getViewLoading(int position, View convertView) {

        if (convertView != null) {
            loadingItemView = (LoadingItemView) convertView;
        } else {
            loadingItemView = LoadingItemView_.build(context);
        }

        return loadingItemView;
    }





    private TopicView getViewTopic(int position, View convertView) {
        final TopicView topicView;
        if (position == getCount() - 5) {
            if(loadMoreListener!=null){
                loadMoreListener.onLoadMore();
            }
        }

        if (convertView != null) {
            topicView = (TopicView) convertView;
        } else {
            topicView = TopicView_.build(context);

        }
        topicView.bind((Topic) getItem(position));
        return topicView;
    }

    private TopicThumbnailView getViewTopicThumbnail(int position, View convertView) {
        final TopicThumbnailView topicView;
        if (position == getCount() - 5) {
            if(loadMoreListener!=null){
                loadMoreListener.onLoadMore();
            }
        }

        if (convertView != null) {
            topicView = (TopicThumbnailView) convertView;
        } else {
            topicView = TopicThumbnailView_.build(context);

        }
        topicView.bind((Topic) getItem(position));
        return topicView;
    }

    public interface LoadMoreListener{
        public void onLoadMore();
    }

}
