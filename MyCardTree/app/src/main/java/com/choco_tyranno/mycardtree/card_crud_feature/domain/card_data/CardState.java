package com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data;

import android.view.View;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingMethod;
import androidx.databinding.ObservableInt;

import com.choco_tyranno.mycardtree.BR;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.generated.callback.OnCheckedChangeListener;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class CardState {
    public static int FRONT_DISPLAYING = 0;
    public static int BACK_DISPLAYING = 1;
    private Front front;
    private Back back;

    public CardState(int initCardVisibility) {
        this.front = new Front(initCardVisibility);
        this.back = new Back(initCardVisibility);
    }

    public void displayFront(){
        this.front.toVisible();
        this.back.toInvisible();
    }

    public void displayBack(){
        this.back.toVisible();
        this.front.toInvisible();
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

        private void toVisible(){
            this.alpha = 1.0f;
            this.rotationX = 0f;
            this.visibility = View.VISIBLE;
        }

        private void toInvisible(){
            this.alpha = 0.0f;
            this.rotationX = 0f;
            this.visibility = View.INVISIBLE;
        }

        private void toReadMode(){
            setMode(READ_MODE);
        }

        private void toEditMode(){
            setMode(EDIT_MODE);
        }

        private void setMode(int mode){
            this.mode = mode;
            notifyPropertyChanged(BR.mode);
        }

        public int getVisibility() {
            return visibility;
        }

        public float getAlpha() {
            return alpha;
        }

        public float getRotationX() {
            return rotationX;
        }

        @Bindable
        public int getMode() {
            return mode;
        }

        public void onSwitchChanged(CardDTO dto, boolean isOn){
            if (isOn)
                toEditMode();
            else
                toReadMode();
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

        public void toVisible(){
            this.alpha = 1.0f;
            this.rotationX = 0f;
            this.visibility = View.VISIBLE;
        }
        public void toInvisible(){
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
