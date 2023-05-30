package com.example.diplomclear;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DialogCalender extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.info_save, null, false);

//        String text = getArguments().getString("text");

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setView(myLayout)
                .setPositiveButton("OK", null)
                .create();
    }
}