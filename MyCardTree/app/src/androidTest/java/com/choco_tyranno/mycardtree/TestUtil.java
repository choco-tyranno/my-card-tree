package com.choco_tyranno.mycardtree;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardEntity;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {
    public static List<CardEntity> createCards(int number) {
        List<CardEntity> testCardEntities = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            testCardEntities.add(new CardEntity(i,1,0, CardEntity.CONTACT_CARD_TYPE));
        }
        return testCardEntities;
    }
}
