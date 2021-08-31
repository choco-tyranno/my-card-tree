package com.choco_tyranno.team_tree.domain.source;

import android.app.Application;

import com.choco_tyranno.team_tree.domain.card_data.CardDao;
import com.choco_tyranno.team_tree.domain.card_data.CardEntity;

import java.util.List;
import java.util.function.Consumer;

public class CardRepository {
    private static CardRepository instance;
    private final CardDao mCardDao;
    private List<CardEntity> _originData;

    public CardRepository(Application application) {
        instance = this;
        TeamTreeDataBase db = TeamTreeDataBase.getDatabase(application);
        mCardDao = db.cardDao();
        execute(mCardDao::findLastInsertedCard);
    }

    public static CardRepository getInstance() {
        return instance;
    }

    public boolean isDataPrepared() {
        return _originData != null;
    }

    public void readData(Consumer<Integer> callback) {
        execute(() -> {
            int loopCount = 0;
            while (!TeamTreeDataBase.isAssetInserted()) {
                try {
                    Thread.sleep(500);
                    loopCount++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loopCount > 30)
                    break;
            }
            _originData = mCardDao.findAllCards();
            int lastContainerNo = mCardDao.findLastContainerNo();
            callback.accept(lastContainerNo);
        });
    }

    public List<CardEntity> getData() {
        return _originData;
    }

    public void insertAndUpdates(CardEntity cardEntity, List<CardEntity> cardEntityList, Consumer<CardEntity> dropEvent) {
        execute(() -> {
            synchronized (this) {
                for (CardEntity testCard : cardEntityList) {
                    _originData.stream()
                            .filter(cardForFilter -> cardForFilter.getCardNo() == testCard.getCardNo())
                            .forEach(cardForCopy -> cardForCopy.copy(testCard));
                }
                CardEntity foundData = mCardDao.insertAndUpdateTransaction(cardEntity, cardEntityList);
                _originData.add(foundData);
                dropEvent.accept(foundData);
            }
        });
    }

    public void insert(CardEntity cardEntity, Consumer<CardEntity> dropEvent) {
        execute(() -> {
            synchronized (this) {
                CardEntity foundData = mCardDao.insertTransaction(cardEntity);
                _originData.add(foundData);
                dropEvent.accept(foundData);
            }
        });
    }

    public void update(CardEntity cardEntity) {
        execute(() -> {
            final int index = _originData.indexOf(cardEntity);
            _originData.get(index).copy(cardEntity);
            mCardDao.update(cardEntity);
        });
    }

    public void delete(List<CardEntity> deleteCardEntities, Consumer<Integer> deleteEvent) {
        execute(() -> {
            synchronized (this) {
                _originData.removeAll(deleteCardEntities);
                int deleteCount = mCardDao.delete(deleteCardEntities).blockingGet();
                deleteEvent.accept(deleteCount);
            }
        });
    }

    public void deleteAndUpdate(List<CardEntity> deleteCardEntities, List<CardEntity> updateCardEntities, Consumer<Integer> deleteEvent) {
        execute(() -> {
            synchronized (this) {
                _originData.removeAll(deleteCardEntities);
                for (CardEntity testCard : updateCardEntities) {
                    _originData.stream()
                            .filter(cardForFilter -> cardForFilter.getCardNo() == testCard.getCardNo())
                            .forEach(cardForCopy -> cardForCopy.copy(testCard));
                }
                int deleteCount = mCardDao.deleteAndUpdateTransaction(deleteCardEntities, updateCardEntities);
                deleteEvent.accept(deleteCount);
            }
        });
    }

    public void update(List<CardEntity> cardEntitiesToUpdate, Runnable finalAction) {
        execute(() -> {
            synchronized (this) {
                for (CardEntity testCard : cardEntitiesToUpdate) {
                    _originData.stream()
                            .filter(cardForFilter -> cardForFilter.getCardNo() == testCard.getCardNo())
                            .forEach(cardForCopy -> cardForCopy.copy(testCard));
                }
                mCardDao.update(cardEntitiesToUpdate);
                finalAction.run();
            }
        });
    }

    private void execute(Runnable action) {
        TeamTreeDataBase.databaseWriteExecutor.execute(action);
    }

}