package com.nantaphop.pantipfanapp.fragment.dialog;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.DialogDismissEvent;
import com.nantaphop.pantipfanapp.event.DialogShowEvent;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import org.androidannotations.annotations.*;

import java.util.ArrayList;

/**
 * Created by nantaphop on 04-Aug-14.
 */
@EFragment(R.layout.dialog_recommend)
public class RecommendDialog extends DialogFragment {

    @App
    BaseApplication app;

    @ViewById
    CardListView cardList;

    @FragmentArg
    ArrayList<String> topics;
    @FragmentArg
    ArrayList<String> urls;
    private ArrayList<Card> cards = new ArrayList<Card>(10);

    @AfterViews
    void init(){
        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
//        window.setBackgroundDrawable(new ColorDrawable(0));
        window.getAttributes().windowAnimations = R.style.RecommendDialogAnimation;
        window.getAttributes().gravity = Gravity.RIGHT+Gravity.TOP;
        window.getAttributes().dimAmount = 0.0f;
        for (int i = 0; i < topics.size(); i++) {
            Card card = new Card(getActivity());
            card.setTitle(topics.get(i));
            card.setClickable(true);
            card.setShadow(false);
            card.setBackgroundResourceId(R.drawable.card_background);
            cards.add(card);
        }
        cardList.setAdapter(new CardArrayAdapter(getActivity(), cards));
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
