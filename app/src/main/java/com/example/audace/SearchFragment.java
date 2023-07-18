package com.example.audace;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.audace.adapter.ProductListAdapter;
import com.example.audace.model.Product;
import com.example.audace.placeholder.PlaceholderContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A fragment representing a list of Items.
 */
public class SearchFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;

    private ProductListAdapter adapter;
    private ArrayList<Product> products;

    private Fragment fragment;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchFragment newInstance(int columnCount) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);
        fragment = this;
        ((TextView) view.findViewById(R.id.timkiemtheoTextView)).setText("Kết quả tìm kiếm: " + DataStorage.getInstance().getSearchText());
        products = new ArrayList<Product>();
        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        adapter = new ProductListAdapter(products);
        int spanCount = 2; // 2 columns
        int spacing = 50; // 50px
        boolean includeEdge = false;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        view.findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
        CrawlProduct();
        //Set spinner
        Spinner spinner = (Spinner)view.findViewById(R.id.dropDown);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.sap_xep_array, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getView().findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
                Log.i("Sort Option", Integer.toString(i));
                products = Sort(products, i);
                adapter = new ProductListAdapter(products);
                recyclerView.setAdapter(adapter );
                adapter.notifyDataSetChanged();
                getView().findViewById(R.id.loadingLayout).setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i("message", "nothing selected");
            }
        });
        return view;
    }

    public void CrawlProduct() {
        Log.i("message", "start Crawl product");
        @SuppressLint("DefaultLocale") String string = String.format("https://audace-ecomerce.herokuapp.com/products");
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
                    String search = DataStorage.getInstance().getSearchText();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString("name").toLowerCase().contains(search.toLowerCase())) {
                            Product item = new Product(jsonObject.getString("_id"), jsonObject.getString("name"), jsonObject.getString("currentPrice"), jsonObject.getString("stablePrice"), jsonObject.getBoolean("isFavourite"), jsonObject.getString("imageURL"));
                            products.add(item);
                        }

                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView view = (RecyclerView) fragment.getView().findViewById(R.id.list);
                            view.getRecycledViewPool().clear();
                            adapter = new ProductListAdapter(Sort(products, 0));
                            view.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            getView().findViewById(R.id.loadingLayout).setVisibility(View.GONE);
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
    private ArrayList<Product> Sort(ArrayList<Product> productArrayList, int type)
    {
        ArrayList<Product> result = new ArrayList<Product>();
        switch (type)
        {
            case 0:
                for(int i = 0; i < productArrayList.size(); i++){
                    Product element = productArrayList.get(i);
                    if(result.size() == 0)
                    {
                        result.add(element);
                        continue;
                    }
                    boolean flag = false;
                    for(int j = 0; j < result.size(); j ++)
                        if(element.getName().toLowerCase().compareTo(result.get(j).getName()) < 0)
                        {
                            result.add(j, element);
                            flag = true;
                            break;
                        }
                    if(!flag)
                        result.add(element);
                }
                break;
            case 1:
                for(int i = 0; i < productArrayList.size(); i++){
                    Product element = productArrayList.get(i);
                    if(result.size() == 0)
                    {
                        result.add(element);
                        continue;
                    }
                    boolean flag = false;
                    for(int j = 0; j < result.size(); j ++)
                        if(element.getPrice().toLowerCase().compareTo(result.get(j).getPrice()) < 0)
                        {
                            result.add(j, element);
                            flag = true;
                            break;
                        }
                    if(!flag)
                        result.add(element);
                }
                break;
            case 2:
                for(int i = 0; i < productArrayList.size(); i++){
                    Product element = productArrayList.get(i);
                    if(result.size() == 0)
                    {
                        result.add(element);
                        continue;
                    }
                    boolean flag = false;
                    for(int j = 0; j < result.size(); j ++)
                        if(element.getPrice().toLowerCase().compareTo(result.get(j).getPrice()) > 0)
                        {
                            result.add(j, element);
                            flag = true;
                            break;
                        }
                    if(!flag)
                        result.add(element);
                }
                break;
        }
        return result;
    }
}