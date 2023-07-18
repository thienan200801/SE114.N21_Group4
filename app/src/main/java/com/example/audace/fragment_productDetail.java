package com.example.audace;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.audace.adapter.ColorAdapter;
import com.example.audace.adapter.SizeAdapter;
import com.example.audace.model.ColorObject;
import com.example.audace.model.DetailOfItem;
import com.example.audace.model.PurchaseProduct;
import com.example.audace.model.SizeObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_productDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_productDetail extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String productID;
    private TextView nameofproduct, rating, description, colorItem, sizeItem;
    private Handler handler;
    Button orderBtn;

    private RecyclerView rvSize, rvColor;
    ArrayList<SizeObject> sizeObjectList;
    ArrayList<ColorObject> colorObjectList;

    private ImageButton searchButton;
    SizeAdapter sizeAdapter;
    ColorAdapter colorAdapter;

    private String colorId = null;
    private String sizeId = null;
    public fragment_productDetail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_productDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_productDetail newInstance(String param1, String param2) {
        fragment_productDetail fragment = new fragment_productDetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private void renderData() {
        Log.i("message","start crawl information");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        String string = String.format("https://audace-ecomerce.herokuapp.com/products/product/%s",productID);
        Request request = new Request.Builder()
                .url(string)
                .method("GET", null)
                .addHeader("Authorization", "Bearer " +DataStorage.getInstance().getAccessToken())
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

                    sizeObjectList.clear();
                    for(int i = 0; i<sizeJsonArray.length(); i++){
                        String res = sizeJsonArray.getJSONObject(i).getString("widthInCentimeter")
                                + "cm x "
                                + sizeJsonArray.getJSONObject(i).getString("heightInCentimeter")
                                + "cm";
                        String id = sizeJsonArray.getJSONObject(i).getString("_id");
                        SizeObject sizeItemObject = new SizeObject(id, res);
                        sizeObjectList.add(sizeItemObject);
                    }

                    colorObjectList.clear();
                    for(int i = 0; i<colorJsonArray.length(); i++){
                        String id = colorJsonArray.getJSONObject(i).getString("_id");
                        String res = colorJsonArray.getJSONObject(i).getString("hex");
                        String name = colorJsonArray.getJSONObject(i).getString("name");

                        ColorObject colorItemObject = new ColorObject(id, name, res);
                        colorObjectList.add(colorItemObject);
                    }
                    handler.post(()->{
                        if(d.getIsFavourite())
                            ((ImageButton)getActivity().findViewById(R.id.favouriteButton)).setBackgroundResource(R.drawable.baseline_favorite_24);
                        getActivity().findViewById(R.id.favouriteButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                handler.post(() -> {
                                    CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(view.getContext());
                                    circularProgressDrawable.setStrokeWidth(5f);
                                    circularProgressDrawable.setCenterRadius(10f);
                                    circularProgressDrawable.start();
                                    view.setBackground(circularProgressDrawable);
                                });

                                if(!d.getIsFavourite())
                                {
                                    OkHttpClient client = new OkHttpClient().newBuilder()
                                            .build();
                                    MediaType mediaType = MediaType.parse("application/json");
                                    String string = String.format("{\n    \"product\": \"%s\",\n    \"quantity\": 1\n}", DataStorage.getInstance().getProductId());
                                    RequestBody body = RequestBody.create(mediaType, string);
                                    Request request = new Request.Builder()
                                            .url("https://audace-ecomerce.herokuapp.com/users/me/favourites")
                                            .method("POST", body)
                                            .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
                                            .addHeader("Content-Type", "application/json")
                                            .build();
                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Log.i("message", "fail to add");
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            d.setFavourite(true);
                                            handler.post(() ->  view.setBackgroundResource(R.drawable.baseline_favorite_24));
                                            Log.i("message", "Success to add");
                                        }
                                    });
                                }
                                else
                                {
                                    OkHttpClient client = new OkHttpClient().newBuilder()
                                            .build();
                                    MediaType mediaType = MediaType.parse("application/json");
                                    String string = String.format("{\r\n    \"id\": \"%s\"\r\n}", DataStorage.getInstance().getProductId());

                                    RequestBody body = RequestBody.create(mediaType, string);
                                    Request request = new Request.Builder()
                                            .url("https://audace-ecomerce.herokuapp.com/users/me/favourites")
                                            .method("DELETE", body)
                                            .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
                                            .addHeader("Content-Type", "application/json")
                                            .build();
                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Log.i("message", "fail to delete");
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            Log.i("message", "Susccess to delete");
                                            d.setFavourite(false);
                                            handler.post(() -> view.setBackgroundResource(R.drawable.baseline_favorite_border_24));
                                        }
                                    });
                                }
                            }
                        });
                        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(((ImageView)getView().findViewById(R.id.img_detail)).getContext());
                        circularProgressDrawable.setStrokeWidth(5f);
                        circularProgressDrawable.setCenterRadius(30f);
                        circularProgressDrawable.start();
                        try {
                            Picasso.get()
                                    .load(jsonObject.getString("imageURL"))
                                    .placeholder(circularProgressDrawable)
                                    .into(((ImageView)getView().findViewById(R.id.img_detail)));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    if(d != null) {
                        handler.post(() -> {
                            nameofproduct.setText(d.getName());
                            rating.setText(Float.toString(d.getRating()));
                            description.setText(d.getDescription());
                            sizeAdapter.notifyDataSetChanged();
                            colorAdapter.notifyDataSetChanged();
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_product_detail, container, false);
        Log.i("message", "start on create");
        handler = new Handler();
        productID = DataStorage.getInstance().getProductId();
        renderData();

        nameofproduct = (TextView) view.findViewById(R.id.nameofproduct);
        rating = (TextView) view.findViewById(R.id.rating);
        description = view.findViewById(R.id.description);
        orderBtn = view.findViewById(R.id.bt3);

        sizeObjectList = new ArrayList<>();
        rvSize = (RecyclerView) view.findViewById(R.id.sizeRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvSize.setLayoutManager(layoutManager);
        rvSize.setHasFixedSize(false);
        sizeAdapter = new SizeAdapter(sizeObjectList, view.findViewById(R.id.sizeView));
        rvSize.setAdapter(sizeAdapter);
        colorObjectList = new ArrayList<>();

        rvColor = (RecyclerView) view.findViewById(R.id.colorRecycler);
        LinearLayoutManager layoutManagerColor = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvColor.setLayoutManager(layoutManagerColor);
        rvColor.setHasFixedSize(false);
        colorAdapter = new ColorAdapter(colorObjectList, view.findViewById(R.id.colorTextView));
        rvColor.setAdapter(colorAdapter);

        orderBtn=view.findViewById(R.id.bt3);
        orderBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(sizeAdapter.getSelectedItem() == -1 || colorAdapter.getSelectedItem() == -1)
                {
                    Toast.makeText(getContext(), "Hãy chọn kích thước và màu sắc", Toast.LENGTH_SHORT).show();
                    return;
                }

                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                String purchasedProduct = new PurchaseProduct(
                        DataStorage.getInstance().getProductId(),
                        colorObjectList.get(colorAdapter.getSelectedItem()).getColorId(),
                        sizeObjectList.get(sizeAdapter.getSelectedItem()).getSizeId())
                    .toJSON();
                Log.i("product", String.format("{\r\n    \"productCheckoutInfos\": [\r\n%s\r\n    ]\r\n}", purchasedProduct));
                RequestBody body = RequestBody.create(mediaType, String.format("{\r\n    \"productCheckoutInfos\": [\r\n%s\r\n    ]\r\n}", purchasedProduct));
                Request request = new Request.Builder()
                        .url("https://audace-ecomerce.herokuapp.com/users/me/cart")
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
                        .addHeader("Content-Type", "application/json")
                        .build();
                try {
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.i("error", e.toString());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Đã thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                                     getCartCount();
                                }
                            });

                        }
                    });
                } catch (Exception e) {
                    Log.i("error", e.toString());
                }
            }
        });


        ImageButton drawerToggleButton = (ImageButton)view.findViewById(R.id.drawerToggleButton);
        drawerToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerNavigationView = view.getRootView().findViewById(R.id.drawerLayout);
                drawerNavigationView.open();
            }
        });
        ImageButton cartButton = (ImageButton) view.findViewById(R.id.cartButton);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(getActivity(), CartScreen.class);
                startActivity(t);
            }
        });
        searchButton = view.findViewById(R.id.backButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = ((EditText)getActivity().findViewById(R.id.searchEditText)).getText().toString();
                Log.i("search", searchText);
                if(searchText.equals(""))
                {
                    Toast.makeText(getActivity(), "Please insert something", Toast.LENGTH_SHORT).show();
                    return;
                }
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                DataStorage.getInstance().setSearchText(searchText);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.bottomNavigationContainer, new MainFragment());
                fragmentTransaction.replace(R.id.fragmentContainerView, new SearchFragment());
                fragmentTransaction.commit();
            }
        });
        return view;
    }
    private void getCartCount() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                .method("GET", null)
                .addHeader("Authorization", "Bearer "+ DataStorage.getInstance().getAccessToken())
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("message", "Fail to fetch data");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.i("user",jsonObject.getJSONArray("cart").toString());
                    DataStorage.getInstance().setCartCount(jsonObject.getJSONArray("cart").length());

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
}