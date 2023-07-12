package com.example.audace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
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
    private ArrayList<NamePrice> namepriceList;
    ArrayList<CheckoutItemDetails> checkoutItemDetailsArrayList;
    CheckoutItemDetailAdapter checkoutItemDetailAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        handler = new Handler(Looper.getMainLooper());
        addressEditText = findViewById(R.id.addressEditText);
        checkoutItemDetailsArrayList = new ArrayList<>();
        rvCheckoutItemDetails = (RecyclerView) findViewById(R.id.rvCheckoutList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCheckoutItemDetails.setLayoutManager(layoutManager);
        rvCheckoutItemDetails.setHasFixedSize(false);
        checkoutItemDetailAdapter = new CheckoutItemDetailAdapter(checkoutItemDetailsArrayList);
        rvCheckoutItemDetails.setAdapter(checkoutItemDetailAdapter);
        GetCart();
        listOfCheckoutItemNamePrice=findViewById(R.id.rvSumListItem);
        namepriceList =  initNamePriceData();
        namepriceAdapter=new NamePriceOfItemAdapter(this, namepriceList);
        listOfCheckoutItemNamePrice.setAdapter(namepriceAdapter);
        //Call API to get Item list

        ///////////////////////////////////////
        //Call API to get address
        Log.i("message","start crawl user address");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("failure", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String address;
                address = null;
                try {
                    Log.i("message","Call API User address successful");
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    address = jsonObject.getString("address");
                } catch (JSONException e) {
                    Log.i("error", e.toString());
                }
                if(address != null) {
                    String finalAddress = address;
                    handler.post(() -> {
                        addressEditText.setText(finalAddress);
                    });
                }
                else{
                    handler.post(() -> {
                        addressEditText.setText("");
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
                        .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
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


        //Total
        total = findViewById(R.id.textView12);

    }

    private ArrayList<NamePrice> initNamePriceData() {
        ArrayList<NamePrice> result = new ArrayList<NamePrice>();
        if(checkoutItemDetailsArrayList != null){
            for(int i = 0; i < checkoutItemDetailsArrayList.size(); i++)
                result.add(new NamePrice(checkoutItemDetailsArrayList.get(i).getName(), checkoutItemDetailsArrayList.get(i).getPrice()));
        }
        else
            return null;
        return result;
    }
    private void GetCart()
    {
        checkoutItemDetailsArrayList.clear();
        OkHttpClient clientItemCart = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaTypeItemCart = MediaType.parse("text/plain");
        Request requestItemCart = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
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
                    JSONArray cartResponse = new JSONObject(response.body().string()).getJSONArray("cart");
                    Log.i("response", cartResponse.toString());
                    for(int i=0; i<cartResponse.length(); i++){
                        String colorId = cartResponse.getJSONObject(i).getString("color");
                        String sizeId = cartResponse.getJSONObject(i).getString("size");
                        final ColorObject[] color = new ColorObject[1];
                        final SizeObject[] size = new SizeObject[1];
                        OkHttpClient clientDetailOfEachItem = new OkHttpClient().newBuilder()
                                .build();
                        MediaType mediaType = MediaType.parse("text/plain");
                        Request requestDetailOfEachItem = new Request.Builder()
                                .url("https://audace-ecomerce.herokuapp.com/products/product/" + cartResponse.getJSONObject(i).getString("product"))
                                .method("GET", null)
                                .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
                                .build();
                        int finalI = i;
                        clientDetailOfEachItem.newCall(requestDetailOfEachItem).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.i("failure", e.toString());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try{
                                    JSONObject jsonResponse = new JSONObject(response.body().string());

                                    JSONArray colors = jsonResponse.getJSONArray("colors");
                                    for (int i = 0; i <colors.length();i++)
                                        if(Objects.equals(colors.getJSONObject(i).getString("_id"), colorId))
                                        {
                                            JSONObject productObject = colors.getJSONObject(i);
                                            String productColorName = productObject.getString("name");
                                            String productColor = productObject.getString("hex");
                                            color[0] = new ColorObject(colorId, productColorName, productColor);
                                            break;
                                        }
                                    JSONArray sizes = jsonResponse.getJSONArray("sizes");
                                    for (int i = 0; i <sizes.length();i++)
                                        if(sizes.getJSONObject(i).getString("_id").equals(sizeId))
                                        {
                                            JSONObject productObject = sizes.getJSONObject(i);
                                            String productWidth = productObject.getString("widthInCentimeter");
                                            String productHeight = productObject.getString("heightInCentimeter");
                                            size[0] = new SizeObject(sizeId, productWidth + "x" + productHeight);
                                        }
                                    checkoutItemDetailsArrayList.add(new CheckoutItemDetails(
                                            cartResponse.getJSONObject(finalI).getString("product"),
                                            size[0],
                                            color[0],
                                            jsonResponse.getString("currentPrice"),
                                            cartResponse.getJSONObject(finalI).getString("quantity"),
                                            jsonResponse.getString("imageURL")));
                                    handler.post(() -> {
                                        checkoutItemDetailAdapter.notifyItemInserted(checkoutItemDetailsArrayList.size() - 1);
                                    });
                                }
                                catch(Exception e){
                                    Log.i("error", e.toString());
                                }
                            }
                        });
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public Bitmap GetImageFromURL(String url){
        HttpUrl URL = HttpUrl.parse(url).newBuilder().build();
        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = new OkHttpClient();
        final Bitmap[] result = {};
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("message", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    try{
                        result[0] = BitmapFactory.decodeStream(response.body().byteStream());
                    }
                    catch (Exception e)
                    {
                        Log.i("error", "Cannot fetch image from url: "+ url);
                    }
                }
            }
        });
        return result[0];
    }
}