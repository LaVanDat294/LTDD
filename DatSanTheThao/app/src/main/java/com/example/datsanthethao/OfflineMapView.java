package com.example.datsanthethao;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

public class OfflineMapView extends View {
    public interface OnChonKhuVucListener {
        void onChon(String khuVuc, String tuKhoa);
    }

    private final Paint pNen = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pSong = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pDuong = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pGhim = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pVienGhim = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pChu = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pDiaDanh = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int dangChon = 0;
    private OnChonKhuVucListener listener;

    private final String[] tenKhuVuc = {"Tất cả Hà Nội", "Hoàng Mai", "Thanh Trì", "Đại Kim", "Đống Đa", "Cầu Giấy"};
    private final String[] tuKhoaTim = {"", "Hoang Mai", "Thanh Tri", "Dai Kim", "Dong Da", "Cau Giay"};
    private final float[] posX = {0.50f, 0.65f, 0.58f, 0.45f, 0.35f, 0.25f};
    private final float[] posY = {0.22f, 0.62f, 0.80f, 0.70f, 0.45f, 0.30f};
    private final int[] soLuong = {3, 2, 1, 1, 1, 1};

    public OfflineMapView(Context context) {
        super(context);
        pNen.setColor(Color.parseColor("#EBF5EE"));
        pSong.setColor(Color.parseColor("#B3E5FC"));
        pSong.setStyle(Paint.Style.STROKE);
        pSong.setStrokeWidth(42f);

        pDuong.setColor(Color.parseColor("#FFD54F"));
        pDuong.setStyle(Paint.Style.STROKE);
        pDuong.setStrokeWidth(10f);

        pVienGhim.setColor(Color.WHITE);
        pChu.setColor(Color.WHITE);
        pChu.setTextSize(34f);
        pChu.setTextAlign(Paint.Align.CENTER);
        pChu.setFakeBoldText(true);

        pDiaDanh.setColor(Color.parseColor("#2E4A3B"));
        pDiaDanh.setTextSize(30f);
        pDiaDanh.setTextAlign(Paint.Align.CENTER);
        pDiaDanh.setFakeBoldText(true);
    }

    public void setOnChonKhuVucListener(OnChonKhuVucListener l) {
        this.listener = l;
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        int w = getWidth(), h = getHeight();
        c.drawRect(0, 0, w, h, pNen);

        Path song = new Path();
        song.moveTo(w * 0.75f, 0);
        song.quadTo(w * 0.88f, h * 0.45f, w * 0.80f, h);
        c.drawPath(song, pSong);
        c.drawCircle(w * 0.35f, h * 0.16f, 55f, pSong);

        c.drawLine(0, h * 0.50f, w, h * 0.50f, pDuong);
        c.drawLine(w * 0.50f, 0, w * 0.50f, h, pDuong);
        c.drawLine(w * 0.15f, h * 0.85f, w * 0.85f, h * 0.25f, pDuong);

        for (int i = 0; i < tenKhuVuc.length; i++) {
            float cx = w * posX[i];
            float cy = h * posY[i];
            pGhim.setColor(i == dangChon ? Color.parseColor("#E65100") : Color.parseColor("#0F5B34"));
            c.drawCircle(cx, cy, 48f, pVienGhim);
            c.drawCircle(cx, cy, 42f, pGhim);
            c.drawText(soLuong[i] + "+", cx, cy + 12f, pChu);
            c.drawText(tenKhuVuc[i], cx, cy + 78f, pDiaDanh);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int w = getWidth(), h = getHeight();
            for (int i = 0; i < tenKhuVuc.length; i++) {
                float dx = event.getX() - w * posX[i];
                float dy = event.getY() - h * posY[i];
                if (dx * dx + dy * dy <= 75f * 75f) {
                    dangChon = i;
                    invalidate();
                    if (listener != null) listener.onChon(tenKhuVuc[i], tuKhoaTim[i]);
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }
}