package com.nantaphop.pantipfanapp.utils;

import com.activeandroid.app.Application;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nantaphop.pantipfanapp.R;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by nantaphop on 20-Jan-15.
 */
@EBean(scope = EBean.Scope.Singleton)
public class AnalyticsUtils {

    public static String CATEGORY_USER_ACTION = "User Action";
    // Misc Action
    public static String ACTION_OPEN_MY_PROFILE = "Open My Profile";
    public static String ACTION_LOGIN = "Login";
    public static String ACTION_LOGOUT = "Logout";
    public static String ACTION_OUT_LINK = "Out Link";
    public static String ACTION_IN_LINK = "In Link";

    // Forum Action
    public static String ACTION_OPEN_FORUM = "Open Forum";
    public static String ACTION_SORT_TOPIC = "Sort Topic";
    public static String ACTION_CHOOSE_TOPIC_TYPE = "Choose Topic Type";
    public static String ACTION_VIEW_TAG_LIST = "View Tag List";
    public static String ACTION_VIEW_CLUB_LIST = "View Club List";
    public static String ACTION_VIEW_RECOMMEND_LIST = "View Recommend List";

    // Topic Action
    public static String ACTION_OPEN_TOPIC = "Open Topic";
    public static String ACTION_SORT_COMMENT = "Sort Comment";
    public static String ACTION_FAVORITE = "Favorite Topi";
    public static String ACTION_UNFAVORITE = "Unfavorite Topic";
    public static String ACTION_SHARE = "Share Topic";
    public static String ACTION_VIEW_BROWSER = "View in Browser";
    public static String ACTION_VOTE_TOPIC = "Vote Topic";
    public static String ACTION_EMO_TOPIC = "Emo Comment";
    public static String ACTION_COMMENT = "Comment on Topic";
    public static String ACTION_REPLY_COMMENT = "Reply on Topic";
    public static String ACTION_VOTE_COMMENT = "Vote Comment";
    public static String ACTION_EMO_COMMENT = "Emo Comment";




    public static String CATEGORY_UI_ACTION = "UI Action";

    @RootContext
    Application app;

    private static Tracker tracker;

    @AfterInject
    void init(){
        tracker = getTracker();
    }

    synchronized Tracker getTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(app);
            tracker = (analytics.newTracker(R.xml.global_tracker));
            tracker.enableAdvertisingIdCollection(true);
        }
        return tracker;
    }

    public static void setScreen(String screenName){
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void sendEvent(String category, String action, String label, long value){
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(value)
                .build());
    }

    public static void sendEvent(String category, String action, String label){
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    public static void sendTiming(String category, long value, String name, String label){
        tracker.send(new HitBuilders.TimingBuilder()
                .setCategory(category)
                .setValue(value)
                .setVariable(name)
                .setLabel(label)
                .build());
    }

}
