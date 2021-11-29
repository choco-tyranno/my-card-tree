package com.choco_tyranno.team_tree.database_util;

import com.choco_tyranno.team_tree.domain.card_data.CardEntity;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MockCard {
    int cardNo;
    int seqNo;
    int containerNo;
    int rootNo;
    String title;
    int childCount;

    MockCard(final List<MockCard> cardList, final AtomicInteger preparedCardNo, final int containerNo, final int seqNo,
             final int rootNo, final List<String> fullNameList, String[] rankArr, AtomicInteger created인턴count) {
        this.cardNo = preparedCardNo.get();
        preparedCardNo.set(this.cardNo + 1);
        this.containerNo = containerNo;
        this.seqNo = seqNo;
        this.rootNo = rootNo;
        this.title = fullNameList.get(this.cardNo - 1)+" "+rankArr[containerNo];
        this.childCount = getChildCount(created인턴count);
        for (int i = 0; i < childCount; i++) {
            createChild(cardList, preparedCardNo, containerNo + 1, i, cardNo, fullNameList, rankArr, created인턴count);
        }
        if (containerNo == 6)
            created인턴count.set(created인턴count.get() + 1);
    }

    void createChild(final List<MockCard> cardList, final AtomicInteger cardNo, final int containerNo, final int seqNo,
                     final int rootNo, final List<String> fullNameList, String[] rankArr, AtomicInteger created인턴count) {
        cardList.add(new MockCard(cardList, cardNo, containerNo, seqNo, rootNo, fullNameList, rankArr, created인턴count));
    }

    int getChildCount(AtomicInteger created인턴Count) {
        final int 실장 = 0;
        final int 부장 = 1;
        final int 차장 = 2;
        final int 과장 = 3;
        final int 대리 = 4;
        final int 주임 = 5;
        final int 인턴 = 6;
        switch (containerNo) {
            case 실장:
                return 2;
            case 부장:
                return 1;
            case 차장:
                return 1;
            case 과장:
                return 1;
            case 대리:
                return 2;
            case 주임:
                if (created인턴Count.get() < 10)
                    return 2;
                return 1;
            case 인턴:
            default:
                return 0;
        }
    }

    public CardEntity toCardEntity(){
        return new CardEntity.Builder().cardNo(cardNo).containerNo(containerNo).seqNo(seqNo).rootNo(rootNo)
                .title(title).type(CardEntity.CONTACT_CARD_TYPE).build();
    }
}
