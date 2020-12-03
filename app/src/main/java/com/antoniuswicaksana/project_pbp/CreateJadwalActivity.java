package com.antoniuswicaksana.project_pbp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.antoniuswicaksana.project_pbp.api.ApiClient;
import com.antoniuswicaksana.project_pbp.api.ApiInterface;
import com.antoniuswicaksana.project_pbp.api.JadwalResponse;
import com.antoniuswicaksana.project_pbp.api.UserResponse;
import com.google.android.material.button.MaterialButton;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateJadwalActivity extends AppCompatActivity {

    private ImageButton ibBack;
    private EditText etId, etTanggal, etWaktu, etKeterangan;
    private MaterialButton btnCancel, btnCreate;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_jadwal);

        progressDialog = new ProgressDialog(this);

        ibBack = findViewById(R.id.ibBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        etId = findViewById(R.id.etTanggal);
        etTanggal = findViewById(R.id.etTanggal);
        etWaktu = findViewById(R.id.etWaktu);
        etKeterangan = findViewById(R.id.etKeterangan);
        btnCancel = findViewById(R.id.btnCancel);
        btnCreate = findViewById(R.id.btnCreate);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etTanggal.getText().toString().isEmpty()) {
                    etTanggal.setError("Isikan dengan benar");
                    etTanggal.requestFocus();
                } else if(etWaktu.getText().toString().isEmpty()) {
                    etWaktu.setError("Isikan dengan benar");
                    etWaktu.requestFocus();
                } else if(etKeterangan.getText().toString().isEmpty()) {
                    etKeterangan.setError("Isikan dengan benar");
                    etKeterangan.requestFocus();
                } else {
                    progressDialog.show();
                    saveJadwal();
                }
            }
        });
    }

    private void saveJadwal() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<JadwalResponse> add = apiService.createJadwal(etId.getText().toString(),etTanggal.getText().toString(), etWaktu.getText().toString(), etKeterangan.getText().toString());

        add.enqueue(new Callback<JadwalResponse>() {
            @Override
            public void onResponse(Call<JadwalResponse> call, Response<JadwalResponse> response) {
                Toast.makeText(com.antoniuswicaksana.project_pbp.CreateJadwalActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                onBackPressed();
            }

            @Override
            public void onFailure(Call<JadwalResponse> call, Throwable t) {
                Toast.makeText(com.antoniuswicaksana.project_pbp.CreateJadwalActivity.this, "Kesalahan Jaringan", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}