package com.choco_tyranno.mycardtree.card_crud_feature.data.bind_data;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.Card;
import com.choco_tyranno.mycardtree.card_crud_feature.data.container_data.CardContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContainerWithCards {

    @Embedded
    public CardContainer cardContainer;

    @Relation(
            parentColumn = "card_container_no",
            entityColumn = "container_no"
    )

    public List<Card> cardList;

    public CardContainer getCardContainer() {
        return cardContainer;
    }

    public void setCardContainer(CardContainer cardContainer) {
        this.cardContainer = cardContainer;
    }

    public ArrayList<Card> getCardList() {
        Collections.sort(cardList);
        return (ArrayList<Card>) cardList;
    }

    public void setCardList(ArrayList<Card> cardList) {
        this.cardList = cardList;
    }

    public ContainerWithCards(CardContainer cardContainer) {
        this.cardContainer = cardContainer;
        this.cardList = new ArrayList<>();
    }

}


