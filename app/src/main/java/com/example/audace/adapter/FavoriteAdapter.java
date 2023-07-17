package com.example.audace.adapter;

import static android.os.Looper.getMainLooper;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.audace.DataStorage;
import com.example.audace.Favorite;
import com.example.audace.FavoriteScreen;
import com.example.audace.ProductDetailScreen;
import com.example.audace.R;
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

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private ArrayList<Favorite> favoriteArrayList;
    private FavoriteScreen activity;


    public FavoriteAdapter(FavoriteScreen activity, ArrayList<Favorite> favoriteArrayList){
        this.activity = activity;
        this.favoriteArrayList = favoriteArrayList;
    }
    @Override
    @NonNull
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_item, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(FavoriteAdapter.ViewHolder holder, int position){
        Favorite item = favoriteArrayList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(String.valueOf(item.getPrice()));
        holder.colorTextView.setText(item.getColor());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.sizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToProductDetail(item.getId());
            }
        });
        holder.colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToProductDetail(item.getId());
            }
        });
        holder.addtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCart(item);
            }
        });
        Picasso.get()
                .load(item.getImage())
                .resize(250,250)
                .into(holder.favoriteImage);

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView ;
        TextView colorTextView ;
        TextView sizeTextView ;
        Button colorBtn;
        Button sizeBtn;
        TextView priceTextView ;
        TextView quantityTextView;
        ImageButton deleteFavorite;
        ImageButton addtoCart;
        ImageView favoriteImage;




        ViewHolder( View view){
            super(view);
            nameTextView =view.findViewById(R.id.fav_nameTextView);
            priceTextView= view.findViewById(R.id.priceTextView);
            favoriteImage = view.findViewById(R.id.favoriteImage);
            deleteFavorite = view.findViewById(R.id.btnDel);
            addtoCart = view.findViewById(R.id.btnAddCart);
            colorBtn = view.findViewById(R.id.btnColor);
            sizeBtn = view.findViewById(R.id.btnSize);
            colorTextView = view.findViewById(R.id.colorTextView);
            sizeTextView = view.findViewById(R.id.sizeTextView);
            quantityTextView = view.findViewById(R.id.quantityTextView);





        }

    }
    private void navigateToProductDetail(String productId) {
        ProductDetailScreen productDetailScreen = new ProductDetailScreen();

        Bundle bundle = new Bundle();
        bundle.putString("productId", productId);
        productDetailScreen.setArguments(bundle);

        productDetailScreen.show(activity.getActivity().getSupportFragmentManager(), "ProductDetailBottomSheet");

    }


    private void addCart(Favorite item){
        try {
            Handler handler = new Handler(getMainLooper());
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject productJson = new JSONObject();
            try {
                productJson.put("product", item.getId());
                productJson.put("size", item.getSize());
                productJson.put("color", item.getColor());
                productJson.put("quantity", item.getQuantity());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray productCheckoutInfo = new JSONArray();
            productCheckoutInfo.put(productJson);



            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject requestBody = new JSONObject();
                    try {
                        requestBody.put("productCheckoutInfos", productCheckoutInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody body = RequestBody.create(mediaType, requestBody.toString());




                    Request request = new Request.Builder()
                            .url("https://audace-ecomerce.herokuapp.com/users/me/cart")
                            .method("POST", body)
                            .addHeader("Authorization", " Bearer " + DataStorage.getInstance().getAccessToken())
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Toast.makeText(activity.getContext(),"Error: Cannot add to cart",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String body=response.body().string();
                            Log.e("data from server", body);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (body.equals("")) {

                                        Toast.makeText(activity.getContext(), "Added to cart", Toast.LENGTH_SHORT).show();

                                    }
                                    else {
                                        Toast.makeText(activity.getContext(), body, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });


                        }
                    });

                }
            }).start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        return favoriteArrayList.size();

    }
    public void setCartList(ArrayList<Favorite> favoriteArrayList){
        this.favoriteArrayList = favoriteArrayList;

    }
}
