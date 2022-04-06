package com.example.motioncontrolcalibration;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

public class thigh_calibration_xr_dialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog (Bundle SavedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thigh Calibration - xr")
                .setMessage("Right thigh Abduction")
                .setPositiveButton("Begin", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return builder.create();
    }
}
