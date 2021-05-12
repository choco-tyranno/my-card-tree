package com.choco_tyranno.mycardtree.card_crud_feature.domain.source;

import android.app.Application;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardEntity;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDAO;

import java.util.List;

public class CardRepository {
    private final String DEBUG_TAG = "!!!:";
    private final CardDAO mCardDAO;
    private List<CardEntity> _originData;

    public CardRepository(Application application) {
        MyCardTreeDataBase db = MyCardTreeDataBase.getDatabase(application);
        mCardDAO = db.cardDAO();
    }

    public void readData(OnDataLoadListener callback) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            synchronized (this) {
                _originData = mCardDAO.findAllCards();
                callback.onLoadData();
            }
        });
    }

    public List<CardEntity> getData() {
        return _originData;
    }

    public void insertCard(CardEntity cardEntity) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> mCardDAO.insertCard(cardEntity));
    }

    public void insertCardAndUpdateCardsSeq(CardEntity cardEntity, List<CardEntity> cardEntityList){
        MyCardTreeDataBase.databaseWriteExecutor.execute(()-> mCardDAO.insertAndUpdateTransaction(cardEntity, cardEntityList));
    }

    public void updateCard(CardEntity cardEntity) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() ->
                mCardDAO.updateCard(cardEntity)
        );
    }

    public void deleteCards(List<CardEntity> invalidCardEntities) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() ->
            mCardDAO.deleteSelectedCards(invalidCardEntities)
        );
    }
}