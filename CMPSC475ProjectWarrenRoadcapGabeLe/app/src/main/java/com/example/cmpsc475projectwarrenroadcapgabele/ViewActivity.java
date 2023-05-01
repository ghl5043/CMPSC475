package com.example.cmpsc475projectwarrenroadcapgabele;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpsc475projectwarrenroadcapgabele.db.RestaurantMenu;
import com.example.cmpsc475projectwarrenroadcapgabele.db.RestaurantMenuDatabase;
import com.example.cmpsc475projectwarrenroadcapgabele.db.RestaurantMenuViewModel;

import java.text.DecimalFormat;
import java.util.List;

public class ViewActivity extends AppCompatActivity {
    private RestaurantMenuViewModel restaurantMenuViewModel;
    public SharedPreferences sharedPref;
    public class MenuEntityListAdapter extends RecyclerView.Adapter<MenuEntityListAdapter.MenuEntityViewHolder>{
        class MenuEntityViewHolder extends RecyclerView.ViewHolder{
            private final TextView titleView;
            private RestaurantMenu menu;
            private MenuEntityViewHolder(View itemView) {
                super(itemView);
                titleView = itemView.findViewById(R.id.txtTitle);
                itemView.setOnLongClickListener(view -> {
                    // Note that we need a reference to the MainActivity instance
                    Intent intent = new Intent(ViewActivity.this, AddActivity.class);
                    // Note getItemId will return the database identifier
                    intent.putExtra("menu_id", menu.id);
                    // Note that we are calling a method of the MainActivity object
                    startActivity(intent);
                    return true;
                });
                itemView.setOnClickListener(view -> displayDescription(menu.id));
            }
        }
        private List<RestaurantMenu> menus;
        private final LayoutInflater layoutInflater;

        MenuEntityListAdapter(Context context){
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public MenuEntityListAdapter.MenuEntityViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View itemView = layoutInflater.inflate(R.layout.list_item, parent, false);
            return new MenuEntityListAdapter.MenuEntityViewHolder(itemView);
        }


        @Override
        public int getItemCount(){
            if(menus != null){
                return menus.size();
            }
            else return 0;
        }

        @Override
        public void onBindViewHolder(MenuEntityListAdapter.MenuEntityViewHolder holder, int position){
            if(menus != null){
                RestaurantMenu current = menus.get(position);
                holder.menu = current;
                holder.titleView.setText(current.item_name);
            }
            else{
                holder.titleView.setText("...initializing...");
            }
        }
        void setMenus(List<RestaurantMenu> menus){
            this.menus = menus;
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        changeTheme();
        setContentView(R.layout.activity_view);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        RecyclerView recyclerView = findViewById(R.id.lstRestaurantMenus);
        MenuEntityListAdapter adapter = new MenuEntityListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        restaurantMenuViewModel = new ViewModelProvider(this).get(RestaurantMenuViewModel.class);
        restaurantMenuViewModel.filterMenu(true);

        restaurantMenuViewModel.getAllMenus().observe(this,adapter::setMenus);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displayDescription(int id){
        RestaurantMenuDatabase.getMenu(id, menu ->
        {
            Bundle args = new Bundle();
            args.putInt("menu_id", menu.id);
            args.putString("menu_item_name", menu.item_name);
            args.putString("menu_description", menu.description);
            args.putDouble("menu_price", menu.price);
            DisplayDescriptionDialog setupDialog = new DisplayDescriptionDialog();
            setupDialog.setArguments(args);
            setupDialog.show(getSupportFragmentManager(), "setupDialog");
        });
    }


    public static class DisplayDescriptionDialog extends DialogFragment {
        int menu_id;
        private static final DecimalFormat df = new DecimalFormat("0.00");
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            menu_id = getArguments().getInt("menu_id");
            final String item_name = getArguments().getString("menu_item_name");
            final String description = getArguments().getString("menu_description");
            final Double price = getArguments().getDouble("menu_price");
            builder.setTitle(item_name)
                    .setMessage(description + '\n' + "$" + df.format(price));
            return builder.create();
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
        }
    }
    public void openSettings(MenuItem item){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    public void changeTheme(){
        boolean preferenceValue = sharedPref.getBoolean("dark_theme_checkbox", false);
        if(preferenceValue){
            setTheme(R.style.AppTheme_Dark);
        }
        else{
            setTheme(R.style.AppTheme);
        }
    }
    public void startAddActivity(MenuItem item){
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

}
