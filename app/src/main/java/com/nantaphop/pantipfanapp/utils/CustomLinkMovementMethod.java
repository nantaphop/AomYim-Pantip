//package com.nantaphop.pantipfanapp.utils;
//
//import android.content.Context;
//import android.content.Intent;
//import android.text.Layout;
//import android.text.method.LinkMovementMethod;
//import android.text.style.URLSpan;
//import android.util.Log;
//import android.view.MotionEvent;
//import com.google.analytics.tracking.android.EasyTracker;
//
///**
// * Created with IntelliJ IDEA.
// * User: Nantaphop
// * Date: 8/14/13
// * Time: 3:06 PM
// * To change this template use File | Settings | File Templates.
// */
//public class CustomLinkMovementMethod extends LinkMovementMethod
//{
//
//    private static Context movementContext;
//
//    private static CustomLinkMovementMethod linkMovementMethod = new CustomLinkMovementMethod();
//
//    public boolean onTouchEvent(android.widget.TextView widget, android.text.Spannable buffer, MotionEvent event)
//    {
//        int action = event.getAction();
//
//        if (action == MotionEvent.ACTION_UP)
//        {
//            int x = (int) event.getX();
//            int y = (int) event.getY();
//
//            x -= widget.getTotalPaddingLeft();
//            y -= widget.getTotalPaddingTop();
//
//            x += widget.getScrollX();
//            y += widget.getScrollY();
//
//            Layout layout = widget.getLayout();
//            int line = layout.getLineForVertical(y);
//            int off = layout.getOffsetForHorizontal(line, x);
//
//            URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
//            if (link.length != 0)
//            {
//                String url = link[0].getURL();
//                if (url.contains("http")){
//
//                    ((MyApp)movementContext.getApplicationContext()).getTracker().sendEvent("user_action", "open_link_internal", url, null);
//
//
//                    if(url.contains("pantip.com/topic")){
//                        Intent i = new Intent(movementContext, TopicActivity_.class);
//                        i.putExtra(TopicActivity.EXTRA_URL, url);
//                        movementContext.startActivity(i);
//                    }
//                    else if(url.contains("pantip.com/forum") || url.contains("pantip.com/tag")){
//                        Intent i = new Intent(movementContext, MainActivity_.class);
//                        i.putExtra(TopicActivity.EXTRA_URL, url);
//                        movementContext.startActivity(i);
//                    }else{
//
//                        Log.d("Link", url);
//                        Intent i = new Intent(movementContext, WebViewActivity_.class);
//                        i.putExtra(WebViewActivity.EXTRA_URL, url);
//                        movementContext.startActivity(i);
//                    }
//                }
//
////                else if (url.contains("tel")){
////                    Log.d("Link", url);
////                    Toast.makeText(movementContext, "Tel was clicked", Toast.LENGTH_LONG).show();
////                }
////
////                else if(url.contains("mailto")) {
////                    Log.d("Link", url);
////                    Toast.makeText(movementContext, "Mail link was clicked", Toast.LENGTH_LONG).show();
////                }
//                return true;
//            }
//        }
//
//        return super.onTouchEvent(widget, buffer, event);
//    }
//
//    public static android.text.method.MovementMethod getInstance(Context c)
//    {
//        movementContext = c;
//        EasyTracker.getInstance().setContext(movementContext);
//        return linkMovementMethod;
//    }
//}
