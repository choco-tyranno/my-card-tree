package com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

public class CardDTO implements Comparable<CardDTO>{
    private ObservableInt mCardNo;

    private ObservableInt mSeqNo;

    private ObservableInt mContainerNo;

    private ObservableInt mBossNo;

    private ObservableInt mType;

    private ObservableField<String> mTitle;

    private ObservableField<String> mSubtitle;

    private ObservableField<String> mContactNumber;

    private ObservableField<String> mFreeNote;

    private ObservableField<String> mImagePath;

    CardDTO() {

    }

    CardDTO(Builder builder) {
        this.mCardNo = new ObservableInt(builder.mCardNo);
        this.mSeqNo = new ObservableInt(builder.mSeqNo);
        this.mContainerNo = new ObservableInt(builder.mContainerNo);
        this.mBossNo = new ObservableInt(builder.mBossNo);
        this.mType = new ObservableInt(builder.mType);
        this.mTitle = new ObservableField<>(builder.mTitle);
        this.mSubtitle = new ObservableField<>(builder.mSubtitle);
        this.mContactNumber = new ObservableField<>(builder.mContactNumber);
        this.mFreeNote = new ObservableField<>(builder.mFreeNote);
        this.mImagePath = new ObservableField<>(builder.mImagePath);
    }

    public CardEntity toEntity() {
        return new CardEntity.Builder().dtoToEntity(this).build();
    }

    public static CardDTO entityToDTO(CardEntity entity) {
        return new CardDTO.Builder().entityToDTO(entity).build();
    }

    @Override
    public int compareTo(CardDTO compareTarget) {
        return Integer.compare(this.getSeqNo(), compareTarget.getSeqNo());
    }

    public static class Builder {
        private int mCardNo;
        private int mSeqNo;
        private int mContainerNo;
        private int mBossNo;
        private int mType;
        private String mTitle;
        private String mSubtitle;
        private String mContactNumber;
        private String mFreeNote;
        private String mImagePath;

        public Builder() {
            init();
        }

        private void init() {
            this.mCardNo = 0;
            this.mSeqNo = 0;
            this.mContainerNo = 0;
            this.mBossNo = 0;
            this.mType = 0;
            this.mTitle = "";
            this.mSubtitle = "";
            this.mContactNumber = "";
            this.mFreeNote = "";
            this.mImagePath = "";
        }

        public Builder entityToDTO(CardEntity entity) {
            this.mCardNo = entity.getCardNo();
            this.mSeqNo = entity.getSeqNo();
            this.mContainerNo = entity.getContainerNo();
            this.mBossNo = entity.getBossNo();
            this.mType = entity.getType();
            this.mTitle = entity.getTitle();
            this.mSubtitle = entity.getSubtitle();
            this.mContactNumber = entity.getContactNumber();
            this.mFreeNote = entity.getFreeNote();
            this.mImagePath = entity.getImagePath();
            return this;
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

        public Builder bossNo(int bossNo) {
            this.mBossNo = bossNo;
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

        public Builder subtitle(String subtitle) {
            this.mSubtitle = subtitle;
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

    public int getBossNo() {
        return mBossNo.get();
    }

    public void setBossNo(int bossNo) {
        this.mBossNo.set(bossNo);
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

    public String getSubtitle() {
        return mSubtitle.get();
    }

    public void setSubtitle(String subTitle) {
        this.mSubtitle.set(subTitle);
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
