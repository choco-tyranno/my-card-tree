package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Optional;

public class NullPassUtil {
    public static LinearLayoutManager checkLinearLayoutManager(LinearLayoutManager lm){
        boolean  isPresent = Optional.ofNullable(lm).isPresent();
        if (isPresent)
            return lm;
        throw new RuntimeException("NullPassUtil#checkLinearLayoutManager not found");
    }

    public static FrameLayout checkFrameLayout(FrameLayout frameLayout){
        boolean  isPresent = Optional.ofNullable(frameLayout).isPresent();
        if (isPresent)
            return frameLayout;
        throw new RuntimeException("NullPassUtil#checkFrameLayout not found");
    }
}
