package com.nantaphop.pantipfanapp.view;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;

import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by nantaphop on 02-Sep-14.
 */
public class MyAnimationAdapter extends AnimationAdapter {

    public MyAnimationAdapter(BaseAdapter baseAdapter) {
        super(baseAdapter);
    }

    @Override
    public Animator[] getAnimators(ViewGroup parent, View view) {
        Animator bottomInAnimator = ObjectAnimator.ofFloat(view, "translationY", 500, 0);
        Animator rotateIn = ObjectAnimator.ofFloat(view, "rotationX", 30, 0);
        bottomInAnimator.setInterpolator(new DecelerateInterpolator());
        rotateIn.setInterpolator(new DecelerateInterpolator());
        return new Animator[] { bottomInAnimator, rotateIn };
    }


    protected long getAnimationDelayMillis() {
        return 50;
    }

    protected long getAnimationDurationMillis() {
        return 1000;
    }
}