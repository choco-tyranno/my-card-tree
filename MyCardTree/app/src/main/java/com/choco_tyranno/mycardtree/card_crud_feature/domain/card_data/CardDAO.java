package com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data;


import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.reactivex.Single;

@Dao
public abstract class CardDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertCard(CardEntity cardEntity);

    @Query("select * from table_card")
    public abstract List<CardEntity> findAllCards();

    @Transaction
    public CardEntity insertAndUpdates(CardEntity toInsertData, List<CardEntity> toUpdateData) {
        insertCard(toInsertData);
        updateCards(toUpdateData);
        return findLastInsertedCard();
    }

    @Transaction
    public CardEntity insert(CardEntity toInsertData) {
        insertCard(toInsertData);
        return findLastInsertedCard();
    }

    @Update
    public abstract void updateCards(List<CardEntity> cardEntities);

    @Query("select * from table_card order by card_no desc limit 1")
    public abstract CardEntity findLastInsertedCard();

    @Update
    public abstract void updateCard(CardEntity cardEntity);

    @Delete
    public abstract Single<Integer> deletes(List<CardEntity> cardEntities);
}
