package com.example.audace;

import static android.os.Looper.getMainLooper;

import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

private ArrayList<Cart> cartList ;
private CartScreen activity;



public CartAdapter(CartScreen activity, ArrayList<Cart> cartList
        ){
        this.activity = activity;
        this.cartList = cartList;
        }
@Override
@NonNull
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(itemView);
}


@Override
public void onBindViewHolder(CartAdapter.ViewHolder holder, int position){
        Cart item = cartList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.colorTextView.setText(item.getColorName());
        holder.sizeTextView.setText(item.getSizeWidth() + "cm x "+item.getSizeHeight() + "cm");
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.priceTextView.setText("$" + item.getPrice());
        if(cartList.size() > 0)
            activity.findViewById(R.id.purchaseLayout).setVisibility(View.VISIBLE);
        Log.i("execute","ok");


    holder.sizeBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            navigateToProductDetail(item.getId(),item.getColor(),item.getSize());
        }
    });
    holder.colorBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            navigateToProductDetail(item.getId(), item.getColor(), item.getSize());
        }
    });
    holder.btnDelete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            deleteItem(item.getId());
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
    TextView priceTextView ;
    TextView quantityTextView;
    ImageButton colorBtn;
    ImageButton sizeBtn;
    ImageView favoriteImage;
    ImageButton btnDelete;
    ImageButton plus;
    ImageButton minus;




    ViewHolder( View view){
        super(view);
        nameTextView =view.findViewById(R.id.cart_name_txtView);
        priceTextView= view.findViewById(R.id.price_txtView);
        quantityTextView = view.findViewById(R.id.numTextView);
        sizeTextView = view.findViewById(R.id.sizeTextView);
        colorTextView = view.findViewById(R.id.colorTextView);
        colorBtn = view.findViewById(R.id.btnColor);
        sizeBtn = view.findViewById(R.id.btnSize);
        favoriteImage = view.findViewById(R.id.cartImage);
        btnDelete = view.findViewById(R.id.btnDel);
        plus = view.findViewById(R.id.btnNumPlus);
        minus = view.findViewById(R.id.btnNumMinus);


    }

}
    private void navigateToProductDetail(String productId,String selectedColor, String selectedSize) {
        ProductDetailScreen productDetailScreen = new ProductDetailScreen();

        Bundle bundle = new Bundle();
        bundle.putString("productId", productId);
        productDetailScreen.setArguments(bundle);
        bundle.putString("selectedColor",selectedColor);
        bundle.putString("selectedSize",selectedSize);

        productDetailScreen.show(activity.getSupportFragmentManager(), "ProductDetailBottomSheet");

    }
    private void deleteItem(String id){
        Handler handler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/json");
        JSONObject requestBody = new JSONObject();
        JSONArray productIdsArray = new JSONArray();
        productIdsArray.put(id);

        try {
            requestBody.put("productIds", productIdsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(mediaType, requestBody.toString());

        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/cart")
                .delete(body)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String body=response.body().string();
                Log.e("data from server", body);
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (body.equals("")) {
                            Toast.makeText(activity, "Item deleted", Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < cartList.size(); i++) {
                                Cart cartItem = cartList.get(i);
                                if (cartItem.getId().equals(id)) {
                                    cartList.remove(i);
                                    notifyItemRemoved(i);
                                    break;
                                }
                            }
                        }
                        else {
                            Toast.makeText(activity, body, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount", String.valueOf(cartList.size()));
        return cartList.size();

    }
    public void setCartList(ArrayList<Cart> cartList){
        this.cartList = cartList;

    }
}

