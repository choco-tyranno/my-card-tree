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
    public abstract void insert(CardEntity cardEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insert(List<CardEntity> cardEntities);

    @Transaction
    public CardEntity insertTransaction(CardEntity insertData) {
        insert(insertData);
        return findLastInsertedCard();
    }

    @Transaction
    public CardEntity insertAndUpdateTransaction(CardEntity insertData, List<CardEntity> updateData) {
        insert(insertData);
        update(updateData);
        return findLastInsertedCard();
    }

    @Query("select * from table_card")
    public abstract List<CardEntity> findAllCards();

    @Query("select * from table_card order by card_no desc limit 1")
    public abstract CardEntity findLastInsertedCard();

    @Delete
    public abstract Single<Integer> delete(List<CardEntity> cardEntities);

    @Transaction
    public int deleteAndUpdateTransaction(List<CardEntity> deleteData, List<CardEntity> updateData) {
        update(updateData);
        Single<Integer> count = delete(deleteData);
        return count.blockingGet();
    }

    @Update
    public abstract void update(List<CardEntity> cardEntities);

    @Update
    public abstract void update(CardEntity cardEntity);
}
