package com.choco_tyranno.mycardtree.card_crud_feature.data.layer_data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_card_container")
public class CardContainer {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "card_container_no")
    private int mContainerNo;

    @ColumnInfo(name = "type")
    private int mType;

    public CardContainer(){}

    public CardContainer(int containerNo, int type){
        this.mContainerNo = containerNo;
        this.mType = type;
    }

    public int getContainerNo() {
        return mContainerNo;
    }

    public int getType() {
        return mType;
    }

    public void setContainerNo(int containerNo) {
        this.mContainerNo = containerNo;
    }

    public void setType(int type) {
        this.mType = type;
    }
}
