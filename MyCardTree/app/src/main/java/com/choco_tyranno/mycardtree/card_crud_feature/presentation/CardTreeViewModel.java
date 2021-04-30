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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CardTreeViewModel extends AndroidViewModel {
    private final String DEBUG_TAG = "!!!:";
    private final CardRepository mCardRepository;
    private final MutableLiveData<List<List<CardDTO>>> mListLiveDataByContainer;
    private final List<List<CardDTO>> allData;
//    private final List<List<CardDTO>> mPresentDataOfCardAdapters;

    private final List<Integer> mPresentFlags;
    private final List<List<CardDTO>> mPresentData;

    public CardTreeViewModel(Application application) {
        super(application);
        mCardRepository = new CardRepository(application);
        mListLiveDataByContainer = new MutableLiveData<>();

//        mPresentDataOfCardAdapters = new ArrayList<>();
        mPresentFlags = new ArrayList<>();
        allData = new ArrayList<>();

        mPresentData = new ArrayList<>();
    }

    public void loadData(OnDataLoadListener callback) {
        mCardRepository.readData(() -> {
            setData();
            callback.onLoadData();
        });
    }

    private void setData() {
        List<CardDTO> allDTOs = mCardRepository.getData().stream().map(CardEntity::toDTO).collect(Collectors.toList());
        List<List<CardDTO>> groupedData = groupDataByContainerNo(allDTOs);
        initContainerPresentFlags(groupedData);
        List<List<CardDTO>> presentData = collectPresentData(groupedData);
        mPresentData.clear();
        mPresentData.addAll(presentData);
        // for Search func
        mListLiveDataByContainer.postValue(groupedData);
        // TODO : check [following 2 line] is it redundant?
        allData.clear();
        allData.addAll(groupedData);
    }

    private void initContainerPresentFlags(List<List<CardDTO>> data) {
        int nextGroupFlag = -1;

        for (int i = 0; i < data.size(); i++) {
            List<CardDTO> dtoList = data.get(i);
            boolean hasNext = false;
            int foundFlag = -1;

            if (data.get(i).isEmpty()) {
                return;
            }

            if (i == 0) {
                foundFlag = dtoList.get(0).getBossNo();
                nextGroupFlag = dtoList.get(0).getCardNo();
                hasNext = true;
            } else {
                for (CardDTO dto : dtoList) {
                    if (dto.getBossNo() == nextGroupFlag) {
                        foundFlag = dto.getBossNo();
                        nextGroupFlag = dto.getCardNo();
                        hasNext = true;
                        break;
                    }
                }
            }

            mPresentFlags.add(i, foundFlag);

            if (!hasNext)
                break;
        }
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

    // TODO : prework, collect mPresentFlags required.
    private List<List<CardDTO>> collectPresentData(List<List<CardDTO>> disorderedData) {
        List<List<CardDTO>> collectBasket = new ArrayList<>();
        boolean start = true;
        boolean hasNext = false;
        for (List<CardDTO> data : disorderedData) {
            if (hasNext||start) {
                hasNext = findPresentData(collectBasket, data, mPresentFlags.get(disorderedData.indexOf(data)));
                start = false;
            } else
                break;
        }
        return collectBasket;
    }

    private boolean findPresentData(List<List<CardDTO>> basket, List<CardDTO> disorderedData, int orderFlag) {
        List<CardDTO> smallBasket = new ArrayList<>();
        for (CardDTO dto : disorderedData){
            if(dto.getBossNo()==orderFlag){
                smallBasket.add(dto);
            }
        }
        if (!smallBasket.isEmpty()){
            Collections.sort(smallBasket);
            basket.add(smallBasket);
        }
        return !smallBasket.isEmpty();
    }

    public LiveData<List<List<CardDTO>>> getAllLiveData() {
        return mListLiveDataByContainer;
    }

    public void getPresentDataBy() {

    }

    /* Container Level */
    public int presentContainerCount() {
        return mPresentData.size();
    }

    /* Card Level */
    public int getPresentCardCount(int containerPosition){
        if (containerPosition!=-1)
        return mPresentData.get(containerPosition).size();
        return 0;
    }

    public CardDTO getCardDTO(int containerPosition, int cardPosition){
        return mPresentData.get(containerPosition).get(cardPosition);
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