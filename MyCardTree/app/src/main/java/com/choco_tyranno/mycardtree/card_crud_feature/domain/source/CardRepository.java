package com.choco_tyranno.mycardtree.card_crud_feature.domain.source;

import android.app.Application;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.Card;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDAO;

import java.util.List;

public class CardRepository {
    private final String DEBUG_TAG = "!!!:";
    private final CardDAO mCardDAO;
    private List<Card> _originData;

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

    public List<Card> getData() {
        return _originData;
    }

    public void insertCard(Card card) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            mCardDAO.insertCard(card);
        });
    }

    public void updateCard(Card card) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            mCardDAO.updateCard(card);
        });
    }

    //refactoring needed
    public void updateCards(List<Card> cardList) {
        for (Card card : cardList) {
            card.setSeqNo(cardList.indexOf(card));
        }

        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            mCardDAO.updateCards(cardList);
        });
    }

    public void deleteCards(List<Card> invalidCards) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            mCardDAO.deleteSelectedCards(invalidCards);
        });
    }
}