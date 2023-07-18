package com.example.audace;

import static android.os.Looper.getMainLooper;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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

public class ProductDetailScreen extends BottomSheetDialogFragment implements ColorAdapter.ColorClickListener,SizeAdapter.SizeClickListener{
    private ArrayList<ColorOption> colorOptionsList = new ArrayList<>();
    private ArrayList<SizeOption> sizeOptions = new ArrayList<>();

    private ColorAdapter colorAdapter;
    private SizeAdapter sizeAdapter;
    private TextView nameTextView, descTextView, selectedColorTextView, selectedSizeTextView;
    private RecyclerView recyclerView;
    private RecyclerView sizeRecyclerView;
    private ImageButton editDetail;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_menu, container, false);

        nameTextView = view.findViewById(R.id.product_name);
        descTextView = view.findViewById(R.id.product_description);
        selectedColorTextView = view.findViewById(R.id.selected_color_txt);
        selectedSizeTextView = view.findViewById(R.id.selected_size_txt);

        recyclerView = view.findViewById(R.id.color_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        colorAdapter = new ColorAdapter(this,colorOptionsList);
        colorAdapter.setColorClickListener(this);
        recyclerView.setAdapter(colorAdapter);


        sizeRecyclerView = view.findViewById(R.id.size_recyclerView);
        sizeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        sizeAdapter = new SizeAdapter(this,sizeOptions);
        sizeAdapter.setSizeClickListener(this);
        sizeRecyclerView.setAdapter(sizeAdapter);

        editDetail = view.findViewById(R.id.btnEditProduct);

        editDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedColorText = selectedColorTextView.getText().toString();
                String selectedSizeText = selectedSizeTextView.getText().toString();
                String[] sizeValues = selectedSizeText.split(" x ");
                String sizeWidth = "";
                String sizeHeight = "";
                if (sizeValues.length == 2) {
                    sizeWidth = sizeValues[0].trim();
                    sizeHeight = sizeValues[1].trim();}
                String selectedSizeId = getSizeIdFromDimensions(sizeWidth, sizeHeight);
                String selectedColorId = getColorIdFromName(selectedColorText);
                Bundle arguments = getArguments();
                if (arguments != null) {
                    String productId = arguments.getString("productId");

                    if (productId != null) {
                        updateCartDetails(productId, selectedColorId, selectedSizeId);
                    }
                }



                dismiss();
            }
        });

        Bundle arguments = getArguments();
        if (arguments != null) {
            String productId = arguments.getString("productId");
            String selectedColor = arguments.getString("selectedColor");
            String selectedSize = arguments.getString("selectedSize");
            if (productId != null) {
                setupData(productId,selectedColor,selectedSize);
            }
        }


        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        return view;
    }
    private String getColorIdFromName(String colorName) {
        for (ColorOption colorOption : colorOptionsList) {
            if (colorOption.getName().equals(colorName)) {

                return colorOption.getId();
            }
        }
        return null;
    }
    private String getSizeIdFromDimensions(String sizeWidth, String sizeHeight) {
        for (SizeOption sizeOption : sizeOptions) {
            if (sizeOption.getWidth().equals(sizeWidth) && sizeOption.getHeight().equals(sizeHeight)) {
                Log.i("sizeId", sizeOption.getId());

                return sizeOption.getId();

            }
        }
        return null; // If no matching size is found
    }

    @Override
    public void onColorClicked(ColorOption colorOption) {
        selectedColorTextView.setText(colorOption.getName());

        for (ColorOption option : colorOptionsList) {
            option.setSelected(option == colorOption);
        }

        colorAdapter.notifyDataSetChanged();
    }
    @Override
    public void onSizeClicked(SizeOption sizeOption) {
        selectedSizeTextView.setText(sizeOption.getWidth()+ " x " + sizeOption.getHeight());

        for (SizeOption option : sizeOptions) {
            option.setSelected(option == sizeOption);
        }

        sizeAdapter.notifyDataSetChanged();
    }
    private void setupData(String productId, String selectedColor,String selectedSize){
        Handler handler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/products/product/"+productId)
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

                            String productName = jsonResponse.getString("name");
                            nameTextView.setText(productName);
                            String productDescription = jsonResponse.getString("description");
                            descTextView.setText(productDescription);

                            JSONArray colors = jsonResponse.getJSONArray("colors");
                                for (int i = 0; i <colors.length();i++) {
                                    JSONObject productObject = colors.getJSONObject(i);
                                    String productColorId = productObject.getString("_id");
                                    String productColorName = productObject.getString("name");
                                    String productColor = productObject.getString("hex");

                                    ColorOption colorOption = new ColorOption(productColorId,productColorName, productColor);
                                    colorOptionsList.add(colorOption);

                                    if (productColorId.equals(selectedColor)) {
                                        selectedColorTextView.setText(productColorName);
                                        colorOption.setSelected(true);
                                    }
                                }
                            colorAdapter.notifyDataSetChanged();

                            JSONArray sizes = jsonResponse.getJSONArray("sizes");
                                for (int i = 0; i <sizes.length();i++) {
                                    JSONObject productObject = sizes.getJSONObject(i);
                                    String productSizeId = productObject.getString("_id");
                                    String productWidth = productObject.getString("widthInCentimeter");
                                    String productHeight = productObject.getString("heightInCentimeter");

                                    SizeOption sizeOption = new SizeOption(productSizeId,productWidth, productHeight);

                                    sizeOptions.add(sizeOption);

                                    if (productSizeId.equals(selectedSize)) {
                                        selectedSizeTextView.setText(productWidth+" x "+ productHeight);
                                        sizeOption.setSelected(true);
                                    }
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
    private void updateCartDetails(String productId, String selectedColor, String selectedSize) {
        Handler handler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        // Retrieve the existing productCheckoutInfos array
        Request getRequest = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                .method("GET", null)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                .build();

        client.newCall(getRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("message", call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray productCheckoutInfosArray = jsonResponse.getJSONArray("cart");

                        // Find the index of the item to be updated
                        int itemIndex = -1;
                        for (int i = 0; i < productCheckoutInfosArray.length(); i++) {
                            JSONObject itemObject = productCheckoutInfosArray.getJSONObject(i);
                            String existingProductId = itemObject.getString("product");
                            if (existingProductId.equals(productId)) {
                                itemIndex = i;
                                break;
                            }
                        }

                        // Update the specific item or add a new item if not found
                        JSONObject productCheckoutInfoObject = new JSONObject();
                        productCheckoutInfoObject.put("product", productId);
                        productCheckoutInfoObject.put("color", selectedColor);
                        productCheckoutInfoObject.put("size", selectedSize);
                        productCheckoutInfoObject.put("quantity", 2);

                        if (itemIndex != -1) {
                            // Replace the existing item
                            productCheckoutInfosArray.put(itemIndex, productCheckoutInfoObject);
                        } else {
                            // Add the new item to the array
                            productCheckoutInfosArray.put(productCheckoutInfoObject);
                        }

                        // Construct the updated request body
                        JSONObject requestBody = new JSONObject();
                        requestBody.put("productCheckoutInfos", productCheckoutInfosArray);

                        // Send the updated request
                        MediaType mediaType = MediaType.parse("application/json");
                        RequestBody body = RequestBody.create(mediaType, requestBody.toString());
                        Request updateRequest = new Request.Builder()
                                .url("https://audace-ecomerce.herokuapp.com/users/me/cart")
                                .method("PATCH", body)
                                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                                .addHeader("Content-Type", "application/json")
                                .build();

                        client.newCall(updateRequest).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.i("message", call.toString());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (response.isSuccessful()) {
                                            Log.i("update", "Update product");
                                        } else {
                                            Log.i("error", body.toString());
                                        }
                                    }
                                });
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateCartDetails1(String productId, String selectedColor, String selectedSize) {
        JSONObject requestBody = new JSONObject();
        try {

            JSONArray productCheckoutInfosArray = new JSONArray();
            JSONObject productCheckoutInfoObject = new JSONObject();
            productCheckoutInfoObject.put("product", productId);
            productCheckoutInfoObject.put("color", selectedColor);
            productCheckoutInfoObject.put("size", selectedSize);
            productCheckoutInfoObject.put("quantity", 2);
            productCheckoutInfosArray.put(productCheckoutInfoObject);
            requestBody.put("productCheckoutInfos", productCheckoutInfosArray);
            Log.i("execute","ok");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Handler handler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestBody.toString());
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/cart")
                .method("PATCH", body)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                .addHeader("Content-Type", "application/json")
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
                        if (response.isSuccessful()) {

                            Log.i("update", "Update product");

                        }
                        else {
                            Log.i("error",body.toString());
                        }
                    }
                });
            }
        });
    }

}
