package com.antoniuswicaksana.project_pbp.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JadwalResponse {

    @SerializedName("data")
    @Expose
    private List<com.antoniuswicaksana.project_pbp.dao.JadwalDAO> jadwal = null;

    @SerializedName("message")
    @Expose
    private String message;

    public List<com.antoniuswicaksana.project_pbp.dao.JadwalDAO> getjadwal() {
        return jadwal;
    }

    public String getMessage() {
        return message;
    }

    public void setJadwals(List<com.antoniuswicaksana.project_pbp.dao.JadwalDAO> jadwal) {
        this.jadwal = jadwal;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
