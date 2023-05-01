package com.example.cmpsc475projectwarrenroadcapgabele.db;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RestaurantMenuDAO {
    @Query("SELECT * FROM restaurant_menu ORDER BY item_name COLLATE NOCASE, rowid")
    LiveData<List<RestaurantMenu>> getAll();

    @Query("SELECT * FROM restaurant_menu WHERE rowid = :menuId")
    RestaurantMenu getById(int menuId);

    @Insert
    void insert(RestaurantMenu... menu);
    @Update
    void update(RestaurantMenu... menu);
    @Delete
    void delete(RestaurantMenu... menu);

    @Query("DELETE FROM restaurant_menu WHERE rowid = :menuId")
    void delete(int menuId);
}
