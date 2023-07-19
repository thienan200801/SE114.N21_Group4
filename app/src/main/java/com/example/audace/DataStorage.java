package com.example.audace;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.audace.model.Catagory;
import com.example.audace.model.Product;

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

public class DataStorage {
    private ArrayList<Catagory> catagoryArrayList;

    private final Object lock = new Object();

    private String AccessToken;

    private String productId;

    private String catagoryId;

    private String SearchText;

    private String avatarURl;

    private Integer cartCount;

    private String userName;

    public ArrayList<Catagory> getCatagoryArrayList() {
        Log.i("catagories", catagoryArrayList.toString());
        if(catagoryArrayList == null || catagoryArrayList.size() == 0)
            CrawlCatagory();
        synchronized (lock)
        {
            while(catagoryArrayList == null || catagoryArrayList.size() == 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return catagoryArrayList;
        }
    }

    public ArrayList<Catagory> getCatagoryArrayList(boolean withImage){
        if(withImage)
            return getCatagoryArrayList();
        else
        {
            ArrayList<Catagory> result = new ArrayList<Catagory>(getCatagoryArrayList());
            for(int i = 0; i < result.size(); i++)
                result.get(i).setImgUrl("");
            return result;
        }
    }
    public void setCatagoryArrayList(ArrayList<Catagory> catagoryArrayList) {
        this.catagoryArrayList = catagoryArrayList;
        if(catagoryArrayList.size() != 0)
            catagoryId = catagoryArrayList.get(0).getCatagoryID();
    }

    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }

    public DataStorage(){
        Log.i("message", "create new storage");
        catagoryArrayList = new ArrayList<Catagory>();
        AccessToken = null;
        catagoryId = null;
        avatarURl = null;
        cartCount = null;
        userName = null;
    }

    private static DataStorage storage;

    public static DataStorage getInstance() {
        if(storage == null)
            storage = new DataStorage();
        return storage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setCatagoryId(String catagoryId) {
        this.catagoryId = catagoryId;
    }

    public String getCatagoryId() {
        return catagoryId;
    }

    public String getSearchText() {
        return SearchText;
    }

    public void setSearchText(String searchText) {
        SearchText = searchText;
    }

    public Object getLock() {
        return lock;
    }

    public void CrawlCatagory(){
            catagoryArrayList.clear();
            Handler handler = new Handler(Looper.getMainLooper());
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://audace-ecomerce.herokuapp.com/categories?withImage=true")
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer " + getAccessToken())
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("message", call.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    synchronized (lock)
                    {
                        catagoryArrayList.clear();
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
                                Catagory item = new Catagory(jsonObject.getString("_id"), jsonObject.getString("name"), jsonObject.getString("imageURL"));
                                JSONArray child = jsonObject.getJSONArray("childCategories");
                                ArrayList<Catagory> sub = new ArrayList<Catagory>();
                                for(int j = 0; j < child.length(); j ++)
                                    sub.add(new Catagory(child.getJSONObject(j).getString("_id"), child.getJSONObject(j).getString("name"), ""));
                                item.setSubCatagories(sub);
                                catagoryArrayList.add(item);
                            }
                            catch(JSONException e)
                            {
                            }
                        }
                        lock.notify();
                    }
                }
            });
    }
    public void tokenValidation(String token){
        Handler handler = new Handler();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/products/best-sale-off?page=0")
                .method("GET", null)
                .addHeader("Authorization", "Bearer "+ DataStorage.getInstance().getAccessToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Token Validation", "Not authorized");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("validation", "acesstoken Ok");
                    setAccessToken(token);
            }
        });
    }

    public String getAvatarURl() {
        return avatarURl;
    }

    public void setAvatarURl(String avatarURl) {
        this.avatarURl = avatarURl;
    }

    public Integer getCartCount() {
        return cartCount;
    }

    public void setCartCount(Integer cartCount) {
        this.cartCount = cartCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
