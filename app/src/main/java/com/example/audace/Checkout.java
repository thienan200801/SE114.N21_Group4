package com.example.audace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
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
import com.example.audace.model.PurchaseProduct;
import com.example.audace.model.SizeObject;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

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
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class Checkout extends AppCompatActivity {
    private static final int REQUEST_CODE_GPS_PERMISSION = 100;

    private TextView nameofproduct, price, size, quantity, color, imgUrl, total;
    private Handler handler = new Handler(Looper.getMainLooper());
    private EditText addressEditText;
    Button checkoutBtn, changeAddressBtn;
    private String cart;

    private String address = null;

    private RecyclerView rvCheckoutItemDetails;
    private ListView listOfCheckoutItemNamePrice;
    NamePriceOfItemAdapter namepriceAdapter;
    private ArrayList<NamePrice> namepriceList = new ArrayList<NamePrice>();
    ArrayList<CheckoutItemDetails> checkoutItemDetailsArrayList = new ArrayList<CheckoutItemDetails>();
    CheckoutItemDetailAdapter checkoutItemDetailAdapter;
    private GoogleMap mMap;
    private final static int PLACE_PICKER_REQUEST = 999;
    private final static int LOCATION_REQUEST_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        rvCheckoutItemDetails = (RecyclerView) findViewById(R.id.rvCheckoutList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        addressEditText = findViewById(R.id.addressEditText);
        rvCheckoutItemDetails.setLayoutManager(layoutManager);
        rvCheckoutItemDetails.setHasFixedSize(false);
        checkoutItemDetailAdapter = new CheckoutItemDetailAdapter(checkoutItemDetailsArrayList);
        rvCheckoutItemDetails.setAdapter(checkoutItemDetailAdapter);
        listOfCheckoutItemNamePrice = findViewById(R.id.rvSumListItem);
        namepriceAdapter = new NamePriceOfItemAdapter(this, namepriceList);
        listOfCheckoutItemNamePrice.setAdapter(namepriceAdapter);
        GetCart();
        //Call API to get Item list

        ///////////////////////////////////////
        //Call API to get address
        getAddress();
        ///////////////////////////////////////
        //Post Address API
        changeAddressBtn = (Button) findViewById(R.id.changeAddressBtn);
        changeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MapActivity.class);
                startActivityForResult(i, 1);
            }
        });
        findViewById(R.id.thanhToanButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (address == null)
                    Toast.makeText(Checkout.this, "Hãy chọn địa chỉ gửi hàng", Toast.LENGTH_SHORT).show();
                else
                    thanhToanHandler();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == 1 && resultCode == 1){
            getAddress();
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        getAddress();
    }

    private void initNamePriceData() {
        namepriceList.clear();
        if (checkoutItemDetailsArrayList != null) {
            for (int i = 0; i < checkoutItemDetailsArrayList.size(); i++)
                namepriceList.add(new NamePrice(checkoutItemDetailsArrayList.get(i).getName(),
                        checkoutItemDetailsArrayList.get(i).getPrice(),
                        Integer.parseInt(checkoutItemDetailsArrayList.get(i).getQuantity())));
        }
        handler.post(() -> {
            namepriceAdapter.notifyDataSetChanged();
        });
    }

    private void GetCart() {
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
                    Log.i("message", "Call API get Item Cart successful");
                    JSONArray cartResponse = new JSONObject(response.body().string()).getJSONArray("cart");
                    Log.i("response", cartResponse.toString());
                    cart = cartResponse.toString();
                    checkoutItemDetailsArrayList.clear();
                    for (int i = 0; i < cartResponse.length(); i++) {
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
                                try {
                                    JSONObject jsonResponse = new JSONObject(response.body().string());

                                    JSONArray colors = jsonResponse.getJSONArray("colors");
                                    for (int i = 0; i < colors.length(); i++)
                                        if (Objects.equals(colors.getJSONObject(i).getString("_id"), colorId)) {
                                            JSONObject productObject = colors.getJSONObject(i);
                                            String productColorName = productObject.getString("name");
                                            String productColor = productObject.getString("hex");
                                            color[0] = new ColorObject(colorId, productColorName, productColor);
                                            break;
                                        }
                                    JSONArray sizes = jsonResponse.getJSONArray("sizes");
                                    for (int i = 0; i < sizes.length(); i++)
                                        if (sizes.getJSONObject(i).getString("_id").equals(sizeId)) {
                                            JSONObject productObject = sizes.getJSONObject(i);
                                            String productWidth = productObject.getString("widthInCentimeter");
                                            String productHeight = productObject.getString("heightInCentimeter");
                                            size[0] = new SizeObject(sizeId, productWidth + "cm x" + productHeight + "cm");
                                        }
                                    checkoutItemDetailsArrayList.add(new CheckoutItemDetails(
                                            jsonResponse.getString("name"),
                                            size[0],
                                            color[0],
                                            jsonResponse.getString("currentPrice"),
                                            cartResponse.getJSONObject(finalI).getString("quantity"),
                                            jsonResponse.getString("imageURL")));
                                    //Total
                                    int sum = 0;
                                    for (int i = 0; i < checkoutItemDetailsArrayList.size(); i++)
                                        sum += Float.parseFloat(checkoutItemDetailsArrayList.get(i).getPrice()) * Integer.parseInt(checkoutItemDetailsArrayList.get(i).getQuantity());
                                    int finalSum = sum;
                                    handler.post(() -> {
                                        initNamePriceData();
                                        checkoutItemDetailAdapter.notifyDataSetChanged();
                                        total = findViewById(R.id.textView12);
                                        total.setText("Tổng tiền: $" + finalSum);
                                    });
                                } catch (Exception e) {
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

    private void getAddress() {
        Log.i("message", "start crawl user address");
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
                String address1;
                address1 = null;
                try {
                    Log.i("message", "Call API User address successful");
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    address1 = jsonObject.getString("address");
                } catch (JSONException e) {
                    Log.i("error", e.toString());
                }
                if (address1 != null) {
                    String finalAddress = address1;
                    address = address1;
                    handler.post(() -> {
                        addressEditText.setText(finalAddress);
                    });
                } else {
                    handler.post(() -> {
                        addressEditText.setHint("Chưa có address");
                    });
                }
            }
        });
    }

    public void thanhToanHandler() {
        findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,  String.format("{\"productCheckoutInfos\": %s}", cart));
        Log.i("request",  String.format("{\"productCheckoutInfos\": %s}", cart));
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/orders")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("error", e.toString());
                    Intent i = new Intent(getBaseContext(), CheckOutFailActivity.class);
                    startActivity(i);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                 DataStorage.getInstance().setCartCount(new JSONObject(response.body().string()).getJSONArray("productCheckoutInfos").length());
                            } catch (IOException e) {
                                Log.i("error", e.toString());
                            } catch (JSONException e) {
                                Log.i("error", e.toString());
                            }
                            Intent i = new Intent(getBaseContext(), CheckOutSuccessActivity.class);
                            startActivity(i);
                            getFragmentManager().popBackStack();
                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.i("error", e.toString());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

}