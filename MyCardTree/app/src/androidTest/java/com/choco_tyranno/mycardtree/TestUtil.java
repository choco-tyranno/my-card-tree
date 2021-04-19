package com.choco_tyranno.mycardtree;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.Card;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {
    public static List<Card> createCards(int number) {
        List<Card> testCards = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            testCards.add(new Card(i,1,0,Card.CONTACT_CARD_TYPE));
        }
        return testCards;
    }
}
