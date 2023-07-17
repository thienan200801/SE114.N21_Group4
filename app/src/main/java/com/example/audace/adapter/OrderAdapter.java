package com.example.audace.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.audace.Favorite;
import com.example.audace.Order;
import com.example.audace.OrderScreen;
import com.example.audace.R;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private ArrayList<Order> orderArrayList;
    private OrderScreen activity;



    public OrderAdapter(OrderScreen activity, ArrayList<Order> orderArrayList
    ){
        this.activity = activity;
        this.orderArrayList = orderArrayList;
    }
    @Override
    @NonNull
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);
        return new OrderAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, int position){
        Order item = orderArrayList.get(position);
        holder.priceTextView.setText(item.getTotalPrice());
        holder.bind(item);

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView priceTextView ;
        RecyclerView productRecyclerView;



        ViewHolder( View view){
            super(view);
            priceTextView= view.findViewById(R.id.totalPrice_txtView);
            productRecyclerView = view.findViewById(R.id.productRecyclerView);
        }
        public void bind(Order order){
            ArrayList<Favorite> productList = order.getProductList();
            ProductOrderAdapter productAdapter = new ProductOrderAdapter(activity,productList);
            productRecyclerView.setAdapter(productAdapter);
            productRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));

        }

    }


    @Override
    public int getItemCount() {

        return orderArrayList.size();

    }
    public void setOrderArrayList(ArrayList<Order> orderArrayList){
        this.orderArrayList = orderArrayList;

    }
}
