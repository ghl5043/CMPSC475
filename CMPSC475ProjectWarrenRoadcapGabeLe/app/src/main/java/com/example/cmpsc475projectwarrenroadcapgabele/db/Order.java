package com.example.cmpsc475projectwarrenroadcapgabele.db;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Order implements Parcelable {
    public int order_number;
    public ArrayList<String> order_items = new ArrayList<>();
    public double order_price;


    public Order(){
        order_number = 0;
        order_items = new ArrayList<>();
        order_price = 0.00;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    protected Order(Parcel in) {
        order_number = in.readInt();
        order_items = in.createStringArrayList();
        order_price = in.readDouble();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(order_number);
        dest.writeStringList(order_items);
        dest.writeDouble(order_price);
    }
}
