package com.choco_tyranno.mycardtree.card_crud_feature.data.card_data;


import androidx.lifecycle.LiveData;
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
    void insertCard(Card card);

    @Update
    void updateCard(Card card);

    @Update
    void updateCards(List<Card> cards);

    @Query("select * from table_card where card_no = :post_card_no")
    LiveData<Card> getLiveCard(int post_card_no);

    @Query("DELETE FROM table_card")
    void deleteAllCards();

    @Delete
    void deleteSelectedCards(List<Card> selectedCards);
}
