package com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data;

import android.view.View;

public class CardState {
    public static int FRONT_DISPLAYING = 0;
    public static int BACK_DISPLAYING = 1;
    public Front front;
    public Back back;

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

    public static class Front {
        public static final int READ_MODE = 0;
        public static final int EDIT_MODE = 1;
        public int mode;
        public float alpha;
        public float rotationX;
        public int visibility;

        public Front(int initCardVisibility) {
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
            this.mode = READ_MODE;
        }

        private void toEditMode(){
            this.mode = EDIT_MODE;
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

        public int getMode() {
            return mode;
        }
    }

    public static class Back {
        public float alpha;
        public float rotationX;
        public int visibility;

        public Back(int initCardVisibility) {
            if (initCardVisibility == CardState.BACK_DISPLAYING)
                toVisible();
            else
                toInvisible();
        }

        private void toVisible(){
            this.alpha = 1.0f;
            this.rotationX = 0f;
            this.visibility = View.VISIBLE;
        }
        private void toInvisible(){
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
