package com.example.market;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BarangAdapter extends ArrayAdapter<Barang> {

    private static class ViewHolder {
        TextView tvNamaBarang;
        TextView tvHargaJumlahBarang;
        TextView tvTotalHarga;
    }

    public BarangAdapter(Context context, int resource, List<Barang> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View ConvertView, ViewGroup parent) {
        Barang barang = getItem(position);
        ViewHolder viewBarang;
        if (ConvertView == null) {
            viewBarang = new ViewHolder();
            ConvertView = LayoutInflater.from(getContext()).inflate(R.layout.item_barang, parent, false);
            viewBarang.tvNamaBarang = (TextView) ConvertView.findViewById(R.id.tvNamaBarang);
            viewBarang.tvHargaJumlahBarang = (TextView) ConvertView.findViewById(R.id.tvHargaJumlahBarang);
            viewBarang.tvTotalHarga = (TextView) ConvertView.findViewById(R.id.tvTotalHarga);
            ConvertView.setTag(viewBarang);
        }
        else {
            viewBarang = (ViewHolder) ConvertView.getTag();
        }

        viewBarang.tvNamaBarang.setText(barang.getNama());
        viewBarang.tvHargaJumlahBarang.setText(String.format("%sxRp%s", barang.getJumlah(), barang.getHarga()));
        viewBarang.tvTotalHarga.setText(String.format("Rp%s", barang.getJumlah() * barang.getHarga()));
        return ConvertView;
    }
}
