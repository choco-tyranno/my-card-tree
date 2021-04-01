package com.choco_tyranno.mycardtree.card_crud_feature.data.card_data;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.choco_tyranno.mycardtree.BR;

public class CardDTO {
    private ObservableInt mCardNo;

    private ObservableInt mSeqNo;

    private ObservableInt mContainerNo;

    private ObservableInt mParentNo;

    private ObservableInt mType;

    private ObservableField<String> mTitle;

    private ObservableField<String> mSubTitle;

    private ObservableField<String> mContactNumber;

    private ObservableField<String> mFreeNote;

    private ObservableField<String> mImagePath;

    CardDTO(Builder builder) {
        this.mCardNo = new ObservableInt(builder.mCardNo);
        this.mSeqNo = new ObservableInt(builder.mSeqNo);
        this.mContainerNo = new ObservableInt(builder.mContainerNo);
        this.mParentNo = new ObservableInt(builder.mParentNo);
        this.mType = new ObservableInt(builder.mType);
        this.mTitle = new ObservableField<>(builder.mTitle);
        this.mSubTitle = new ObservableField<>(builder.mSubTitle);
        this.mContactNumber = new ObservableField<>(builder.mContactNumber);
        this.mFreeNote = new ObservableField<>(builder.mFreeNote);
        this.mImagePath = new ObservableField<>(builder.mImagePath);
    }

    public static class Builder {

        private int mCardNo = 0;

        private int mSeqNo = 0;

        private int mContainerNo = 0;

        private int mParentNo = 0;

        private int mType = 0;

        private String mTitle = "";

        private String mSubTitle = "";

        private String mContactNumber = "";

        private String mFreeNote = "";

        private String mImagePath = "";

        public Builder() {

        }

        public Builder cardNo(int cardNo) {
            this.mCardNo = cardNo;
            return this;
        }

        public Builder seqNo(int seqNo) {
            this.mSeqNo = seqNo;
            return this;
        }

        public Builder containerNo(int containerNo) {
            this.mContainerNo = containerNo;
            return this;
        }

        public Builder parentNo(int parentNo) {
            this.mParentNo = parentNo;
            return this;
        }

        public Builder type(int type) {
            this.mType = type;
            return this;
        }

        public Builder title(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder subTitle(String subTitle) {
            this.mSubTitle = subTitle;
            return this;
        }

        public Builder contactNumber(String contactNumber) {
            this.mContactNumber = contactNumber;
            return this;
        }

        public Builder freeNote(String freeNote) {
            this.mFreeNote = freeNote;
            return this;
        }

        public Builder imagePath(String imagePath) {
            this.mImagePath = imagePath;
            return this;
        }

        public CardDTO build() {
            return new CardDTO(this);
        }
    }

    public int getCardNo() {
        return mCardNo.get();
    }

    public void setCardNo(int cardNo) {
        this.mCardNo.set(cardNo);
    }

    public int getSeqNo() {
        return mSeqNo.get();
    }

    public void setSeqNo(int seqNo) {
        this.mSeqNo.set(seqNo);
    }

    public int getContainerNo() {
        return mContainerNo.get();
    }

    public void setContainerNo(int containerNo) {
        this.mContainerNo.set(containerNo);
    }

    public int getParentNo() {
        return mParentNo.get();
    }

    public void setParentNo(int parentNo) {
        this.mParentNo.set(parentNo);
    }

    public int getType() {
        return mType.get();
    }

    public void setType(int type) {
        this.mType.set(type);
    }

    public String getTitle() {
        return mTitle.get();
    }

    public void setTitle(String title) {
        this.mTitle.set(title);
    }

    public String getSubTitle() {
        return mSubTitle.get();
    }

    public void setSubTitle(String subTitle) {
        this.mSubTitle.set(subTitle);
    }

    public String getContactNumber() {
        return mContactNumber.get();
    }

    public void setContactNumber(String contactNumber) {
        this.mContactNumber.set(contactNumber);
    }

    public String getFreeNote() {
        return mFreeNote.get();
    }

    public void setFreeNote(String freeNote) {
        this.mFreeNote.set(freeNote);
    }

    public String getImagePath() {
        return mImagePath.get();
    }

    public void setImagePath(String imagePath) {
        this.mImagePath.set(imagePath);
    }
}
