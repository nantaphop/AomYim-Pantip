package com.nantaphop.pantipfanapp.fragment;

import android.view.View;
import android.widget.*;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.OpenTopicEvent;
import com.nantaphop.pantipfanapp.event.UpdateLoginStateEvent;
import com.nantaphop.pantipfanapp.event.OpenForumRearrangeEvent;
import com.nantaphop.pantipfanapp.event.OpenLoginScreenEvent;
import com.nantaphop.pantipfanapp.pref.UserPref_;
import com.nantaphop.pantipfanapp.response.Topic;
import com.nantaphop.pantipfanapp.utils.CircleTransform;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by nantaphop on 11-Aug-14.
 */
@EFragment(R.layout.fragment_drawer)
public class DrawerFragment extends BaseFragment {

    @Pref
    UserPref_ userPref;


    @StringArrayRes
    String[] drawer_menu;
    @ViewById
    ImageView avatar;
    @ViewById
    TextView usernameTxt;
    @ViewById
    LinearLayout userPane;
    @ViewById
    ListView list;
    @ViewById
    Button login;

    private static DisplayImageOptions displayImageOptions =  new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .displayer(new RoundedBitmapDisplayer((int) 180f))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .showImageOnLoading(R.drawable.ic_image)
            .build();

    @AfterViews
    void init() {
        app.getEventBus().register(this);
        if(userPref.username().exists()){
            login.setVisibility(View.GONE);
            userPane.setVisibility(View.VISIBLE);
            usernameTxt.setText(userPref.username().get());
            Picasso.with(getAttachedActivity()).load(userPref.avatar().get()).transform(new CircleTransform()).into(avatar);

        }else{
            login.setVisibility(View.VISIBLE);
            userPane.setVisibility(View.GONE);
        }

        list.setAdapter(new ArrayAdapter<String>(getAttachedActivity(), android.R.layout.simple_list_item_1, drawer_menu));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        app.getEventBus().post(new OpenForumRearrangeEvent());
                        break;
//                    case 1:
//                        Topic topic = new Topic();
////                        topic.setId(31049053);
//                        topic.setId(32772497);
//                        topic.setTitle("[SR]รีวิวสั้น Pantip Fan App - Dark Theme [Android]");
//                        app.getEventBus().post(new OpenTopicEvent(topic));
//                        break;

                }
            }
        });
    }

    @Subscribe
    public void update(UpdateLoginStateEvent e) {
        init();
    }

    @Click
    void login(){
        app.getEventBus().post(new OpenLoginScreenEvent());
    }
    @Click
    void userPane(){
        app.getEventBus().post(new OpenLoginScreenEvent());
    }
}
