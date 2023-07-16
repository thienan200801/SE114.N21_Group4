package com.example.audace.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audace.R;
import com.example.audace.model.SizeObject;

import java.util.ArrayList;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.ViewHolder>{
    ArrayList<SizeObject> sizeArray;
    private int selectedItem;

    private TextView sizeTextView;
    public SizeAdapter(ArrayList<SizeObject> input, TextView textView) {
        sizeArray = input;
        selectedItem = -1;
        this.sizeTextView = textView;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view){
            super(view);
            textView = (TextView) view.findViewById(R.id.textView5);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    @NonNull
    @Override
    public SizeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.size_item_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textView.setText(sizeArray.get(position).getSizeInCentimeter());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sizeTextView.setText(sizeArray.get(position).getSizeInCentimeter());
                selectedItem = position;
            }
        });
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    @Override
    public int getItemCount() {
        return sizeArray == null ? 0 : sizeArray.size();
    }
}
