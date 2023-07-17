package com.example.audace;

import static android.os.Looper.getMainLooper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        holder.colorTextView.setText(item.getColor());
        holder.sizeTextView.setText(item.getSize());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.viewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToDetailActivity(item.getId());
            }
        });

        Picasso.get()
                .load(item.getImage())
                .resize(250,250)
                .into(holder.historyImage);

    }
    private void navigateToDetailActivity(String productId) {

        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra("productId", productId);
        activity.startActivity(intent);

    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView ;
        TextView colorTextView ;
        TextView sizeTextView ;

        TextView priceTextView ;
        TextView quantityTextView;
        ImageButton viewDetail;

        ImageView historyImage;


        ViewHolder( View view){
            super(view);
            nameTextView =view.findViewById(R.id.history_name_TextView);
            priceTextView= view.findViewById(R.id.price_txtView);
            historyImage = view.findViewById(R.id.historyImage);
            quantityTextView = view.findViewById(R.id.quantityTextView);
            viewDetail = view.findViewById(R.id.btnViewDetail);
            colorTextView = view.findViewById(R.id.colorTextView);
            sizeTextView = view.findViewById(R.id.sizeTextView);



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
