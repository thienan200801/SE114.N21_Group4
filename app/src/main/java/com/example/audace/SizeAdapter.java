package com.example.audace;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.ViewHolder> {

    private ArrayList<SizeOption> sizeArrayList;
    private ProductDetailScreen activity;
    SizeClickListener sizeClickListener;

    public void setSizeClickListener(SizeAdapter.SizeClickListener listener) {
        this.sizeClickListener = listener;
    }

    public SizeAdapter(ProductDetailScreen activity, ArrayList<SizeOption> sizeArrayList
    ){
        this.activity = activity;
        this.sizeArrayList = sizeArrayList;
    }
    @Override
    @NonNull
    public SizeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.size_item, parent, false);
        return new SizeAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(SizeAdapter.ViewHolder holder, int position){
        SizeOption item = sizeArrayList.get(position);
        holder.sizeTextView.setText(item.getWidth()+"cm"+"x"+item.getHeight()+"cm");
        holder.size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sizeClickListener != null) {
                    sizeClickListener.onSizeClicked(item);
                }
            }
        });

        if (item.isSelected()) {
            holder.size.setSelected(true);
        } else {
            holder.size.setSelected(false);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageButton size;
        TextView sizeTextView;



        ViewHolder( View view){
            super(view);
            size = view.findViewById(R.id.size);
            sizeTextView=view.findViewById(R.id.sizeTextView);

        }

    }


    @Override
    public int getItemCount() {

        return sizeArrayList.size();

    }
    public void setSizeList(ArrayList<SizeOption> sizeArrayList){
        this.sizeArrayList = sizeArrayList;

    }
    public interface SizeClickListener {
        void onSizeClicked(SizeOption sizeOption);
    }
}

