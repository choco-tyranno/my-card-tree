package com.choco_tyranno.mycardtree.card_crud_feature.domain.source;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardEntity;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDAO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.ContactCardViewHolder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {CardEntity.class}, version = 1, exportSchema = false)
public abstract class MyCardTreeDataBase extends RoomDatabase {
    public abstract CardDAO cardDAO();
    private static volatile MyCardTreeDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MyCardTreeDataBase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (MyCardTreeDataBase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyCardTreeDataBase.class, "my_card_tree_database")
                            .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                CardDAO cardDAO = INSTANCE.cardDAO();
                CardEntity welcomeCardEntity = new CardEntity.Builder().seqNo(0).containerNo(1).bossNo(0).title("초코").contactNumber("010-3899-8450").type(ContactCardViewHolder.CONTACT_CARD_TYPE).build();
                CardEntity welcomeCardEntity2 =new CardEntity.Builder().seqNo(1).containerNo(1).bossNo(0).title("1ee").contactNumber("010-3523-7735").type(ContactCardViewHolder.CONTACT_CARD_TYPE).build();
                CardEntity welcomeCardEntity3 =new CardEntity.Builder().seqNo(0).containerNo(2).bossNo(1).title("찹소").contactNumber("010-0000-0000").type(ContactCardViewHolder.CONTACT_CARD_TYPE).build();
                cardDAO.insertCard(welcomeCardEntity);
                cardDAO.insertCard(welcomeCardEntity2);
                cardDAO.insertCard(welcomeCardEntity3);
            });

        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };
}

