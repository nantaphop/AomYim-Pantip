package com.nantaphop.pantipfanapp.utils;

import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by nantaphop on 19-Jan-15.
 */
public class ShowcaseUtils {
    public static short REARRANGE_FORUM_ID = 1;
    public static short FIRST_TIME_LAUNCH_ID = 2;

    public static RelativeLayout.LayoutParams getCenterInParentLayoutParam(){
        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        return lp;

    }
}
