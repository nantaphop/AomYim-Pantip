package com.nantaphop.pantipfanapp.fragment.dialog;

import android.animation.*;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.DialogDismissEvent;
import com.nantaphop.pantipfanapp.event.DialogShowEvent;
import com.nantaphop.pantipfanapp.event.OpenTopicEvent;
import com.nantaphop.pantipfanapp.response.Topic;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import org.androidannotations.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nantaphop on 04-Aug-14.
 */
@EFragment(R.layout.dialog_list)
public class ListDialog extends DialogFragment {

    @App
    BaseApplication app;
    @FragmentArg
    String[] choices;
    @FragmentArg
    ArrayList<String> choicesArrayList;
    @FragmentArg
    int listItemLayoutRes;
    @FragmentArg
    String title;
    AdapterView.OnItemClickListener onItemClickListener;

    @ViewById
    ListView list;

    @AfterViews
    void init() {
        Window window = getDialog().getWindow();
//        window.requestFeature(Window.FEATURE_NO_TITLE);
//        window.setBackgroundDrawable(new ColorDrawable(0));
        window.getAttributes().windowAnimations = R.style.RecommendDialogAnimation;
        window.getAttributes().gravity = Gravity.RIGHT + Gravity.TOP;
        window.getAttributes().dimAmount = 0.0f;
        if (choices != null) {
            list.setAdapter(new ArrayAdapter<String>(getActivity(), listItemLayoutRes, choices));
        } else {
            list.setAdapter(new ArrayAdapter<String>(getActivity(), listItemLayoutRes, choicesArrayList));
        }
        getDialog().setTitle(title);
        list.setOnItemClickListener(onItemClickListener);

    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        app.getEventBus().post(new DialogShowEvent());
    }

    @Override
    public void onStop() {
        super.onStop();
        app.getEventBus().post(new DialogDismissEvent());
    }




}
