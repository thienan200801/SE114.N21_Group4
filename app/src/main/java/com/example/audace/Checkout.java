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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        addressEditText = findViewById(R.id.addressEditText);
        rvCheckoutItemDetails = (RecyclerView) findViewById(R.id.rvCheckoutList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCheckoutItemDetails.setLayoutManager(layoutManager);
        rvCheckoutItemDetails.setHasFixedSize(false);
        checkoutItemDetailAdapter = new CheckoutItemDetailAdapter(checkoutItemDetailsArrayList);
        rvCheckoutItemDetails.setAdapter(checkoutItemDetailAdapter);
        GetCart();
        listOfCheckoutItemNamePrice = findViewById(R.id.rvSumListItem);
        namepriceAdapter = new NamePriceOfItemAdapter(this, namepriceList);
        listOfCheckoutItemNamePrice.setAdapter(namepriceAdapter);
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
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

                try {
                    startActivity(intentBuilder.build(Checkout.this));
                } catch (GooglePlayServicesRepairableException |
                         GooglePlayServicesNotAvailableException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        findViewById(R.id.thanhToanButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (address == null)
                    Toast.makeText(Checkout.this, "Hãy nhập address và nhấn thay đổi", Toast.LENGTH_SHORT).show();
                else
                    thanhToanHandler();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                StringBuilder stringBuilder = new StringBuilder();
                addressEditText.setText(place.getAddress());
                postAddress();
            }

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
                    cart = cartResponse.toString();
                    Log.i("response", cartResponse.toString());
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
                                    initNamePriceData();
                                    //Total
                                    int sum = 0;
                                    for (int i = 0; i < checkoutItemDetailsArrayList.size(); i++)
                                        sum += Float.parseFloat(checkoutItemDetailsArrayList.get(i).getPrice()) * Integer.parseInt(checkoutItemDetailsArrayList.get(i).getQuantity());
                                    int finalSum = sum;
                                    handler.post(() -> {
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
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = new FormBody.Builder().add("productCheckoutInfos", cart).build();
        Log.i("body", body.toString());
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/orders")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
                .addHeader("Content-Type", "application/json")
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

    private void postAddress() {
        Log.i("message", "start crawl user address");
        OkHttpClient changeAddressClient = new OkHttpClient().newBuilder()
                .build();
        MediaType changeAddressMediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(changeAddressMediaType, "{\r\n    \"address\": \"" + addressEditText.getText().toString() + "\"\r\n}");
        Request changeAddressRequest = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/address")
                .method("PATCH", body)
                .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
                .addHeader("Content-Type", "application/json")
                .build();
        changeAddressClient.newCall(changeAddressRequest).enqueue(new Callback() {
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
    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //TODO: Get current location
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_GPS_PERMISSION);
        }
    }
    private void getCurrentLocation() {
        FusedLocationProviderClient mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            return;
                        }
                        LatLng currentLocation =
                                new LatLng(location.getLatitude(), location.getLongitude());
                        GoogleMap mMap = null;
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker in current location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    }
                });
    }
}