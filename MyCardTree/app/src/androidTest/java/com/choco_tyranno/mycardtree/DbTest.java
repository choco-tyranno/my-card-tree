package com.choco_tyranno.mycardtree;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardEntity;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDAO;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.MyCardTreeDataBase;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardTreeViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DbTest {
    private CardDAO cardDAO;
    private MyCardTreeDataBase testDb;
    private CardTreeViewModel viewModel;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, MyCardTreeDataBase.class).build();
        cardDAO = testDb.cardDAO();
    }

    @After
    public void closeDb() throws IOException {
        testDb.close();
    }

    @Test
    public void writeCardAndReadCardList() throws Exception {
        List<CardEntity> testCardEntities = TestUtil.createCards(5);
        CardEntity testCardEntity = testCardEntities.get(0);
        cardDAO.insertCards(testCardEntities);
//        LiveData<List<CardEntity>> cards = cardDAO.findAllCards();
//        assertThat(cards.getValue().get(0), is(testCardEntity));
    }

}
