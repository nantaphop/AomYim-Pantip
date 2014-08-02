package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by nantaphop on 02-Aug-14.
 */
public class RecommendCard extends Card{
    private String title;
    private String url;

    public static final int TYPE = 1;

    public RecommendCard(String title, String url, Context context) {
        super(context);
        this.title = title;
        this.url = url;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView title = (TextView) parent.findViewById(android.R.id.text1);
        title.setText(this.title);
    }
}
