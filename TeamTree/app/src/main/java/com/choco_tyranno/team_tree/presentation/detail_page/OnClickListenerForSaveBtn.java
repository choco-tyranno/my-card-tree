package com.choco_tyranno.team_tree.presentation.detail_page;

import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.choco_tyranno.team_tree.databinding.ActivityDetailBinding;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;

public class OnClickListenerForSaveBtn implements View.OnClickListener {
    DetailPageViewModel viewModel;

    public OnClickListenerForSaveBtn(DetailPageViewModel viewModel){
        this.viewModel = viewModel;
    }

    @Override
    public void onClick(View v) {
        ActivityDetailBinding binding = ((DetailCardActivity) v.getContext()).getBinding();
        DetailPage pageState = binding.getDetailPage();
        compareAndSave(binding);
        pageState.switchMode();
    }

    private void compareAndSave(ActivityDetailBinding binding) {
        CardDto cardDto = binding.getCard();
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
        if (!TextUtils.equals(cardDto.getTitle(), currentTitle)) {
            cardDto.setTitle(currentTitle);
            modified = true;
        }
        if (!TextUtils.equals(cardDto.getSubtitle(), currentSubtitle)) {
            cardDto.setSubtitle(currentSubtitle);
            modified = true;
        }
        if (!TextUtils.equals(cardDto.getContactNumber(), currentContactNumber)) {
            cardDto.setContactNumber(currentContactNumber);
            modified = true;
        }
        if (!TextUtils.equals(cardDto.getFreeNote(), currentFreeNote)) {
            cardDto.setFreeNote(currentFreeNote);
            modified = true;
        }

        if (modified)
            viewModel.update(cardDto);
    }

}
