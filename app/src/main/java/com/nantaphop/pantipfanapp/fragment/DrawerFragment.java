package com.nantaphop.pantipfanapp.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.OpenForumRearrangeEvent;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

/**
 * Created by nantaphop on 11-Aug-14.
 */
@EFragment(R.layout.fragment_drawer)
public class DrawerFragment extends BaseFragment {
    @App
    BaseApplication app;
    @ViewById
    ListView list;

    @StringArrayRes
    String[] drawer_menu;

    @AfterViews
    void init() {
        list.setAdapter(new ArrayAdapter<String>(getAttachedActivity(), android.R.layout.simple_list_item_1, drawer_menu));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        app.getEventBus().post(new OpenForumRearrangeEvent());
                        break;
                }
            }
        });
    }
}
