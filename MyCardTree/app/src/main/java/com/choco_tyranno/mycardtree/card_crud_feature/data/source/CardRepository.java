package com.choco_tyranno.mycardtree.card_crud_feature.data.source;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.choco_tyranno.mycardtree.card_crud_feature.data.bind_data.ContainerWithCards;
import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.Card;
import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.CardDAO;
import com.choco_tyranno.mycardtree.card_crud_feature.data.container_data.CardContainer;
import com.choco_tyranno.mycardtree.card_crud_feature.data.container_data.CardContainerDAO;
import com.choco_tyranno.mycardtree.card_crud_feature.utils.WorkerThreadManager;

import java.util.List;

public class CardRepository {
    private CardDAO cardDAO;
    private CardContainerDAO cardContainerDAO;
    private LiveData<List<ContainerWithCards>> allContainerCards;

    public CardRepository(Application application, OnDataLoadListener dataLoadCallback) {
        MyCardTreeDataBase db = MyCardTreeDataBase.getDatabase(application);
        cardDAO = db.cardDAO();
        cardContainerDAO = db.cardContainerDAO();
        WorkerThreadManager.instance.execute(new Runnable() {
            @Override
            public void run() {
                readAllData(dataLoadCallback);
            }
        });
    }

    private void readAllData(OnDataLoadListener callback) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            allContainerCards = cardContainerDAO.getAllContainerLiveData();
            callback.onLoadData();
        });
    }

    public LiveData<List<ContainerWithCards>> getData() {
        return allContainerCards;
    }

    public LiveData<Card> getLiveCard(int card_no) {
        return cardDAO.getLiveCard(card_no);
    }

    public void insertLayer(CardContainer cardContainer) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            cardContainerDAO.insertContainer(cardContainer);
        });
    }

    public void insertCard(Card card) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            cardDAO.insertCard(card);
        });
    }

    public void updateCard(Card card) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            cardDAO.updateCard(card);
        });
    }

    public void updateCards(List<Card> cardList) {
        for (Card card : cardList) {
            card.setSeqNo(cardList.indexOf(card));
        }

        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            cardDAO.updateCards(cardList);
        });
    }

    public void deleteCards(List<Card> invalidCards) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            cardDAO.deleteSelectedCards(invalidCards);
        });
    }
}