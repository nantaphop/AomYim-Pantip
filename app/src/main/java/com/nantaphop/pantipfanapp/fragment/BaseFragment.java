package com.nantaphop.pantipfanapp.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import com.github.mrengineer13.snackbar.SnackBar;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.service.PantipRestClient;
import com.nantaphop.pantipfanapp.view.BaseActivity;
import org.androidannotations.annotations.*;
import org.jsoup.Connection;

/**
 * Created by nantaphop on 08-Aug-14.
 */
@EFragment
public class BaseFragment extends Fragment {
    @App
    protected BaseApplication app;
    @Bean
    protected PantipRestClient client;
    private BaseActivity activity;

    @Override
    public void onAttach(Activity activity) {
        this.activity = (BaseActivity)activity;
        super.onAttach(activity);
    }

    @OptionsItem(android.R.id.home)
    void back(){
        Log.d("action", "up pressed");
        getAttachedActivity().onBackPressed();
    }

    public BaseActivity getAttachedActivity() {
        return activity;
    }

    private void toast(String msg, short duration){
        activity.getSnackBar().show(msg, duration);
    }

    void toastInfo(String msg){
        toast(msg, SnackBar.SHORT_SNACK);
    }

    void toastAlert(String msg) {
        toast(msg, SnackBar.LONG_SNACK);
    }
}
