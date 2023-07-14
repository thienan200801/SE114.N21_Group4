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

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<Favorite> historyArrayList;
    private HistoryScreen activity;



    public HistoryAdapter(HistoryScreen activity, ArrayList<Favorite> historyArrayList
    ){
        this.activity = activity;
        this.historyArrayList = historyArrayList;
    }
    @Override
    @NonNull
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new HistoryAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position){
        Favorite item = historyArrayList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(String.valueOf(item.getPrice()));
        holder.quantityTextView.setText(item.getQuantity());
        Picasso.get()
                .load(item.getImage())
                .resize(250,250)
                .into(holder.historyImage);

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView ;
        TextView colorTextView ;
        TextView sizeTextView ;

        TextView priceTextView ;
        TextView quantityTextView;
        ImageButton addtoCart;

        Spinner spinner;
        ImageView historyImage;
/*
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_items, android.R.layout.simple_spinner_item);
*/


        ViewHolder( View view){
            super(view);
            nameTextView =view.findViewById(R.id.history_name_TextView);
            priceTextView= view.findViewById(R.id.price_txtView);
            historyImage = view.findViewById(R.id.historyImage);
            quantityTextView = view.findViewById(R.id.quantityTextView);





        }

    }


    @Override
    public int getItemCount() {

        return historyArrayList.size();

    }
    public void setHistoryArrayList(ArrayList<Favorite> historyArrayList){
        this.historyArrayList = historyArrayList;

    }
}
