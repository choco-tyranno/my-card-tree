package com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CardDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCard(CardEntity cardEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCards(List<CardEntity> cardEntities);

    @Query("select * from table_card")
    List<CardEntity> findAllCards();

    @Query("select * from table_card where container_no = :key_container_no")
    List<CardEntity> findCardsByContainerNo(int key_container_no);

    @Update
    void updateCard(CardEntity cardEntity);

    @Update
    void updateCards(List<CardEntity> cardEntities);

    @Query("DELETE FROM table_card")
    void deleteAllCards();

    @Delete
    void deleteSelectedCards(List<CardEntity> selectedCardEntities);
}
