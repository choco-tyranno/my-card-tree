package com.choco_tyranno.mycardtree.card_crud_feature.domain.source;

import android.app.Application;
import android.util.Pair;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardEntity;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDAO;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardState;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class CardRepository {
    private final String DEBUG_TAG = "!!!:";
    private final CardDAO mCardDAO;
    private List<CardEntity> _originData;

    public CardRepository(Application application) {
        MyCardTreeDataBase db = MyCardTreeDataBase.getDatabase(application);
        mCardDAO = db.cardDAO();
        MyCardTreeDataBase.databaseWriteExecutor.execute(mCardDAO::findLastInsertedCard);
    }

    public void readData(OnDataLoadListener callback) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
                    int loopCount = 0;
                    while (!MyCardTreeDataBase.isAssetInserted()) {
                        try {
                            Thread.sleep(1000);
                            loopCount++;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loopCount>90)
                            break;
                    }
                    _originData = mCardDAO.findAllCards();
                    callback.onLoadData();
                }
        );
    }

    public List<CardEntity> getData() {
        return _originData;
    }

    public void insertAndUpdates(CardEntity cardEntity, List<CardEntity> cardEntityList, Consumer<CardEntity> dropEvent) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            synchronized (this) {
                CardEntity foundData = mCardDAO.insertAndUpdates(cardEntity, cardEntityList);
                dropEvent.accept(foundData);
            }
        });
    }

    public void insert(CardEntity cardEntity, Consumer<CardEntity> dropEvent) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            synchronized (this) {
                CardEntity foundData = mCardDAO.insert(cardEntity);
                dropEvent.accept(foundData);
            }
        });
    }

    public void update(CardEntity cardEntity) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() ->
                mCardDAO.updateCard(cardEntity)
        );
    }

    public void deletes(List<CardEntity> cardEntities, Consumer<Integer> deleteEvent) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() ->
                deleteEvent.accept(mCardDAO.deletes(cardEntities).blockingGet())
        );
    }
}