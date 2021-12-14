package com.choco_tyranno.team_tree.ui.detail_page;

import android.view.View;


public class OnClickListenerForModeSwitchBtn implements View.OnClickListener {
    DetailPage pageState;

    public OnClickListenerForModeSwitchBtn(DetailPage pageState){
        this.pageState = pageState;
    }

    @Override
    public void onClick(View v) {
        pageState.switchMode();

    }
}
