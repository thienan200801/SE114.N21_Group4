package com.example.audace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class CartScreen extends AppCompatActivity {

    private ArrayList<Cart> cartList = new ArrayList<>();
    private CartAdapter cartAdapter;

    private ImageButton checkoutButton;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);
        checkoutButton = findViewById(R.id.btnPurchase);
        recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        cartAdapter = new CartAdapter(this,cartList);
        setupData();
        recyclerView.setAdapter(cartAdapter);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(CartScreen.this,Checkout.class);
                startActivity(t);
            }
        });
    }
    public void setupData() {
        Handler handler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                .method("GET", null)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("message", call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        cartList.clear();
                        try {
                            JSONObject jsonResponse = new JSONObject(response.body().string());
                            JSONArray cartArray = jsonResponse.getJSONArray("cart");
                            for (int i = 0; i < cartArray.length(); i++) {
                                JSONObject productObject = cartArray.getJSONObject(i);
                                String productId = productObject.getString("product");
                                String productName = productObject.getString("name");
                                double productPrice = productObject.getDouble("currentPrice");
                                String productColor = productObject.getString("color");
                                String productSize = productObject.getString("size");
                                String imageURL = productObject.getString("imageURL");
                                String productQuantity = productObject.getString("quantity");

                                Cart cartItem = new Cart(productId,productName,productPrice,productQuantity,productColor,productSize,imageURL);

                                cartList.add(cartItem);
                            }
                            cartAdapter.notifyDataSetChanged();
                        }catch (JSONException e) {
                            e.printStackTrace();}
                        catch (IOException e){
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
        }
    }



