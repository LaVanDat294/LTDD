package com.example.datsanthethao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ThemSanActivity extends AppCompatActivity {

    String duongDanQrDaLuu = "";
    List<String> dsAnhSanDaLuu = new ArrayList<>();

    ImageView imgQrPreview;
    TextView tvTrangThaiAnhQr, tvTrangThaiAnhSan;
    HorizontalScrollView hsvAnhSanPreview;
    LinearLayout llAnhSanPreview;

    private final ActivityResultLauncher<String> chonNhieuAnhSanLauncher =
            registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), uris -> {
                if (uris != null && !uris.isEmpty()) {
                    int dem = 0;
                    for (Uri uri : uris) {
                        String path = luuAnhVaoBoNhoTrong(uri, "anh_san_");
                        if (!path.isEmpty()) {
                            dsAnhSanDaLuu.add(path);
                            dem++;
                        }
                    }
                    capNhatPreviewAnhSan();
                    Toast.makeText(this, "Đã thêm " + dem + " ảnh sân!", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> chonAnhQrLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    String path = luuAnhVaoBoNhoTrong(uri, "qr_san_");
                    if (!path.isEmpty()) {
                        duongDanQrDaLuu = path;
                        hienAnhQr(path);
                        Toast.makeText(this, "Đã tải ảnh QR lên!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_them_san);

        CoSoDuLieu db = new CoSoDuLieu(this);
        int chuId = getIntent().getIntExtra("id", 0);
        int sanId = getIntent().getIntExtra("sanId", 0);

        EditText edtTen = findViewById(R.id.edtTenSan);
        EditText edtLoai = findViewById(R.id.edtLoaiSan);
        EditText edtSoSan = findViewById(R.id.edtSoSan);
        EditText edtDiaChi = findViewById(R.id.edtDiaChi);
        EditText edtSdt = findViewById(R.id.edtSdt);
        EditText edtGia = findViewById(R.id.edtGia);
        EditText edtGioMo = findViewById(R.id.edtGioMo);
        EditText edtGioDong = findViewById(R.id.edtGioDong);
        EditText edtTienIch = findViewById(R.id.edtTienIch);

        hsvAnhSanPreview = findViewById(R.id.hsvAnhSanPreview);
        llAnhSanPreview = findViewById(R.id.llAnhSanPreview);
        tvTrangThaiAnhSan = findViewById(R.id.tvTrangThaiAnhSan);
        imgQrPreview = findViewById(R.id.imgQrPreview);
        tvTrangThaiAnhQr = findViewById(R.id.tvTrangThaiAnhQr);
        Button btnLuu = findViewById(R.id.btnLuuSan);

        findViewById(R.id.btnChonAnhSan).setOnClickListener(v -> chonNhieuAnhSanLauncher.launch("image/*"));
        findViewById(R.id.btnChonAnhQr).setOnClickListener(v -> chonAnhQrLauncher.launch("image/*"));

        if (sanId > 0) {
            CoSoDuLieu.San s = db.laySan(sanId);
            if (s != null) {
                ((TextView) findViewById(R.id.tvTieuDeThemSan)).setText("CẬP NHẬT THÔNG TIN SÂN");
                edtTen.setText(s.ten);
                edtLoai.setText(String.valueOf(s.loai));
                edtSoSan.setText(String.valueOf(s.soSan));
                edtDiaChi.setText(s.diaChi);
                edtSdt.setText(s.sdt);
                edtGia.setText(String.valueOf(s.gia));
                edtGioMo.setText(String.valueOf(s.gioMo));
                edtGioDong.setText(String.valueOf(s.gioDong));
                edtTienIch.setText(s.tienIch);

                if (s.anhSan != null && !s.anhSan.isEmpty()) {
                    for (String p : s.anhSan.split(",")) {
                        if (!p.trim().isEmpty() && new File(p.trim()).exists()) {
                            dsAnhSanDaLuu.add(p.trim());
                        }
                    }
                    capNhatPreviewAnhSan();
                }

                if (s.duongDanQr != null && !s.duongDanQr.isEmpty()) {
                    duongDanQrDaLuu = s.duongDanQr;
                    hienAnhQr(duongDanQrDaLuu);
                }
                btnLuu.setText("CẬP NHẬT THÔNG TIN SÂN");
            }
        }

        btnLuu.setOnClickListener(v -> {
            try {
                String ten = edtTen.getText().toString().trim();
                int loai = Integer.parseInt(edtLoai.getText().toString().trim());
                int soSan = Integer.parseInt(edtSoSan.getText().toString().trim());
                String diaChi = edtDiaChi.getText().toString().trim();
                String sdt = edtSdt.getText().toString().trim();
                int gia = Integer.parseInt(edtGia.getText().toString().trim());
                int gioMo = Integer.parseInt(edtGioMo.getText().toString().trim());
                int gioDong = Integer.parseInt(edtGioDong.getText().toString().trim());
                String tienIch = edtTienIch.getText().toString().trim();
                String chuoiAnhSan = TextUtils.join(",", dsAnhSanDaLuu);

                if (ten.isEmpty() || soSan < 1 || soSan > 20 || gioMo < 0 || gioDong > 24 || gioMo >= gioDong)
                    throw new Exception();

                if (sanId > 0) {
                    db.suaSan(sanId, ten, loai, diaChi, gia, soSan, gioMo, gioDong, sdt, tienIch, duongDanQrDaLuu, chuoiAnhSan);
                    Toast.makeText(this, "Đã cập nhật sân & hình ảnh", Toast.LENGTH_SHORT).show();
                } else {
                    db.themSan(ten, loai, diaChi, gia, chuId, soSan, gioMo, gioDong, sdt, tienIch, duongDanQrDaLuu, chuoiAnhSan);
                    Toast.makeText(this, "Đã thêm sân mới", Toast.LENGTH_SHORT).show();
                }
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Nhập đầy đủ và đúng định dạng (giờ mở < giờ đóng, 0-24)", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void capNhatPreviewAnhSan() {
        llAnhSanPreview.removeAllViews();
        if (dsAnhSanDaLuu.isEmpty()) {
            hsvAnhSanPreview.setVisibility(View.GONE);
            tvTrangThaiAnhSan.setText("Chưa có ảnh sân nào");
            tvTrangThaiAnhSan.setTextColor(0xFF757575);
            return;
        }
        hsvAnhSanPreview.setVisibility(View.VISIBLE);
        tvTrangThaiAnhSan.setText("✅ Đã chọn " + dsAnhSanDaLuu.size() + " ảnh (Chạm vào ảnh để xóa)");
        tvTrangThaiAnhSan.setTextColor(0xFF0F5B34);

        int size = (int) (110 * getResources().getDisplayMetrics().density);
        int margin = (int) (8 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < dsAnhSanDaLuu.size(); i++) {
            final int idx = i;
            String path = dsAnhSanDaLuu.get(i);
            ImageView iv = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMargins(0, 0, margin, 0);
            iv.setLayoutParams(lp);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageBitmap(decodeAnToan(path, 300));
            iv.setOnClickListener(v -> new AlertDialog.Builder(this)
                    .setTitle("Xóa ảnh này?")
                    .setMessage("Bạn muốn bỏ ảnh sân số " + (idx + 1) + "?")
                    .setPositiveButton("Xóa", (d, w) -> {
                        dsAnhSanDaLuu.remove(idx);
                        capNhatPreviewAnhSan();
                    })
                    .setNegativeButton("Giữ lại", null).show());
            llAnhSanPreview.addView(iv);
        }
    }

    private void hienAnhQr(String path) {
        if (new File(path).exists()) {
            imgQrPreview.setVisibility(View.VISIBLE);
            imgQrPreview.setImageBitmap(decodeAnToan(path, 400));
            tvTrangThaiAnhQr.setText("✅ Đã gắn ảnh QR thanh toán cho sân");
            tvTrangThaiAnhQr.setTextColor(0xFF0F5B34);
        }
    }

    private String luuAnhVaoBoNhoTrong(Uri uri, String prefix) {
        try (InputStream in = getContentResolver().openInputStream(uri)) {
            if (in == null) return "";
            File file = new File(getFilesDir(), prefix + System.nanoTime() + ".jpg");
            try (OutputStream out = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            return "";
        }
    }

    public static Bitmap decodeAnToan(String path, int reqWidth) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int inSampleSize = 1;
        while (opts.outWidth / inSampleSize > reqWidth * 2) {
            inSampleSize *= 2;
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(path, opts);
    }
}