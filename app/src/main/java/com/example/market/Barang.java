package com.example.market;

public class Barang {
    private String uniqueID;
    private String nama;
    private double jumlah, harga;

    public Barang(String uniqueID, String nama, double jumlah, double harga) {
        this.uniqueID = uniqueID;
        this.nama = nama;
        this.jumlah = jumlah;
        this.harga = harga;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getJumlah() {
        return jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }
}
