package com.nantaphop.pantipfanapp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.OpenChangelogDialog;
import com.nantaphop.pantipfanapp.event.OpenForumRearrangeEvent;
import com.nantaphop.pantipfanapp.event.OpenLoginScreenEvent;
import com.nantaphop.pantipfanapp.event.OpenUserEvent;
import com.nantaphop.pantipfanapp.event.UpdateLoginStateEvent;
import com.nantaphop.pantipfanapp.pref.UserPref_;
import com.nantaphop.pantipfanapp.utils.CircleTransform;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
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
    RelativeLayout userPane;
    @ViewById
    ListView list;
    @ViewById
    TextView login;
    @ViewById
    TextView myProfile;
    @ViewById
    TextView changelog;
    @ViewById
    TextView facebook;

    private static DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
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
        if (userPref.username().exists()) {
            myProfile.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            userPane.setVisibility(View.VISIBLE);
            usernameTxt.setText(userPref.username().get());
            Picasso.with(getAttachedActivity()).load(userPref.avatar().get()).transform(new CircleTransform()).into(avatar);

        } else {
            login.setVisibility(View.VISIBLE);
            userPane.setVisibility(View.GONE);
            myProfile.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void update(UpdateLoginStateEvent e) {
        init();
    }

    @Click
    void login() {
        BaseApplication.getEventBus().post(new OpenLoginScreenEvent());
    }

    @Click
    void userPane() {
        BaseApplication.getEventBus().post(new OpenLoginScreenEvent());
    }

    @Click
    void rearrangeRoom() {
        BaseApplication.getEventBus().post(new OpenForumRearrangeEvent());
    }

    @Click
    void myProfile() {
        BaseApplication.getEventBus().post(new OpenUserEvent(userPref.userId().get(), userPref.username().get(), userPref.avatar().get()));
    }

    @Click
    void changelog(){
        BaseApplication.getEventBus().post(new OpenChangelogDialog());
    }

    @Click
    void facebook(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/aomyimpantip"));
        startActivity(browserIntent);
    }

}
