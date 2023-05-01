package com.example.cmpsc475projectwarrenroadcapgabele;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.cmpsc475projectwarrenroadcapgabele.db.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderViewModel extends AndroidViewModel {
    private ArrayList<Order> orders = new ArrayList<>();
    public OrderViewModel(Application application){
        super(application);
    }
    public void getOrders(OnOrdersRetrievedListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ordersRef = db.collection("orders");
        ordersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        i++;
                        orders.add(document.toObject(Order.class));
                        Log.d("Receiving order: " , ""+i);
                    }
                    listener.onOrdersRetrieved(orders);
                } else {
                    Log.d("Error retrieving: ", "hi");
                }
            }
        });
    }
    public interface OnOrdersRetrievedListener {
        void onOrdersRetrieved(ArrayList<Order> orders);
    }
    public ArrayList<Order> getAllOrders(){
        return orders;
    }
}
