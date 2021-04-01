package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.choco_tyranno.mycardtree.card_crud_feature.data.bind_data.ContainerWithCards;
import com.choco_tyranno.mycardtree.card_crud_feature.data.source.CardRepository;

import java.util.List;

public class CardViewModel extends AndroidViewModel {
    private CardRepository cardRepository;
    private LiveData<List<ContainerWithCards>> allContainerCards;


    public CardViewModel(Application application) {
        super(application);
        cardRepository = new CardRepository(application);
        allContainerCards = cardRepository.getAllContainerCards();
    }

    public LiveData<List<ContainerWithCards>> getData(){
        return allContainerCards;
    };

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