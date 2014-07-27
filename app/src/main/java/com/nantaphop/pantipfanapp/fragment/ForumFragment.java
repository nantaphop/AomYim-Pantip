package com.nantaphop.pantipfanapp.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.model.ForumPagerItem;
import com.nantaphop.pantipfanapp.response.Forum;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import com.nantaphop.pantipfanapp.utils.RESTUtils;
import com.nantaphop.pantipfanapp.view.TopicItemView;
import com.nantaphop.pantipfanapp.view.TopicItemView_;
import mehdi.sakout.dynamicbox.DynamicBox;
import org.androidannotations.annotations.*;
import org.apache.http.Header;

import static com.nantaphop.pantipfanapp.service.PantipRestClient.ForumType;
import static com.nantaphop.pantipfanapp.service.PantipRestClient.TopicType;

/**
 * Created by nantaphop on 27-Jul-14.
 */
@EFragment(R.layout.fragment_forum)
public class ForumFragment extends Fragment {

    @App
    BaseApplication app;

    @Bean
    PantipRestClient client;

    @ViewById
    ListView list;

    @FragmentArg
    ForumPagerItem forumPagerItem;

    @InstanceState
    String lastIdCurrentPage ="0";
    @InstanceState
    int currentPage;

    @InstanceState
    Forum forum;

    ForumAdapter adapter;


    private boolean mIsScrollingUp;
    private int mLastFirstVisibleItem;


    BaseJsonHttpResponseHandler forumCallback = new BaseJsonHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, String s, Object o) {
            forum = (Forum) o;
//            lastIdCurrentPage = forum.getLastIdCurrentPage();
            if(adapter == null){
                adapter = new ForumAdapter();
            }
            list.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Object o) {

        }

        @Override
        protected Object parseResponse(String s, boolean b) throws Throwable {
            return RESTUtils.parseForum(s);
        }
    };

    @AfterViews
    void init(){
        client.getForum(forumPagerItem.url, ForumType.Room, TopicType.All_Except_Sell, ++currentPage, lastIdCurrentPage, true, forumCallback);
        DynamicBox box = new DynamicBox(getActivity(), list);
        box.showLoadingLayout();

    }

    private class ForumAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return forum.getTopics().size();
        }

        @Override
        public Topic getItem(int i) {
            return forum.getTopics().get(i);
        }

        @Override
        public long getItemId(int i) {
            return forum.getTopics().get(i).getId();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TopicItemView item;
            if(view == null){
                item = TopicItemView_.build(getActivity());
            }else{
                item = (TopicItemView) view;
            }
            item.bind(getItem(i));
            return item;
        }
    }


}
