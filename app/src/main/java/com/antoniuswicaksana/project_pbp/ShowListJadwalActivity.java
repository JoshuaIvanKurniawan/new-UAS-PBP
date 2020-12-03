package com.antoniuswicaksana.project_pbp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.antoniuswicaksana.project_pbp.api.ApiClient;
import com.antoniuswicaksana.project_pbp.api.ApiInterface;
import com.antoniuswicaksana.project_pbp.api.JadwalResponse;
import com.antoniuswicaksana.project_pbp.dao.JadwalDAO;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowListJadwalActivity extends AppCompatActivity {

    private ImageButton ibBack;
    private RecyclerView recyclerView;
    private JadwalRecyclerAdapter recyclerAdapter;
    private List<JadwalDAO> Jadwal = new ArrayList<>();
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefresh;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list_jadwal);

        ibBack = findViewById(R.id.ibBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        searchView = findViewById(R.id.searchJadwal);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        shimmerFrameLayout = findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmer();

//        swipeRefresh.setRefreshing(true);
        loadJadwal();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadJadwal();
            }
        });
    }

    public void loadJadwal() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<JadwalResponse> call = apiService.getAllJadwal("data");

        call.enqueue(new Callback<JadwalResponse>() {
            @Override
            public void onResponse(Call<JadwalResponse> call, Response<JadwalResponse> response) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                generateDataList(response.body().getjadwal());
//                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<JadwalResponse> call, Throwable t) {
                Toast.makeText(ShowListJadwalActivity.this, "Kesalahan Jaringan", Toast.LENGTH_SHORT).show();
//                swipeRefresh.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        });
    }

    private void generateDataList(List<JadwalDAO> customerList) {
        recyclerView = findViewById(R.id.JadwalRecyclerView);
        recyclerAdapter = new JadwalRecyclerAdapter(this, customerList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ShowListJadwalActivity.this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                recyclerAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                recyclerAdapter.getFilter().filter(s);
                return false;
            }
        });
    }
}