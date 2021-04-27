package com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_card")
public class Card implements Comparable<Card> {
    @Ignore
    public static final int CONTACT_CARD_TYPE = 100;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "card_no")
    private int mCardNo;

    @ColumnInfo(name = "seq_no")
    private int mSeqNo;

    @ColumnInfo(name = "container_no")
    private int mContainerNo;

    @ColumnInfo(name = "boss_no")
    private int mBossNo;

    @ColumnInfo(name = "type")
    private int mType;

    @ColumnInfo(name = "title")
    private String mTitle;

    @ColumnInfo(name = "subtitle")
    private String mSubtitle;

    @ColumnInfo(name = "contact_number")
    private String mContactNumber;

    @ColumnInfo(name = "free_note")
    private String mFreeNote;

    @ColumnInfo(name = "image_path")
    private String mImagePath;

    public Card() {
        init();
    }

    @Ignore
    private void init(){
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

    //Use case : dtoTOEntity
    @Ignore
    public Card(Card.Builder builder) {
        this.mCardNo = builder.mCardNo;
        this.mSeqNo = builder.mSeqNo;
        this.mContainerNo = builder.mContainerNo;
        this.mBossNo = builder.mBossNo;
        this.mType = builder.mType;
        this.mTitle = builder.mTitle;
        this.mSubtitle = builder.mSubtitle;
        this.mContactNumber = builder.mContactNumber;
        this.mFreeNote = builder.mFreeNote;
        this.mImagePath = builder.mImagePath;
    }

    //Use case : prepopulate db data
    @Ignore
    public Card(int seqNo, int containerNo, int bossNo, int type) {
        init();
        this.mSeqNo = seqNo;
        this.mContainerNo = containerNo;
        this.mBossNo = bossNo;
        this.mType = type;
    }

    @Ignore
    public CardDTO toDTO() {
        return new CardDTO.Builder().entityToDTO(this).build();
    }

    @Override
    public int compareTo(Card card) {
        if (this.mSeqNo > card.mSeqNo) {
            return 1;
        } else if (this.mSeqNo < card.mSeqNo) {
            return -1;
        } else {
            return 0;
        }
    }

    public int getCardNo() {
        return mCardNo;
    }

    public int getSeqNo() {
        return mSeqNo;
    }

    public int getContainerNo() {
        return mContainerNo;
    }

    public int getBossNo() {
        return mBossNo;
    }

    public int getType() {
        return mType;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public String getContactNumber() {
        return mContactNumber;
    }

    public String getFreeNote() {
        return mFreeNote;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setCardNo(int cardNo) {
        this.mCardNo = cardNo;
    }

    public void setSeqNo(int seqNo) {
        this.mSeqNo = seqNo;
    }

    public void setContainerNo(int containerNo) {
        this.mContainerNo = containerNo;
    }

    public void setBossNo(int bossNo) {
        this.mBossNo = bossNo;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setSubtitle(String subtitle) {
        this.mSubtitle = subtitle;
    }

    public void setContactNumber(String contactNumber) {
        this.mContactNumber = contactNumber;
    }

    public void setFreeNote(String freeNote) {
        this.mFreeNote = freeNote;
    }

    public void setImagePath(String imagePath) {
        this.mImagePath = imagePath;
    }

    //

    public static class Builder {

        private int mCardNo = 0;

        private int mSeqNo = 0;

        private int mContainerNo = 0;

        private int mBossNo = 0;

        private int mType = 0;

        private String mTitle = "";

        private String mSubtitle = "";

        private String mContactNumber = "";

        private String mFreeNote = "";

        private String mImagePath = "";

        public Builder() {
            init();
        }

        private void init(){
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

        public Card.Builder dtoToEntity(CardDTO dto) {
            this.mCardNo = dto.getCardNo();
            this.mSeqNo = dto.getSeqNo();
            this.mContainerNo = dto.getContainerNo();
            this.mBossNo = dto.getBossNo();
            this.mType = dto.getType();
            this.mTitle = dto.getTitle();
            this.mSubtitle = dto.getSubtitle();
            this.mContactNumber = dto.getContactNumber();
            this.mFreeNote = dto.getFreeNote();
            this.mImagePath = dto.getImagePath();
            return this;
        }

        public Card.Builder cardNo(int cardNo) {
            this.mCardNo = cardNo;
            return this;
        }

        public Card.Builder seqNo(int seqNo) {
            this.mSeqNo = seqNo;
            return this;
        }

        public Card.Builder containerNo(int containerNo) {
            this.mContainerNo = containerNo;
            return this;
        }

        public Card.Builder bossNo(int bossNo) {
            this.mBossNo = bossNo;
            return this;
        }

        public Card.Builder type(int type) {
            this.mType = type;
            return this;
        }

        public Card.Builder title(String title) {
            this.mTitle = title;
            return this;
        }

        public Card.Builder subTitle(String subTitle) {
            this.mSubtitle = subTitle;
            return this;
        }

        public Card.Builder contactNumber(String contactNumber) {
            this.mContactNumber = contactNumber;
            return this;
        }

        public Card.Builder freeNote(String freeNote) {
            this.mFreeNote = freeNote;
            return this;
        }

        public Card.Builder imagePath(String imagePath) {
            this.mImagePath = imagePath;
            return this;
        }

        public Card build() {
            return new Card(this);
        }
    }
}
