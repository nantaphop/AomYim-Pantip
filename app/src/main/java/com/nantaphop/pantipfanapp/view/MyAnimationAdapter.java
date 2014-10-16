package com.nantaphop.pantipfanapp.view;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import com.nineoldandroids.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nineoldandroids.animation.Animator;

/**
 * Created by nantaphop on 02-Sep-14.
 */
public class MyAnimationAdapter extends AnimationAdapter {

    public MyAnimationAdapter(BaseAdapter baseAdapter) {
        super(baseAdapter);
    }

    @Override
    public Animator[] getAnimators(ViewGroup parent, View view) {
        Animator bottomInAnimator = ObjectAnimator.ofFloat(view, "translationY", 1000, 0);
        Animator rotateIn = ObjectAnimator.ofFloat(view, "rotationX", 60, 0);
        bottomInAnimator.setInterpolator(new DecelerateInterpolator());
        rotateIn.setInterpolator(new DecelerateInterpolator());
        return new Animator[] { bottomInAnimator, rotateIn };
    }

    @Override
    protected long getAnimationDelayMillis() {
        return 50;
    }

    @Override
    protected long getAnimationDurationMillis() {
        return 500;
    }
}