package com.choco_tyranno.mycardtree.card_crud_feature.data.card_data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_card")
public class Card implements Comparable<Card> {

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

    }

    public Card(String tempTitle) {
        this.mTitle = tempTitle;
        this.mContactNumber = "";
    }

    public Card(int seqNo, int containerNo, int bossNo, int type) {
        this.mSeqNo = seqNo;
        this.mContainerNo = containerNo;
        this.mBossNo = bossNo;
        this.mType = type;
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
}
