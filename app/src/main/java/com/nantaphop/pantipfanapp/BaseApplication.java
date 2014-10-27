package com.nantaphop.pantipfanapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.activeandroid.app.Application;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import org.androidannotations.annotations.EApplication;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by nantaphop on 26-Jul-14.
 */
@EApplication
public class BaseApplication extends Application {

    static private Bus eventBus = null;
    static private Gson gson = null;

    LruCache<String, BitmapDrawable> imageCache;
    private Bitmap tmpDrawingCache;
    ArrayList<String> loadingList;
    private ImageLoader imageLoader;

    public static Bus getEventBus() {
        if (eventBus == null) {
            eventBus = new Bus(ThreadEnforcer.ANY);
        }
        return eventBus;
    }

    public void fireEvent(Object event){
        eventBus.post(event);
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().setDateFormat("MM/dd/yyyy HH:mm:ss").create();
        }
        return gson;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public ImageLoader getImageLoader(){
        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(new ImageLoaderConfiguration.Builder(this)
                    .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                    .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new LruMemoryCache(10 * 1024 * 1024))
                    .memoryCacheSize(10 * 1024 * 1024)
                    .diskCacheSize(50 * 1024 * 1024)
                    .diskCacheFileCount(100)
                    .writeDebugLogs()
                    .build());
        }
        return imageLoader;
    }

    public Bitmap getTmpDrawingCache() {
        return tmpDrawingCache;
    }

    public void setTmpDrawingCache(Bitmap tmpDrawingCache) {
        this.tmpDrawingCache = tmpDrawingCache;
    }

    public boolean isNetworkAvailable() {
        return ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public BitmapDrawable getBitmapDrawableFromCache(String urlString) {
        initBitmapCache();
        return imageCache.get(urlString);

    }

    public void addBitmapDrawableToCache(String urlString, BitmapDrawable bitmap) {
        initBitmapCache();
        imageCache.put(urlString, bitmap);

    }

    public static void loadAds(Context context, ViewGroup adHolder) {

//        AdView adView = new AdView(context);
//        adView.setAdSize(AdSize.SMART_BANNER);
//        adView.setAdUnitId(context.getResources().getString(R.string.ads_id));
//        adHolder.addView(adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        // Start loading the ad in the background.
//        adView.loadAd(adRequest);
    }

    private void initBitmapCache() {
        if (imageCache == null) {
            // Get memory class of this device, exceeding this amount will throw an
            // OutOfMemory exception.
            final int memClass
                    = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = 1024 * 1024 * memClass / 8;

            imageCache = new LruCache<String, BitmapDrawable>(cacheSize) {
                @Override
                protected int sizeOf(String key, BitmapDrawable bitmap) {
                    return (bitmap.getBitmap().getRowBytes() * bitmap.getBitmap().getHeight());
                }
            };
        }
    }

    public boolean isLoading(String url) {
        if (loadingList == null) {
            loadingList = new ArrayList<String>(10);
        }

        return loadingList.contains(url);

    }

    public InputStream getImage(String urlString) throws IOException {


        loadingList.add(urlString);


        String fileName = urlString.replace("/", "").replace(":", "").replace("?", "");

        File file = new File(getExternalCacheDir().getAbsolutePath(), fileName);


        try {
            loadingList.remove(urlString);
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlString);
            HttpResponse response = httpClient.execute(request);


            InputStream is = response.getEntity().getContent();
            BufferedInputStream bis = new BufferedInputStream(is);

               /*
                * Read bytes to the Buffer until there is nothing more to read(-1).
                */
            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }


            boolean newFile = file.createNewFile();
               /* Convert the Bytes read to a String. */
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();

            loadingList.remove(urlString);
            return new FileInputStream(file);
        }


    }
}
