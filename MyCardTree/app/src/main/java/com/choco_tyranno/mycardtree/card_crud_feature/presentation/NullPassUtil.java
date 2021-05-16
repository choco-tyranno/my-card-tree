package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.ContactCardViewHolder;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;

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

    public static ContactCardViewHolder checkContactCardViewHolder(ContactCardViewHolder contactCardViewHolder){
        boolean  isPresent = Optional.ofNullable(contactCardViewHolder).isPresent();
        if (isPresent)
            return contactCardViewHolder;
        throw new RuntimeException("NullPassUtil#checkFrameLayout not found");
    }

    public static ItemCardFrameBinding checkItemCardFrameBinding(ItemCardFrameBinding itemCardFrameBinding){
        boolean  isPresent = Optional.ofNullable(itemCardFrameBinding).isPresent();
        if (isPresent)
            return itemCardFrameBinding;
        throw new RuntimeException("NullPassUtil#checkFrameLayout not found");
    }
}
