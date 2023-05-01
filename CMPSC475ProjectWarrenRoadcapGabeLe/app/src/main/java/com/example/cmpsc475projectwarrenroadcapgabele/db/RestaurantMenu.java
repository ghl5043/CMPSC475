package com.example.cmpsc475projectwarrenroadcapgabele.db;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="restaurant_menu")
public class RestaurantMenu {
    public RestaurantMenu(int id, String item_name, String description, double price){
        this.id = id;
        this.item_name = item_name;
        this.description = description;
        this.price = price;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name ="rowid")
    public int id;

    @ColumnInfo(name = "item_name")
    public String item_name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "price")
    public double price;
}
