package com.antoniuswicaksana.project_pbp.dao;

import com.google.gson.annotations.SerializedName;

public class JadwalDAO {

    @SerializedName("id")
    private String id;

    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("waktu")
    private String waktu;

    @SerializedName("keterangan")
    private String keterangan;


    public JadwalDAO(String id, String tanggal, String waktu, String keterangan) {
        this.id = id;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.keterangan = keterangan;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String gettanggal() {
        return tanggal;
    }

    public void settanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getwaktu() {
        return waktu;
    }

    public void setwaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getketerangan() {
        return keterangan;
    }

    public void setketerangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
