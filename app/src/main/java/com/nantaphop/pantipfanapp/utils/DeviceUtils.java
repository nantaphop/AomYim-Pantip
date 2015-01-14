package com.nantaphop.pantipfanapp.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by nantaphop on 14-Jan-15.
 */
public class DeviceUtils {
    public static int getDisplayCenterPixel(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y/2;
    }
}
