package com.choco_tyranno.mycardtree;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.Card;
import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.CardDAO;
import com.choco_tyranno.mycardtree.card_crud_feature.data.source.MyCardTreeDataBase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DbTest {
    private CardDAO cardDAO;
    private MyCardTreeDataBase testDb;

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
        List<Card> testCards = TestUtil.createCards(5);
        Card testCard = testCards.get(0);
        cardDAO.insertCards(testCards);
        LiveData<List<Card>> cards = cardDAO.findAllCards();
    }
}
