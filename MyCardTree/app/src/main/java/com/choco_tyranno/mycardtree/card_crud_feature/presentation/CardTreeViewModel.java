package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.choco_tyranno.mycardtree.card_crud_feature.data.bind_data.ContainerWithCards;
import com.choco_tyranno.mycardtree.card_crud_feature.data.source.CallbackCollector;
import com.choco_tyranno.mycardtree.card_crud_feature.data.source.CardRepository;
import com.choco_tyranno.mycardtree.card_crud_feature.data.source.OnDataLoadListener;

import java.util.List;

public class CardTreeViewModel extends AndroidViewModel {
    private final String DEBUG_TAG = "!!!:";
    private final String DATA_IN = "CARDVIEWMODEL_DATA_IN";
    private CardRepository mCardRepository;
    private LiveData<List<ContainerWithCards>> mAllCards;
    public MutableLiveData<List<ContainerWithCards>> mutableData;
    private OnDataLoadListener onDataLoadListener;

//    public void testTriggerDataChange() {
//        Log.d(DEBUG_TAG, "before value :" + mAllCards.getValue().get(0).getCardContainer().getContainerNo());
//        this.mutableData.getValue().get(0).getCardContainer().setContainerNo(10);
//        Log.d(DEBUG_TAG, "result :" + mAllCards.getValue().get(0).getCardContainer().getContainerNo());
//    }

    public CardTreeViewModel(Application application) {
        super(application);
        mCardRepository = new CardRepository(application, () -> {
            mAllCards = mCardRepository.getData();
            onDataLoadListener.onLoadData();
        });
    }


    public LiveData<List<ContainerWithCards>> getData() {
        return mAllCards != null ? mAllCards : loadData();
    }

    private LiveData<List<ContainerWithCards>> loadData() {
//        return mAllCards = mCardRepository.getAllContainerCards();
    }

    public String hasListObj() {
        return "" + (isNull(mAllCards) ? "null" : "exist");
    }

    public boolean isNull(Object object) {
        return object == null;
    }

    public void postDataLoadListener(OnDataLoadListener onDataLoadListener) {
        this.onDataLoadListener = onDataLoadListener;
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