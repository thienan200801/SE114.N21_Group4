package com.example.audace.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audace.DataStorage;
import com.example.audace.Favorite;
import com.example.audace.HistoryScreen;
import com.example.audace.R;
import com.example.audace.fragment_productDetail;
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
        holder.colorTextView.setText(item.getColorName());
        holder.sizeTextView.setText(item.getSizeWidth() + " x "+item.getSizeHeight());
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
        DataStorage.getInstance().setProductId(productId);
        FragmentContainerView navHostFragment = (FragmentContainerView) activity.getActivity().findViewById(R.id.bottomNavigationContainer);
        FragmentTransaction transaction = navHostFragment.getFragment().getFragmentManager().beginTransaction();
        transaction.replace(R.id.bottomNavigationContainer, new fragment_productDetail());
        transaction.addToBackStack(null);
        transaction.commit();


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
