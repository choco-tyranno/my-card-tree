package com.choco_tyranno.mycardtree.card_crud_feature.data.container_data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.choco_tyranno.mycardtree.card_crud_feature.data.bind_data.ContainerWithCards;

import java.util.List;

@Dao
public interface CardContainerDAO {

    @Transaction
    @Query("Select * From table_card_container ORDER BY card_container_no ASC")
    LiveData<List<ContainerWithCards>> getAllContainerLiveData();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertContainer(CardContainer cardContainer);

    @Delete
    void deleteContainer(CardContainer cardContainer);

    @Query("DELETE FROM table_card_container")
    void deleteAllContainers();

}
