package com.laodev.dwbrouter.util;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.laodev.dwbrouter.R;

public class BannerUtil {

    public static void onShowSuccessAlertEvent(View view, String alert, int milliseconds) {
        Snackbar.make(view, alert,milliseconds)
                .setBackgroundTint(view.getContext().getResources().getColor(R.color.colorMainGreen))
                .setActionTextColor(view.getContext().getResources().getColor(R.color.colorMainWhite)).show();
    }

    public static void onShowErrorAlertEvent(View view, String alert, int milliseconds) {
        Snackbar.make(view, alert,milliseconds)
                .setBackgroundTint(view.getContext().getResources().getColor(R.color.colorRoosterRed))
                .setActionTextColor(view.getContext().getResources().getColor(R.color.colorMainWhite)).show();
    }

    public static void onShowWaringAlertEvent(View view, String alert, int milliseconds) {
        Snackbar.make(view, alert,milliseconds)
                .setBackgroundTint(view.getContext().getResources().getColor(R.color.colorRoosterYellow))
                .setActionTextColor(view.getContext().getResources().getColor(R.color.colorMainWhite)).show();
    }

    public static void onShowProcessingAlertEvent(View view, String alert, int milliseconds) {
        Snackbar.make(view, alert,milliseconds)
                .setBackgroundTint(view.getContext().getResources().getColor(R.color.colorRoosterBlue))
                .setActionTextColor(view.getContext().getResources().getColor(R.color.colorMainWhite)).show();
    }


}
