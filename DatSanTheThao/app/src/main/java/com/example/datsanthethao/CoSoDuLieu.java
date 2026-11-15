package com.example.datsanthethao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CoSoDuLieu extends SQLiteOpenHelper {

    public static class San {
        public int id, chuId, loai, gia, soSan, gioMo, gioDong;
        public String ten, diaChi, sdt, tienIch, duongDanQr, anhSan;
        @Override public String toString() {
            return ten + " (San " + loai + " nguoi, " + soSan + " san con)\n" + diaChi
                    + "\n" + tien(gia) + " d/gio | " + gioMo + "h - " + gioDong + "h";
        }
    }

    public static class DonDat {
        public int id, sanId, khachId, chuId, sanSo, batDau, soGio, tongTien;
        public String ngay, trangThai, tenSan, diaChiSan, tenKhach, phuongThuc, ghiChu;
        @Override public String toString() {
            String tt = trangThai.equals("DATHANHTOAN") ? "Đã xác nhận thanh toán"
                    : trangThai.equals("HUY") ? "Đã hủy" : "Chờ xác nhận";
            String pt = (phuongThuc == null || phuongThuc.isEmpty()) ? "Tiền mặt" : phuongThuc;
            return "#" + id + " - " + tenSan + " (Sân " + sanSo + ")\n"
                    + "Ngày " + ngay + " | " + batDau + "h - " + (batDau + soGio) + "h\n"
                    + "Tổng: " + tien(tongTien) + " đ (" + pt + ") - " + tt;
        }
    }

    public static String tien(int n) { return String.format(Locale.US, "%,d", n); }

    static final String COT_SAN = "id,ten,loai,diachi,gia,chusan_id,sosan,giomo,giodong,sdt,tienich,duongdanqr,anhsan";

    public CoSoDuLieu(Context c) { super(c, "datsan.db", null, 5); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE nguoidung(id INTEGER PRIMARY KEY AUTOINCREMENT, tendangnhap TEXT UNIQUE, matkhau TEXT, hoten TEXT, vaitro TEXT)");
        db.execSQL("CREATE TABLE san(id INTEGER PRIMARY KEY AUTOINCREMENT, ten TEXT, loai INTEGER, diachi TEXT, gia INTEGER, chusan_id INTEGER, sosan INTEGER, giomo INTEGER, giodong INTEGER, sdt TEXT, tienich TEXT, duongdanqr TEXT, anhsan TEXT)");
        db.execSQL("CREATE TABLE datsan(id INTEGER PRIMARY KEY AUTOINCREMENT, san_id INTEGER, khach_id INTEGER, ngay TEXT, sanso INTEGER, giobatdau INTEGER, sogio INTEGER, tongtien INTEGER, trangthai TEXT, phuongthuc TEXT, ghichu TEXT)");
        db.execSQL("CREATE TABLE cauhinh(chusan_id INTEGER PRIMARY KEY, nganhang TEXT, sotk TEXT, tentk TEXT, duongdanqr TEXT)");

        db.execSQL("INSERT INTO nguoidung(tendangnhap,matkhau,hoten,vaitro) VALUES('chusan','123','Chu San A','CHUSAN')");
        db.execSQL("INSERT INTO nguoidung(tendangnhap,matkhau,hoten,vaitro) VALUES('khach','123','Tran Duc Hung','KHACH')");
        db.execSQL("INSERT INTO cauhinh(chusan_id,nganhang,sotk,tentk,duongdanqr) VALUES(1,'OCB - Ngan hang Phuong Dong','0206100002549008','NGO THI CHUONG','')");

        String ti = "Wifi mien phi,Bai dau xe may,Bai dau o to,Phong thay do,Toilet,Den chieu sang,Nuoc uong mien phi,Camera an ninh,Mai che";
        db.execSQL("INSERT INTO san(ten,loai,diachi,gia,chusan_id,sosan,giomo,giodong,sdt,tienich,duongdanqr,anhsan) VALUES('San Bong Thanh Liet 1',5,'Ngo 104 Nguyen An Ninh, Hoang Mai, Ha Noi',200000,1,4,6,22,'0859999468','" + ti + "','','')");
        db.execSQL("INSERT INTO san(ten,loai,diachi,gia,chusan_id,sosan,giomo,giodong,sdt,tienich,duongdanqr,anhsan) VALUES('San Bong Thanh Liet 2',7,'Thanh Liet, Thanh Tri, Ha Noi',350000,1,3,6,22,'0912345678','" + ti + "','','')");
        db.execSQL("INSERT INTO san(ten,loai,diachi,gia,chusan_id,sosan,giomo,giodong,sdt,tienich,duongdanqr,anhsan) VALUES('San Bong 11 Nguoi Hoang Mai',11,'Dai Kim, Hoang Mai, Ha Noi',800000,1,2,6,22,'0987654321','" + ti + "','','')");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int o, int n) {
        db.execSQL("DROP TABLE IF EXISTS nguoidung");
        db.execSQL("DROP TABLE IF EXISTS san");
        db.execSQL("DROP TABLE IF EXISTS datsan");
        db.execSQL("DROP TABLE IF EXISTS cauhinh");
        onCreate(db);
    }

    // ---------- Nguoi dung ----------
    public String[] dangNhap(String u, String p, String vaiTro) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,hoten,vaitro FROM nguoidung WHERE tendangnhap=? AND matkhau=? AND vaitro=?",
                new String[]{u, p, vaiTro});
        String[] r = null;
        if (c.moveToFirst()) r = new String[]{c.getString(0), c.getString(1), c.getString(2)};
        c.close();
        return r;
    }

    public boolean dangKy(String u, String p, String hoTen, String vaiTro) {
        ContentValues v = new ContentValues();
        v.put("tendangnhap", u); v.put("matkhau", p); v.put("hoten", hoTen); v.put("vaitro", vaiTro);
        return getWritableDatabase().insert("nguoidung", null, v) != -1;
    }

    public String layTenNguoiDung(int id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT hoten FROM nguoidung WHERE id=?", new String[]{String.valueOf(id)});
        String ten = c.moveToFirst() ? c.getString(0) : "Khách hàng";
        c.close();
        return ten;
    }

    // ---------- San ----------
    public void themSan(String ten, int loai, String diaChi, int gia, int chuId,
                        int soSan, int gioMo, int gioDong, String sdt, String tienIch,
                        String duongDanQr, String anhSan) {
        ContentValues v = new ContentValues();
        v.put("ten", ten); v.put("loai", loai); v.put("diachi", diaChi); v.put("gia", gia);
        v.put("chusan_id", chuId); v.put("sosan", soSan); v.put("giomo", gioMo);
        v.put("giodong", gioDong); v.put("sdt", sdt); v.put("tienich", tienIch);
        v.put("duongdanqr", duongDanQr); v.put("anhsan", anhSan);
        getWritableDatabase().insert("san", null, v);
    }

    public void suaSan(int id, String ten, int loai, String diaChi, int gia,
                       int soSan, int gioMo, int gioDong, String sdt, String tienIch,
                       String duongDanQr, String anhSan) {
        ContentValues v = new ContentValues();
        v.put("ten", ten); v.put("loai", loai); v.put("diachi", diaChi); v.put("gia", gia);
        v.put("sosan", soSan); v.put("giomo", gioMo); v.put("giodong", gioDong);
        v.put("sdt", sdt); v.put("tienich", tienIch);
        v.put("duongdanqr", duongDanQr); v.put("anhsan", anhSan);
        getWritableDatabase().update("san", v, "id=?", new String[]{String.valueOf(id)});
    }

    public void xoaSan(int id) {
        getWritableDatabase().delete("san", "id=?", new String[]{String.valueOf(id)});
    }

    San docSan(Cursor c) {
        San s = new San();
        s.id = c.getInt(0); s.ten = c.getString(1); s.loai = c.getInt(2); s.diaChi = c.getString(3);
        s.gia = c.getInt(4); s.chuId = c.getInt(5); s.soSan = c.getInt(6); s.gioMo = c.getInt(7);
        s.gioDong = c.getInt(8); s.sdt = c.getString(9); s.tienIch = c.getString(10);
        s.duongDanQr = c.getString(11); s.anhSan = c.getString(12);
        return s;
    }

    public List<San> layDanhSachSan(int chuId) {
        List<San> ds = new ArrayList<>();
        Cursor c = chuId > 0
                ? getReadableDatabase().rawQuery("SELECT " + COT_SAN + " FROM san WHERE chusan_id=?", new String[]{String.valueOf(chuId)})
                : getReadableDatabase().rawQuery("SELECT " + COT_SAN + " FROM san", null);
        while (c.moveToNext()) ds.add(docSan(c));
        c.close();
        return ds;
    }

    public List<San> timSan(String tuKhoa, int loai) {
        List<San> ds = new ArrayList<>();
        String sql = "SELECT " + COT_SAN + " FROM san WHERE (ten LIKE ? OR diachi LIKE ?)"
                + (loai > 0 ? " AND loai=" + loai : "");
        String k = "%" + tuKhoa + "%";
        Cursor c = getReadableDatabase().rawQuery(sql, new String[]{k, k});
        while (c.moveToNext()) ds.add(docSan(c));
        c.close();
        return ds;
    }

    public San laySan(int id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT " + COT_SAN + " FROM san WHERE id=?",
                new String[]{String.valueOf(id)});
        San s = c.moveToFirst() ? docSan(c) : null;
        c.close();
        return s;
    }

    // ---------- Dat san & Thanh toan ----------
    public boolean biTrung(int sanId, String ngay, int sanSo, int bd, int kt) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM datsan WHERE san_id=? AND ngay=? AND sanso=? AND trangthai<>'HUY' AND giobatdau<? AND (giobatdau+sogio)>?",
                new String[]{String.valueOf(sanId), ngay, String.valueOf(sanSo), String.valueOf(kt), String.valueOf(bd)});
        c.moveToFirst();
        boolean trung = c.getInt(0) > 0;
        c.close();
        return trung;
    }

    public Set<String> layGioDaDat(int sanId, String ngay) {
        Set<String> kq = new HashSet<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT sanso,giobatdau,sogio FROM datsan WHERE san_id=? AND ngay=? AND trangthai<>'HUY'",
                new String[]{String.valueOf(sanId), ngay});
        while (c.moveToNext())
            for (int h = c.getInt(1); h < c.getInt(1) + c.getInt(2); h++) kq.add(c.getInt(0) + "_" + h);
        c.close();
        return kq;
    }

    public long datSanChiTiet(int sanId, int khachId, String ngay, int sanSo, int bd, int soGio,
                              int tong, String trangThai, String phuongThuc, String ghiChu) {
        ContentValues v = new ContentValues();
        v.put("san_id", sanId); v.put("khach_id", khachId); v.put("ngay", ngay); v.put("sanso", sanSo);
        v.put("giobatdau", bd); v.put("sogio", soGio); v.put("tongtien", tong);
        v.put("trangthai", trangThai); v.put("phuongthuc", phuongThuc); v.put("ghichu", ghiChu);
        return getWritableDatabase().insert("datsan", null, v);
    }

    public void datSan(int sanId, int khachId, String ngay, int sanSo, int bd, int soGio, int tong) {
        datSanChiTiet(sanId, khachId, ngay, sanSo, bd, soGio, tong, "CHOTHANHTOAN", "Tiền mặt", "");
    }

    DonDat docDon(Cursor c) {
        DonDat d = new DonDat();
        d.id = c.getInt(0); d.sanId = c.getInt(1); d.khachId = c.getInt(2); d.ngay = c.getString(3);
        d.batDau = c.getInt(4); d.soGio = c.getInt(5); d.tongTien = c.getInt(6);
        d.trangThai = c.getString(7); d.tenSan = c.getString(8); d.tenKhach = c.getString(9);
        d.chuId = c.getInt(10); d.sanSo = c.getInt(11); d.diaChiSan = c.getString(12);
        d.phuongThuc = c.getString(13); d.ghiChu = c.getString(14);
        return d;
    }

    static final String SELECT_DON = "SELECT d.id,d.san_id,d.khach_id,d.ngay,d.giobatdau,d.sogio,d.tongtien,d.trangthai,s.ten,n.hoten,s.chusan_id,d.sanso,s.diachi,d.phuongthuc,d.ghichu "
            + "FROM datsan d JOIN san s ON d.san_id=s.id JOIN nguoidung n ON d.khach_id=n.id ";

    public DonDat layChiTietDon(int donId) {
        Cursor c = getReadableDatabase().rawQuery(SELECT_DON + "WHERE d.id=?", new String[]{String.valueOf(donId)});
        DonDat d = c.moveToFirst() ? docDon(c) : null;
        c.close();
        return d;
    }

    public List<DonDat> layDon(int id, boolean laChu) {
        List<DonDat> ds = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                SELECT_DON + "WHERE " + (laChu ? "s.chusan_id=?" : "d.khach_id=?") + " ORDER BY d.id DESC",
                new String[]{String.valueOf(id)});
        while (c.moveToNext()) ds.add(docDon(c));
        c.close();
        return ds;
    }

    public List<DonDat> layDonTheoTrangThai(int chuId, String trangThai) {
        List<DonDat> ds = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                SELECT_DON + "WHERE s.chusan_id=? AND d.trangthai=? ORDER BY d.id DESC",
                new String[]{String.valueOf(chuId), trangThai});
        while (c.moveToNext()) ds.add(docDon(c));
        c.close();
        return ds;
    }

    public void capNhatTrangThai(int donId, String tt) {
        ContentValues v = new ContentValues();
        v.put("trangthai", tt);
        getWritableDatabase().update("datsan", v, "id=?", new String[]{String.valueOf(donId)});
    }

    public void capNhatThanhToanDon(int donId, String trangThai, String phuongThuc, String ghiChu) {
        ContentValues v = new ContentValues();
        v.put("trangthai", trangThai);
        v.put("phuongthuc", phuongThuc);
        v.put("ghichu", ghiChu);
        getWritableDatabase().update("datsan", v, "id=?", new String[]{String.valueOf(donId)});
    }

    // ---------- Thong ke Chu san ----------
    public int doanhThuNgay(int chuId, String ngay) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT COALESCE(SUM(d.tongtien),0) FROM datsan d JOIN san s ON d.san_id=s.id WHERE s.chusan_id=? AND d.trangthai='DATHANHTOAN' AND d.ngay=?",
                new String[]{String.valueOf(chuId), ngay});
        c.moveToFirst(); int t = c.getInt(0); c.close(); return t;
    }

    public int doanhThuThang(int chuId, String thangNam) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT COALESCE(SUM(d.tongtien),0) FROM datsan d JOIN san s ON d.san_id=s.id WHERE s.chusan_id=? AND d.trangthai='DATHANHTOAN' AND d.ngay LIKE ?",
                new String[]{String.valueOf(chuId), thangNam + "%"});
        c.moveToFirst(); int t = c.getInt(0); c.close(); return t;
    }

    public int tongGioDat(int chuId, String ngay) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT COALESCE(SUM(d.sogio),0) FROM datsan d JOIN san s ON d.san_id=s.id WHERE s.chusan_id=? AND d.trangthai<>'HUY' AND d.ngay=?",
                new String[]{String.valueOf(chuId), ngay});
        c.moveToFirst(); int g = c.getInt(0); c.close(); return g;
    }

    // ---------- Cau hinh thanh toan ----------
    public String[] layCauHinh(int chuId) {
        Cursor c = getReadableDatabase().rawQuery("SELECT nganhang,sotk,tentk,duongdanqr FROM cauhinh WHERE chusan_id=?",
                new String[]{String.valueOf(chuId)});
        String[] r = {"OCB", "0206100002549008", "CHU SAN BONG", ""};
        if (c.moveToFirst()) r = new String[]{c.getString(0), c.getString(1), c.getString(2), c.getString(3)};
        c.close();
        return r;
    }

    public void luuCauHinh(int chuId, String nh, String stk, String ttk, String qr) {
        ContentValues v = new ContentValues();
        v.put("chusan_id", chuId); v.put("nganhang", nh); v.put("sotk", stk);
        v.put("tentk", ttk); v.put("duongdanqr", qr);
        getWritableDatabase().insertWithOnConflict("cauhinh", null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }
}