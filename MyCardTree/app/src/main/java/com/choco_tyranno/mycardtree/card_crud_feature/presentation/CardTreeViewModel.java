package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardEntity;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.CardRepository;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.OnDataLoadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CardTreeViewModel extends AndroidViewModel {
    private final String DEBUG_TAG = "!!!:";
    private final CardRepository mCardRepository;
    private MutableLiveData<List<List<CardDTO>>> DTOListGroupedByContainerNo;

    public CardTreeViewModel(Application application) {
        super(application);
        mCardRepository = new CardRepository(application);
        DTOListGroupedByContainerNo = new MutableLiveData<>();
    }

    public void loadData(OnDataLoadListener callback) {
        mCardRepository.readData(()->{
            setData();
            callback.onLoadData();
        });
    }

    private void setData() {
        List<CardDTO> allDTOs = mCardRepository.getData().stream().map(CardEntity::toDTO).collect(Collectors.toList());
        DTOListGroupedByContainerNo.postValue(groupDataByContainerNo(allDTOs));
    }

    private List<List<CardDTO>> groupDataByContainerNo(List<CardDTO> data) {
        List<List<CardDTO>> basket = new ArrayList<>();
        Optional.ofNullable(data).ifPresent(dtoList -> {
            for (CardDTO dto : dtoList) {
                int position = dto.getContainerNo() - 1;
                if (position > basket.size() - 1) {
                    basket.add(new ArrayList<>());
                }
                basket.get(position).add(dto);
            }
        });
        return basket;
    }

    public LiveData<List<List<CardDTO>>> getData() {
        return DTOListGroupedByContainerNo;
    }

//    public void addCard(CardDTO newData) {
//        List<CardDTO> oldData = _dtoCards.getValue();
//        oldData.add(newData);
//        _dtoCards.setValue(oldData);
//        mCardRepository.insertCard(newData.toEntity());
//    }

//    public void updateCard(CardDTO modData) {
////      _dtoCards.setValue() -> trigger onChanged
//        mCardRepository.updateCard(modData.toEntity());
//    }

}