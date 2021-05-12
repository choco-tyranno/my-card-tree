package com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class CardDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertCard(CardEntity cardEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertCards(List<CardEntity> cardEntities);

    @Query("select * from table_card")
    public abstract List<CardEntity> findAllCards();

    @Transaction
    public void insertAndUpdateTransaction(CardEntity toInsertData, List<CardEntity> toUpdateData){
        updateCards(toUpdateData);
        insertCard(toInsertData);
    }

//    @Query("select * from table_card where container_no = :key_container_no")
//    List<CardEntity> findCardsByContainerNo(int key_container_no);

    @Update
    public abstract void updateCard(CardEntity cardEntity);

    @Update
    public abstract void updateCards(List<CardEntity> cardEntities);

    @Query("DELETE FROM table_card")
    public abstract void deleteAllCards();

    @Delete
    public abstract void deleteSelectedCards(List<CardEntity> selectedCardEntities);
}
