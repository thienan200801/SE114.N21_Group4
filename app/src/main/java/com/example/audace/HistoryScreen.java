package com.example.audace;

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
                                Favorite historyProduct = new Favorite(productId,productName,imageURL,currentPrice);
                                historyProduct.setQuantity(productQuantity);
                                historyProduct.setSize(selectedSize);
                                historyProduct.setColor(selectedColor);
                                historyArrayList.add(historyProduct);
                            }
                            historyAdapter.notifyDataSetChanged();
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