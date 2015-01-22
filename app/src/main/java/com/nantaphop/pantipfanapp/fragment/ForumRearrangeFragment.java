package com.nantaphop.pantipfanapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.R;
import com.nantaphop.pantipfanapp.RoomArrangementActivity;
import com.nantaphop.pantipfanapp.event.UpdateForumListEvent;
import com.nantaphop.pantipfanapp.model.ForumPagerItem;
import com.nantaphop.pantipfanapp.utils.DeviceUtils;
import com.nantaphop.pantipfanapp.utils.ShowcaseUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.gmariotti.cardslib.library.extra.dragdroplist.internal.CardDragDropArrayAdapter;
import it.gmariotti.cardslib.library.extra.dragdroplist.view.CardListDragDropView;
import it.gmariotti.cardslib.library.internal.Card;

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
        getAttachedActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        cardList.setScrollSpeed(6f);
        cardList.setAdapter(cardDragDropArrayAdapter);
//        delay1SecBeforeShowcase();
        showcase();
    }

//    @Background
//    void delay1SecBeforeShowcase(){
//        try {
//            Thread.sleep(1000);
//            showcase();
//        } catch (InterruptedException e) {
//        }
//    }

    @UiThread
    void showcase() {



        final View item = cardList.getChildAt(1);





        ShowcaseView svScreen = new ShowcaseView.Builder(getAttachedActivity(), true)
                .setTarget(Target.NONE)
                .hideOnTouchOutside()
                .setContentTitle("จัดการห้อง")
                .singleShot(ShowcaseUtils.REARRANGE_FORUM_ID)
                .setContentText("คุณสามารถจัดการห้องต่างๆ\nได้ในหน้าจอนี้")
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                            Log.d("showcase", "hide");
                        ShowcaseView svShowHide = new ShowcaseView.Builder(getAttachedActivity())
                                .setTarget(new ViewTarget(item.findViewById(R.id.enable)))
                                .setContentTitle("เลือกแสดงเฉพาะห้องที่ต้องการ")
                                .setContentText("คงไม่มีใครอ่าน Pantip ทุกห้อง\nดังนั้นเลือกแสดงเฉพาะห้องที่คุณชอบดีกว่า")
                                .setShowcaseEventListener(new OnShowcaseEventListener() {
                                    @Override
                                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                        ShowcaseView svSort = new ShowcaseView.Builder(getAttachedActivity())
                                                .setTarget(new ViewTarget(((LinearLayout) item).getChildAt(1)))
                                                .setContentTitle("เรียงลำดับห้อง ตามที่คุณชอบ")
                                                .setContentText("กดค้างด้านขวา แล้วลาก\nให้การอ่าน Pantip ของคุณเป็นไปอย่างลื่นไหล\nเรียงลำดับห้องตามที่คุณต้องการ")
                                                .setShowcaseEventListener(new OnShowcaseEventListener() {
                                                    @Override
                                                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                                        ShowcaseView svSave = new ShowcaseView.Builder(getAttachedActivity())
                                                                .setTarget(new PointTarget(DeviceUtils.getDisplayWidth(getAttachedActivity()), 0))
                                                                .setContentTitle("อย่าลืมกดบันทึก")
                                                                .setContentText("เสร็จแล้วก็เริ่มอ่าน Pantip กันได้เลย!")
                                                                .build();
                                                        svSave.setButtonPosition(ShowcaseUtils.getCenterInParentLayoutParam());
                                                    }

                                                    @Override
                                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                                    }

                                                    @Override
                                                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                                    }
                                                })
                                                .build();
                                        svSort.setButtonPosition(ShowcaseUtils.getCenterInParentLayoutParam());
                                    }

                                    @Override
                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                    }

                                    @Override
                                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                    }
                                })
                                .build();
                        svShowHide.setButtonPosition(ShowcaseUtils.getCenterInParentLayoutParam());
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        Log.d("showcase", "did hide");

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
                        Log.d("showcase", "show");
                    }
                })
                .build();


        svScreen.setButtonPosition(ShowcaseUtils.getCenterInParentLayoutParam());


    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @OptionsItem
    void action_save() {
        toastInfo(R.string.feedback_save_arrange_inprogress);
        for (int i = 0; i < cardDragDropArrayAdapter.getCount(); i++) {
            PagerItemCard item = (PagerItemCard) cardDragDropArrayAdapter.getItem(i);
            ForumPagerItem forumPagerItem = item.getForumPagerItem();
            forumPagerItem.position = i;
            forumPagerItem.save();
        }
        app.getEventBus().post(new UpdateForumListEvent());
        toastInfo(R.string.feedback_save_arrange_done);

        doneSave();
    }



    @UiThread
    void doneSave(){

        Intent i = app.getPackageManager()
                .getLaunchIntentForPackage( app.getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        ((RoomArrangementActivity)getActivity()).back();
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
