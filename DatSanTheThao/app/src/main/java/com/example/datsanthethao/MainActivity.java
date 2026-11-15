package com.example.datsanthethao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    CoSoDuLieu db;
    EditText edtUser, edtPass, edtHoTen;
    RadioGroup rgVaiTro;
    Button btnDangNhap, btnCheDoDangKy;
    boolean dangKyMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new CoSoDuLieu(this);
        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        edtHoTen = findViewById(R.id.edtHoTen);
        rgVaiTro = findViewById(R.id.rgVaiTro);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        btnCheDoDangKy = findViewById(R.id.btnCheDoDangKy);

        rgVaiTro.setOnCheckedChangeListener((group, checkedId) -> {
            if (!dangKyMode) {
                edtUser.setText(checkedId == R.id.rbKhach ? "khach" : "chusan");
                edtPass.setText("123");
            }
        });

        btnCheDoDangKy.setOnClickListener(v -> {
            dangKyMode = !dangKyMode;
            edtHoTen.setVisibility(dangKyMode ? View.VISIBLE : View.GONE);
            btnDangNhap.setText(dangKyMode ? "XÁC NHẬN ĐĂNG KÝ" : "ĐĂNG NHẬP");
            btnCheDoDangKy.setText(dangKyMode ? "Quay lại Đăng nhập" : "Chưa có tài khoản? Đăng ký mới Offline");
            if (dangKyMode) {
                edtUser.setText("");
                edtPass.setText("");
                edtHoTen.setText("");
            }
        });

        btnDangNhap.setOnClickListener(v -> {
            String u = edtUser.getText().toString().trim();
            String p = edtPass.getText().toString().trim();
            String vaiTro = rgVaiTro.getCheckedRadioButtonId() == R.id.rbKhach ? "KHACH" : "CHUSAN";

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dangKyMode) {
                String ten = edtHoTen.getText().toString().trim();
                if (ten.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (db.dangKy(u, p, ten, vaiTro)) {
                    Toast.makeText(this, "Đăng ký thành công! Đang đăng nhập...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String[] tk = db.dangNhap(u, p, vaiTro);
            if (tk == null) {
                Toast.makeText(this, "Sai tài khoản, mật khẩu hoặc vai trò!", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = Integer.parseInt(tk[0]);
            String hoTen = tk[1];

            if ("KHACH".equals(vaiTro)) {
                Intent i = new Intent(this, KhachActivity.class);
                i.putExtra("id", userId);
                i.putExtra("khachId", userId);
                i.putExtra("hoTen", hoTen);
                startActivity(i);
            } else {
                Intent i = new Intent(this, ChuSanActivity.class);
                i.putExtra("id", userId);
                i.putExtra("hoTen", hoTen);
                startActivity(i);
            }
            finish();
        });
    }
}