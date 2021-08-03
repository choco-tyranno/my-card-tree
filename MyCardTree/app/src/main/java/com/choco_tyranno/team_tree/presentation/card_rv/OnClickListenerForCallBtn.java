package com.choco_tyranno.team_tree.presentation.card_rv;

import android.view.View;

import com.choco_tyranno.team_tree.presentation.SingleToastManager;
import com.choco_tyranno.team_tree.presentation.SingleToaster;

public class OnClickListenerForCallBtn implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        SingleToastManager.show(SingleToaster.makeTextShort(v.getContext(), "Call"));
    }
}
