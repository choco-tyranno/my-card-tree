package com.choco_tyranno.team_tree;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.choco_tyranno.team_tree.database_util.MockCard;
import com.choco_tyranno.team_tree.database_util.MockCardFactory;
import com.choco_tyranno.team_tree.domain.card_data.CardEntity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(AndroidJUnit4.class)
public class PopulationTest {
    @Test
    public void makeName() {
        MockCardFactory mockCardFactory = new MockCardFactory();
        List<String> fullNameList = mockCardFactory.createFullNameList();

        //output OK.
        for (String name : fullNameList) {
            Log.d("@@PopulationTest", name);
        }
        Assert.assertEquals(56, fullNameList.size());

//        create cards and relationships.
        /*
         * 총 카드 수 : 56
         * 직책별 수 array : {실장:3, 부장 6, 차장 6, 과장 6, 대리 6, 주임 12, 인턴 17}
         *
         * 실장1명당 직속 부장 2명,
         * 부장1명당 직속 차장 1명,
         * 차장1명당 직속 과장 1명,
         * 과장1명당 직속 대리 1명,
         * 대리1명당 직속 주임 2명,
         * 주임{주임 cardNo가 빠른순서대로 주임 5명은 직속 인턴 2명,
         * 나머지 주임 7명은 직속 인턴 1명}
         * */

        /*
         * containerNo , seqNo, rootNo, cardNo
         * title, type
         * */

        List<MockCard> mockCardList = new MockCardFactory().createCards(fullNameList);
//        mockCardList.sort(Comparator.comparingInt(o -> o.containerNo));
//        for (MockCard mockCard : mockCardList) {
//            Log.d("@@TEST",
//                    "_MockingCard: card[" + mockCard.cardNo +
//                            "] - title:" + mockCard.title +
//                            "/container:" + mockCard.containerNo +
//                            "/seq:" + mockCard.seqNo +
//                            "/root:" + mockCard.rootNo
//            );
//        }
        List<CardEntity> result = mockCardList.stream()
                .flatMap(mockCard-> Stream.of(mockCard.toCardEntity()))
                .collect(Collectors.toList());

        for (CardEntity cardEntity : result) {
            Log.d("@@TEST",
                    "_MockingCard: card[" + cardEntity.getCardNo() +
                            "] - title:" + cardEntity.getTitle() +
                            "/container:" + cardEntity.getContainerNo() +
                            "/seq:" + cardEntity.getSeqNo() +
                            "/root:" + cardEntity.getRootNo()
            );
        }

        Assert.assertEquals(56, result.size());
//        Assert.assertEquals(56, mockCardList.size());
    }


}
