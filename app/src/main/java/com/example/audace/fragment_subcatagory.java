package com.example.audace;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
 * Use the {@link fragment_subcatagory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_subcatagory extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Catagory> catagories;

    private ArrayList<Catagory> subCatagories;

    private ArrayList<Product> products;

    private CatagoryListAdapter catagoryListAdapter;
    private CatagoryListAdapter subCatagoryListAdapter;
    private ProductListAdapter productListAdapter;
    private Fragment fragment;

    public fragment_subcatagory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_subcatagory.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_subcatagory newInstance(String param1, String param2) {
        fragment_subcatagory fragment = new fragment_subcatagory();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("message", "create fragment");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment = this;
        // Inflate the layout for this fragment
        View  view = inflater.inflate(R.layout.fragment_subcatagory, container, false);
        catagories = new ArrayList<>();
        CrawlCatagory();
        catagoryListAdapter = new CatagoryListAdapter(catagories);
        LinearLayoutManager catagoryManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView catagoryRecycleView =  (RecyclerView) view.findViewById(R.id.catagoryRecyclerView);
        catagoryRecycleView.setLayoutManager(catagoryManager);
        catagoryRecycleView.setAdapter(catagoryListAdapter);
        subCatagories = new ArrayList<>();
        subCatagoryListAdapter = new CatagoryListAdapter(subCatagories);
        LinearLayoutManager subCatagoryManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView subCatagoryRecyclerView = (RecyclerView) view.findViewById(R.id.subCatagoryRecyclerView);
        subCatagoryRecyclerView.setLayoutManager(subCatagoryManager);
        subCatagoryRecyclerView.setAdapter(subCatagoryListAdapter);
        DividerItemDecoration subCatagoryDividerItemDecoration = new DividerItemDecoration(subCatagoryRecyclerView.getContext(), subCatagoryManager.getOrientation());
        subCatagoryDividerItemDecoration.setDrawable(ContextCompat.getDrawable(subCatagoryRecyclerView.getContext(), R.drawable.recycler1_view_seperator));
        subCatagoryRecyclerView.addItemDecoration(subCatagoryDividerItemDecoration);
        subCatagoryListAdapter.notifyDataSetChanged();

        RecyclerView gridView = (RecyclerView) view.findViewById(R.id.itemGridView);
        products = new ArrayList<>();
        CrawlProduct();
        productListAdapter = new ProductListAdapter(products);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(gridView.getContext(), 2);
        gridView.setAdapter(productListAdapter);
        gridView.setLayoutManager(gridLayoutManager);
        return view;
    }
    public void CrawlCatagory(){
        Handler handler = new Handler(getContext().getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/categories?withImage=false")
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
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Catagory item = new Catagory(jsonObject.get("_id").toString(), jsonObject.get("name").toString(), jsonObject.get("imageURL").toString());
                                catagories.add(item);
                            }
                            catch(JSONException e)
                            {
                                Log.i("exception", e.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = jsonArray.getJSONObject(i);
                                } catch (JSONException ex) {
                                    throw new RuntimeException(ex);
                                }
                                Catagory item = null;
                                try {
                                    item = new Catagory(jsonObject.get("_id").toString(), jsonObject.get("name").toString(), "");
                                } catch (JSONException ex) {
                                    throw new RuntimeException(ex);
                                }
                                catagories.add(item);
                            }
                        }
                        catagoryListAdapter = new CatagoryListAdapter(catagories);
                        RecyclerView catagoryRecycleView =  (RecyclerView) fragment.getView().findViewById(R.id.catagoryRecyclerView);
                        catagoryRecycleView.setAdapter(catagoryListAdapter);
                        catagoryListAdapter.notifyItemRangeInserted(0, products.size());
                    }
                });
            }
        });
    }
    public void CrawlProduct(){
        Handler handler = new Handler(getContext().getMainLooper());
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
                Log.i("message", call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    products.clear();
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for(int i = 0; i < jsonArray.length(); i ++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Product item = new Product(jsonObject.getString("_id"),jsonObject.getString("name"), "00000", jsonObject.getBoolean("isFavourite"),jsonObject.getString("imageURL") );
                        products.add(item);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            productListAdapter.notifyItemRangeInserted(0, products.size());
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
