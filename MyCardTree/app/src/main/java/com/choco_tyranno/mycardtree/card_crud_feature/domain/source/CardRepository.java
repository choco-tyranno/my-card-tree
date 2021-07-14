package com.choco_tyranno.mycardtree.card_crud_feature.domain.source;

import android.app.Application;
import android.util.Pair;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardEntity;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDAO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardState;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class CardRepository {
    private static CardRepository instance;
    private final String DEBUG_TAG = "!!!:";
    private final CardDAO mCardDAO;
    private List<CardEntity> _originData;
    private static final int REQ_SUCCESS = 100;
    private static final int REQ_FAIL = -100;

    public CardRepository(Application application) {
        instance = this;
        MyCardTreeDataBase db = MyCardTreeDataBase.getDatabase(application);
        mCardDAO = db.cardDAO();
        execute(mCardDAO::findLastInsertedCard);
    }

    public static CardRepository getInstance() {
        return instance;
    }

    public boolean isDataPrepared(){
        return _originData != null;
    }

//    public void readData(OnDataLoadListener callback) {
//        execute(()->{
//            int loopCount = 0;
//            while (!MyCardTreeDataBase.isAssetInserted()) {
//                try {
//                    Thread.sleep(500);
//                    loopCount++;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (loopCount > 30)
//                    break;
//            }
//            _originData = mCardDAO.findAllCards();
//            mCardDAO.findLastContainerNo();
//            callback.onLoadData();
//        });
//    }

    public void readData(Consumer<Integer> callback) {
        execute(() -> {
            int loopCount = 0;
            while (!MyCardTreeDataBase.isAssetInserted()) {
                try {
                    Thread.sleep(500);
                    loopCount++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loopCount > 30)
                    break;
            }
            _originData = mCardDAO.findAllCards();
            int lastContainerNo = mCardDAO.findLastContainerNo();
            callback.accept(lastContainerNo);
        });
    }

    public List<CardEntity> getData() {
        return _originData;
    }

    public void insertAndUpdates(CardEntity cardEntity, List<CardEntity> cardEntityList, Consumer<CardEntity> dropEvent) {
        execute(() -> {
            synchronized (this) {
                CardEntity foundData = mCardDAO.insertAndUpdateTransaction(cardEntity, cardEntityList);
                dropEvent.accept(foundData);
            }
        });
    }

    public void insert(CardEntity cardEntity, Consumer<CardEntity> dropEvent) {
        execute(() -> {
            synchronized (this) {
                CardEntity foundData = mCardDAO.insertTransaction(cardEntity);
                dropEvent.accept(foundData);
            }
        });
    }

    public void update(CardEntity cardEntity) {
        execute(() -> mCardDAO.update(cardEntity));
    }

    public void delete(List<CardEntity> deleteCardEntities, Consumer<Integer> deleteEvent) {
        execute(() -> {
            synchronized (this) {
                int deleteCount = mCardDAO.delete(deleteCardEntities).blockingGet();
                deleteEvent.accept(deleteCount);
            }
        });
    }

    public void deleteAndUpdate(List<CardEntity> deleteCardEntities, List<CardEntity> updateCardEntities, Consumer<Integer> deleteEvent) {
        execute(() -> {
            synchronized (this) {
                int deleteCount = mCardDAO.deleteAndUpdateTransaction(deleteCardEntities, updateCardEntities);
                deleteEvent.accept(deleteCount);
            }
        });
    }

    public void update(List<CardEntity> cardEntitiesToUpdate, Runnable finalAction) {
        execute(() -> {
            synchronized (this) {
                mCardDAO.update(cardEntitiesToUpdate);
                finalAction.run();
            }
        });
    }

    private void execute(Runnable action) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(action);
    }
}