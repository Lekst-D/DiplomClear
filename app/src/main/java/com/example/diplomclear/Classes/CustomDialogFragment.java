package com.example.diplomclear.Classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.annotation.NonNull;

public class CustomDialogFragment extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String text = getArguments().getString("text");

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Оповещение")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(text)
                .setPositiveButton("OK", null)
                .create();
    }
}