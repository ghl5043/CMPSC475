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
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpsc475projectwarrenroadcapgabele.db.Order;


import java.text.DecimalFormat;
import java.util.ArrayList;

public class ViewOrdersActivity extends AppCompatActivity implements OrderViewModel.OnOrdersRetrievedListener {
    ArrayList<Order> globalorders;
    private OrderViewModel orderViewModel;
    public SharedPreferences sharedPref;
    public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
        class OrderViewHolder extends RecyclerView.ViewHolder{
            private final TextView titleView;
            private Order order;
            private OrderViewHolder(View itemView) {
                super(itemView);
                titleView = itemView.findViewById(R.id.ordNum);
                itemView.setOnClickListener(view -> displayDescription(order.order_number));
            }
        }

        private ArrayList<Order> orders;

        private final LayoutInflater layoutInflater;

        OrderAdapter(Context context){
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public OrderAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.order_item_history, parent, false);
            return new OrderAdapter.OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder( OrderAdapter.OrderViewHolder holder, int position) {
            if(orders!=null) {
                Order order = orders.get(position);
                holder.titleView.setText("Order Number: "+order.order_number);
                holder.order = order;
            }
            else{
                holder.titleView.setText("...initializing...");
            }
        }

        @Override
        public int getItemCount() {
            if(orders!=null) {
                return orders.size();
            }
            else{
                return 0;
            }
        }

        void setOrders(ArrayList<Order> orders){
            this.orders = orders;
            notifyDataSetChanged();
        }
    }
    OrderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        changeTheme();
        setContentView(R.layout.activity_view_orders);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        RecyclerView recyclerView = findViewById(R.id.lstOrders);
        adapter = new OrderAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        if (savedInstanceState != null) {
            globalorders = savedInstanceState.getParcelableArrayList("globalorders");
            adapter.setOrders(globalorders);
        } else {
            orderViewModel.getOrders(this);
        }

    }
    @Override
    public void onOrdersRetrieved(ArrayList<Order> orders) {
        for(Order order: orders){
            Log.d("Displaying Price: ", ""+order.order_price);
        }
        adapter.setOrders(orders); // Update the adapter with the retrieved orders
        globalorders = orders;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }
    public void displayDescription(int order_id){
        Order order = globalorders.get(order_id-1);
        Bundle args = new Bundle();
        args.putInt("order_id", order_id);
        args.putStringArrayList("food_items", order.order_items);


        args.putDouble("order_price",order.order_price);
        DisplayDescriptionDialog setupDialog = new DisplayDescriptionDialog();
        setupDialog.setArguments(args);
        setupDialog.show(getSupportFragmentManager(), "showOrder");
    }
    public static class DisplayDescriptionDialog extends DialogFragment {
        int order_id;
        private static final DecimalFormat df = new DecimalFormat("0.00");
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            order_id = getArguments().getInt("order_id");
            final ArrayList<String> food_items = getArguments().getStringArrayList("food_items");
            final Double price = getArguments().getDouble("order_price");
            StringBuilder sb = new StringBuilder();
            for(String s : food_items){
                sb.append(s);
                sb.append("\n");
            }
            String foods = sb.toString();
            builder.setTitle("Order # " + order_id)
                    .setMessage(foods + '\n' + "$" + df.format(price));
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
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("globalorders", globalorders);
    }
}
