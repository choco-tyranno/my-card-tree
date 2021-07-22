package com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page;

import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.databinding.ActivityDetailFrameBinding;

public class OnClickListenerForSaveBtn implements View.OnClickListener {
    DetailPageViewModel viewModel;

    public OnClickListenerForSaveBtn(DetailPageViewModel viewModel){
        this.viewModel = viewModel;
    }

    @Override
    public void onClick(View v) {
        ActivityDetailFrameBinding binding = ((DetailCardActivity) v.getContext()).getBinding();
        DetailPage pageState = binding.getDetailPage();
        compareAndSave(binding);
        pageState.switchMode();
    }

    private void compareAndSave(ActivityDetailFrameBinding binding) {
        CardDTO cardDTO = binding.getCard();
        AppCompatEditText titleEt = binding.detailTitleEt;
        AppCompatEditText subtitleEt = binding.detailSubtitleEt;
        AppCompatEditText contactNumberEt = binding.detailContactNumberEt;
        AppCompatEditText freeNoteEt = binding.detailFreeNoteEt;
        String currentTitle;
        String currentSubtitle;
        String currentContactNumber;
        String currentFreeNote;
        if (titleEt.getText() == null)
            currentTitle = "";
        else
            currentTitle = titleEt.getText().toString();

        if (subtitleEt.getText() == null)
            currentSubtitle = "";
        else
            currentSubtitle = subtitleEt.getText().toString();

        if (contactNumberEt.getText() == null)
            currentContactNumber = "";
        else
            currentContactNumber = contactNumberEt.getText().toString();

        if (freeNoteEt.getText() == null)
            currentFreeNote = "";
        else
            currentFreeNote = freeNoteEt.getText().toString();

        boolean modified = false;
        if (!TextUtils.equals(cardDTO.getTitle(), currentTitle)) {
            cardDTO.setTitle(currentTitle);
            modified = true;
        }
        if (!TextUtils.equals(cardDTO.getSubtitle(), currentSubtitle)) {
            cardDTO.setSubtitle(currentSubtitle);
            modified = true;
        }
        if (!TextUtils.equals(cardDTO.getContactNumber(), currentContactNumber)) {
            cardDTO.setContactNumber(currentContactNumber);
            modified = true;
        }
        if (!TextUtils.equals(cardDTO.getFreeNote(), currentFreeNote)) {
            cardDTO.setFreeNote(currentFreeNote);
            modified = true;
        }

        if (modified)
            viewModel.update(cardDTO);
    }

}
