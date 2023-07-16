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

public class VoucherScreen extends AppCompatActivity {

    private ArrayList<Voucher> vchList = new ArrayList<>();
    private VoucherAdapter voucherAdapter;


    private RecyclerView vchRV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voucher);


        vchRV = findViewById(R.id.voucherRecyclerView);
        vchRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        voucherAdapter = new VoucherAdapter(this,vchList);
        setupData();
        vchRV.setAdapter(voucherAdapter);

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
                .url("https://audace-ecomerce.herokuapp.com/vouchers")
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
                            JSONArray jsonResponse = new JSONArray(response.body().string());
                            for (int i = 0; i < jsonResponse.length(); i++) {
                                JSONObject product = jsonResponse.getJSONObject(i);
                                String productId = product.getString("_id");
                                String saleOff = product.getString("name");
                                String code = product.getString("code");
                                String startDate = product.getString("start");
                                String endDate = product.getString("end");



                                Voucher voucherProduct = new Voucher(productId,saleOff,code,startDate,endDate,saleOff);

                                vchList.add(voucherProduct);
                            }
                            voucherAdapter.notifyDataSetChanged();
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