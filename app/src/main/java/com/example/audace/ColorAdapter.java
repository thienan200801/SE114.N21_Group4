package com.example.audace;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {

    private ArrayList<ColorOption> colorArrayList;
    private ProductDetailScreen activity;
    private ColorClickListener colorClickListener;

    public ColorAdapter(ProductDetailScreen activity, ArrayList<ColorOption> colorArrayList) {
        this.activity = activity;
        this.colorArrayList = colorArrayList;
    }

    public void setColorClickListener(ColorClickListener listener) {
        this.colorClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.color_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ColorOption item = colorArrayList.get(position);
        holder.color.setBackgroundColor(Color.parseColor(item.getHex()));

        if (item.isSelected()) {
            holder.color.setSelected(true);
        } else {
            holder.color.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return colorArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button color;

        ViewHolder(View view) {
            super(view);
            color = view.findViewById(R.id.color);
            color.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ColorOption colorOption = colorArrayList.get(position);
                colorOption.setSelected(true);

                if (colorClickListener != null) {
                    colorClickListener.onColorClicked(colorOption);
                }
            }
        }
    }

    public interface ColorClickListener {
        void onColorClicked(ColorOption colorOption);
    }
}
