package com.example.audace.adapter;

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
    public SizeAdapter(ArrayList<SizeObject> input) {
        sizeArray = input;
        selectedItem = 0;
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
    public void onBindViewHolder(@NonNull SizeAdapter.ViewHolder holder, int position) {
        holder.textView.setText(sizeArray.get(position).getSizeInCentimeter());
    }

    @Override
    public int getItemCount() {
        return sizeArray == null ? 0 : sizeArray.size();
    }
}
