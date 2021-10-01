package com.example.market;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Timestamp;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView tvNamaCustomer, tvTotalBayar, tvJumlahUang, tvBonus, tvKeterangan, tvKembalian;
    private ArrayList<Barang> listBarang;
    private BarangAdapter bAdapter;
    private SQLiteOpenHelper openDB;
    private SQLiteDatabase DB;
    private Barang pointerBarang = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton btnTambahBarang, btnHapusBarang, btnClear;
        btnTambahBarang = findViewById(R.id.btnTambahBarang);
        btnHapusBarang = findViewById(R.id.btnHapusBarang);
        btnClear = findViewById(R.id.btnClear);

        final Button btnCheckout = findViewById(R.id.btnCheckout);

        tvNamaCustomer = findViewById(R.id.tvNamaCustomer);
        tvTotalBayar = findViewById(R.id.tvTotalBayar);
        tvJumlahUang = findViewById(R.id.tvJumlahUang);
        tvBonus = findViewById(R.id.tvBonus);
        tvKeterangan = findViewById(R.id.tvKeterangan);
        tvKembalian = findViewById(R.id.tvKembalian);

        ListView lvBarang = findViewById(R.id.lvBarang);

        listBarang = new ArrayList<>();
        bAdapter = new BarangAdapter(this, 0, listBarang);
        lvBarang.setAdapter(bAdapter);
        lvBarang.setOnItemClickListener((adapterView, view, i, l) -> {
            Barang barang = (Barang) adapterView.getItemAtPosition(i);
            Toast.makeText(this, "Barang " + barang.getNama() + " dipilih", Toast.LENGTH_LONG).show();
            pointerBarang = barang;
        });

        btnTambahBarang.setOnClickListener(operasi);
        btnHapusBarang.setOnClickListener(operasi);
        btnCheckout.setOnClickListener(operasi);
        btnClear.setOnClickListener(operasi);

        openDB = new SQLiteOpenHelper(this, "db2.sql", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) { }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }
        };

        DB = openDB.getWritableDatabase();

        DB.execSQL("CREATE TABLE IF NOT EXISTS barang (unique_id VARCHAR(16), nama VARCHAR(64), jumlah DOUBLE, harga DOUBLE)");

        loadBarang();
    }

    protected void onStop() {
        DB.close();
        openDB.close();
        super.onStop();
    }

    private void loadBarang() {
        Cursor cur = DB.rawQuery("SELECT unique_id, nama, jumlah, harga FROM barang", null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            do {
                bAdapter.add(new Barang(cur.getString(0), cur.getString(1), cur.getDouble(2), cur.getDouble(3)));
            }
            while (cur.moveToNext());
        }
        cur.close();
    }

    private void addBarang(String nama, double jumlah, double harga) {
        // Make identifier
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String uniqueID = String.valueOf(timestamp.getTime());
        // Insert barang to database
        ContentValues dataBarang = new ContentValues();
        dataBarang.put("unique_id", uniqueID);
        dataBarang.put("nama", nama);
        dataBarang.put("jumlah", jumlah);
        dataBarang.put("harga", harga);
        DB.insert("barang", null, dataBarang);
        // Update teks total bayar
        tvTotalBayar.setText(String.format("Total Bayar: Rp%s", getTotalBayar()));
        // Insert barang to List View
        bAdapter.add(new Barang(uniqueID, nama, jumlah, harga));
    }

    private void deleteBarang(Barang barang) {
        // Delete from database
        DB.delete("barang", "unique_id = '" + barang.getUniqueID() + "'", null);
        // Delete from List View
        bAdapter.remove(barang);
        // Update teks total bayar
        tvTotalBayar.setText(String.format("Total Bayar: Rp%s", getTotalBayar()));
    }

    private double getTotalBayar() {
        double total = 0.0;
        for (Barang barang : listBarang) {
            total += barang.getJumlah()*barang.getHarga();
        }
        return total;
    }

    private String getBonus(double total) {
        String bonus;
        if (total >= 200000) bonus = "HardDisk 1TB";
        else if (total >= 50000) bonus = "Keyboard Gaming";
        else if (total >= 40000) bonus = "Mouse Gaming";
        else bonus = "Tidak ada bonus!";
        return bonus;
    }

    private void checkout(String nama, double jumlahUang) {
        double totalBayar = getTotalBayar();
        double kembalian = jumlahUang - totalBayar;
        tvNamaCustomer.setText(String.format("Nama Customer: %s", nama));
        tvJumlahUang.setText(String.format("Jumlah Uang: Rp%s", jumlahUang));
        tvTotalBayar.setText(String.format("Total Bayar: Rp%s", totalBayar));
        tvBonus.setText(String.format("Bonus : %s", getBonus(totalBayar)));
        tvKembalian.setText(String.format("Kembalian: Rp%s", kembalian));
        if (kembalian < 0) tvKeterangan.setText(String.format("Uang bayar kurang Rp%s", -kembalian));
        else if (kembalian > 0) tvKeterangan.setText(String.format("Tunggu kembalian Rp%s", kembalian));
    }

    private void clearBarang() {
        bAdapter.clear();
        tvNamaCustomer.setText("Nama Customer: -");
        tvJumlahUang.setText("Jumlah Uang: Rp0");
        tvTotalBayar.setText("Total Bayar: Rp0");
        tvBonus.setText("Bonus: -");
        tvKembalian.setText("Kembalian: Rp0");
        tvKeterangan.setText("Keterangan: -");
        Toast.makeText(this, "Clear", Toast.LENGTH_LONG).show();
    }

    private void showTambahBarangDialog() {
        AlertDialog.Builder aD = new AlertDialog.Builder(this);
        aD.setTitle("Tambah Barang");
        View v = LayoutInflater.from(this).inflate(R.layout.add_barang, null);
        final EditText etNamaBarang = v.findViewById(R.id.etNamaBarang),
                etJumlahBarang = v.findViewById(R.id.etJumlahBarang),
                etHargaBarang = v.findViewById(R.id.etHargaBarang);
        aD.setView(v);
        aD.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
            addBarang(etNamaBarang.getText().toString(),
                    Double.parseDouble(etJumlahBarang.getText().toString()),
                    Double.parseDouble(etHargaBarang.getText().toString()));
            Toast.makeText(this, "Barang Ditambahkan", Toast.LENGTH_LONG).show();
            dialogInterface.dismiss();
        });
        aD.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());
        aD.show();
    }

    private void showHapusBarangDialog() {
        if (pointerBarang == null) Toast.makeText(this, "Klik salah satu barang terlebih dahulu.", Toast.LENGTH_LONG).show();
        else {
            AlertDialog.Builder aD = new AlertDialog.Builder(this);
            aD.setTitle("Hapus Barang");
            View v = LayoutInflater.from(this).inflate(R.layout.delete_barang, null);
            final TextView tvPrompt = v.findViewById(R.id.tvPrompt);
            tvPrompt.setText(String.format("Apakah Anda yakin akan menghapus %s dari keranjang belanja?", pointerBarang.getNama()));
            aD.setView(v);
            aD.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                Toast.makeText(this, "Barang Dihapus", Toast.LENGTH_LONG).show();
                deleteBarang(pointerBarang);
                pointerBarang = null;
                dialogInterface.dismiss();
            });
            aD.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());
            aD.show();
        }
    }

    private void showCheckoutDialog() {
        AlertDialog.Builder aD = new AlertDialog.Builder(this);
        aD.setTitle("Tambah Barang");
        View v = LayoutInflater.from(this).inflate(R.layout.checkout, null);
        final EditText etNamaCustomer = v.findViewById(R.id.etNamaCustomer),
                etJumlahUang = v.findViewById(R.id.etJumlahUang);
        aD.setView(v);
        aD.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
            checkout(etNamaCustomer.getText().toString(),
                    Double.parseDouble(etJumlahUang.getText().toString()));
            Toast.makeText(this, "Checkout Berhasil", Toast.LENGTH_LONG).show();
            dialogInterface.dismiss();
        });
        aD.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());
        aD.show();
    }

    @SuppressLint("NonConstantResourceId")
    View.OnClickListener operasi = v -> {
        switch (v.getId()) {
            case R.id.btnTambahBarang: showTambahBarangDialog(); break;
            case R.id.btnCheckout: showCheckoutDialog(); break;
            case R.id.btnHapusBarang: showHapusBarangDialog(); break;
            case R.id.btnClear: clearBarang(); break;
        }
    };
}