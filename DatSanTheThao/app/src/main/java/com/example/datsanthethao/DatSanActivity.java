package com.example.datsanthethao;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class DatSanActivity extends AppCompatActivity {
    CoSoDuLieu db;
    CoSoDuLieu.San san;
    int sanId, khachId;
    String ngay;
    boolean laHomNay = true;
    Set<Integer> chon = new TreeSet<>();
    Set<String> daDat = new HashSet<>();
    TableLayout tbLich;
    TextView tvTong;
    String[] dsNgay = new String[7];
    TextView[] nutNgay = new TextView[7];

    int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density); }

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_dat_san);
        db = new CoSoDuLieu(this);
        sanId = getIntent().getIntExtra("sanId", 0);
        khachId = getIntent().getIntExtra("khachId", 0);
        san = db.laySan(sanId);
        if (san == null) { finish(); return; }
        tbLich = findViewById(R.id.tbLich);
        tvTong = findViewById(R.id.tvTong);
        ((TextView) findViewById(R.id.tvTieuDe)).setText(san.ten);
        ((TextView) findViewById(R.id.tvLuuY)).setText("Luu y: Vui long dat lien khong de khoang trong giua cac ca.\n"
                + "Hotline: " + san.sdt + " | Gia: " + CoSoDuLieu.tien(san.gia) + " d/gio");

        String[] thu = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        LinearLayout llNgay = findViewById(R.id.llNgay);
        for (int i = 0; i < 7; i++) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, i);
            dsNgay[i] = String.format(Locale.US, "%04d-%02d-%02d", c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
            String nhan = (i == 0 ? "Hom nay" : thu[c.get(Calendar.DAY_OF_WEEK) - 1]) + "\n"
                    + String.format(Locale.US, "%02d/%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1);
            TextView tv = new TextView(this);
            tv.setText(nhan);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(dp(12), dp(8), dp(12), dp(8));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
            lp.setMargins(0, 0, dp(6), 0);
            tv.setLayoutParams(lp);
            final int k = i;
            tv.setOnClickListener(v -> chonNgay(k));
            llNgay.addView(tv);
            nutNgay[i] = tv;
        }

        findViewById(R.id.btnTiepTheo).setOnClickListener(v -> tiepTheo());
        chonNgay(0);
    }

    void chonNgay(int i) {
        ngay = dsNgay[i];
        laHomNay = (i == 0);
        chon.clear();
        for (int k = 0; k < 7; k++) {
            nutNgay[k].setBackgroundResource(k == i ? R.drawable.bg_the : R.drawable.bg_chip_chon);
            nutNgay[k].setTextColor(Color.parseColor(k == i ? "#0F5B34" : "#FFFFFF"));
        }
        taiLich();
    }

    TextView o(String text, int w, int h, int mau, int mauChu) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(mau);
        tv.setTextColor(mauChu);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(dp(w), dp(h));
        lp.setMargins(1, 1, 1, 1);
        tv.setLayoutParams(lp);
        return tv;
    }

    void taiLich() {
        tbLich.removeAllViews();
        daDat = db.layGioDaDat(sanId, ngay);
        int gioHienTai = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        TableRow hd = new TableRow(this);
        hd.addView(o("", 64, 36, Color.parseColor("#B3E5FC"), Color.BLACK));
        for (int h = san.gioMo; h < san.gioDong; h++)
            hd.addView(o(h + "h", 60, 36, Color.parseColor("#B3E5FC"), Color.BLACK));
        tbLich.addView(hd);

        for (int s = 1; s <= san.soSan; s++) {
            TableRow row = new TableRow(this);
            row.addView(o("San " + s, 64, 48, Color.parseColor("#D7F0E0"), Color.BLACK));
            for (int h = san.gioMo; h < san.gioDong; h++) {
                final int key = s * 100 + h;
                final TextView cell;
                if (daDat.contains(s + "_" + h)) {
                    cell = o("", 60, 48, Color.parseColor("#FF6B6B"), Color.WHITE);
                } else if (laHomNay && h <= gioHienTai) {
                    cell = o("", 60, 48, Color.parseColor("#9E9E9E"), Color.WHITE);
                } else {
                    cell = o("", 60, 48, chon.contains(key) ? Color.parseColor("#4CAF50") : Color.WHITE, Color.WHITE);
                    cell.setOnClickListener(v -> {
                        if (chon.contains(key)) { chon.remove(key); cell.setBackgroundColor(Color.WHITE); }
                        else { chon.add(key); cell.setBackgroundColor(Color.parseColor("#4CAF50")); }
                        capNhatTong();
                    });
                }
                row.addView(cell);
            }
            tbLich.addView(row);
        }
        capNhatTong();
    }

    void capNhatTong() {
        tvTong.setText("Da chon " + chon.size() + " gio - Tong: " + CoSoDuLieu.tien(chon.size() * san.gia) + " d");
    }

    void tiepTheo() {
        if (chon.isEmpty()) {
            Toast.makeText(this, "Chưa chọn khung giờ nào", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> dsKhoi = new ArrayList<>();
        int cs = -1, bd = 0, kt = 0;
        for (int k : chon) {
            int s = k / 100, h = k % 100;
            if (s == cs && h == kt) kt++;
            else {
                if (cs != -1) dsKhoi.add(cs + ":" + bd + ":" + kt);
                cs = s; bd = h; kt = h + 1;
            }
        }
        dsKhoi.add(cs + ":" + bd + ":" + kt);

        Intent i = new Intent(this, ThanhToanActivity.class);
        i.putExtra("sanId", sanId);
        i.putExtra("khachId", khachId);
        i.putExtra("ngay", ngay);
        i.putStringArrayListExtra("dsKhoi", dsKhoi);
        startActivity(i);
        finish();
    }
}