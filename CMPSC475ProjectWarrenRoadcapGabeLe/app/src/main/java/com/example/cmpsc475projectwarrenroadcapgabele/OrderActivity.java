package com.example.cmpsc475projectwarrenroadcapgabele;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpsc475projectwarrenroadcapgabele.db.Order;
import com.example.cmpsc475projectwarrenroadcapgabele.db.RestaurantMenu;
import com.example.cmpsc475projectwarrenroadcapgabele.db.RestaurantMenuDatabase;
import com.example.cmpsc475projectwarrenroadcapgabele.db.RestaurantMenuViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    FirebaseFirestore db;
    private RestaurantMenuViewModel restaurantMenuViewModel;
    public SharedPreferences sharedPref;
    public Order current_order = new Order();
    private int order_number;
    public class MenuEntityListAdapter extends RecyclerView.Adapter<MenuEntityListAdapter.MenuEntityViewHolder>{
        class MenuEntityViewHolder extends RecyclerView.ViewHolder{
            private final TextView titleView;
            private final ImageView imageview;
            private RestaurantMenu menu;
            private MenuEntityViewHolder(View itemView) {
                super(itemView);
                titleView = itemView.findViewById(R.id.txtTitle);
                titleView.setOnClickListener(view -> displayDescription(menu.id));
                imageview = itemView.findViewById(R.id.addTo);
                imageview.setOnClickListener(view -> addToOrder(menu.id));
            }
        }
        private List<RestaurantMenu> menus;
        private final LayoutInflater layoutInflater;

        MenuEntityListAdapter(Context context){
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public MenuEntityListAdapter.MenuEntityViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View itemView = layoutInflater.inflate(R.layout.order_list_item, parent, false);
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
    TextView current_price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously().addOnCompleteListener(this,task ->{
            if(task.isSuccessful()){
                FirebaseUser user = auth.getCurrentUser();
            }
            else{

            }
        } );
        changeTheme();
        setContentView(R.layout.activity_order);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        current_price = findViewById(R.id.txtPrice);
        int savedOrderNumber = sharedPref.getInt("order_number", 0);
        if(savedOrderNumber == 0){
            order_number = 1;
        }
        else{
            order_number = savedOrderNumber;
        }
        RecyclerView recyclerView = findViewById(R.id.lst_RestaurantMenus);
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
        inflater.inflate(R.menu.order_activity, menu);
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
            DisplayDescriptionDialog descriptionDialog = new DisplayDescriptionDialog();
            descriptionDialog.setArguments(args);
            descriptionDialog.show(getSupportFragmentManager(), "setupDialog");
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
    private static final DecimalFormat df = new DecimalFormat("0.00");
    public void addToOrder(int id){
        RestaurantMenuDatabase.getMenu(id, menu ->
        {
            current_order.order_items.add(menu.item_name);
            current_order.order_price += menu.price;
            current_order.order_price = Double.parseDouble(df.format(current_order.order_price));
            current_price.setText("$"+df.format(current_order.order_price));
        });
    }

    public void submit(MenuItem item) {
        if (current_order.order_items.size() == 0) {
            Toast.makeText(this, "You must add items to the order!", Toast.LENGTH_SHORT).show();
        } else {
            String document_num = "order" + order_number;
            current_order.order_number = order_number;
            order_number++;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("order_number", order_number);
            editor.apply();
            db.collection("orders").document(document_num)
                    .set(current_order)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(OrderActivity.this, "Order Added", Toast.LENGTH_SHORT).show();
                            Log.d("SUCC", "Order added with ID: " + document_num);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("FAIL", "Error adding order", e);
                        }
                    });
            finish();
        }
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        String check = savedInstanceState.getString("price");
        ArrayList<String> order_items = savedInstanceState.getStringArrayList("current_order");
        Double restored_price = savedInstanceState.getDouble("current_price");
        current_price.setText(check);
        current_order.order_items = order_items;
        current_order.order_price = restored_price;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // code to save instance state goes here
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("price", current_price.getText().toString());
        savedInstanceState.putStringArrayList("current_order", current_order.order_items);
        savedInstanceState.putDouble("current_price", current_order.order_price);
    }
}
