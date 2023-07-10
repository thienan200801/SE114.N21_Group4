package com.example.audace;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.audace.adapter.CatagoryListAdapter;
import com.example.audace.adapter.ProductListAdapter;
import com.example.audace.model.Catagory;
import com.example.audace.model.Product;

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

    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager catagoryManager;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment = this;
        catagories = new ArrayList<>();
        Log.i("message", DataStorage.getInstance().getCatagoryArrayList().toString());
        products = new ArrayList<>();
        // Inflate the layout for this fragment
        View  view = inflater.inflate(R.layout.fragment_subcatagory, container, false);
        RecyclerView gridView = (RecyclerView) view.findViewById(R.id.itemGridView);
        productListAdapter = new ProductListAdapter(products);
        gridLayoutManager = new GridLayoutManager(gridView.getContext(), 2);
        gridView.setLayoutManager(gridLayoutManager);
        gridView.setAdapter(productListAdapter);
        int spanCount = 2; // 2 columns
        int spacing = 50; // 50px
        boolean includeEdge = false;
        gridView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        catagoryListAdapter = new CatagoryListAdapter(catagories);
        RecyclerView catagoryRecycleView =  (RecyclerView) view.findViewById(R.id.catagoryRecyclerView);
        catagoryManager = new LinearLayoutManager(catagoryRecycleView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        catagoryRecycleView.setLayoutManager(catagoryManager);
        catagoryRecycleView.setAdapter(catagoryListAdapter);
        catagoryListAdapter.notifyDataSetChanged();
        subCatagories = new ArrayList<>();
        subCatagoryListAdapter = new CatagoryListAdapter(subCatagories);
        LinearLayoutManager subCatagoryManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView subCatagoryRecyclerView = (RecyclerView) view.findViewById(R.id.subCatagoryRecyclerView);
        subCatagoryRecyclerView.setLayoutManager(subCatagoryManager);
        subCatagoryRecyclerView.setAdapter(subCatagoryListAdapter);
        DividerItemDecoration subCatagoryDividerItemDecoration = new DividerItemDecoration(subCatagoryRecyclerView.getContext(), subCatagoryManager.getOrientation());
        subCatagoryDividerItemDecoration.setDrawable(ContextCompat.getDrawable(subCatagoryRecyclerView.getContext(), R.drawable.recycler1_view_seperator));
        subCatagoryRecyclerView.addItemDecoration(subCatagoryDividerItemDecoration);
        Log.i("message", "end on create");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        CrawlCatagory();
        CrawlProduct(0,999999999,DataStorage.getInstance().getCatagoryId());
    }

    public void CrawlCatagory(){
        Handler handler = new Handler(Looper.getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/categories?withImage=false")
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
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
                        } catch (JSONException | IOException e) {
                            throw new RuntimeException(e);
                        }
                        for(int i = 0; i < jsonArray.length(); i ++)
                        {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Catagory item = new Catagory(jsonObject.get("_id").toString(), jsonObject.get("name").toString(), "");
                                catagories.add(item);
                            }
                            catch(JSONException e)
                            {
                                Log.i("exception", e.toString());
                            }
                        }
                        catagoryListAdapter.setRunnable(new Runnable() {
                            @Override
                            public void run() {
                                CrawlProduct(0, 99999999, DataStorage.getInstance().getCatagoryId());
                            }
                        });
                        RecyclerView catagoryRecycleView =  (RecyclerView) fragment.requireView().findViewById(R.id.catagoryRecyclerView);
                        catagoryRecycleView.getRecycledViewPool().clear();
                        catagoryListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
    public void CrawlProduct(int min, int max, String catagoryId){
        Log.i("message", "start Crawl product");
        @SuppressLint("DefaultLocale") String string = String.format("https://audace-ecomerce.herokuapp.com/products/search-filter?min=%d&max=%d&categoryId=%s&page=0",min,max,catagoryId);
        Handler handler = new Handler(Looper.getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        Request request = new Request.Builder()
                .url(string)
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
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
                        Product item = new Product(jsonObject.getString("_id"),jsonObject.getString("name"), jsonObject.getString("currentPrice"), jsonObject.getBoolean("isFavourite"),jsonObject.getString("imageURL") );
                        products.add(item);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView view = (RecyclerView) fragment.getView().findViewById(R.id.itemGridView);
                            view.getRecycledViewPool().clear();
                            Log.i("message",products.toString());
                            productListAdapter = new ProductListAdapter(products);
                            view.setAdapter(productListAdapter);
                            productListAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
