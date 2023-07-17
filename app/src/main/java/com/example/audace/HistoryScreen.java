package com.example.audace;

import static android.os.Looper.getMainLooper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audace.adapter.HistoryAdapter;

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

public class HistoryScreen extends Fragment {

    private ArrayList<Favorite> historyArrayList = new ArrayList<>();
    private HistoryAdapter historyAdapter;

    private RecyclerView recyclerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.history, container, false);
        recyclerView = view.findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        historyAdapter = new HistoryAdapter(this,historyArrayList);
        recyclerView.setAdapter(historyAdapter);
        view.findViewById(R.id.cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getBaseContext(), CartScreen.class);
                startActivity(i);
            }
        });
        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        setupData();
        return view;
    }

    public void setupData() {
        Handler handler = new Handler(Looper.getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/history")
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
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
                        try {
                            JSONArray jsonResponse = new JSONArray(response.body().string());
                            historyArrayList.clear();
                            for (int i = 0; i < jsonResponse.length(); i++) {
                                JSONObject productObject = jsonResponse.getJSONObject(i);
                                JSONObject product = productObject.getJSONObject("product");
                                String productId = product.getString("_id");
                                String productName = product.getString("name");
                                String imageURL = product.getString("imageURL");
                                int currentPrice = product.getInt("currentPrice");
                                int productQuantity = productObject.getInt("quantity");
                                String selectedColor = productObject.getJSONObject("color").getString("_id");
                                String selectedSize = productObject.getJSONObject("size").getString("_id");
                                getProductInfo(productId,selectedColor,selectedSize, new OrderScreen.ProductInfoCallback() {
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
                                        Favorite historyProduct = new Favorite(productId,productName,imageURL,currentPrice);
                                        historyProduct.setQuantity(productQuantity);
                                        historyProduct.setSizeWidth(product.getSizeWidth());
                                        historyProduct.setSizeHeight(product.getSizeHeight());
                                        historyProduct.setColorName(product.getColorName());
                                        historyArrayList.add(historyProduct);
                                        historyAdapter.notifyDataSetChanged();

                                    }
                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Log.i("error",errorMessage);
                                    }
                                });

                            }

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