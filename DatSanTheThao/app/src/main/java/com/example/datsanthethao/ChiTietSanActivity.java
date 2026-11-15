package com.example.datsanthethao;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.Locale;

public class ChiTietSanActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_chi_tiet_san);
        CoSoDuLieu db = new CoSoDuLieu(this);
        int sanId = getIntent().getIntExtra("sanId", 0);
        int khachId = getIntent().getIntExtra("khachId", 0);
        CoSoDuLieu.San s = db.laySan(sanId);
        if (s == null) { finish(); return; }

        findViewById(R.id.tvQuayLai).setOnClickListener(v -> finish());
        TextView tvBanner = findViewById(R.id.tvBanner);
        tvBanner.setText("SAN BONG DA\n" + s.loai + " NGUOI");

        if (s.anhSan != null && !s.anhSan.isEmpty() && tvBanner.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) tvBanner.getParent();
            HorizontalScrollView hsv = new HorizontalScrollView(this);
            LinearLayout rowAnh = new LinearLayout(this);
            rowAnh.setOrientation(LinearLayout.HORIZONTAL);
            int pad = (int) (8 * getResources().getDisplayMetrics().density);
            rowAnh.setPadding(pad, pad, pad, pad);

            boolean coAnh = false;
            for (String p : s.anhSan.split(",")) {
                if (!p.trim().isEmpty() && new File(p.trim()).exists()) {
                    coAnh = true;
                    ImageView iv = new ImageView(this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            (int) (240 * getResources().getDisplayMetrics().density),
                            (int) (150 * getResources().getDisplayMetrics().density));
                    lp.setMargins(0, 0, pad, 0);
                    iv.setLayoutParams(lp);
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv.setImageBitmap(ThemSanActivity.decodeAnToan(p.trim(), 500));
                    rowAnh.addView(iv);
                }
            }
            if (coAnh) {
                hsv.addView(rowAnh);
                parent.addView(hsv, parent.indexOfChild(tvBanner) + 1);
            }
        }

        ((TextView) findViewById(R.id.tvTen)).setText(s.ten);
        ((TextView) findViewById(R.id.tvLoai)).setText("Bong da - San " + s.loai + " nguoi");
        ((TextView) findViewById(R.id.tvDiaChi)).setText("Dia chi: " + s.diaChi);
        ((TextView) findViewById(R.id.tvGio)).setText(String.format(Locale.US,
                "Gio mo cua: %02d:00 - %02d:00 (%d san con)", s.gioMo, s.gioDong, s.soSan));
        ((TextView) findViewById(R.id.tvSdt)).setText("Lien he: " + s.sdt);
        ((TextView) findViewById(R.id.tvGia)).setText("Gia: " + CoSoDuLieu.tien(s.gia) + " d/gio");

        GridLayout gl = findViewById(R.id.glTienIch);
        int m = (int) (4 * getResources().getDisplayMetrics().density);
        if (s.tienIch != null) {
            for (String t : s.tienIch.split(",")) {
                t = t.trim();
                if (t.isEmpty()) continue;
                TextView tv = new TextView(this);
                tv.setText(t);
                tv.setTextColor(0xFF0F5B34);
                tv.setBackgroundResource(R.drawable.bg_tien_ich);
                tv.setPadding(m * 3, m * 2, m * 3, m * 2);
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams(
                        GridLayout.spec(GridLayout.UNDEFINED), GridLayout.spec(GridLayout.UNDEFINED, 1f));
                lp.width = 0;
                lp.setMargins(m, m, m, m);
                tv.setLayoutParams(lp);
                gl.addView(tv);
            }
        }

        findViewById(R.id.btnDatLich).setOnClickListener(v ->
                startActivity(new Intent(this, DatSanActivity.class)
                        .putExtra("sanId", sanId).putExtra("khachId", khachId)));
    }
}