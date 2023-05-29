package com.example.audace;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home extends Fragment {
    ArrayList<Catagory> catagories;
    ArrayList<Banner> banners;

    ArrayList<Product> products;
    CatagoryListAdapter catagoryListAdapter;
    BannerListAdapter bannerListAdapter;

    ProductListAdapter productListAdapter;

    ArrayList<Product> saleProducts;

    ProductListAdapter saleProductListAdapter;
    Fragment fragment;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home.
     */
    // TODO: Rename and change types and number of parameters
    public static home newInstance(String param1, String param2) {
        home fragment = new home();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment = this;
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        catagories = new ArrayList<Catagory>();
        CrawlCatagory();
        catagoryListAdapter = new CatagoryListAdapter(catagories);
        LinearLayoutManager catagoryManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView catagoryRecycleView = (RecyclerView) view.findViewById(R.id.catagoryListView);
        catagoryRecycleView.setLayoutManager(catagoryManager);
        catagoryRecycleView.setAdapter(catagoryListAdapter);
        DividerItemDecoration catagoryDividerItemDecoration = new DividerItemDecoration(catagoryRecycleView.getContext(), catagoryManager.getOrientation());
        catagoryDividerItemDecoration.setDrawable(ContextCompat.getDrawable(catagoryRecycleView.getContext(), R.drawable.recycler1_view_seperator));
        catagoryRecycleView.addItemDecoration(catagoryDividerItemDecoration);

        banners = new ArrayList<>();
        CrawlBanner();
        bannerListAdapter = new BannerListAdapter(banners);
        LinearLayoutManager bannerManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView bannerRecycleView = (RecyclerView) view.findViewById(R.id.BannerRecyclerView);
        bannerRecycleView.setLayoutManager(bannerManager);
        bannerRecycleView.setAdapter(bannerListAdapter);

        DividerItemDecoration bannerDividerItemDecoration = new DividerItemDecoration(bannerRecycleView.getContext(), bannerManager.getOrientation());
        bannerDividerItemDecoration.setDrawable(ContextCompat.getDrawable(bannerRecycleView.getContext(), R.drawable.recycler1_view_seperator));
        bannerRecycleView.addItemDecoration(bannerDividerItemDecoration);

        products = new ArrayList<Product>();
        CrawlBanChayProduct();
        productListAdapter = new ProductListAdapter(products);

        LinearLayoutManager productListManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView banChayRecycleView = (RecyclerView) view.findViewById(R.id.banChayItemList);

        banChayRecycleView.setLayoutManager(productListManager);
        banChayRecycleView.setAdapter(productListAdapter);
        DividerItemDecoration productDividerItemDecoration = new DividerItemDecoration(banChayRecycleView.getContext(), productListManager.getOrientation());
        productDividerItemDecoration.setDrawable(ContextCompat.getDrawable(banChayRecycleView.getContext(), R.drawable.recycler1_view_seperator));

        banChayRecycleView.addItemDecoration(productDividerItemDecoration);

        saleProducts = new ArrayList<Product>();
        CrawlSaleOffProduct();
        saleProductListAdapter = new ProductListAdapter(saleProducts);
        RecyclerView saleOffRecycleView = (RecyclerView) view.findViewById(R.id.saleOffItemList);
        LinearLayoutManager saleOffLinearLayout = new LinearLayoutManager(saleOffRecycleView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        saleOffRecycleView.setAdapter(saleProductListAdapter);

        saleOffRecycleView.setLayoutManager(saleOffLinearLayout);
        saleOffRecycleView.addItemDecoration(productDividerItemDecoration);

        View bstButton = view.findViewById(R.id.bst_more_button);
        bstButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_home_to_fragment_subcatagory);
            }
        });
        return view;
    }
    public void CrawlCatagory(){
        Handler handler = new Handler(getContext().getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/categories?withImage=true")
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
                        catagories.clear();
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response.body().string());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        for(int i = 0; i < jsonArray.length(); i ++)
                        {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = jsonArray.getJSONObject(i);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            Catagory item = null;
                            try {
                                item = new Catagory(jsonObject.get("_id").toString(), jsonObject.get("name").toString(), jsonObject.get("imageURL").toString());
                            } catch (JSONException e) {
                            }
                            catagories.add(item);
                        }
                        catagoryListAdapter = new CatagoryListAdapter(catagories);
                        RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(R.id.catagoryListView);
                        recyclerView.setAdapter(catagoryListAdapter);
                        catagoryListAdapter.notifyItemRangeInserted(0, products.size());
                    }
                });
            }
        });
    }
    public void CrawlBanner(){
        Handler handler = new Handler(getContext().getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/collections")
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
                        catagories.clear();
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response.body().string());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        for(int i = 0; i < jsonArray.length(); i ++)
                        {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = jsonArray.getJSONObject(i);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            Banner item = null;
                            try {
                                item = new Banner(jsonObject.get("_id").toString(), jsonObject.get("imageURL").toString());
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            banners.add(item);
                        }
                        bannerListAdapter = new BannerListAdapter(banners);
                        RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(R.id.BannerRecyclerView);
                        recyclerView.setAdapter(bannerListAdapter);
                        bannerListAdapter.notifyItemRangeInserted(0, products.size());
                    }
                });
            }
        });
    }
    public void CrawlBanChayProduct(){
        Handler handler = new Handler(getContext().getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/products/best-sellers?page=0")
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
                        products.clear();
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response.body().string());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        for(int i = 0; i < jsonArray.length(); i ++)
                        {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = jsonArray.getJSONObject(i);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            Product item = null;
                            try {
                                item = new Product(jsonObject.getString("_id"),jsonObject.getString("name"), "00000", jsonObject.getBoolean("isFavourite"),jsonObject.getString("imageURL") );
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            products.add(item);
                        }
                        productListAdapter = new ProductListAdapter(products);
                        RecyclerView banChayRecycleView = (RecyclerView) fragment.getView().findViewById(R.id.banChayItemList);
                        banChayRecycleView.setAdapter(productListAdapter);
                        productListAdapter.notifyItemRangeInserted(0, products.size());
                    }
                });
            }
        });
    }
    public void CrawlSaleOffProduct(){
        Handler handler = new Handler();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/products/best-sale-off?page=0")
                .method("GET", null)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    saleProducts.clear();
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for(int i = 0; i < jsonArray.length(); i ++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Product item = new Product(jsonObject.getString("_id"),jsonObject.getString("name"), "00000", jsonObject.getBoolean("isFavourite"),jsonObject.getString("imageURL") );
                        saleProducts.add(item);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            saleProductListAdapter.notifyDataSetChanged();

                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

