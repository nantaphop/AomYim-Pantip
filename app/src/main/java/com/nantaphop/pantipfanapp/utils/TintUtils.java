package com.nantaphop.pantipfanapp.utils;

import android.app.Activity;

import com.nantaphop.pantipfanapp.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by nantaphop on 19-Nov-14.
 */
public class TintUtils {
    public static SystemBarTintManager tintMe(Activity activity) {
        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(false);
        // set a custom tint color for all system bars
        int color = activity.getResources().getColor(R.color.base_color);
        tintManager.setTintColor(color);
// set a custom navigation bar resource
        tintManager.setNavigationBarTintColor(color);
        return tintManager;
    }
}
