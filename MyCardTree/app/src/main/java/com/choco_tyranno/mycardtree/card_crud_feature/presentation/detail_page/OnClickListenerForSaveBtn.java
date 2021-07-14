package com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page;

import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.DetailCardActivity;
import com.choco_tyranno.mycardtree.databinding.ActivityDetailFrameBinding;
import com.google.android.material.button.MaterialButton;

public class OnClickListenerForSaveBtn implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        ActivityDetailFrameBinding binding = ((DetailCardActivity)v.getContext()).getBinding();
        CardDTO cardDTO = binding.getCard();
        AppCompatEditText titleEt = binding.detailTitleEt;
        AppCompatEditText subtitleEt = binding.detailSubtitleEt;
        AppCompatEditText contactNumberEt = binding.detailContactNumberEt;
        AppCompatEditText freeNoteEt = binding.detailFreeNoteEt;
        boolean modified = isModified(v);
        if (modified){
            cardDTO.setTitle(titleEt.getText().toString());

        }
        //compare
        //save if necessary
        //switchMode.
    }

    private boolean isModified(View v){

    }
}
