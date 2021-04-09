package com.choco_tyranno.mycardtree.card_crud_feature.data.source;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.Card;
import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.CardDAO;
import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.CardDTO;

import java.util.List;

public class CardRepository {
    private final String DEBUG_TAG = "!!!:";
    private final CardDAO mCardDAO;
    private LiveData<List<Card>> mCardData;

    public CardRepository(Application application) {
        MyCardTreeDataBase db = MyCardTreeDataBase.getDatabase(application);
        mCardDAO = db.cardDAO();
        Log.d(DEBUG_TAG,"db?"+db.toString());
    }

    public void readData(OnDataLoadListener callback) {
        Log.d(DEBUG_TAG,"repo#readData");
        MyCardTreeDataBase.databaseWriteExecutor.execute(() -> {
            Log.d(DEBUG_TAG,"repo#readData/multi threading -s");
            mCardData = mCardDAO.findAllCards();

            if (mCardData.getValue()==null){
                Handler handler = new Handler(Looper.getMainLooper());
                waitDataLoad(handler, callback, 1);
            }
        });
    }

    public void waitDataLoad(Handler handler, OnDataLoadListener callback, int recursionCount){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(DEBUG_TAG,"repo#readData/multi threading/data in?: data :"+
                        (mCardData==null? "nll": "exst size:"+(mCardData.getValue()==null? "nll": ""+mCardData.getValue().size())));
                if (mCardData.getValue()==null){
                    if (recursionCount<5){
                        waitDataLoad(handler,callback,recursionCount+1 );
                    }else {
                        callback.onLoadData();
                    }
                }else {
                    callback.onLoadData();
                }
            }
        }, 2000);
    }

    //    Callback 으로 호출됨.
    public MutableLiveData<List<Card>> getData() {
        MutableLiveData<List<Card>> transformedValue =  new MutableLiveData<>();
        transformedValue.postValue(mCardData.getValue());
        return transformedValue;
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