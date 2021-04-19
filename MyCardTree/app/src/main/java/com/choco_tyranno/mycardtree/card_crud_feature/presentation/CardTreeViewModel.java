package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.Card;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.CardRepository;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.OnDataLoadListener;

import java.util.List;
import java.util.stream.Collectors;

public class CardTreeViewModel extends AndroidViewModel {
    private final String DEBUG_TAG = "!!!:";
    private final CardRepository mCardRepository;
    private MutableLiveData<List<CardDTO>> _dtoCards;

    public CardTreeViewModel(Application application) {
        super(application);
        mCardRepository = new CardRepository(application);
        _dtoCards = new MutableLiveData<List<CardDTO>>();
    }

    public void loadData(OnDataLoadListener callback) {
        mCardRepository.readData(()->{
            setData();
            callback.onLoadData();
        });
    }

    private void setData() {
        List<CardDTO> allDTOs = mCardRepository.getData().stream().map(Card::toDTO).collect(Collectors.toList());
        _dtoCards.postValue(allDTOs);
    }

    public LiveData<List<CardDTO>> getData() {
        return _dtoCards;
    }

    public void addCard(CardDTO newData) {
        List<CardDTO> oldData = _dtoCards.getValue();
        oldData.add(newData);
        _dtoCards.setValue(oldData);
        mCardRepository.insertCard(newData.toEntity());
    }

    public void updateCard(CardDTO modData) {
//      _dtoCards.setValue() -> trigger onChanged
        mCardRepository.updateCard(modData.toEntity());
    }
}