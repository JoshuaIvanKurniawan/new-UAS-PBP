package com.antoniuswicaksana.project_pbp.;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link com.antoniuswicaksana.project_pbp.DetailJadwalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailJadwalFragment extends DialogFragment {

    private TextView twTanggal, twWaktu, twKeterangan;
    private String sIdJadwal, sTanggal, sWaktu, sKeterangan;
    private ImageButton ibClose;
    private ProgressDialog progressDialog;
    private Button btnDelete, btnEdit;
    private List<com.antoniuswicaksana.project_pbp.dao.JadwalDAO> Jadwals;

    public static com.antoniuswicaksana.project_pbp.DetailJadwalFragment newInstance() { return new com.antoniuswicaksana.project_pbp.DetailJadwalFragment(); }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_jadwal, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();

        ibClose = v.findViewById(R.id.ibClose);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        twTanggal = v.findViewById(R.id.twTanggal);
        twWaktu = v.findViewById(R.id.twWaktu);
        twKeterangan = v.findViewById(R.id.twKeterangan);

        btnDelete = v.findViewById(R.id.btnDelete);
        btnEdit = v.findViewById(R.id.btnEdit);

        sIdJadwal = getArguments().getString("id", "");
        loadJadwalById(sIdJadwal);

        return v;
    }

    private void loadJadwalById(String id) {
        com.antoniuswicaksana.project_pbp.api.ApiInterface apiService = com.antoniuswicaksana.project_pbp.api.ApiClient.getClient().create(com.antoniuswicaksana.project_pbp.api.ApiInterface.class);
        Call<com.antoniuswicaksana.project_pbp.api.UserResponse> add = apiService.getUserById(id, "data");

        add.enqueue(new Callback<com.antoniuswicaksana.project_pbp.api.JadwalResponse>() {
            @Override
            public void onResponse(Call<com.antoniuswicaksana.project_pbp.api.JadwalResponse> call, Response<com.antoniuswicaksana.project_pbp.api.JadwalResponse> response) {
                sTanggal = response.body().getjadwal().get(0).gettanggal();
                sWaktu = response.body().getjadwal().get(0).getwaktu();
                sKeterangan = response.body().getjadwal().get(0).getketerangan();

                twTanggal.setText(sTanggal);
                twWaktu.setText(sWaktu);
                twKeterangan.setText(sKeterangan);
                progressDialog.dismiss();

                Jadwals = response.body().getjadwal();
                final com.antoniuswicaksana.project_pbp.dao.JadwalDAO Jadwal = Jadwals.get(0);

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteJadwal(sIdJadwal);
                    }
                });

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), com.antoniuswicaksana.project_pbp.EditJadwalActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("id", Jadwal.getId());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<com.antoniuswicaksana.project_pbp.api.JadwalResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Kesalahan Jaringan", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void deleteJadwal(final String sIdJadwal) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Are you sure, You wanted to make decision");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        com.antoniuswicaksana.project_pbp.api.ApiInterface apiService = com.antoniuswicaksana.project_pbp.api.ApiClient.getClient().create(com.antoniuswicaksana.project_pbp.api.ApiInterface.class);
                        Call<com.antoniuswicaksana.project_pbp.api.JadwalResponse> call = apiService.deleteJadwal(sIdJadwal);

                        call.enqueue(new Callback<com.antoniuswicaksana.project_pbp.api.JadwalResponse>() {
                            @Override
                            public void onResponse(Call<com.antoniuswicaksana.project_pbp.api.JadwalResponse> call, Response<com.antoniuswicaksana.project_pbp.api.JadwalResponse> response) {
                                Toast.makeText(getContext(), "Jadwal berhasil dihapus", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<com.antoniuswicaksana.project_pbp.api.JadwalResponse> call, Throwable t) {
                                Toast.makeText(getContext(), "Kesalahan Jaringan", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}