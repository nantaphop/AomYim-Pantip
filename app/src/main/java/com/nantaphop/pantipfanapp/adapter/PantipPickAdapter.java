package com.nantaphop.pantipfanapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.view.PantipPickView;
import com.nantaphop.pantipfanapp.view.PantipPickView_;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nantaphop on 21-Jan-15.
 */
public class PantipPickAdapter extends BaseAdapter{
    private Context context;
    List<Topic> topics;

    public PantipPickAdapter(Context context) {
        this.context = context;
        topics = new ArrayList<>();
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return topics.size();
    }

    @Override
    public Object getItem(int position) {
        return topics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PantipPickView pantipPickView;
        if(convertView == null){
            pantipPickView = PantipPickView_.build(context);
        }else{
            pantipPickView = (PantipPickView)convertView;
        }
        pantipPickView.bind((Topic) getItem(position));
        return pantipPickView;
    }
}
