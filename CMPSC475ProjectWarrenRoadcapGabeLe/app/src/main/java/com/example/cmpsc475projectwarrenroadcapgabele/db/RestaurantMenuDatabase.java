package com.example.cmpsc475projectwarrenroadcapgabele.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {RestaurantMenu.class}, version = 1, exportSchema = false)
public abstract class RestaurantMenuDatabase extends RoomDatabase{
    public interface MenuListener{
        void onMenuReturned(RestaurantMenu menu);
    }

    public abstract RestaurantMenuDAO menuDAO();

    private static RestaurantMenuDatabase INSTANCE;

    public static RestaurantMenuDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (RestaurantMenuDatabase.class){
                if(INSTANCE == null){

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), RestaurantMenuDatabase.class, "menu_database").addCallback(createMenuDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback createMenuDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
            createMenuTable();
        }
    };

    private static void createMenuTable() {
        for (int i = 0; i< DefaultContent.ITEM_NAME.length; i++){
            insert(new RestaurantMenu(0, DefaultContent.ITEM_NAME[i], DefaultContent.DESCRIPTION[i], DefaultContent.PRICE[i]));
        }
    }


    public static void insert(RestaurantMenu menu){
        (new Thread(()->
                INSTANCE.menuDAO().insert(menu))).start();
    }

    public static void  update(RestaurantMenu menu){
        (new Thread(()->
                INSTANCE.menuDAO().update(menu))).start();
    }

    public static void delete(int menu_id){
        (new Thread(() ->
                INSTANCE.menuDAO().delete(menu_id))).start();
    }

    public static void getMenu(int id, final MenuListener listener){
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage (Message msg){
                super.handleMessage(msg);
                listener.onMenuReturned((RestaurantMenu) msg.obj);
            }
        };
        new Thread(()->{
            Message msg = handler.obtainMessage();
            msg.obj = INSTANCE.menuDAO().getById(id);
            handler.sendMessage(msg);
        }).start();
    }
}
