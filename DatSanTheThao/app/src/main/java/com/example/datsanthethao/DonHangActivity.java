package com.example.datsanthethao;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class DonHangActivity extends AppCompatActivity {
    CoSoDuLieu db;
    int id;
    boolean laChu;
    ListView lv;
    List<CoSoDuLieu.DonDat> ds;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_don_hang);
        db = new CoSoDuLieu(this);
        id = getIntent().getIntExtra("id", 0);
        laChu = "CHUSAN".equals(getIntent().getStringExtra("vaitro"));
        lv = findViewById(R.id.lvDon);
        lv.setOnItemClickListener((p, v, pos, i) -> {
            if (laChu) menuChu(ds.get(pos)); else menuKhach(ds.get(pos));
        });
    }

    @Override protected void onResume() { super.onResume(); taiLai(); }

    void taiLai() {
        ds = db.layDon(id, laChu);
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ds));
    }

    void menuChu(CoSoDuLieu.DonDat d) {
        new AlertDialog.Builder(this).setTitle("Đơn #" + d.id)
                .setItems(new String[]{"Xác nhận đã thu tiền", "Hủy đơn"}, (dlg, which) -> {
                    db.capNhatTrangThai(d.id, which == 0 ? "DATHANHTOAN" : "HUY");
                    taiLai();
                }).show();
    }

    void menuKhach(CoSoDuLieu.DonDat d) {
        startActivity(new Intent(this, ThanhToanActivity.class).putExtra("donId", d.id));
    }
}