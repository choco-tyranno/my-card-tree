package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class MySuperToast {
    public static Toast mySuperToast;

    private MySuperToast() {
    }

    public static void showTextShort(Context context, String msg) {
        if (mySuperToast!=null)
            mySuperToast.cancel();
        mySuperToast = Toast.makeText(context, msg,Toast.LENGTH_SHORT);
        mySuperToast.show();
    }
    public static void showTextLong(Context context, String msg) {
        if (mySuperToast!=null)
            mySuperToast.cancel();
        mySuperToast = Toast.makeText(context, msg,Toast.LENGTH_LONG);
        mySuperToast.show();
    }

    public static void cancel(){
        if (mySuperToast!=null)
        mySuperToast.cancel();
    }

}
