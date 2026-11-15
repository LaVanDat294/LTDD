package com.example.datsanthethao;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class KhachActivity extends AppCompatActivity {

    CoSoDuLieu db;
    int khachId;
    String hoTen;
    LinearLayout llNoiDung;
    TextView[] tabs;
    int tabHienTai = 0;
    int loaiDangChon = 0;
    String tuKhoa = "";
    int ngayKhamPhaIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khach);

        db = new CoSoDuLieu(this);
        khachId = getIntent().getIntExtra("khachId", getIntent().getIntExtra("id", 2));
        hoTen = getIntent().getStringExtra("hoTen");
        if (hoTen == null || hoTen.isEmpty()) hoTen = "Khách hàng";

        llNoiDung = findViewById(R.id.llNoiDungTab);
        tabs = new TextView[]{
                findViewById(R.id.tabTrangChu),
                findViewById(R.id.tabBanDo),
                findViewById(R.id.tabKhamPha),
                findViewById(R.id.tabNoiBat),
                findViewById(R.id.tabTaiKhoan)
        };

        for (int i = 0; i < tabs.length; i++) {
            final int idx = i;
            tabs[i].setOnClickListener(v -> chonTab(idx));
        }

        chonTab(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        chonTab(tabHienTai);
    }

    int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    GradientDrawable boGoc(int mauNen, int banKinhDp) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(mauNen);
        gd.setCornerRadius(dp(banKinhDp));
        return gd;
    }

    void chonTab(int idx) {
        tabHienTai = idx;
        for (int i = 0; i < tabs.length; i++) {
            tabs[i].setTextColor(Color.parseColor(i == idx ? "#0F5B34" : "#757575"));
            tabs[i].setTypeface(null, i == idx ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        }
        llNoiDung.removeAllViews();
        if (idx == 0) veTrangChu();
        else if (idx == 1) veBanDo();
        else if (idx == 2) veKhamPha();
        else if (idx == 3) veNoiBat();
        else veTaiKhoan();
    }

    void veTrangChu() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundColor(Color.parseColor("#0F5B34"));
        header.setPadding(dp(16), dp(20), dp(16), dp(18));

        String homNayText = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN")).format(new Date());
        TextView tvNgay = new TextView(this);
        tvNgay.setText(homNayText);
        tvNgay.setTextColor(Color.parseColor("#C8E6C9"));
        tvNgay.setTextSize(13);
        header.addView(tvNgay);

        LinearLayout rowTop = new LinearLayout(this);
        rowTop.setOrientation(LinearLayout.HORIZONTAL);
        rowTop.setGravity(Gravity.CENTER_VERTICAL);
        TextView tvTen = new TextView(this);
        tvTen.setText("⚽ " + hoTen);
        tvTen.setTextColor(Color.WHITE);
        tvTen.setTextSize(19);
        tvTen.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTen.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        rowTop.addView(tvTen);

        TextView btnChuong = new TextView(this);
        btnChuong.setText("🔔 Thông báo");
        btnChuong.setTextColor(Color.WHITE);
        btnChuong.setBackground(boGoc(Color.parseColor("#1B7A46"), 16));
        btnChuong.setPadding(dp(10), dp(6), dp(10), dp(6));
        btnChuong.setOnClickListener(v -> hienThongBaoOffline());
        rowTop.addView(btnChuong);
        header.addView(rowTop);

        EditText edtTim = new EditText(this);
        edtTim.setHint("🔍 Tìm tên sân bóng, địa chỉ...");
        edtTim.setText(tuKhoa);
        edtTim.setBackground(boGoc(Color.WHITE, 20));
        edtTim.setPadding(dp(14), dp(10), dp(14), dp(10));
        LinearLayout.LayoutParams lpTim = new LinearLayout.LayoutParams(-1, -2);
        lpTim.setMargins(0, dp(12), 0, 0);
        edtTim.setLayoutParams(lpTim);
        header.addView(edtTim);
        llNoiDung.addView(header);

        HorizontalScrollView hsv = new HorizontalScrollView(this);
        hsv.setPadding(dp(12), dp(12), dp(12), dp(6));
        LinearLayout llChip = new LinearLayout(this);
        llChip.setOrientation(LinearLayout.HORIZONTAL);

        int[] loaiArr = {0, 5, 7, 11};
        String[] tenArr = {"⚽ Tất cả sân", "Sân 5 người", "Sân 7 người", "Sân 11 người"};
        LinearLayout dsCardContainer = new LinearLayout(this);
        dsCardContainer.setOrientation(LinearLayout.VERTICAL);
        dsCardContainer.setPadding(dp(14), dp(6), dp(14), dp(16));

        for (int i = 0; i < loaiArr.length; i++) {
            final int l = loaiArr[i];
            TextView chip = new TextView(this);
            chip.setText(tenArr[i]);
            chip.setPadding(dp(14), dp(8), dp(14), dp(8));
            chip.setBackground(boGoc(loaiDangChon == l ? Color.parseColor("#0F5B34") : Color.WHITE, 18));
            chip.setTextColor(loaiDangChon == l ? Color.WHITE : Color.parseColor("#0F5B34"));
            LinearLayout.LayoutParams lpC = new LinearLayout.LayoutParams(-2, -2);
            lpC.setMargins(0, 0, dp(8), 0);
            chip.setLayoutParams(lpC);
            chip.setOnClickListener(v -> {
                loaiDangChon = l;
                chonTab(0);
            });
            llChip.addView(chip);
        }
        hsv.addView(llChip);
        llNoiDung.addView(hsv);
        llNoiDung.addView(dsCardContainer);

        Runnable capNhatDs = () -> {
            dsCardContainer.removeAllViews();
            List<CoSoDuLieu.San> ds = db.timSan(tuKhoa, loaiDangChon);
            if (ds.isEmpty()) {
                TextView empty = new TextView(this);
                empty.setText("Không tìm thấy sân bóng phù hợp.");
                empty.setPadding(dp(10), dp(20), dp(10), dp(20));
                dsCardContainer.addView(empty);
                return;
            }
            for (CoSoDuLieu.San s : ds) {
                dsCardContainer.addView(taoTheSan(s, false));
            }
        };

        edtTim.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tuKhoa = s.toString().trim();
                capNhatDs.run();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        capNhatDs.run();
    }

    LinearLayout taoTheSan(CoSoDuLieu.San s, boolean laNoiBat) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(boGoc(Color.WHITE, 14));
        LinearLayout.LayoutParams lpCard = new LinearLayout.LayoutParams(-1, -2);
        lpCard.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(lpCard);

        if (s.anhSan != null && !s.anhSan.isEmpty()) {
            for (String p : s.anhSan.split(",")) {
                if (!p.trim().isEmpty() && new File(p.trim()).exists()) {
                    ImageView ivAnh = new ImageView(this);
                    ivAnh.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(145)));
                    ivAnh.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ivAnh.setImageBitmap(ThemSanActivity.decodeAnToan(p.trim(), 600));
                    card.addView(ivAnh);
                    break;
                }
            }
        }

        LinearLayout banner = new LinearLayout(this);
        banner.setOrientation(LinearLayout.HORIZONTAL);
        banner.setBackgroundColor(Color.parseColor(laNoiBat ? "#1B5E20" : "#2E7D32"));
        banner.setPadding(dp(12), dp(8), dp(12), dp(8));
        TextView tvTag = new TextView(this);
        tvTag.setText((laNoiBat ? "🔥 NỔI BẬT • " : "★ ĐƠN NGÀY • ") + "Sân " + s.loai + " người (" + s.soSan + " sân con)");
        tvTag.setTextColor(Color.WHITE);
        tvTag.setTextSize(12);
        tvTag.setTypeface(null, android.graphics.Typeface.BOLD);
        banner.addView(tvTag);
        card.addView(banner);

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.HORIZONTAL);
        body.setGravity(Gravity.CENTER_VERTICAL);
        body.setPadding(dp(12), dp(12), dp(12), dp(12));

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));

        TextView tvTen = new TextView(this);
        tvTen.setText(s.ten);
        tvTen.setTextColor(Color.parseColor("#111111"));
        tvTen.setTextSize(16);
        tvTen.setTypeface(null, android.graphics.Typeface.BOLD);
        info.addView(tvTen);

        TextView tvDc = new TextView(this);
        tvDc.setText("📍 " + s.diaChi);
        tvDc.setTextColor(Color.parseColor("#555555"));
        tvDc.setTextSize(12);
        info.addView(tvDc);

        TextView tvGioGia = new TextView(this);
        tvGioGia.setText(String.format(Locale.US, "🕒 %02d:00 - %02d:00  |  💰 %s đ/giờ",
                s.gioMo, s.gioDong, CoSoDuLieu.tien(s.gia)));
        tvGioGia.setTextColor(Color.parseColor("#0F5B34"));
        tvGioGia.setTextSize(12);
        tvGioGia.setTypeface(null, android.graphics.Typeface.BOLD);
        info.addView(tvGioGia);
        body.addView(info);

        Button btnDat = new Button(this);
        btnDat.setText("ĐẶT LỊCH");
        btnDat.setBackgroundColor(Color.parseColor("#F5B82E"));
        btnDat.setTextColor(Color.WHITE);
        btnDat.setTextSize(12);
        btnDat.setOnClickListener(v -> startActivity(new Intent(this, ChiTietSanActivity.class)
                .putExtra("sanId", s.id).putExtra("khachId", khachId)));
        body.addView(btnDat);

        card.addView(body);
        card.setOnClickListener(v -> startActivity(new Intent(this, ChiTietSanActivity.class)
                .putExtra("sanId", s.id).putExtra("khachId", khachId)));
        return card;
    }

    void veBanDo() {
        TextView title = new TextView(this);
        title.setText("🗺️ BẢN ĐỒ SÂN BÓNG KHU VỰC (OFFLINE)");
        title.setBackgroundColor(Color.parseColor("#0F5B34"));
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(dp(16), dp(16), dp(16), dp(16));
        llNoiDung.addView(title);

        OfflineMapView mapView = new OfflineMapView(this);
        mapView.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(290)));
        llNoiDung.addView(mapView);

        TextView tvKhuVuc = new TextView(this);
        tvKhuVuc.setText("Chạm vào ghim trên bản đồ để lọc sân (Đang xem: Tất cả Hà Nội)");
        tvKhuVuc.setPadding(dp(14), dp(10), dp(14), dp(6));
        tvKhuVuc.setTextColor(Color.parseColor("#0F5B34"));
        tvKhuVuc.setTypeface(null, android.graphics.Typeface.BOLD);
        llNoiDung.addView(tvKhuVuc);

        LinearLayout boxSan = new LinearLayout(this);
        boxSan.setOrientation(LinearLayout.VERTICAL);
        boxSan.setPadding(dp(14), dp(4), dp(14), dp(16));
        llNoiDung.addView(boxSan);

        for (CoSoDuLieu.San s : db.layDanhSachSan(0)) boxSan.addView(taoTheSan(s, false));

        mapView.setOnChonKhuVucListener((khuVuc, key) -> {
            tvKhuVuc.setText("📍 Khu vực đang chọn: " + khuVuc);
            boxSan.removeAllViews();
            List<CoSoDuLieu.San> ds = key.isEmpty() ? db.layDanhSachSan(0) : db.timSan(key, 0);
            if (ds.isEmpty()) ds = db.layDanhSachSan(0);
            for (CoSoDuLieu.San s : ds) boxSan.addView(taoTheSan(s, false));
        });
    }

    void veKhamPha() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundColor(Color.parseColor("#0F5B34"));
        header.setPadding(dp(16), dp(18), dp(16), dp(14));

        TextView tvTieuDe = new TextView(this);
        tvTieuDe.setText("KHÁM PHÁ LỊCH SÂN TRỐNG");
        tvTieuDe.setTextColor(Color.WHITE);
        tvTieuDe.setTextSize(19);
        tvTieuDe.setTypeface(null, android.graphics.Typeface.BOLD);
        header.addView(tvTieuDe);
        llNoiDung.addView(header);

        HorizontalScrollView hsv = new HorizontalScrollView(this);
        hsv.setPadding(dp(12), dp(10), dp(12), dp(8));
        LinearLayout llNgay = new LinearLayout(this);
        llNgay.setOrientation(LinearLayout.HORIZONTAL);
        String[] thu = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        String[] dsNgay = new String[7];

        for (int i = 0; i < 7; i++) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, i);
            dsNgay[i] = String.format(Locale.US, "%04d-%02d-%02d", c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
            String nhan = (i == 0 ? "Hôm nay" : thu[c.get(Calendar.DAY_OF_WEEK) - 1]) + "\n"
                    + String.format(Locale.US, "%02d/%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1);
            TextView tv = new TextView(this);
            tv.setText(nhan);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(dp(14), dp(8), dp(14), dp(8));
            tv.setBackground(boGoc(i == ngayKhamPhaIndex ? Color.parseColor("#0F5B34") : Color.WHITE, 12));
            tv.setTextColor(i == ngayKhamPhaIndex ? Color.WHITE : Color.BLACK);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
            lp.setMargins(0, 0, dp(8), 0);
            tv.setLayoutParams(lp);
            final int k = i;
            tv.setOnClickListener(v -> {
                ngayKhamPhaIndex = k;
                chonTab(2);
            });
            llNgay.addView(tv);
        }
        hsv.addView(llNgay);
        llNoiDung.addView(hsv);

        List<CoSoDuLieu.San> dsSan = db.layDanhSachSan(0);
        String ngayChon = dsNgay[ngayKhamPhaIndex];

        TextView tvThongKe = new TextView(this);
        tvThongKe.setText(dsSan.size() + " cụm sân bóng • Ngày " + ngayChon);
        tvThongKe.setPadding(dp(14), dp(4), dp(14), dp(8));
        tvThongKe.setTypeface(null, android.graphics.Typeface.BOLD);
        llNoiDung.addView(tvThongKe);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(14), 0, dp(14), dp(16));

        for (CoSoDuLieu.San s : dsSan) {
            Set<String> daDat = db.layGioDaDat(s.id, ngayChon);
            int tongO = s.soSan * (s.gioDong - s.gioMo);
            int conTrong = Math.max(0, tongO - daDat.size());

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackground(boGoc(Color.WHITE, 12));
            card.setPadding(dp(14), dp(12), dp(14), dp(12));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 0, 0, dp(10));
            card.setLayoutParams(lp);

            TextView t1 = new TextView(this);
            t1.setText(s.ten + " (Sân " + s.loai + ")");
            t1.setTextSize(16);
            t1.setTypeface(null, android.graphics.Typeface.BOLD);
            card.addView(t1);

            TextView t2 = new TextView(this);
            t2.setText("🟢 Còn trống " + conTrong + "/" + tongO + " ca giờ  •  Giá: " + CoSoDuLieu.tien(s.gia) + " đ/h");
            t2.setTextColor(Color.parseColor("#0F5B34"));
            t2.setPadding(0, dp(4), 0, dp(8));
            card.addView(t2);

            Button btn = new Button(this);
            btn.setText("+ Đặt chỗ khung giờ trống");
            btn.setBackgroundColor(Color.parseColor("#E8F5E9"));
            btn.setTextColor(Color.parseColor("#0F5B34"));
            btn.setOnClickListener(v -> startActivity(new Intent(this, DatSanActivity.class)
                    .putExtra("sanId", s.id).putExtra("khachId", khachId)));
            card.addView(btn);
            box.addView(card);
        }
        llNoiDung.addView(box);
    }

    void veNoiBat() {
        TextView header = new TextView(this);
        header.setText("🔥 SÂN BÓNG NỔI BẬT & ĐÁNH GIÁ CAO");
        header.setBackgroundColor(Color.parseColor("#0F5B34"));
        header.setTextColor(Color.WHITE);
        header.setTextSize(18);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setPadding(dp(16), dp(18), dp(16), dp(18));
        llNoiDung.addView(header);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(14), dp(12), dp(14), dp(16));

        for (CoSoDuLieu.San s : db.layDanhSachSan(0)) {
            box.addView(taoTheSan(s, true));
        }
        llNoiDung.addView(box);
    }

    void veTaiKhoan() {
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.VERTICAL);
        top.setBackgroundColor(Color.parseColor("#0F5B34"));
        top.setPadding(dp(20), dp(24), dp(20), dp(24));

        TextView tvName = new TextView(this);
        tvName.setText("👤 " + hoTen);
        tvName.setTextColor(Color.WHITE);
        tvName.setTextSize(20);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        top.addView(tvName);

        TextView tvSub = new TextView(this);
        tvSub.setText("Thành viên Khách đặt sân • Chế độ Offline");
        tvSub.setTextColor(Color.parseColor("#C8E6C9"));
        top.addView(tvSub);
        llNoiDung.addView(top);

        LinearLayout menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setPadding(dp(16), dp(16), dp(16), dp(16));

        themDongMenu(menu, "📅 Lịch đã đặt & Thanh toán", () -> startActivity(
                new Intent(this, DonHangActivity.class).putExtra("id", khachId).putExtra("vaitro", "KHACH")));
        themDongMenu(menu, "🔔 Thông báo xác nhận đặt sân", this::hienThongBaoOffline);
        themDongMenu(menu, "📜 Điều khoản sử dụng", this::hienDieuKhoan);
        themDongMenu(menu, "🚪 Đăng xuất / Đổi tài khoản", () -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        llNoiDung.addView(menu);
    }

    void themDongMenu(LinearLayout parent, String text, Runnable action) {
        TextView item = new TextView(this);
        item.setText(text);
        item.setTextSize(16);
        item.setTextColor(Color.parseColor("#222222"));
        item.setBackground(boGoc(Color.WHITE, 12));
        item.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 0, 0, dp(10));
        item.setLayoutParams(lp);
        item.setOnClickListener(v -> action.run());
        parent.addView(item);
    }

    void hienThongBaoOffline() {
        List<CoSoDuLieu.DonDat> ds = db.layDon(khachId, false);
        StringBuilder sb = new StringBuilder();
        if (ds.isEmpty()) {
            sb.append("Chưa có thông báo đặt sân nào.");
        } else {
            for (CoSoDuLieu.DonDat d : ds) {
                String tt = d.trangThai.equals("DATHANHTOAN") ? "ĐÃ ĐƯỢC XÁC NHẬN "
                        : d.trangThai.equals("HUY") ? "ĐÃ HỦY " : "ĐANG CHỜ XÁC NHẬN ⏳";
                sb.append("• Đơn #").append(d.id).append(" - ").append(d.tenSan)
                        .append(" (").append(d.ngay).append(", ").append(d.batDau).append("h): ")
                        .append(tt).append("\n\n");
            }
        }
        new AlertDialog.Builder(this).setTitle("Thông báo đặt lịch").setMessage(sb.toString())
                .setPositiveButton("Đóng", null).show();
    }

    void hienDieuKhoan() {
        String nd = "ĐIỀU KHOẢN SỬ DỤNG ỨNG DỤNG ĐẶT SÂN BÓNG\n\n"
                + "1. Phạm vi áp dụng: Ứng dụng hỗ trợ tìm kiếm và đặt lịch sân bóng đá trực tiếp trên thiết bị (Offline).\n"
                + "2. Đặt chỗ & Thanh toán: Khách hàng chọn khung giờ trống, chọn phương thức tiền mặt hoặc chuyển khoản theo QR của chủ sân.\n"
                + "3. Hủy lịch: Vui lòng hủy trước giờ bóng lăn nếu thay đổi kế hoạch để chủ sân mở lại khung giờ.";
        new AlertDialog.Builder(this).setTitle("Điều khoản sử dụng").setMessage(nd)
                .setPositiveButton("Đồng ý", null).show();
    }
}