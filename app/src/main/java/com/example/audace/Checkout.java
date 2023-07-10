package com.example.audace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.audace.adapter.CheckoutItemDetailAdapter;
import com.example.audace.adapter.ColorAdapter;
import com.example.audace.adapter.NamePriceOfItemAdapter;
import com.example.audace.adapter.SizeAdapter;
import com.example.audace.model.CheckoutItemDetails;
import com.example.audace.model.ColorObject;
import com.example.audace.model.DetailOfItem;
import com.example.audace.model.NamePrice;
import com.example.audace.model.SizeObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Checkout extends AppCompatActivity {
    private TextView nameofproduct, price, size, quantity, color, imgUrl, total;
    private Handler handler;
    private EditText addressEditText;
    Button checkoutBtn, changeAddressBtn;

    private RecyclerView rvCheckoutItemDetails;
    private ListView listOfCheckoutItemNamePrice;
    NamePriceOfItemAdapter namepriceAdapter;
    private NamePrice[] namepriceList;
    ArrayList<CheckoutItemDetails> checkoutItemDetailsArrayList;
    CheckoutItemDetailAdapter checkoutItemDetailAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        listOfCheckoutItemNamePrice=findViewById(R.id.rvSumListItem);
        initNamePriceData();
        namepriceAdapter=new NamePriceOfItemAdapter(this, namepriceList);
        listOfCheckoutItemNamePrice.setAdapter(namepriceAdapter);

        addressEditText = findViewById(R.id.addressEditText);
        handler = new Handler();
        //Call API to get Item list
        OkHttpClient clientItemCart = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaTypeItemCart = MediaType.parse("text/plain");
        RequestBody bodyItemCart = RequestBody.create(mediaTypeItemCart, "");
        Request requestItemCart = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                .method("GET", null)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODIwMDgxNjl9.Cgn99CpH3ypDpTVm_Fh_E1nn2anvJWZAnZHS4Qkwnn4")
                .build();
        clientItemCart.newCall(requestItemCart).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("failure", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Log.i("message","Call API get Item Cart successful");
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    List<String> itemOrderList= new ArrayList<>();
                    JSONArray jArray = jsonObject.getJSONArray("orders");
                    if (jArray != null) {
                        for (int i=0;i<jArray.length();i++){
                            itemOrderList.add(jArray.getString(i));
                        }
                    }
                    for(int i=0; i<itemOrderList.size(); i++){
                        OkHttpClient clientDetailOfEachItem = new OkHttpClient().newBuilder()
                                .build();
                        MediaType mediaType = MediaType.parse("text/plain");
                        Request requestDetailOfEachItem = new Request.Builder()
                                .url("https://audace-ecomerce.herokuapp.com/products/product/6459ed39b33d8814d8301f2a")
                                .method("GET", null)
                                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                                .build();
                        clientDetailOfEachItem.newCall(requestDetailOfEachItem).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.i("failure", e.toString());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    Log.i("message","Call API successful");
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    DetailOfItem d = new DetailOfItem();
                                    d.setName(jsonObject.getString("name"));
                                    d.setRating(Float.parseFloat(jsonObject.getString("rating")));
                                    d.setDescription(jsonObject.getString("description"));
                                    d.setFavourite(jsonObject.getBoolean("isFavourite"));
                                    JSONArray sizeJsonArray = jsonObject.getJSONArray("sizes");
                                    JSONArray colorJsonArray = jsonObject.getJSONArray("colors");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //Call API to get Item details
        Log.i("message","start crawl information");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/products/product/6459ed39b33d8814d8301f2a")
                .method("GET", null)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("failure", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Log.i("message","Call API successful");
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    DetailOfItem d = new DetailOfItem();
                    d.setName(jsonObject.getString("name"));
                    d.setRating(Float.parseFloat(jsonObject.getString("rating")));
                    d.setDescription(jsonObject.getString("description"));
                    d.setFavourite(jsonObject.getBoolean("isFavourite"));
                    JSONArray sizeJsonArray = jsonObject.getJSONArray("sizes");
                    JSONArray colorJsonArray = jsonObject.getJSONArray("colors");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        ///////////////////////////////////////
        //Call API to get address
        Log.i("message","start crawl user address");
        OkHttpClient client2 = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType2 = MediaType.parse("text/plain");
        Request request2 = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                .method("GET", null)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                .build();
        client.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("failure", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String address;
                try {
                    Log.i("message","Call API User address successful");
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    address = jsonObject.getString("address");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                if(address != null) {
                    handler.post(() -> {
                        addressEditText.setText(address);
                    });
                }
            }
        });

        //////////////////////////////////////

        ///////////////////////////////////////
        //Post Address API
        changeAddressBtn = (Button) findViewById(R.id.changeAddressBtn);
        changeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postAddress();
            }

            private void postAddress() {
                Log.i("message","start crawl user address");
                OkHttpClient client3 = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType3 = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"address\": \"" + addressEditText.getText().toString() +"\"\r\n}");
                Request request3 = new Request.Builder()
                        .url("https://audace-ecomerce.herokuapp.com/users/me/address")
                        .method("PATCH", body)
                        .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODIwMDgxNjl9.Cgn99CpH3ypDpTVm_Fh_E1nn2anvJWZAnZHS4Qkwnn4")
                        .addHeader("Content-Type", "application/json")
                        .build();
                client.newCall(request3).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("failure", e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("message", "Address Changed");
                    }
                });
            }
        });

        checkoutItemDetailsArrayList = new ArrayList<>();
        checkoutItemDetailsArrayList.add(new CheckoutItemDetails("Túi Gucci 100% da cái sấu thật, nhập khẩu nguyên mẫu từ Ý, c...", "50cmx60cm", "Đen", "$2000", "1", "https://i.ibb.co/HnV0rm3/Rectangle-10.png"));

        rvCheckoutItemDetails = (RecyclerView) findViewById(R.id.rvCheckoutList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCheckoutItemDetails.setLayoutManager(layoutManager);
        rvCheckoutItemDetails.setHasFixedSize(false);
        checkoutItemDetailAdapter = new CheckoutItemDetailAdapter(checkoutItemDetailsArrayList);
        rvCheckoutItemDetails.setAdapter(checkoutItemDetailAdapter);


        //Total
        total = findViewById(R.id.textView12);

    }

    private void initNamePriceData() {
        String[] names = {"Gucci bag", "Dior bag"};
        String[] prices = {"$2000", "$1800"};
        namepriceList=new NamePrice[names.length];
        for(int i=0; i<names.length; i++){
            namepriceList[i]=new NamePrice(names[i], prices[i]);
        }
    }
}