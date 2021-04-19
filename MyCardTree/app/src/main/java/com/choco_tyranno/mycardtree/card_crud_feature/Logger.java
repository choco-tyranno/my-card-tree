package com.choco_tyranno.mycardtree.card_crud_feature;

import android.util.Log;

import androidx.annotation.NonNull;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.Card;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;

import java.util.List;

public interface Logger {

    public static void message(String msg) {
        Log.d("!!!:", msg);
    }

    public static void nullCheck(@NonNull Object obj, String location) {
        Log.d("!!!:", "[Null_check]" + location + "? : " + (obj == null ? "null" : "nonNull"));
    }

    public static void cardDTOSizeCheck(@NonNull List<CardDTO> list, String location) {
        Log.d("!!!:", "[Size_check]" + location + "? : " + list.size());
    }
    public static void cardSizeCheck(@NonNull List<Card> list, String location) {
        Log.d("!!!:", "[Size_check]" + location + "? : " + list.size());
    }
}
