package com.choco_tyranno.team_tree.presentation.card_rv;

import android.telephony.PhoneNumberUtils;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.choco_tyranno.team_tree.BR;
import com.choco_tyranno.team_tree.databinding.ItemCardFrameBinding;
import com.choco_tyranno.team_tree.databinding.ItemCardFrameBindingImpl;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.SingleToastManager;
import com.choco_tyranno.team_tree.presentation.SingleToaster;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CardState extends BaseObservable{
    public static int FRONT_DISPLAYING = 0;
    public static int BACK_DISPLAYING = 1;
    private final Front front;
    private final Back back;
    private int removeBtnVisibility;

    public CardState(){
        this.front = new Front(FRONT_DISPLAYING);
        this.back = new Back(FRONT_DISPLAYING);
        this.removeBtnVisibility = View.INVISIBLE;
    }

    public CardState(int initCardVisibility) {
        this.front = new Front(initCardVisibility);
        this.back = new Back(initCardVisibility);
        this.removeBtnVisibility = View.INVISIBLE;
    }

    public boolean isFlipped(){
        return getBack().visibility == View.VISIBLE;
    }

    public void displayFront() {
        this.front.toVisible();
        this.back.toInvisible();
    }

    public void displayBack() {
        this.back.toVisible();
        this.front.toInvisible();
    }

    public void editMode(View view){
        view.bringToFront();
    }

    public static class Builder{
        private final CardState instance;

        public Builder(){
            instance = new CardState();
        }

        public Builder removeBtnVisibility(int removeBtnVisibility){
            instance.setRemoveBtnVisibility(removeBtnVisibility);
            return Builder.this;
        }

        public CardState build(){
            return instance;
        }
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
            setCardFrontAlpha(1.0f);
            setCardFrontRotationX(0f);
            setCardFrontVisibility(View.VISIBLE);
//            this.alpha = 1.0f;
//            this.rotationX = 0f;
//            this.visibility = View.VISIBLE;
        }

        private void toInvisible() {
            setCardFrontAlpha(0.0f);
            setCardFrontRotationX(0f);
            setCardFrontVisibility(View.INVISIBLE);
//            this.alpha = 0.0f;
//            this.rotationX = 0f;
//            this.visibility = View.INVISIBLE;
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

        public void onSwitchChanged(CardDto dto, boolean isOn) {
            if (isOn)
                toEditMode();
            else
                toReadMode();
        }

        public void onSaveButtonClicked(ItemCardFrameBindingImpl cardFrameBinding, CardDto cardDTO, CardViewModel viewModel) {
            SingleToastManager.show(SingleToaster.makeTextShort(cardFrameBinding.getRoot().getContext(), "onSaveBtnClicked! cardNo:"
                    +cardDTO.getCardNo()+"/title:"+cardDTO.getTitle()));

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
                SingleToastManager.show(SingleToaster.makeTextShort(cardFrameBinding.getRoot().getContext(), "카드가 수정되었습니다."));
                switchView.setChecked(false);
                return;
            }

            SingleToastManager.show(SingleToaster.makeTextShort(cardFrameBinding.getRoot().getContext(), "수정된 정보가 없습니다."));
        }

        public void onCancelButtonClicked(ItemCardFrameBindingImpl cardFrameBinding, CardDto cardDTO) {
            SwitchMaterial switchView = cardFrameBinding.cardFrontLayout.frontCardSwitch;
            AppCompatEditText titleEditText = cardFrameBinding.cardFrontLayout.frontCardTitleEditText;
            AppCompatEditText contactNumberEditText = cardFrameBinding.cardFrontLayout.frontCardContactNumberEditText;
            titleEditText.setText(cardDTO.getTitle());
            contactNumberEditText.setText(cardDTO.getContactNumber());
            switchView.setChecked(false);
        }

        //Getter
        @Bindable
        public int getCardFrontVisibility() {
            return visibility;
        }

        @Bindable
        public float getCardFrontAlpha() {
            return alpha;
        }

        @Bindable
        public float getCardFrontRotationX() {
            return rotationX;
        }

        public void setCardFrontVisibility(int visibility){
            this.visibility = visibility;
            notifyPropertyChanged(BR.cardFrontVisibility);
        }

        public void setCardFrontAlpha(float alpha){
            this.alpha = alpha;
            notifyPropertyChanged(BR.cardFrontAlpha);
        }

        public void setCardFrontRotationX(float rotationX){
            this.rotationX =rotationX;
            notifyPropertyChanged(BR.cardFrontRotationX);
        }

        @Bindable
        public int getMode() {
            return mode;
        }
    }

    public static class Back extends BaseObservable{
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
            setAlpha(1.0f);
            setRotationX(0f);
            setVisibility(View.VISIBLE);
        }

        public void toInvisible() {
            setAlpha(0f);
            setRotationX(0f);
            setVisibility(View.INVISIBLE);
        }

        @Bindable
        public float getCardBackAlpha() {
            return alpha;
        }

        @Bindable
        public float getCardBackRotationX() {
            return rotationX;
        }

        @Bindable
        public int getCardBackVisibility() {
            return visibility;
        }

        public void setAlpha(float alpha){
            this.alpha = alpha;
            notifyPropertyChanged(BR.cardBackAlpha);
        }

        public void setRotationX(float rotationX){
            this.rotationX = rotationX;
            notifyPropertyChanged(BR.cardBackRotationX);
        }
        public void setVisibility(int visibility){
            this.visibility = visibility;
            notifyPropertyChanged(BR.cardBackVisibility);
        }
    }

    public Front getFront() {
        return front;
    }

    public Back getBack() {
        return back;
    }

    @Bindable
    public int getRemoveBtnVisibility(){
        return this.removeBtnVisibility;
    }

    public void setRemoveBtnVisibility(int visibility){
        if (visibility!=View.INVISIBLE&&visibility!=View.VISIBLE&&visibility!=View.GONE)
            throw new RuntimeException("CardState#setRemoveBtnVisibility/ has incompatible visibility value");
        this.removeBtnVisibility = visibility;
        notifyPropertyChanged(BR.removeBtnVisibility);
    }
}
