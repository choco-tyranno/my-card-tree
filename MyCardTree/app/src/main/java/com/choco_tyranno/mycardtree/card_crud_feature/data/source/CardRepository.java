package com.choco_tyranno.mycardtree.card_crud_feature.data.source;

import android.app.Application;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.Card;
import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.CardDAO;

import java.util.ArrayList;
import java.util.List;

public class CardRepository {
    private final String DEBUG_TAG = "!!!:";
    private final CardDAO mCardDAO;
    //    private LiveData<List<Card>> mCardData;
    private Handler mMainHandler;
    private List<Card> _originData;

    public CardRepository(Application application) {
        MyCardTreeDataBase db = MyCardTreeDataBase.getDatabase(application);
        mCardDAO = db.cardDAO();
    }

    //**
    public void readData(OnDataLoadListener callback) {
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            //DAO#findAllCards doesn't guarantee data. just return LiveData instance and asynchronously data set.

//            Observer<List<Card>> dataListener = new Observer<List<Card>>() {
//                @Override
//                public void onChanged(List<Card> changedData) {
//                    Logger.message("repo#readData/Observer#onChanged");
//                    Logger.sizeCheck(changedData,"repo#readData/Observer#onChanged");
//                    if (!changedData.isEmpty()) {
//                        _originData.getValue().clear();
//                        _originData.getValue().addAll(changedData);
//                    }
//                }
//            };
            synchronized (this){
                _originData = mCardDAO.findAllCards();
//                Logger.sizeCheck(_originData,"after add All");
                callback.onLoadData();
            }

//            List<Card> dataReceiver = new ArrayList<>();
//            dataListener.onChanged(dataReceiver);

//            List<Card> foundData = mCardDAO.findAllCards();
//            dataReceiver.addAll(mCardDAO.findAllCards());
//            Logger.nullCheck(foundData,"repo#readData/dao.findAllCards");
//            Logger.sizeCheck(foundData,"repo#readData/dao.findAllCards");
//            dataReceiver.addAll(mCardDAO.findAllCards());
        });
    }

    public List<Card> getData() {
        return _originData;
    }

    public void setHandler(Handler handler) {
        this.mMainHandler = handler;
    }

//    public MutableLiveData<List<Card>> getData() {
//        MutableLiveData<List<Card>> transformedValue =  new MutableLiveData<>();
//        transformedValue.setValue(mCardData.getValue());
//        return transformedValue;
//    }


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