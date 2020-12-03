package com.antoniuswicaksana.project_pbp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class JadwalRecyclerAdapter extends RecyclerView.Adapter<com.antoniuswicaksana.project_pbp.JadwalRecyclerAdapter.RoomViewHolder> implements Filterable {

    private List<com.antoniuswicaksana.project_pbp.dao.JadwalDAO> dataList;
    private List<com.antoniuswicaksana.project_pbp.dao.JadwalDAO> filteredDataList;
    private Context context;

    public JadwalRecyclerAdapter(Context context, List<com.antoniuswicaksana.project_pbp.dao.JadwalDAO> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.filteredDataList = dataList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycle_adapter_jadwal, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.antoniuswicaksana.project_pbp.JadwalRecyclerAdapter.RoomViewHolder holder, int position) {
        final com.antoniuswicaksana.project_pbp.dao.JadwalDAO brg = filteredDataList.get(position);
        holder.twTanggal.setText(brg.gettanggal());
        holder.twWaktu.setText(brg.getwaktu());

        holder.mParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                DetailJadwalFragment dialog = new DetailJadwalFragment();
                dialog.show(manager, "dialog");

                Bundle args = new Bundle();
                args.putString("id", brg.getId());
                dialog.setArguments(args);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredDataList.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder {
        private TextView twTanggal, twWaktu;
        private LinearLayout mParent;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            twTanggal = itemView.findViewById(R.id.twTanggal);
            twWaktu = itemView.findViewById(R.id.twWaktu);
            mParent = itemView.findViewById(R.id.linearLayout);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSequenceString = constraint.toString();
                if (charSequenceString.isEmpty()) {
                    filteredDataList = dataList;
                } else {
                    List<com.antoniuswicaksana.project_pbp.dao.JadwalDAO> filteredList = new ArrayList<>();
                    for (com.antoniuswicaksana.project_pbp.dao.JadwalDAO JadwalDAO : dataList) {
                        if (JadwalDAO.gettanggal().toLowerCase().contains(charSequenceString.toLowerCase())) {
                            filteredList.add(JadwalDAO);
                        }
                        filteredDataList = filteredList;
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredDataList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredDataList = (List<com.antoniuswicaksana.project_pbp.dao.JadwalDAO>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
