package com.example.audace.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audace.R;
import com.example.audace.model.ColorObject;
import com.example.audace.model.SizeObject;

import java.util.ArrayList;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder>{
    ArrayList<ColorObject> colorArray;

    private int selectedItem;

    private TextView textView;

    public ColorAdapter(ArrayList<ColorObject> input, TextView textView) {
        colorArray = input;
        selectedItem = -1;
        this.textView = textView;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Button colorBtn;

        public ViewHolder(View view){
            super(view);
            colorBtn = (Button) view.findViewById(R.id.colorItemDetail);
        }

        public Button getButton() {
            return colorBtn;
        }
    }

    @NonNull
    @Override
    public ColorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_item_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.colorBtn.setBackgroundColor(Color.parseColor(colorArray.get(position).getColor()));
        holder.colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(colorArray.get(position).getColorName());
                selectedItem = position;
            }
        });
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    @Override
    public int getItemCount() {
        return colorArray == null ? 0 : colorArray.size();
    }
}
