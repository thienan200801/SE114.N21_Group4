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

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private ArrayList<Favorite> favoriteArrayList;
    private OrderScreen activity;



    public OrderAdapter(OrderScreen activity, ArrayList<Favorite> favoriteArrayList
    ){
        this.activity = activity;
        this.favoriteArrayList = favoriteArrayList;
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
        Favorite item = favoriteArrayList.get(position);
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
        TextView numTextView;
        ImageButton deleteFavorite;
        ImageButton addtoCart;

        Spinner spinner;
        ImageView orderImage;
/*
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_items, android.R.layout.simple_spinner_item);
*/


        ViewHolder( View view){
            super(view);
            nameTextView =view.findViewById(R.id.fav_nameTextView);
            priceTextView= view.findViewById(R.id.priceTextView);
            orderImage = view.findViewById(R.id.order_image);
            deleteFavorite = view.findViewById(R.id.btnDel);
            addtoCart = view.findViewById(R.id.btnCart);





        }

    }


    @Override
    public int getItemCount() {

        return favoriteArrayList.size();

    }
    public void setCartList(ArrayList<Favorite> favoriteArrayList){
        this.favoriteArrayList = favoriteArrayList;

    }
}
