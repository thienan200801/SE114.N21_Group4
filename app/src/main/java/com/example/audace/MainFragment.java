package com.example.audace;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.audace.model.Catagory;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

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
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton drawerToggleButton;
    private ImageButton cartButton;
    private ImageButton logoutButton;

    private ImageButton searchButton;

    private Fragment fragment;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment = this;
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        // Inflate the layout for this fragment
        drawerToggleButton = view.findViewById(R.id.drawerToggleButton);
        drawerToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerNavigationView = view.getRootView().findViewById(R.id.drawerLayout);
                drawerNavigationView.open();
            }
        });
        cartButton = view.findViewById(R.id.cartButton);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(getActivity(), CartScreen.class);
                startActivity(t);
            }
        });
        logoutButton = getActivity().findViewById(R.id.LogoutButton);
        if(logoutButton != null)
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataStorage.getInstance().setAccessToken(null);
                    Intent t = new Intent(getActivity(), LoginScreen.class);
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("token", Context.MODE_PRIVATE).edit();
                    editor.remove("token");
                    editor.apply();
                    startActivity(t);
                    getActivity().finishActivity(0);
                }
            });
        searchButton = view.findViewById(R.id.searchButton);
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
                NavController navController = Navigation.findNavController(getActivity().findViewById(R.id.fragmentContainerView));
                navController.navigate(R.id.action_global_searchFragment);
            }
        });
        populateDrawerMenu();
        NavigationView navigationView = (NavigationView) fragment.getActivity().findViewById(R.id.drawerNavigationView);
        getUserAvatar(navigationView.getHeaderView(0).findViewById(R.id.user_imageButton));
        navigationView.getHeaderView(0).findViewById(R.id.user_imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerNavigationView = view.getRootView().findViewById(R.id.drawerLayout);
                drawerNavigationView.close();
                Intent intent = new Intent(getContext(), ProfileScreen.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void getUserAvatar(ImageButton view) {
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
                    Log.i("message",jsonObject.getString("imageURL"));
                    new Handler(Looper.getMainLooper()).post(() ->{
                        try {
                            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(view.getContext());
                            circularProgressDrawable.setStrokeWidth(5f);
                            circularProgressDrawable.setCenterRadius(30f);
                            circularProgressDrawable.start();
                            Picasso.get()
                                    .load(jsonObject.getString("imageURL"))
                                    .placeholder(circularProgressDrawable)
                                    .error(R.drawable.baseline_wifi_tethering_error_24)
                                    .fit()
                                    .into(view);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    public void populateDrawerMenu()
    {
        NavigationView navigationView = (NavigationView) fragment.getActivity().findViewById(R.id.drawerNavigationView);
        Menu menu = navigationView.getMenu();
        ArrayList<Catagory> catagoryArrayList = DataStorage.getInstance().getCatagoryArrayList();
        menu.clear();
        for(int i = 0; i <catagoryArrayList.size(); i ++)
        {
            if(catagoryArrayList.get(i).getSubCatagories().size() == 0)
                menu.add(catagoryArrayList.get(i).getCatagoryName());
            else
            {
                SubMenu subMenu = menu.addSubMenu(catagoryArrayList.get(i).getCatagoryName());
                ArrayList<Catagory> child = catagoryArrayList.get(i).getSubCatagories();
                for(int j = 0; j < child.size(); j ++)
                {
                    MenuItem item = subMenu.add(child.get(j).getCatagoryName());
                    int index = j;
                    item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                            Log.i("message", "Item clicked");
                            DataStorage.getInstance().setCatagoryId(child.get(index).getCatagoryID());
                            if(getActivity().findViewById(R.id.fragmentContainerView) != null)
                                Navigation.findNavController(getActivity().findViewById(R.id.fragmentContainerView)).navigate(R.id.action_global_fragment_subcatagory);
                            ((DrawerLayout)navigationView.getParent()).closeDrawer(GravityCompat.START);
                            return true;
                        }
                    });
                    Log.i("message", child.get(j).getCatagoryName());
                }
                subMenu.clearHeader();

            }

        }
        navigationView.invalidate();
    }
}