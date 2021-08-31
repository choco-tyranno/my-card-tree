package com.choco_tyranno.team_tree;


import com.choco_tyranno.team_tree.domain.card_data.CardDto;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ViewModelMethodTest {

    @Test
    public void resetContainerPresentFlags(){
        int headContainerPosition = 1;
        int headCardPosition = 2;
        List<List<CardDto>> allData = new ArrayList<>();
        List<Integer> mPresentFlags = new ArrayList<>();

        final int prevFlagListLastPosition = mPresentFlags.size()-1;
        if (prevFlagListLastPosition == headContainerPosition)
            return;
        if (prevFlagListLastPosition > headContainerPosition) {
            mPresentFlags.subList(headContainerPosition + 1, prevFlagListLastPosition + 1).clear();
        }
        int nextBossFlag = allData.get(headContainerPosition).get(headCardPosition).getCardNo();
        for (int i = headContainerPosition+1; i < allData.size(); i++){
            List<CardDto> testList= allData.get(i);
            boolean hasFound = false;
            for (CardDto dto :testList){
                if (dto.getRootNo()== nextBossFlag&&dto.getSeqNo()==0){
                    mPresentFlags.add(nextBossFlag);
                    nextBossFlag = dto.getCardNo();
                    hasFound =true;
                    break;
                }
            }
            if (!hasFound)
                break;
        }

//        assertThat
    }
}
