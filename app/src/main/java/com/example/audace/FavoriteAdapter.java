package com.example.audace;

import android.net.Uri;
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

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private ArrayList<Favorite> favoriteArrayList;
    private FavoriteScreen activity;



    public FavoriteAdapter(FavoriteScreen activity, ArrayList<Favorite> favoriteArrayList
    ){
        this.activity = activity;
        this.favoriteArrayList = favoriteArrayList;
    }
    @Override
    @NonNull
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_item, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(FavoriteAdapter.ViewHolder holder, int position){
        Favorite item = favoriteArrayList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(String.valueOf(item.getPrice()));
        Picasso.get()
                .load(item.getImage())
                .resize(250,250)
                .into(holder.favoriteImage);

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
        ImageView favoriteImage;
/*
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_items, android.R.layout.simple_spinner_item);
*/


        ViewHolder( View view){
            super(view);
            nameTextView =view.findViewById(R.id.fav_nameTextView);
            priceTextView= view.findViewById(R.id.priceTextView);
            favoriteImage = view.findViewById(R.id.favoriteImage);
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
