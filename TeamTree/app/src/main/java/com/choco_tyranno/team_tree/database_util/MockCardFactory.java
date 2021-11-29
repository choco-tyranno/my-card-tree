package com.choco_tyranno.team_tree.database_util;

import com.choco_tyranno.team_tree.domain.card_data.CardDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MockCardFactory {
    private final List<MockCard> cardList = new ArrayList<>();
    private final List<String> fullNameList = new ArrayList<>();
    private final AtomicInteger createdCardNo = new AtomicInteger(1);
    private final AtomicInteger created인턴Count = new AtomicInteger(0);
    private final String[] lastNameArr = {"김", "이", "박", "안", "원", "고", "안"};
    private final String[] firstNameArr = {"민정", "소영", "민수", "아영", "소정", "민기", "비오", "아린"};
    private final String[] rankArr = {"실장", "부장", "차장", "과장", "대리", "주임", "인턴"};

    public List<MockCard> createCards(final List<String> fullNameList) {
        final int rootContainerItemCount = 3;
        for (int i = 0; i < rootContainerItemCount; i++) {
            cardList.add(new MockCard(cardList, createdCardNo, 0, i, CardDto.NO_ROOT_CARD, fullNameList, rankArr, created인턴Count));
        }
        return cardList;
    }

    public List<String> createFullNameList(){
        Arrays.stream(lastNameArr).forEach(
                lastName -> Arrays.stream(firstNameArr).forEach(
                        firstName -> fullNameList.add(lastName + firstName)
                )
        );
        return fullNameList;
    }
}
