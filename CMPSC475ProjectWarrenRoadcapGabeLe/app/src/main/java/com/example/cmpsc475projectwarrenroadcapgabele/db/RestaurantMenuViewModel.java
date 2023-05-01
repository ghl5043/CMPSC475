package com.example.cmpsc475projectwarrenroadcapgabele.db;
import android.app.Application;
import java.util.List;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class RestaurantMenuViewModel extends AndroidViewModel{
    private LiveData<List<RestaurantMenu>> menus;
    public RestaurantMenuViewModel(Application application){
        super(application);
    }

    public void filterMenu(boolean filtered){
        menus = RestaurantMenuDatabase.getDatabase(getApplication()).menuDAO().getAll();
    }

    public LiveData<List<RestaurantMenu>> getAllMenus(){
        return menus;
    }
}
