package com.example.datsanthethao;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class ThanhToanActivity extends AppCompatActivity {

    CoSoDuLieu db;
    int donId = 0;
    int sanId = 0;
    int khachId = 2;
    String ngay = "";
    ArrayList<String> dsKhoiStr;
    CoSoDuLieu.San san;
    CoSoDuLieu.DonDat donHienTai;
    int tongTien = 0;

    TextView tvDemNguoc, tvThongTinDon, tvHuongDanTienMat, tvTongTien;
    EditText edtTenKhach, edtSdtKhach, edtGhiChu;
    RadioGroup rgPhuongThuc;
    LinearLayout llKhungChuyenKhoan, llCumNutHanhDong;
    Button btnXacNhan, btnHuyDon;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        db = new CoSoDuLieu(this);
        donId = getIntent().getIntExtra("donId", 0);
        sanId = getIntent().getIntExtra("sanId", 0);
        khachId = getIntent().getIntExtra("khachId", 2);
        ngay = getIntent().getStringExtra("ngay");
        dsKhoiStr = getIntent().getStringArrayListExtra("dsKhoi");

        tvDemNguoc = findViewById(R.id.tvDemNguoc);
        tvThongTinDon = findViewById(R.id.tvThongTinDonTT);
        tvHuongDanTienMat = findViewById(R.id.tvHuongDanTienMat);
        tvTongTien = findViewById(R.id.tvTongTienTT);
        edtTenKhach = findViewById(R.id.edtTenKhachTT);
        edtSdtKhach = findViewById(R.id.edtSdtKhachTT);
        edtGhiChu = findViewById(R.id.edtGhiChuTT);
        rgPhuongThuc = findViewById(R.id.rgPhuongThuc);
        llKhungChuyenKhoan = findViewById(R.id.llKhungChuyenKhoan);
        llCumNutHanhDong = findViewById(R.id.llCumNutHanhDong);
        btnXacNhan = findViewById(R.id.btnXacNhanThanhToan);
        btnHuyDon = findViewById(R.id.btnHuyDonChiTiet);

        findViewById(R.id.tvQuayLaiTT).setOnClickListener(v -> finish());

        if (donId > 0) {
            napDuLieuTuDonCu();
        } else {
            napDuLieuTuDatSanMoi();
        }

        rgPhuongThuc.setOnCheckedChangeListener((group, checkedId) -> {
            boolean laChuyenKhoan = (checkedId == R.id.rbChuyenKhoan);
            tvHuongDanTienMat.setVisibility(laChuyenKhoan ? View.GONE : View.VISIBLE);
            llKhungChuyenKhoan.setVisibility(laChuyenKhoan ? View.VISIBLE : View.GONE);
            btnXacNhan.setText(laChuyenKhoan ? "✓ Tôi đã chuyển khoản" : "✓ Xác nhận đặt sân");
        });

        btnXacNhan.setOnClickListener(v -> xuLyXacNhan());
        btnHuyDon.setOnClickListener(v -> xuLyHuyDonChiTiet());
    }

    void napDuLieuTuDatSanMoi() {
        san = db.laySan(sanId);
        if (san == null || dsKhoiStr == null || dsKhoiStr.isEmpty()) {
            finish();
            return;
        }
        edtTenKhach.setText(db.layTenNguoiDung(khachId));

        StringBuilder sb = new StringBuilder();
        sb.append("• Tên CLB: ").append(san.ten).append(" (Sân ").append(san.loai).append(" người)\n");
        sb.append("• Địa chỉ: ").append(san.diaChi).append("\n");
        sb.append("• Ngày đá: ").append(ngay).append("\n");

        int tongGio = 0;
        for (String item : dsKhoiStr) {
            String[] p = item.split(":");
            int s = Integer.parseInt(p[0]), bd = Integer.parseInt(p[1]), kt = Integer.parseInt(p[2]);
            int gio = kt - bd;
            tongGio += gio;
            sb.append("   - Sân con ").append(s).append(": ").append(bd).append("h00 - ").append(kt)
                    .append("h00 (").append(CoSoDuLieu.tien(gio * san.gia)).append(" đ)\n");
        }
        tongTien = tongGio * san.gia;
        sb.append("• Tổng thời lượng: ").append(tongGio).append(" giờ");

        tvThongTinDon.setText(sb.toString());
        tvTongTien.setText(CoSoDuLieu.tien(tongTien) + " đ");
        hienThongTinNganHang(san.chuId, "DATSAN " + ngay);
        batDauDemNguoc();
    }

    void napDuLieuTuDonCu() {
        donHienTai = db.layChiTietDon(donId);
        if (donHienTai == null) {
            finish();
            return;
        }
        san = db.laySan(donHienTai.sanId);
        khachId = donHienTai.khachId;
        tongTien = donHienTai.tongTien;
        edtTenKhach.setText(donHienTai.tenKhach);
        if (donHienTai.ghiChu != null) edtGhiChu.setText(donHienTai.ghiChu);

        String tt = donHienTai.trangThai.equals("DATHANHTOAN") ? "✅ ĐÃ XÁC NHẬN THANH TOÁN"
                : donHienTai.trangThai.equals("HUY") ? "❌ ĐÃ HỦY ĐƠN" : "⏳ CHỜ XÁC NHẬN / THANH TOÁN";

        String chiTiet = "• Mã đơn: #" + donHienTai.id + "\n"
                + "• Sân bóng: " + donHienTai.tenSan + " (Sân con " + donHienTai.sanSo + ")\n"
                + "• Địa chỉ: " + donHienTai.diaChiSan + "\n"
                + "• Thời gian: " + donHienTai.ngay + " | " + donHienTai.batDau + "h00 - " + (donHienTai.batDau + donHienTai.soGio) + "h00\n"
                + "• Trạng thái: " + tt;
        tvThongTinDon.setText(chiTiet);
        tvTongTien.setText(CoSoDuLieu.tien(tongTien) + " đ");

        if ("Chuyển khoản".equals(donHienTai.phuongThuc)) {
            rgPhuongThuc.check(R.id.rbChuyenKhoan);
            tvHuongDanTienMat.setVisibility(View.GONE);
            llKhungChuyenKhoan.setVisibility(View.VISIBLE);
            btnXacNhan.setText("✓ Tôi đã chuyển khoản");
        }

        hienThongTinNganHang(donHienTai.chuId, "DATSAN #" + donHienTai.id);

        if (!"CHOTHANHTOAN".equals(donHienTai.trangThai)) {
            findViewById(R.id.llDemNguoc).setVisibility(View.GONE);
            llCumNutHanhDong.setVisibility(View.GONE);
        } else {
            batDauDemNguoc();
        }
    }

    void hienThongTinNganHang(int chuId, String noiDungCk) {
        String[] ch = db.layCauHinh(chuId);
        ((TextView) findViewById(R.id.tvChuTkTT)).setText(ch[2]);
        ((TextView) findViewById(R.id.tvSoTkTT)).setText("Số TK: " + ch[1]);
        ((TextView) findViewById(R.id.tvNganHangTT)).setText("Ngân hàng: " + ch[0]);
        ((TextView) findViewById(R.id.tvNoiDungCkTT)).setText("Nội dung CK: " + noiDungCk);

        String duongDanQr = (san != null && san.duongDanQr != null && !san.duongDanQr.isEmpty())
                ? san.duongDanQr : ch[3];

        FrameLayout flQr = findViewById(R.id.flKhungQr);
        flQr.removeAllViews();
        if (duongDanQr != null && !duongDanQr.isEmpty() && new File(duongDanQr).exists()) {
            ImageView img = new ImageView(this);
            img.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            img.setImageBitmap(ThemSanActivity.decodeAnToan(duongDanQr, 500));
            flQr.addView(img);
        } else {
            TextView tvChuaCoQr = new TextView(this);
            tvChuaCoQr.setText("Chủ sân\nchưa tải\nảnh QR");
            tvChuaCoQr.setGravity(Gravity.CENTER);
            tvChuaCoQr.setTextSize(12);
            flQr.addView(tvChuaCoQr);
        }

        findViewById(R.id.btnCopyStk).setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("STK", ch[1]));
            Toast.makeText(this, "Đã sao chép số tài khoản: " + ch[1], Toast.LENGTH_SHORT).show();
        });
    }

    void batDauDemNguoc() {
        timer = new CountDownTimer(359000, 1000) {
            @Override public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                tvDemNguoc.setText(String.format(Locale.US, "%02d:%02d", sec / 60, sec % 60));
            }
            @Override public void onFinish() {
                tvDemNguoc.setText("00:00 (Hết thời gian giữ chỗ)");
            }
        }.start();
    }

    void xuLyXacNhan() {
        boolean laChuyenKhoan = (rgPhuongThuc.getCheckedRadioButtonId() == R.id.rbChuyenKhoan);
        String phuongThuc = laChuyenKhoan ? "Chuyển khoản" : "Tiền mặt";
        String ghiChu = "SĐT: " + edtSdtKhach.getText().toString().trim() + " - " + edtGhiChu.getText().toString().trim();

        String tieuDe = laChuyenKhoan ? "Xác nhận đã chuyển khoản" : "Xác nhận đặt sân thanh toán tiền mặt";
        String loiNhan = laChuyenKhoan
                ? "Bạn xác nhận đã chuyển khoản " + CoSoDuLieu.tien(tongTien) + " đ theo mã QR của chủ sân?"
                : "Xác nhận đặt lịch với hình thức Tiền mặt (" + CoSoDuLieu.tien(tongTien) + " đ) thanh toán trực tiếp tại sân?";

        new AlertDialog.Builder(this)
                .setTitle(tieuDe)
                .setMessage(loiNhan)
                .setPositiveButton("Đồng ý xác nhận", (d, w) -> {
                    if (donId > 0) {
                        db.capNhatThanhToanDon(donId, "CHOTHANHTOAN", phuongThuc, ghiChu);
                    } else {
                        for (String item : dsKhoiStr) {
                            String[] p = item.split(":");
                            int s = Integer.parseInt(p[0]), bd = Integer.parseInt(p[1]), kt = Integer.parseInt(p[2]);
                            if (db.biTrung(sanId, ngay, s, bd, kt)) {
                                Toast.makeText(this, "Sân " + s + " vừa có người đặt trước, vui lòng chọn lại!", Toast.LENGTH_LONG).show();
                                finish();
                                return;
                            }
                            db.datSanChiTiet(sanId, khachId, ngay, s, bd, kt - bd, (kt - bd) * san.gia,
                                    "CHOTHANHTOAN", phuongThuc, ghiChu);
                        }
                    }
                    Toast.makeText(this, "Đặt sân thành công (" + phuongThuc + ")!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, DonHangActivity.class)
                            .putExtra("id", khachId).putExtra("vaitro", "KHACH"));
                    finish();
                })
                .setNegativeButton("Xem lại", null)
                .show();
    }

    void xuLyHuyDonChiTiet() {
        String[] lyDo = {
                "Thay đổi kế hoạch / Đổi giờ đá",
                "Đặt nhầm loại sân hoặc khung giờ",
                "Tìm được sân khác phù hợp hơn",
                "Lý do cá nhân khác"
        };
        final int[] chonLyDo = {0};

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy đơn đặt sân")
                .setSingleChoiceItems(lyDo, 0, (dlg, which) -> chonLyDo[0] = which)
                .setPositiveButton("Xác nhận hủy đơn", (dlg, w) -> {
                    if (donId > 0) {
                        db.capNhatThanhToanDon(donId, "HUY", "", "Khách hủy: " + lyDo[chonLyDo[0]]);
                        Toast.makeText(this, "Đã hủy đơn #" + donId + " (" + lyDo[chonLyDo[0]] + ")", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Đã hủy yêu cầu đặt sân", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                })
                .setNegativeButton("Giữ lại đơn", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}