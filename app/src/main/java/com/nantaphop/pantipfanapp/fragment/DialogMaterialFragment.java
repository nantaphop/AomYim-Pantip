package com.nantaphop.pantipfanapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.nantaphop.pantipfanapp.R;

import it.gmariotti.changelibs.library.view.ChangeLogListView;

/**
 * Created by nantaphop on 21-Jan-15.
 */
public class DialogMaterialFragment extends DialogFragment {

    public DialogMaterialFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ChangeLogListView chgList=(ChangeLogListView)layoutInflater.inflate(R.layout.changelog_fragment_material, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle("รายการสิ่งที่เปลี่ยนแปลง")
                .setView(chgList)
                .setPositiveButton("ตกลง",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();

    }

}