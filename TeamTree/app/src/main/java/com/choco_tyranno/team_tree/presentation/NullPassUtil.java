package com.choco_tyranno.team_tree.presentation;

import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;


import com.choco_tyranno.team_tree.databinding.ItemCardframeBinding;
import com.choco_tyranno.team_tree.presentation.card_rv.ContactCardViewHolder;

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

    public static ItemCardframeBinding checkItemCardFrameBinding(ItemCardframeBinding itemCardframeBinding){
        boolean  isPresent = Optional.ofNullable(itemCardframeBinding).isPresent();
        if (isPresent)
            return itemCardframeBinding;
        throw new RuntimeException("NullPassUtil#checkFrameLayout not found");
    }
}
