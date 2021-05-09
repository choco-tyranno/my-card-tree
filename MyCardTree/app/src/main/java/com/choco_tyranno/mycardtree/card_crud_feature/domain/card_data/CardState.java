package com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data;

import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.choco_tyranno.mycardtree.BR;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardTreeViewModel;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBindingImpl;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class CardState {
    public static int FRONT_DISPLAYING = 0;
    public static int BACK_DISPLAYING = 1;
    private boolean isPersistence;
    private final Front front;
    private final Back back;

    public CardState(int initCardVisibility, boolean isPersistence) {
        this.front = new Front(initCardVisibility);
        this.back = new Back(initCardVisibility);
        this.isPersistence = isPersistence;
    }

    public void displayFront() {
        this.front.toVisible();
        this.back.toInvisible();
    }

    public void displayBack() {
        this.back.toVisible();
        this.front.toInvisible();
    }

    public boolean isPersistence(){
        return isPersistence;
    }

    public void toPersistence(){
        this.isPersistence = true;
    }

    public static class Front extends BaseObservable {
        public static final int READ_MODE = 0;
        public static final int EDIT_MODE = 1;
        private int mode;
        private float alpha;
        private float rotationX;
        private int visibility;

        private Front(int initCardVisibility) {
            if (initCardVisibility == CardState.FRONT_DISPLAYING)
                toVisible();
            else
                toInvisible();
            toReadMode();
        }

        private void toVisible() {
            this.alpha = 1.0f;
            this.rotationX = 0f;
            this.visibility = View.VISIBLE;
        }

        private void toInvisible() {
            this.alpha = 0.0f;
            this.rotationX = 0f;
            this.visibility = View.INVISIBLE;
        }

        private void toReadMode() {
            setMode(READ_MODE);
        }

        private void toEditMode() {
            setMode(EDIT_MODE);
        }

        private void setMode(int mode) {
            this.mode = mode;
            notifyPropertyChanged(BR.mode);
        }

        public void onContactNumberEditTextChanged(ItemCardFrameBindingImpl cardFrameBinding){
            AtomicReference<String> editTextValue = new AtomicReference<>();
            Optional.ofNullable(cardFrameBinding.cardFrontLayout.frontCardContactNumberEditText.getText()).ifPresent((text)->editTextValue.set(text.toString()));
            String formatNumber = PhoneNumberUtils.formatNumber(editTextValue.get(), Locale.getDefault().getCountry());
            if(editTextValue.get().equals(formatNumber))
                return;
            cardFrameBinding.cardFrontLayout.frontCardContactNumberEditText.setText(formatNumber);
            cardFrameBinding.cardFrontLayout.frontCardContactNumberEditText.setSelection(formatNumber.length());
        }

        public void onSwitchChanged(CardDTO dto, boolean isOn) {
            if (isOn)
                toEditMode();
            else
                toReadMode();
        }

        public void onSaveButtonClicked(ItemCardFrameBindingImpl cardFrameBinding, CardDTO cardDTO, CardTreeViewModel viewModel) {
            SwitchMaterial switchView = cardFrameBinding.cardFrontLayout.frontCardSwitch;
            AppCompatEditText titleEditText = cardFrameBinding.cardFrontLayout.frontCardTitleEditText;
            AppCompatEditText contactNumberEditText = cardFrameBinding.cardFrontLayout.frontCardContactNumberEditText;

            AtomicReference<String> textOfTitleEditText = new AtomicReference<>("");
            Optional.ofNullable(titleEditText.getText()).ifPresent(text->
                textOfTitleEditText.set(text.toString())
            );

            AtomicReference<String> textOfContactNumberEditText = new AtomicReference<>("");
            Optional.ofNullable(contactNumberEditText.getText()).ifPresent(text->
                textOfContactNumberEditText.set(text.toString())
            );

            boolean isTitleChanged = false;
            boolean isContactNumberChanged = false;

            if (!cardDTO.getTitle().equals(textOfTitleEditText.get())){
                isTitleChanged = true;
                cardDTO.setTitle(textOfTitleEditText.get());
            }

            if (!cardDTO.getContactNumber().equals(textOfContactNumberEditText.get())){
                isContactNumberChanged = true;
                cardDTO.setContactNumber(textOfContactNumberEditText.get());
            }

            if (isTitleChanged||isContactNumberChanged){
                viewModel.updateCard(cardDTO);
                Toast.makeText(cardFrameBinding.getRoot().getContext(), "카드가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                switchView.setChecked(false);
                return;
            }

            Toast.makeText(cardFrameBinding.getRoot().getContext(), "수정된 정보가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        public void onCancelButtonClicked(ItemCardFrameBindingImpl cardFrameBinding, CardDTO cardDTO) {
            SwitchMaterial switchView = cardFrameBinding.cardFrontLayout.frontCardSwitch;
            AppCompatEditText titleEditText = cardFrameBinding.cardFrontLayout.frontCardTitleEditText;
            AppCompatEditText contactNumberEditText = cardFrameBinding.cardFrontLayout.frontCardContactNumberEditText;
            titleEditText.setText(cardDTO.getTitle());
            contactNumberEditText.setText(cardDTO.getContactNumber());
            switchView.setChecked(false);
        }

        //Getter
        public int getVisibility() {
            return visibility;
        }

        public float getAlpha() {
            return alpha;
        }

        public float getRotationX() {
            return rotationX;
        }

        //BR. id create
        @Bindable
        public int getMode() {
            return mode;
        }
    }

    public static class Back {
        private float alpha;
        private float rotationX;
        private int visibility;

        private Back(int initCardVisibility) {
            if (initCardVisibility == CardState.BACK_DISPLAYING)
                toVisible();
            else
                toInvisible();
        }

        public void toVisible() {
            this.alpha = 1.0f;
            this.rotationX = 0f;
            this.visibility = View.VISIBLE;
        }

        public void toInvisible() {
            this.alpha = 0f;
            this.rotationX = 0f;
            this.visibility = View.INVISIBLE;
        }

        public float getAlpha() {
            return alpha;
        }

        public float getRotationX() {
            return rotationX;
        }

        public int getVisibility() {
            return visibility;
        }
    }

    public Front getFront() {
        return front;
    }

    public Back getBack() {
        return back;
    }
}
