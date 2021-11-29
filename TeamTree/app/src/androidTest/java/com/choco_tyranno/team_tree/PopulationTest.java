package com.choco_tyranno.team_tree;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.choco_tyranno.team_tree.domain.card_data.CardEntity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PopulationTest {
    @Test
    public void makeName() {
        List<CardEntity> prepopulateData = new ArrayList<>();
//                직급별
        String[] lastNameArr = {"김", "이", "박", "안", "원", "고", "안"};
        String[] firstNameArr = {"민정", "소영", "민수", "아영", "소정", "민기", "비오", "아린"};
        String[] rankArr = {"실장", "부장", "차장", "과장", "대리", "주임", "인턴"};
        int[] vacantArr = {3, 6, 6, 6, 6, 12, 17};
        List<String> fullNameList = new ArrayList<>();
        String space = " ";
        Arrays.stream(lastNameArr).forEach(
                lastName -> Arrays.stream(firstNameArr).forEach(
                        firstName -> fullNameList.add(lastName + firstName)
                )
        );
        //총 좌석수 : 56
        int 실장수 = 3;
        //53 left
        int 부장수 = 6;
        //47 left
        int 차장수 = 6;
        //41 left
        int 과장수 = 6;
        //35 left
        int 대리수 = 6;
        //29 left
        int 주임수 = 12;
        //17 left
        int 인턴수 = 17;
        // 인턴 5+1명은 마지막 동그룹에 함께.

        int workedCount = 0;
        for (int i = 0; i < vacantArr.length; i++) {
            final int vacantValue = vacantArr[i];
            final String rank = rankArr[i];
            for (int j = 0; j < vacantValue; j++) {
                fullNameList.set(workedCount, fullNameList.get(workedCount) + space + rank);
                workedCount++;
            }
        }
        //output OK.
        for (String name : fullNameList) {
            Log.d("@@PopulationTest", name);
        }
        Assert.assertEquals(56, fullNameList.size());
    }
}
