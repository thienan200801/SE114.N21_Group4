package com.example.audace;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {

    private ArrayList<ColorOption> colorArrayList;
    private ProductDetailScreen activity;



    public ColorAdapter(ProductDetailScreen activity, ArrayList<ColorOption> colorArrayList
    ){
        this.activity = activity;
        this.colorArrayList = colorArrayList;
    }
    @Override
    @NonNull
    public ColorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.color_item, parent, false);
        return new ColorAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ColorAdapter.ViewHolder holder, int position){
        ColorOption item = colorArrayList.get(position);
        holder.color.setBackgroundColor(Color.parseColor(item.getHex()));


    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        Button color;
/*
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_items, android.R.layout.simple_spinner_item);
*/


        ViewHolder( View view){
            super(view);
            color = view.findViewById(R.id.color);

        }

    }


    @Override
    public int getItemCount() {

        return colorArrayList.size();

    }
    public void setCartList(ArrayList<ColorOption> colorArrayList){
        this.colorArrayList = colorArrayList;

    }
}
