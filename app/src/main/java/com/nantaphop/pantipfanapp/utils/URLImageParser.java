package com.nantaphop.pantipfanapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.nantaphop.pantipfanapp.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created with IntelliJ IDEA.
 * User: Nantaphop
 * Date: 7/26/13
 * Time: 10:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class URLImageParser implements Html.ImageGetter {
    private static final String TAG = "PantipFanApp";

    private final Context c;
    TextView container;
    int screenWidth;



    public URLImageParser(TextView t, Context c) {
        this.c = c;
        this.container = t;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)c).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels - (c.getResources().getDimensionPixelSize(R.dimen.padding_default)*6); // Minus Padding

    }

    @Override
    public Drawable getDrawable(String s) {

        BaseApplication myApp = (BaseApplication) c.getApplicationContext();
        BitmapDrawable drawableFromCache = myApp.getBitmapDrawableFromCache(s);


        URLDrawable urlDrawable;

        if (drawableFromCache == null) {
            urlDrawable = new URLDrawable();

            urlDrawable.src = s;

            // get the actual source
            if (!myApp.isLoading(s)) {
                ImageGetterAsyncTask asyncTask =
                        new ImageGetterAsyncTask( urlDrawable);

                asyncTask.execute(s);

                //TODO return some loading image
                Log.d(TAG, "Load " + s);
            }

            urlDrawable.drawable = c.getResources().getDrawable(android.R.drawable.ic_menu_gallery);
        urlDrawable.drawable.setBounds(0,0,urlDrawable.drawable.getIntrinsicWidth(), urlDrawable.drawable.getIntrinsicHeight());

        urlDrawable.setBounds(0,0,urlDrawable.drawable.getIntrinsicWidth(), urlDrawable.drawable.getIntrinsicHeight());


            // return reference to URLDrawable where I will change with actual image from
            // the src tag
            return urlDrawable;
        } else {
            return drawableFromCache;
        }

    }


    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }



        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
//            return fetchDrawable(source);
            try {
                return fetchBitmap(source);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable result) {
            // set the correct bound according to the result from HTTP call
            // TODO Some Exception found result is null when scroll long list of image
            try {
                Log.d("height", "" + result.getIntrinsicHeight());
                Log.d("width",""+result.getIntrinsicWidth());
                // Resize to fit screen if image too large
                int newHeight = 0;
                if(result.getIntrinsicWidth() > screenWidth){
                    newHeight = (int)((double)(result.getIntrinsicHeight()) / (double)(result.getIntrinsicWidth()) * (double)(screenWidth));
                    urlDrawable.setBounds(0, 0, container.getWidth(), newHeight);
                }else{
                    urlDrawable.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());
                }

//                int newHeight = result.getIntrinsicHeight() / result.getIntrinsicWidth() * container.getWidth();
//                urlDrawable.setBounds(0, 0, container.getWidth(), newHeight);

                // change the reference of the current drawable to the result
                // from the HTTP call
                urlDrawable.drawable = result;

                // redraw the image by invalidating the container
                URLImageParser.this.container.invalidate();

                // For ICS

                Integer previousAddedHeight = (Integer) container.getTag();
                if(previousAddedHeight == null){
                    previousAddedHeight = 0 ;
                }

                int addHeight = newHeight > 0 ? newHeight : result.getIntrinsicHeight();
                addHeight -= previousAddedHeight;

//            URLImageParser.this.container.setHeight((URLImageParser.this.container.getHeight() + addHeight));

                container.setTag(previousAddedHeight + addHeight);


                // Pre ICS
                URLImageParser.this.container.setEllipsize(null);

                container.setText(container.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                // Calculate ratios of height and width to requested height and width
                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);

                // Choose the smallest ratio as inSampleSize value, this will guarantee
                // a final image with both dimensions larger than or equal to the
                // requested height and width.
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }

            return inSampleSize;
        }


        public BitmapDrawable fetchBitmap(String urlString){

            try {

                BaseApplication myApp = (BaseApplication) c.getApplicationContext();
                BitmapDrawable drawableFromCache = myApp.getBitmapDrawableFromCache(urlString);


                if (drawableFromCache == null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    BitmapFactory.decodeStream(fetch(urlString), new Rect(), options);
                    int imageHeight = options.outHeight;
                    int imageWidth = options.outWidth;

                    int reqHeight = imageHeight;
                    int reqWidth = imageWidth;


                    // Resize to fit screen if image too large
//                if(imageWidth > container.getWidth()){
//                    reqHeight = (int)((double)(imageHeight) / (double)(imageWidth) * (double)(container.getWidth()));
//                    reqWidth = container.getWidth();
////                    drawable.setBounds(0, 0, container.getWidth(), newHeight);
//                }
                    if(imageWidth > screenWidth){
                        reqHeight = (int)((double)(imageHeight) / (double)(imageWidth) * (double)(screenWidth));
                        reqWidth = screenWidth;
    //                    drawable.setBounds(0, 0, container.getWidth(), newHeight);
                    }

//                    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);


                    options.inJustDecodeBounds = false;
//                    InputStream in = fetch(urlString);

                    InputStream in;
                    if (urlString.contains("ptcdn")) {
                        Log.d(TAG, "Load Resize : "+"http://pantip.com/timthumb?src="+urlString+"&q=80&w="+reqWidth+"&h="+reqHeight);
                        in = fetch(urlString.replace("-o.jpg","-s.jpg"));
                    } else {
                        Log.d(TAG, "Load : "+"http://pantip.com/timthumb?src="+urlString+"&q=80&w="+reqWidth+"&h="+reqHeight);

                        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                        in = fetch(urlString);
                    }
                    BufferedInputStream buf = new BufferedInputStream(in, 8192);
                    Bitmap bitmap = null;
                    try {
                        bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(buf, new Rect(0, 0, 0, 0), options), reqWidth, reqHeight, false);
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        return null;
                    }


                    BitmapDrawable bitmapDrawable = new BitmapDrawable(c.getResources(), bitmap);
                    bitmapDrawable.setBounds(0,0,reqWidth,reqHeight);

                    myApp.addBitmapDrawableToCache(urlString, bitmapDrawable);

                    return bitmapDrawable;
                } else {
                    return drawableFromCache;
                }


            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return null;
            }

        }



        private InputStream fetch(String urlString) throws MalformedURLException, IOException {
//                DefaultHttpClient httpClient = new DefaultHttpClient();
//                HttpGet request = new HttpGet(urlString);
//                HttpResponse response = httpClient.execute(request);
//                return response.getEntity().getContent();

            return ((BaseApplication)c.getApplicationContext()).getImage(urlString);
        }
    }

    public class URLDrawable extends BitmapDrawable implements View.OnClickListener{
        // the drawable that you need to set, you could set the initial drawing
        // with the loading image if you need to
        protected Drawable drawable;

        public String src;



        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if(drawable != null) {
                drawable.draw(canvas);
            }
        }




        @Override
        public void onClick(View view) {
            Log.d(TAG, "click "+src);
        }
    }


}
