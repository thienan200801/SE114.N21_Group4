package com.example.audace;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductOrderAdapter extends RecyclerView.Adapter<ProductOrderAdapter.ViewHolder> {

    private ArrayList<Favorite> productArrayList;
    private OrderScreen activity;



    public ProductOrderAdapter(OrderScreen activity, ArrayList<Favorite> productArrayList){
        this.activity = activity;
        this.productArrayList = productArrayList;
    }
    @Override
    @NonNull
    public ProductOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_order_item, parent, false);
        return new ProductOrderAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ProductOrderAdapter.ViewHolder holder, int position){
        Favorite item = productArrayList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(String.valueOf(item.getPrice()));
        Picasso.get()
                .load(item.getImage())
                .resize(250,250)
                .into(holder.orderImage);

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView ;
        TextView colorTextView ;
        TextView sizeTextView ;
        TextView priceTextView ;
        ImageView orderImage;


        ViewHolder( View view){
            super(view);
            nameTextView =view.findViewById(R.id.product_name_txtView);
            priceTextView= view.findViewById(R.id.price_txtView);
            orderImage = view.findViewById(R.id.order_image);





        }

    }


    @Override
    public int getItemCount() {

        return productArrayList.size();

    }
    public void setOrderArrayList(ArrayList<Favorite> productArrayList){
        this.productArrayList = productArrayList;

    }
}

