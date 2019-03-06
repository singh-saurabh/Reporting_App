package com.example.sihuserapp.list;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.sihuserapp.Interfaces.APIInterface;
import com.example.sihuserapp.Objects.PingResponse;
import com.example.sihuserapp.PostListUtil;
import com.example.sihuserapp.R;
import com.example.sihuserapp.adapters.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QueryListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    public APIInterface mAPIService;
    String phone;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_list);

        recyclerView=findViewById(R.id.recycler);
        mAPIService = PostListUtil.getAPIService();
        imageView=findViewById(R.id.lonley);


        Intent intent = getIntent();
        if(intent.getExtras().get("phone_number")!=null) {
            phone= (String) intent.getExtras().get("phone_number");
        }
        else {
            phone="9149370198";
        }
        mAPIService.getList(phone).enqueue(new Callback<ArrayList<PingResponse>>(){
            @Override
            public void onResponse(Call<ArrayList<PingResponse>> call, Response<ArrayList<PingResponse>> response) {
                setPing(response.body());
                if(response.body()==null){
                    imageView.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<ArrayList<PingResponse>> call, Throwable t) {
                imageView.setVisibility(View.VISIBLE);
            }
        });
    }
    void setPing(ArrayList<PingResponse> pong){
            ListAdapter adapter = new ListAdapter(pong);
            Log.v("TAG pong length", String.valueOf(pong.size()));
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.setAdapter(adapter);

    }
}
