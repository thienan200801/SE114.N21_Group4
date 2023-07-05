package com.example.audace;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

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

public class ProductDetailScreen extends AppCompatActivity {
    private ArrayList<ColorOption> colorOptionsList = new ArrayList<>();
    private ArrayList<SizeOption> sizeOptions = new ArrayList<>();

    private ColorAdapter colorAdapter;
    private SizeAdapter sizeAdapter;

    private RecyclerView recyclerView;
    private RecyclerView sizeRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_menu);


        recyclerView = findViewById(R.id.color_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        colorAdapter = new ColorAdapter(this,colorOptionsList);
        recyclerView.setAdapter(colorAdapter);


        sizeRecyclerView = findViewById(R.id.size_recyclerView);
        sizeRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        sizeAdapter = new SizeAdapter(this,sizeOptions);
        sizeRecyclerView.setAdapter(sizeAdapter);


        setupData();


    }
    private void setupData(){
        Handler handler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/products/product/6459ed39b33d8814d8301f2a")
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
                        try {
                            JSONObject jsonResponse = new JSONObject(response.body().string());

                                JSONArray colors = jsonResponse.getJSONArray("colors");
                                for (int i = 0; i <colors.length();i++) {
                                    JSONObject productObject = colors.getJSONObject(i);
                                    String productColorName = productObject.getString("name");
                                    String productColor = productObject.getString("hex");

                                    ColorOption colorOption = new ColorOption(productColorName, productColor);

                                    colorOptionsList.add(colorOption);
                                }
                            colorAdapter.notifyDataSetChanged();

                            JSONArray sizes = jsonResponse.getJSONArray("sizes");
                                for (int i = 0; i <sizes.length();i++) {
                                    JSONObject productObject = sizes.getJSONObject(i);
                                    String productWidth = productObject.getString("widthInCentimeter");
                                    String productHeight = productObject.getString("heightInCentimeter");

                                    SizeOption sizeOption = new SizeOption(productWidth, productHeight);

                                    sizeOptions.add(sizeOption);
                                }
                            sizeAdapter.notifyDataSetChanged();

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
