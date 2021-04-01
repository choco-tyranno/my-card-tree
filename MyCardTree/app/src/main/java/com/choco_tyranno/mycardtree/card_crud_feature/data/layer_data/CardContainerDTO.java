package com.choco_tyranno.mycardtree.card_crud_feature.data.layer_data;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.room.ColumnInfo;


public class CardContainerDTO {

    private ObservableInt mContainerNo;

    private ObservableInt mType;

    public CardContainerDTO() {
    }

    private CardContainerDTO(Builder builder) {
        this.mContainerNo = new ObservableInt(builder.mContainerNo);
        this.mType = new ObservableInt(builder.mType);
    }

    public static class Builder {

        private int mContainerNo;

        private int mType;

        public Builder() {

        }

        public Builder containerNo(int containerNo) {
            this.mContainerNo = containerNo;
            return this;
        }

        public Builder type(int type) {
            this.mType = type;
            return this;
        }

        public CardContainerDTO build() {
            return new CardContainerDTO(this);
        }
    }

    public int getContainerNo() {
        return mContainerNo.get();
    }

    public void setContainerNo(int containerNo) {
        this.mContainerNo.set(containerNo);
    }

    public int getType() {
        return mType.get();
    }

    public void setType(int type) {
        this.mType.set(type);
    }
}
