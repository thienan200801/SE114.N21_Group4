package com.example.audace;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

private ArrayList<Cart> cartList ;
private CartScreen activity;



public CartAdapter(CartScreen activity, ArrayList<Cart> cartList
        ){
        this.activity = activity;
        this.cartList = cartList;
        }
@Override
@NonNull
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(itemView);
        }


@Override
public void onBindViewHolder(ViewHolder holder, int position){
        Cart item = cartList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.colorTextView.setText(item.getColor());
        holder.sizeTextView.setText(item.getSize());
        holder.numTextView.setText(item.getQuantity());
        holder.saveCart.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {

        }
        });

        }

public class ViewHolder extends RecyclerView.ViewHolder{
    TextView nameTextView ;
    TextView colorTextView ;
    TextView sizeTextView ;
    TextView priceTextView ;
    TextView numTextView;
    ImageButton saveCart;
    Spinner spinner;
/*
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_items, android.R.layout.simple_spinner_item);
*/


    ViewHolder( View view){
        super(view);
        nameTextView =view.findViewById(R.id.cart_name_txtView);
        priceTextView= view.findViewById(R.id.price_txtView);
        numTextView = view.findViewById(R.id.numTextView);






    }

}


    @Override
    public int getItemCount() {

        return cartList.size();

    }
    public void setCartList(ArrayList<Cart> cartList){
        this.cartList = cartList;

    }
}

