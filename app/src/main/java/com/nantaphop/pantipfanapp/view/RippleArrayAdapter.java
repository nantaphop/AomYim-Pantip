package com.nantaphop.pantipfanapp.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.nantaphop.pantipfanapp.R;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.styles.ListStyle;
import com.r0adkll.postoffice.ui.Mail;

import java.util.List;

/**
 * Created by nantaphop on 18-Oct-14.
 */
public class RippleArrayAdapter extends ArrayAdapter{
    private ListStyle.OnItemAcceptedListener<CharSequence> listener;
    Delivery delivery;

    public RippleArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public RippleArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public RippleArrayAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
    }

    public RippleArrayAdapter(Context context, int resource, int textViewResourceId, Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public RippleArrayAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    public RippleArrayAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public RippleArrayAdapter(Context ctx, int i, CharSequence[] contents, ListStyle.OnItemAcceptedListener<CharSequence> listener) {
        super(ctx, i, contents);
        this.listener = listener;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        RippleDrawable.createRipple(view, getContext().getResources().getColor(R.color.base_color_bright));
        view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        listener.onItemAccepted("", position);
                                        if(delivery != null){
                                            delivery.dismiss();
                                        }
                                    }
                                });
        return view;
    }
}
