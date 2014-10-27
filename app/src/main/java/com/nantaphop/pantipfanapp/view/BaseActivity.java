package com.nantaphop.pantipfanapp.view;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import com.github.mrengineer13.snackbar.SnackBar;
import com.nantaphop.pantipfanapp.R;

/**
 * Created by nantaphop on 27-Oct-14.
 */
public class BaseActivity extends ActionBarActivity {

    private static final String SAVED_SNACKBAR = "snackbar";
    private SnackBar mSnackBar;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.scale_from_bottom_center, R.anim.fragment_exit_slide_to_bottom);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
        // use this to save your snacks for later
//        saveState.putBundle(SAVED_SNACKBAR, mSnackBar.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle loadState) {
        super.onRestoreInstanceState(loadState);
        // use this to load your snacks for later
//        mSnackBar.onRestoreInstanceState(loadState.getBundle(SAVED_SNACKBAR));
    }

    protected void overrideAnimationBeforeStartActivity() {
        overridePendingTransition(R.anim.fragment_enter_slide_from_bottom, R.anim.scale_to_bottom_center);
    }

    public SnackBar getSnackBar() {
        if(mSnackBar == null){
            mSnackBar = new SnackBar(this);
        }
        return mSnackBar;
    }


}
