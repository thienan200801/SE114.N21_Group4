package com.example.audace;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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
    ImageButton btnPurchase;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);


        recyclerView = findViewById(R.id.cartRecyclerView);

        cartAdapter = new CartAdapter(this,cartList);
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        setupData();

        btnPurchase = findViewById(R.id.btnPurchase);
        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartList.isEmpty()){
                    Toast.makeText(CartScreen.this, "Giỏ hàng chưa có sản phẩm", Toast.LENGTH_SHORT).show();

                }
                else {

                }
            }
        });



    }

    public void setupData() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
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

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return response.body().string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String responseString) {
                if (responseString != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseString);
                        JSONArray cartArray = jsonResponse.getJSONArray("cart");
                        Log.i("cartSize", String.valueOf(cartArray.length()));
                        for (int i = 0; i < cartArray.length(); i++) {
                            JSONObject productObject = cartArray.getJSONObject(i);
                            String productId = productObject.getString("product");
                            String productColor;
                            productColor = productObject.getString("color");
                            String productSize;
                            productSize = productObject.getString("size");

                            final int index = i;
                            getProductInfo(productId, productColor, productSize, new OrderScreen.ProductInfoCallback() {
                                @Override
                                public void onProductInfoReceived(Favorite product) {
                                    String productName = product.getName();
                                    Log.i("productName",productName);

                                    int productPrice = product.getPrice();
                                    String imageURL = product.getImage();
                                    Log.i("img",imageURL);



                                    int productQuantity = 0;
                                    try {
                                        productQuantity = productObject.getInt("quantity");
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                    Cart cartItem = new Cart(productId,productName,productPrice,productColor,productSize,imageURL);
                                    cartItem.setColorName(product.getColorName());
                                    cartItem.setSizeHeight(product.getSizeHeight());
                                    cartItem.setSizeWidth(product.getSizeWidth());
                                    cartItem.setQuantity(productQuantity);
                                    cartList.add(cartItem);
                                    Log.i("cartList",String.valueOf(cartList.size()));
                                    if (index == cartArray.length() - 1) {
                                        cartAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Log.i("error", errorMessage);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }


    private void getProductInfo(String productId,String color,String size, OrderScreen.ProductInfoCallback callback) {
        Handler handler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/products/product/" + productId)
                .method("GET", null)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String errorMessage = e.getMessage();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(errorMessage);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String productName = jsonResponse.getString("name");
                    String productDescription = jsonResponse.getString("description");
                    String imageURL = jsonResponse.getString("imageURL");
                    int currentPrice = jsonResponse.getInt("currentPrice");
                    JSONArray colorsArray = jsonResponse.getJSONArray("colors");
                    String colorName = "";
                    for (int j = 0; j < colorsArray.length(); j++) {
                        JSONObject colorObject = colorsArray.getJSONObject(j);
                        String colorObjectId = colorObject.getString("_id");
                        if (colorObjectId.equals(color)) {
                            colorName = colorObject.getString("name");
                            break;
                        }
                    }
                    JSONArray sizesArray = jsonResponse.getJSONArray("sizes");
                    String sizeWidth = "";
                    String sizeHeight = "";
                    for (int k = 0; k < sizesArray.length(); k++) {
                        JSONObject sizeObject = sizesArray.getJSONObject(k);
                        String sizeObjectId = sizeObject.getString("_id");
                        if (sizeObjectId.equals(size)) {
                            sizeWidth = sizeObject.getString("widthInCentimeter");
                            sizeHeight = sizeObject.getString("heightInCentimeter");
                            break;
                        }
                    }
                    Favorite product = new Favorite(productId, productName, imageURL, currentPrice);
                    product.setColorName(colorName);
                    product.setSizeWidth(sizeWidth);
                    product.setSizeHeight(sizeHeight);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onProductInfoReceived(product);
                        }
                    });
                } catch (JSONException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e.getMessage());
                        }
                    });
                }
            }
        });
    }
}



