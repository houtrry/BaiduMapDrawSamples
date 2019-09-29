package com.houtrry.baidumapdrawsamples;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;

/**
 * @author: houtrry
 * @date: 2019/9/29 16:33
 * @version: $
 * @description:
 */
public class CirclePointView {

    private static final String TAG = "CirclePointView";
    private PointF point;
    private LatLng latLng;
    private int radius;
    private int stokeWidth;
    private int stokeColor;
    private int solidColor;

    private float fixX = 0;
    private float fixY = 0;

    private float touchRatio;

    private int centerRadius = 0;
    private int centerColor;

    public PointF getPoint() {
        return point;
    }

    public void setPoint(PointF point) {
        this.point = point;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getStokeWidth() {
        return stokeWidth;
    }

    public void setStokeWidth(int stokeWidth) {
        this.stokeWidth = stokeWidth;
    }

    public int getStokeColor() {
        return stokeColor;
    }

    public void setStokeColor(int stokeColor) {
        this.stokeColor = stokeColor;
    }

    public int getSolidColor() {
        return solidColor;
    }

    public void setSolidColor(int solidColor) {
        this.solidColor = solidColor;
    }

    public float getTouchRatio() {
        return touchRatio;
    }

    public void setTouchRatio(float touchRatio) {
        this.touchRatio = touchRatio;
    }

    public float getFixX() {
        return fixX;
    }

    public void setFixX(float fixX) {
        this.fixX = fixX;
    }

    public float getFixY() {
        return fixY;
    }

    public void setFixY(float fixY) {
        this.fixY = fixY;
    }

    public int getCenterRadius() {
        return centerRadius;
    }

    public void setCenterRadius(int centerRadius) {
        this.centerRadius = centerRadius;
    }

    public int getCenterColor() {
        return centerColor;
    }

    public void setCenterColor(int centerColor) {
        this.centerColor = centerColor;
    }

    private float centerX = 0;
    private float centerY = 0;

    public void draw(Canvas canvas, Paint paint) {
        if (canvas == null) {
            return;
        }
        centerX = point.x + fixX;
        centerY = point.y + fixY;
        paint.setColor(stokeColor);
        canvas.drawCircle(centerX, centerY, radius, paint);
        paint.setColor(solidColor);
        canvas.drawCircle(centerX, centerY, radius - stokeWidth, paint);
        if (centerRadius > 0) {
            paint.setColor(centerColor);
            canvas.drawCircle(centerX, centerY, centerRadius, paint);
        }
    }

    @Override
    public String toString() {
        return "CirclePointView{" +
                "point=" + point +
                ", latLng=" + latLng +
                ", radius=" + radius +
                ", stokeWidth=" + stokeWidth +
                ", stokeColor=" + stokeColor +
                ", solidColor=" + solidColor +
                ", touchRatio=" + touchRatio +
                '}';
    }

    public boolean isTargetView(float x, float y) {
        return Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2)) < radius * touchRatio;
    }

    public void fixPosition(float dx, float dy) {
        Log.d(TAG, "===>>>fixPosition: fixX: " + fixX + ", fixY: " + fixY + ", dx: " + dx + ", dy:" + dy);
        fixX = dx;
        fixY = dy;
        centerX = point.x + fixX;
        centerY = point.y + fixY;
        Log.d(TAG, "===>>>fixPosition: fixX: " + fixX + ", fixY: " + fixY+", centerX: "+centerX+", centerY: "+centerY);
    }
}
