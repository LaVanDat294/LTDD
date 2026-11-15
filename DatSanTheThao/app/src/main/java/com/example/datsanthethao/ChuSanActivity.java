package com.example.datsanthethao;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChuSanActivity extends AppCompatActivity {

    CoSoDuLieu db;
    int chuId;
    String hoTen;
    String trangThaiDangXem = "CHOTHANHTOAN";

    TextView tvDoanhThuNgay, tvDoanhThuThang, tvGioDatSan;
    Button btnTabChuaXN, btnTabDaXN, btnTabDaHuy;
    LinearLayout llDanhSachDonChu, llDanhSachSanChu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chu_san);

        db = new CoSoDuLieu(this);
        chuId = getIntent().getIntExtra("id", 1);
        hoTen = getIntent().getStringExtra("hoTen");
        if (hoTen == null) hoTen = "Chủ sân";

        ((TextView) findViewById(R.id.tvTenChuSan)).setText("🏟️ " + hoTen);
        tvDoanhThuNgay = findViewById(R.id.tvDoanhThuNgay);
        tvDoanhThuThang = findViewById(R.id.tvDoanhThuThang);
        tvGioDatSan = findViewById(R.id.tvGioDatSan);

        btnTabChuaXN = findViewById(R.id.btnTabChuaXN);
        btnTabDaXN = findViewById(R.id.btnTabDaXN);
        btnTabDaHuy = findViewById(R.id.btnTabDaHuy);
        llDanhSachDonChu = findViewById(R.id.llDanhSachDonChu);
        llDanhSachSanChu = findViewById(R.id.llDanhSachSanChu);

        btnTabChuaXN.setOnClickListener(v -> { trangThaiDangXem = "CHOTHANHTOAN"; taiTatCa(); });
        btnTabDaXN.setOnClickListener(v -> { trangThaiDangXem = "DATHANHTOAN"; taiTatCa(); });
        btnTabDaHuy.setOnClickListener(v -> { trangThaiDangXem = "HUY"; taiTatCa(); });

        findViewById(R.id.btnThemSanMoi).setOnClickListener(v ->
                startActivity(new Intent(this, ThemSanActivity.class).putExtra("id", chuId)));

        findViewById(R.id.btnCaiDatQR).setOnClickListener(v -> moCaiDatNganHang());

        findViewById(R.id.btnDangXuatChu).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiTatCa();
    }

    void taiTatCa() {
        String homNay = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String thangNay = new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date());
        ((TextView) findViewById(R.id.tvNgayHomNayChu)).setText("Ngày: " + homNay);

        int dtNgay = db.doanhThuNgay(chuId, homNay);
        int dtThang = db.doanhThuThang(chuId, thangNay);
        int gioHomNay = db.tongGioDat(chuId, homNay);

        tvDoanhThuNgay.setText(CoSoDuLieu.tien(dtNgay) + " đ");
        tvDoanhThuThang.setText(CoSoDuLieu.tien(dtThang) + " đ");
        tvGioDatSan.setText("⏱️ Giờ đặt sân hôm nay: " + gioHomNay + " giờ");

        capNhatMauTab(btnTabChuaXN, "CHOTHANHTOAN".equals(trangThaiDangXem), "#E65100");
        capNhatMauTab(btnTabDaXN, "DATHANHTOAN".equals(trangThaiDangXem), "#0F5B34");
        capNhatMauTab(btnTabDaHuy, "HUY".equals(trangThaiDangXem), "#C62828");

        taiDanhSachDon();
        taiDanhSachSan();
    }

    void capNhatMauTab(Button b, boolean active, String hexColor) {
        b.setBackgroundColor(Color.parseColor(active ? hexColor : "#E0E0E0"));
        b.setTextColor(active ? Color.WHITE : Color.parseColor("#333333"));
    }

    void taiDanhSachDon() {
        llDanhSachDonChu.removeAllViews();
        List<CoSoDuLieu.DonDat> ds = db.layDonTheoTrangThai(chuId, trangThaiDangXem);
        if (ds.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("Không có đơn đặt nào trong mục này.");
            tv.setBackgroundColor(Color.WHITE);
            tv.setPadding(24, 24, 24, 24);
            llDanhSachDonChu.addView(tv);
            return;
        }

        for (CoSoDuLieu.DonDat d : ds) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundColor(Color.WHITE);
            card.setPadding(24, 20, 24, 20);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 0, 0, 14);
            card.setLayoutParams(lp);

            TextView tvInfo = new TextView(this);
            String nhanTT = d.trangThai.equals("DATHANHTOAN") ? "✅ Đã xác nhận"
                    : d.trangThai.equals("HUY") ? "❌ Đã hủy bởi khách hàng" : "⏳ Chưa xác nhận";
            String pt = (d.phuongThuc == null || d.phuongThuc.isEmpty()) ? "Tiền mặt" : d.phuongThuc;
            tvInfo.setText("Đơn #" + d.id + " • " + d.tenSan + " (Sân con " + d.sanSo + ")\n"
                    + "Khách đặt: " + d.tenKhach + "\n"
                    + "Thời gian: " + d.ngay + " | " + d.batDau + "h - " + (d.batDau + d.soGio) + "h (" + d.soGio + " giờ)\n"
                    + "Tổng tiền: " + CoSoDuLieu.tien(d.tongTien) + " đ (" + pt + ")  •  " + nhanTT);
            tvInfo.setTextColor(Color.parseColor("#222222"));
            card.addView(tvInfo);

            if ("CHOTHANHTOAN".equals(d.trangThai)) {
                LinearLayout rowBtn = new LinearLayout(this);
                rowBtn.setOrientation(LinearLayout.HORIZONTAL);
                rowBtn.setPadding(0, 12, 0, 0);

                Button btnXacNhan = new Button(this);
                btnXacNhan.setText("✓ Xác nhận");
                btnXacNhan.setBackgroundColor(Color.parseColor("#0F5B34"));
                btnXacNhan.setTextColor(Color.WHITE);
                btnXacNhan.setOnClickListener(v -> {
                    db.capNhatTrangThai(d.id, "DATHANHTOAN");
                    Toast.makeText(this, "Đã xác nhận đơn #" + d.id, Toast.LENGTH_SHORT).show();
                    taiTatCa();
                });

                Button btnHuy = new Button(this);
                btnHuy.setText("✕ Hủy đơn");
                btnHuy.setBackgroundColor(Color.parseColor("#D32F2F"));
                btnHuy.setTextColor(Color.WHITE);
                LinearLayout.LayoutParams lpHuy = new LinearLayout.LayoutParams(-2, -2);
                lpHuy.setMargins(16, 0, 0, 0);
                btnHuy.setLayoutParams(lpHuy);
                btnHuy.setOnClickListener(v -> {
                    db.capNhatTrangThai(d.id, "HUY");
                    Toast.makeText(this, "Đã hủy đơn #" + d.id, Toast.LENGTH_SHORT).show();
                    taiTatCa();
                });

                rowBtn.addView(btnXacNhan);
                rowBtn.addView(btnHuy);
                card.addView(rowBtn);
            }
            llDanhSachDonChu.addView(card);
        }
    }

    void taiDanhSachSan() {
        llDanhSachSanChu.removeAllViews();
        List<CoSoDuLieu.San> ds = db.layDanhSachSan(chuId);
        for (CoSoDuLieu.San s : ds) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundColor(Color.WHITE);
            card.setPadding(24, 20, 24, 20);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 0, 0, 14);
            card.setLayoutParams(lp);

            TextView tv = new TextView(this);
            tv.setText(s.ten + " (Sân " + s.loai + " người - " + s.soSan + " sân con)\n"
                    + "Địa chỉ: " + s.diaChi + "\n"
                    + "Giá: " + CoSoDuLieu.tien(s.gia) + " đ/giờ | Giờ mở: " + s.gioMo + "h - " + s.gioDong + "h");
            tv.setTextColor(Color.parseColor("#222222"));
            card.addView(tv);

            LinearLayout rowAction = new LinearLayout(this);
            rowAction.setOrientation(LinearLayout.HORIZONTAL);
            rowAction.setPadding(0, 12, 0, 0);

            Button btnSua = new Button(this);
            btnSua.setText("✏️ Sửa sân");
            btnSua.setBackgroundColor(Color.parseColor("#1565C0"));
            btnSua.setTextColor(Color.WHITE);
            btnSua.setOnClickListener(v -> startActivity(
                    new Intent(this, ThemSanActivity.class).putExtra("id", chuId).putExtra("sanId", s.id)));

            Button btnXoa = new Button(this);
            btnXoa.setText("🗑️ Xóa sân");
            btnXoa.setBackgroundColor(Color.parseColor("#C62828"));
            btnXoa.setTextColor(Color.WHITE);
            LinearLayout.LayoutParams lpXoa = new LinearLayout.LayoutParams(-2, -2);
            lpXoa.setMargins(16, 0, 0, 0);
            btnXoa.setLayoutParams(lpXoa);
            btnXoa.setOnClickListener(v -> new AlertDialog.Builder(this)
                    .setTitle("Xóa sân bóng")
                    .setMessage("Bạn có chắc muốn xóa \"" + s.ten + "\"?")
                    .setPositiveButton("Xóa", (d, w) -> {
                        db.xoaSan(s.id);
                        Toast.makeText(this, "Đã xóa sân", Toast.LENGTH_SHORT).show();
                        taiTatCa();
                    })
                    .setNegativeButton("Hủy", null).show());

            rowAction.addView(btnSua);
            rowAction.addView(btnXoa);
            card.addView(rowAction);
            llDanhSachSanChu.addView(card);
        }
    }

    void moCaiDatNganHang() {
        String[] ch = db.layCauHinh(chuId);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(40, 20, 40, 20);

        EditText edtNh = new EditText(this);
        edtNh.setHint("Tên ngân hàng (vd: MB Bank, Vietcombank)");
        edtNh.setText(ch[0]);

        EditText edtStk = new EditText(this);
        edtStk.setHint("Số tài khoản");
        edtStk.setText(ch[1]);

        EditText edtTen = new EditText(this);
        edtTen.setHint("Tên chủ tài khoản");
        edtTen.setText(ch[2]);

        box.addView(edtNh);
        box.addView(edtStk);
        box.addView(edtTen);

        new AlertDialog.Builder(this).setTitle("Cài đặt tài khoản nhận tiền")
                .setView(box)
                .setPositiveButton("Lưu", (d, w) -> {
                    db.luuCauHinh(chuId, edtNh.getText().toString().trim(),
                            edtStk.getText().toString().trim(), edtTen.getText().toString().trim(), ch[3]);
                    Toast.makeText(this, "Đã lưu thông tin thanh toán", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null).show();
    }
}