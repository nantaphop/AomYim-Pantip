package com.nantaphop.pantipfanapp;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.nantaphop.pantipfanapp.fragment.ForumRearrangeFragment;
import com.nantaphop.pantipfanapp.fragment.ForumRearrangeFragment_;
import com.nantaphop.pantipfanapp.view.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by nantaphop on 17-Nov-14.
 */
@EActivity(R.layout.activity_fragment)
public class RoomArrangementActivity extends BaseActivity {
    @ViewById
    Toolbar toolbar;
    @ViewById
    FrameLayout contentFrame;
    private ForumRearrangeFragment rearrangeFragment;

    @AfterViews
    void init(){
        rearrangeFragment = ForumRearrangeFragment_.builder().build();
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, rearrangeFragment, "rearrange").commit();
    }
}
