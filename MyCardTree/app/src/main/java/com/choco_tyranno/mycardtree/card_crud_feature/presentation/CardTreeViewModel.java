package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.Card;
import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.data.source.CardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CardTreeViewModel extends AndroidViewModel {
    private final String DEBUG_TAG = "!!!:";
    private final CardRepository mCardRepository;


    private Handler mainHandler;
    //    private MutableLiveData<List<CardDTO>> mAllCards;

    private LiveData<List<Card>> entityCards;
    private MutableLiveData<List<CardDTO>> _dtoCards;

    public CardTreeViewModel(Application application) {
        super(application);
        mCardRepository = new CardRepository(application);
        _dtoCards = new MutableLiveData<List<CardDTO>>(new ArrayList<>());
    }

    public void setMainHandler(Handler handler) {
        this.mainHandler = handler;
        postMainHandler(handler);
    }
    public void postMainHandler(Handler handler) {
        mCardRepository.setHandler(handler);
    }

    public void loadData(){
        mCardRepository.readData(this::setData);
    }

    private void setData(){
        List<CardDTO> transformedData = mCardRepository.getData().stream().map(Card::toDTO).collect(Collectors.toList());
        Logger.cardDTOSizeCheck(transformedData,"vm#setData/transformed data");
        _dtoCards.getValue().clear();
        _dtoCards.getValue().addAll(transformedData);

//        entityCards = mCardRepository.getData();
    }
    //    public void loadData() {
//        mCardRepository.readData();
////        mCardRepository.readData(this::observe);

    public LiveData<List<CardDTO>> getData() {
        return _dtoCards;
    }

//    }

    public void getDTO() {
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