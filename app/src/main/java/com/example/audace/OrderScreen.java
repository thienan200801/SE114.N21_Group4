package com.example.audace;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audace.adapter.OrderAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderScreen extends AppCompatActivity {
    private ArrayList<Order> orderArrayList = new ArrayList<>();
    private OrderAdapter orderAdapter;
    public interface ProductInfoCallback {
        void onProductInfoReceived(Favorite product);
        void onFailure(String errorMessage);
    }
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);


        recyclerView = findViewById(R.id.orderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        orderAdapter = new OrderAdapter(this,orderArrayList);
        setupData();
        recyclerView.setAdapter(orderAdapter);

    }

    public void getupData() {
        Handler handler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/orders")
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
                try {
                    JSONArray jsonResponse = new JSONArray(response.body().string());
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject orderObject = jsonResponse.getJSONObject(i);
                        JSONArray productCheckoutInfos = orderObject.getJSONArray("productCheckoutInfos");
                        ArrayList<Favorite> productList = new ArrayList<>();
                        final AtomicInteger productCallbackCount = new AtomicInteger(0);
                        for (int j = 0; j < productCheckoutInfos.length(); j++) {
                            JSONObject productCheckoutInfo = productCheckoutInfos.getJSONObject(j);
                            String productId = productCheckoutInfo.getString("product");
                            Log.i("product",productId);
                            String productColor = productCheckoutInfo.getString("color");
                            String productSize = productCheckoutInfo.getString("size");


                            getProductInfo(productId,productColor,productSize, new ProductInfoCallback() {
                                @Override
                                public void onProductInfoReceived(Favorite product) {
                                    productList.add(product);
                                    int count = productCallbackCount.incrementAndGet(); // Increment the callback count

                                    // Check if all product info callbacks have been received
                                    if (count == productCheckoutInfos.length()) {

                                        String orderId = null;
                                        try {
                                            orderId = orderObject.getString("_id");
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                        String totalPrice = null;
                                        try {
                                            totalPrice = orderObject.getString("total");
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }

                                        Order order = new Order(orderId, totalPrice, productList);
                                        orderArrayList.add(order);

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                orderAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Log.i("error",errorMessage);
                                }
                            });
                        }


                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            orderAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void setupData() {
        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = RequestBody.create(mediaType, "");
                Request request = new Request.Builder()
                        .url("https://audace-ecomerce.herokuapp.com/users/me/orders")
                        .method("GET", null)
                        .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseString = response.body().string();
                        return new JSONArray(responseString);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(JSONArray jsonResponse) {
                if (jsonResponse != null) {
                    try {
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject orderObject = jsonResponse.getJSONObject(i);
                            JSONArray productCheckoutInfos = orderObject.getJSONArray("productCheckoutInfos");
                            ArrayList<Favorite> productList = new ArrayList<>();
                            final AtomicInteger productCallbackCount = new AtomicInteger(0);
                            for (int j = 0; j < productCheckoutInfos.length(); j++) {
                                JSONObject productCheckoutInfo = productCheckoutInfos.getJSONObject(j);
                                String productId = productCheckoutInfo.getString("product");
                                Log.i("product",productId);
                                String productColor = productCheckoutInfo.getString("color");
                                String productSize = productCheckoutInfo.getString("size");

                                final int index = i;
                                getProductInfo(productId, productColor, productSize, new ProductInfoCallback() {
                                    @Override
                                    public void onProductInfoReceived(Favorite product) {
                                        productList.add(product);
                                        int count = productCallbackCount.incrementAndGet(); // Increment the callback count

                                        // Check if all product info callbacks have been received
                                        if (count == productCheckoutInfos.length()) {
                                            String orderId = null;
                                            try {
                                                orderId = orderObject.getString("_id");
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                            String totalPrice = null;
                                            try {
                                                totalPrice = orderObject.getString("total");
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }

                                            Order order = new Order(orderId, totalPrice, productList);
                                            orderArrayList.add(order);

                                            if (index == jsonResponse.length() - 1) {
                                                orderAdapter.notifyDataSetChanged();

                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Log.i("error", errorMessage);
                                    }
                                });
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    private void getProductInfo(String productId,String color,String size, ProductInfoCallback callback) {
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
                .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
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
