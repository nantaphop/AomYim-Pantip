package com.nantaphop.pantipfanapp.view;

import android.content.DialogInterface;
import android.view.View;
import it.gmariotti.cardslib.library.prototypes.CardSection;

/**
 * Created by nantaphop on 02-Aug-14.
 */
public class TopicSectionCard extends CardSection {

    String buttonText;
    private View.OnClickListener onClickListener;

    public TopicSectionCard(int firstPosition, String title, String buttonText, View.OnClickListener onClickListener) {
        super(firstPosition, title);
        this.buttonText = buttonText;
        this.onClickListener = onClickListener;
    }

    public String getButtonText() {
        return buttonText;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }
}
