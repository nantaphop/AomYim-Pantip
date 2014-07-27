package com.nantaphop.pantipfanapp;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import com.nantaphop.pantipfanapp.fragment.ForumHolderFragment_;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity {

    @ViewById
    FrameLayout content_frame;

    @AfterViews
    void init(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new ForumHolderFragment_());
        fragmentTransaction.commit();
    }

}
