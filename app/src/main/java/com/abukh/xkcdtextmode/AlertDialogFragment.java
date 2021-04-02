package com.abukh.xkcdtextmode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;


public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // getActivity gets us the context from the activity that is calling this code
        Context context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(context); // makes an empty dialog box

        // add title and body content text to our dialog
        builder.setTitle("Oops! Sorry").setMessage("There was a network error. Please try again.");

        builder.setPositiveButton("OK", null);

        return  builder.create();
    }
}
