package com.choco_tyranno.mycardtree.card_crud_feature;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardEntity;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;

import java.util.List;

public interface Logger {

    static void message(String msg) {
        Log.d("!!!:", msg);
    }

    static void nullCheck(@Nullable Object obj, String location) {
        Log.d("!!!:", "[Null_check]" + location + "? : " + (obj == null ? "null" : "nonNull"));
    }

    static void cardDTOSizeCheck(@NonNull List<CardDTO> list, String location) {
        Log.d("!!!:", "[Size_check]" + location + "? : " + list.size());
    }
    static void cardSizeCheck(@NonNull List<CardEntity> list, String location) {
        Log.d("!!!:", "[Size_check]" + location + "? : " + list.size());
    }
}
