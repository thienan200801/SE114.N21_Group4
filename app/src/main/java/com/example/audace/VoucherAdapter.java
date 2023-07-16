package com.example.audace;

import static android.os.Looper.getMainLooper;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {

    private ArrayList<Voucher> voucherList ;
    private VoucherScreen activity;

    public VoucherAdapter(VoucherScreen activity, ArrayList<Voucher> voucherList
    ){
        this.activity = activity;
        this.voucherList = voucherList;
    }
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.voucher_item, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Voucher item = voucherList.get(position);
        holder.mathangTextView.setText(item.getName());
        holder.dieukienTextView.setText(item.getCode());
        holder.thoigianTextView.setText(item.getStart());
        holder.luuyTextView.setText(item.getEnd());
        holder.saleoffTextView.setText(item.getSaleoff());
        holder.saveVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveVoucher(item);
            }
        });





    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mathangTextView ;
        TextView dieukienTextView ;
        TextView thoigianTextView ;
        TextView luuyTextView ;
        TextView saleoffTextView;
        ImageButton saveVoucher;

        ViewHolder( View view){
            super(view);
            mathangTextView =view.findViewById(R.id.txtmathang);
            dieukienTextView =view.findViewById(R.id.txtdieukien);
            thoigianTextView =view.findViewById(R.id.txtthoigian);
            luuyTextView = view.findViewById(R.id.txtluuy);
            saleoffTextView = view.findViewById(R.id.saleoff_txt);
            saveVoucher = view.findViewById(R.id.btnSaveVoucher);






        }

        }

    private void saveVoucher(Voucher item){
        try {
            Handler handler = new Handler(getMainLooper());
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject productJson = new JSONObject();
            try {
                productJson.put("_id", item.getId());
                productJson.put("name", item.getName());
                productJson.put("start", item.getStart());
                productJson.put("end", item.getEnd());
                productJson.put("code", item.getCode());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray voucher = new JSONArray();
            voucher.put(productJson);



            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject requestBody = new JSONObject();
                    try {
                        requestBody.put("voucher", voucher);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody body = RequestBody.create(mediaType, requestBody.toString());




                    Request request = new Request.Builder()
                            .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                            .method("POST", body)
                            .addHeader("Authorization", " Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Toast.makeText(activity,"Error: Cannot add to voucher list",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String body=response.body().string();
                            Log.e("data from server", body);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (body.equals("")) {
                                        Toast.makeText(activity, "Added to voucher list", Toast.LENGTH_SHORT).show();

                                    }
                                    else {
                                        Toast.makeText(activity, body, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });


                        }
                    });

                }
            }).start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {

        return voucherList.size();

    }
    public void setVoucherList(ArrayList<Voucher> voucherList){
        this.voucherList = voucherList;

    }
}
