package com.choco_tyranno.team_tree.domain.source;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.choco_tyranno.team_tree.domain.card_data.CardDao;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.domain.card_data.CardEntity;
import com.choco_tyranno.team_tree.presentation.card_rv.ContactCardViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Database(entities = {CardEntity.class}, version = 1, exportSchema = false)
public abstract class TeamTreeDataBase extends RoomDatabase {
    public abstract CardDao cardDao();

    private static volatile TeamTreeDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final AtomicBoolean assetInsertState = new AtomicBoolean(false);

    public static TeamTreeDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TeamTreeDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext()
                            , TeamTreeDataBase.class, "team_tree_database")
                            .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                CardDao cardDAO = INSTANCE.cardDao();
                /*
                 * create rootContainerItem.
                 * create nextItem for each rootCard.
                 * */
//                List<CardEntity> prepopulateData = new ArrayList<>();
//                int registeredCardNo = 0;
//                for (int i = 0; i < 10; i++) {
//                    registeredCardNo++;
//                    prepopulateData.add(
//                            new CardEntity.Builder().seqNo(i).containerNo(0).rootNo(0).title("test item["+i+"]")
//                                    .contactNumber("010-0000-0000").type(ContactCardViewHolder.CONTACT_CARD_TYPE).build()
//                    );
//                }
//                final int lastRegisteredCardNo = registeredCardNo;
//                for (int j = 1; j <lastRegisteredCardNo+1; j++){
//                    for (int i = 0; i < 10; i++) {
//                        prepopulateData.add(
//                                new CardEntity.Builder().seqNo(i).containerNo(1).rootNo(j).title("test item[1_"+i+"]")
//                                        .contactNumber("010-0000-0000").type(ContactCardViewHolder.CONTACT_CARD_TYPE).build()
//                        );
//                    }
//                }
//                cardDAO.insert(prepopulateData);
                cardDAO.insert(new CardEntity.Builder().seqNo(0).containerNo(0).rootNo(CardDto.NO_ROOT_CARD).title("내 카드").build());
                TeamTreeDataBase.setAssetInsertState(true);
            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            TeamTreeDataBase.setAssetInsertState(true);
        }
    };

    private static void setAssetInsertState(boolean state) {
        assetInsertState.set(state);
    }

    public static boolean isAssetInserted() {
        return assetInsertState.get();
    }
}

