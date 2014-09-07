package com.nantaphop.pantipfanapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.event.UpdateForumListEvent;
import com.nantaphop.pantipfanapp.model.ForumPagerItem;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import it.gmariotti.cardslib.library.extra.dragdroplist.internal.CardDragDropArrayAdapter;
import it.gmariotti.cardslib.library.extra.dragdroplist.view.CardListDragDropView;
import it.gmariotti.cardslib.library.internal.Card;
import org.androidannotations.annotations.*;

import java.util.*;

/**
 * Created by nantaphop on 11-Aug-14.
 */
@OptionsMenu(R.menu.menu_forum_rearrange)
@EFragment(R.layout.fragment_forum_rearrange)
public class ForumRearrangeFragment extends BaseFragment {
    public static final String TAG ="rearrange";

    @App
    BaseApplication app;

    @ViewById
    CardListDragDropView cardList;
    int[] newPosition;
    private CardDragDropArrayAdapter cardDragDropArrayAdapter;
    private ArrayList<Card> cards;

    @AfterViews
    void init() {
        List<ForumPagerItem> forumPagerItems = ForumPagerItem.getAll();
        cards = new ArrayList<Card>();
        HashMap<String, ForumPagerItem> pagerItemMap = new HashMap<String, ForumPagerItem>();
        for (final ForumPagerItem item : forumPagerItems) {

            //Create a Card
            PagerItemCard card = new PagerItemCard(getActivity(), item);

            //Card must have a stable Id.
            card.setId("" + item.position);
            pagerItemMap.put(card.getId(), item);
            card.setTitle(item.title);

            card.setClickable(true);
            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
//                    Toast.makeText(getActivity(), "Card " + card.getId() + " click "+item.title, Toast.LENGTH_SHORT).show();
                }
            });
            cards.add(card);
        }
        cardDragDropArrayAdapter = new CardDragDropArrayAdapter(getActivity(), cards);
        cardList.setAdapter(cardDragDropArrayAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    @OptionsItem
    void action_save() {
        Crouton.makeText(getActivity(), R.string.feedback_save_arrange_inprogress, Style.CONFIRM).show();
        for (int i = 0; i < cardDragDropArrayAdapter.getCount(); i++) {
            PagerItemCard item = (PagerItemCard) cardDragDropArrayAdapter.getItem(i);
            ForumPagerItem forumPagerItem = item.getForumPagerItem();
            forumPagerItem.position = i;
            forumPagerItem.save();
        }
        app.getEventBus().post(new UpdateForumListEvent());
        Crouton.makeText(getActivity(), R.string.feedback_save_arrange_done, Style.CONFIRM).show();

        doneSave();
    }



    @UiThread
    void doneSave(){

        Intent i = app.getPackageManager()
                .getLaunchIntentForPackage( app.getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        getActivity().onBackPressed();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_forum_rearrange, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    class PagerItemCard extends Card  {

        private ForumPagerItem forumPagerItem;

        public PagerItemCard(Context context, ForumPagerItem forumPagerItem) {
            super(context, R.layout.card_forum_arrange_content);
//            super(context);
            this.forumPagerItem = forumPagerItem;
        }

        public ForumPagerItem getForumPagerItem() {
            return forumPagerItem;
        }

        @Override
        public void setTitle(String title) {
            super.setTitle(title);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);
            final CheckBox enable = (CheckBox) parent.findViewById(R.id.enable);
            TextView title = (TextView) parent.findViewById(R.id.title);
            TextView desc = (TextView) parent.findViewById(R.id.desc);

            title.setText(forumPagerItem.title);
            desc.setText(forumPagerItem.desc);

            enable.setChecked(forumPagerItem.enable);
            enable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("enable", "onClick "+forumPagerItem.title+" "+forumPagerItem.enable);
                    forumPagerItem.enable = !forumPagerItem.enable;
                }
            });

        }


    }
}
