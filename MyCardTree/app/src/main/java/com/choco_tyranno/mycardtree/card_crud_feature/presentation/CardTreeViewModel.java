package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Application;
import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.Card;
import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.data.source.CardRepository;
import com.choco_tyranno.mycardtree.card_crud_feature.data.source.OnDataLoadListener;

import java.util.ArrayList;
import java.util.List;

public class CardTreeViewModel extends AndroidViewModel {
    private final String DEBUG_TAG = "!!!:";
    private final CardRepository mCardRepository;

    //    private LiveData<List<Card>> mAllCards;
    private MutableLiveData<List<CardDTO>> mAllCards;
    public LiveData<List<CardDTO>> allCards;

    public CardTreeViewModel(Application application) {
        super(application);
        mCardRepository = new CardRepository(application);
    }

    public void prepareData(OnDataLoadListener callback) {
        Log.d(DEBUG_TAG,"vm#preparedData");
        mCardRepository.readData(()->{onLoadData(callback);});
    }

    private void onLoadData(OnDataLoadListener callback) {
        Log.d(DEBUG_TAG,"@callback/ vm#onLoadData");
        MutableLiveData<List<Card>> entityList = mCardRepository.getData();
        Log.d(DEBUG_TAG,"vm#onLoadData entityList : "+(entityList==null? "null": "exist size:"+entityList.getValue().size()));

        mAllCards = (MutableLiveData<List<CardDTO>>) Transformations.map(entityList, new Function<List<Card>, List<CardDTO>>() {
            @Override
            public List<CardDTO> apply(List<Card> input) {
                List<CardDTO> result = new ArrayList<>();
                for (Card entity : input) {
                    result.add(CardDTO.entityToDTO(entity));
                }
                return result;
            }
        });

        allCards = (LiveData<List<CardDTO>>) mAllCards;

        callback.onLoadData();
    }

    public LiveData<List<CardDTO>> getData() {
        return allCards;
    }

//    public String hasListObj() {
//        return "" + (isNull(mAllCards) ? "null" : "exist");
//    }

    public boolean isNull(Object object) {
        return object == null;
    }


//    public LiveData<CardVO> getLiveCard(int card_no) {
//        return cardRepository.getLiveCard(card_no);
//    }
//
//    public LiveData<List<LayerWithCardsVo>> getAllCards() {
//        return allLayersWithCardsLiveData;
//    }
//
//    public void deleteCards(List<CardVO> invalidCards) {
//        cardRepository.deleteCards(invalidCards);
//    }
//
//    public void insertLayer(LayerVo layerVo) {
//        cardRepository.insertLayer(layerVo);
//    }
//
//    public void insertCardBody(CardVO cardVO) {
//        cardRepository.insertCard(cardVO);
//    }
//
//    public void updateCardBody(CardVO cardVO) {
//        cardRepository.updateCard(cardVO);
//    }
//
//    public void updateCardSequences(List<CardVO> cardList) {
//        cardRepository.updateCards(cardList);
//    }
}