package com.nantaphop.pantipfanapp.fragment;

import android.widget.ImageView;

import com.nantaphop.pantipfanapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by nantaphop on 23-Jan-15.
 */
@EFragment(R.layout.fragment_photoview)
public class PhotoViewFragment extends BaseFragment {
    @ViewById
    ImageView photo;

    @FragmentArg
    String photoUrl;
    private PhotoViewAttacher mAttacher;

    @AfterViews
    void init(){


        Picasso.with(getAttachedActivity()).load(photoUrl).into(photo, new Callback() {
            @Override
            public void onSuccess() {
                // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
                mAttacher = new PhotoViewAttacher(photo);
            }

            @Override
            public void onError() {

            }
        });


    }
}
