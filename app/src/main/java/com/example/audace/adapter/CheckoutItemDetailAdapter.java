package com.example.audace.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.audace.R;
import com.example.audace.model.CheckoutItemDetails;
import com.example.audace.model.SizeObject;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckoutItemDetailAdapter extends RecyclerView.Adapter<CheckoutItemDetailAdapter.ViewHolder>{
    ArrayList<CheckoutItemDetails> checkoutItemDetailsArray;
    private int selectedItem;
    public CheckoutItemDetailAdapter(ArrayList<CheckoutItemDetails> input) {
        checkoutItemDetailsArray = input;
        selectedItem = 0;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, size, color, price, quantity;
        private ImageView imageView;

        public ViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.textView6);
            size = (TextView) view.findViewById(R.id.textView7);
            color = (TextView) view.findViewById(R.id.textView8);
            price= (TextView) view.findViewById(R.id.textView9);
            quantity= (TextView) view.findViewById(R.id.textView10);
            imageView = (ImageView) view.findViewById(R.id.imageView3);
        }

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getColor() {
            return color;
        }

        public TextView getPrice() {
            return price;
        }

        public TextView getQuantity() {
            return quantity;
        }

        public TextView getSize() {
            return size;
        }

        public TextView getTitle() {
            return title;
        }
    }

    @NonNull
    @Override
    public CheckoutItemDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CheckoutItemDetailAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(checkoutItemDetailsArray.get(position).getName());
        holder.quantity.setText("Số lượng: " + checkoutItemDetailsArray.get(position).getQuantity());
        holder.color.setText("Màu sắc: " + checkoutItemDetailsArray.get(position).getColor().getColorName());
        holder.size.setText(checkoutItemDetailsArray.get(position).getSize().getSizeInCentimeter());
        holder.price.setText("$" + checkoutItemDetailsArray.get(position).getPrice());

        if(!Objects.equals(checkoutItemDetailsArray.get(position).getImageURL(), ""))
        {
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(holder.getImageView().getContext());
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();
            Picasso.get()
                    .load(checkoutItemDetailsArray.get(position).getImageURL())
                    .fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(circularProgressDrawable)
                    .into(holder.getImageView(), new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get()
                                    .load(checkoutItemDetailsArray.get(position).getImageURL())
                                    .placeholder(circularProgressDrawable)
                                    .fit()
                                    .into(holder.getImageView());
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return checkoutItemDetailsArray.size();
    }
}
