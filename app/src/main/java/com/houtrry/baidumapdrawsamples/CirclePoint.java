package com.houtrry.baidumapdrawsamples;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.baidu.mapapi.model.LatLng;

/**
 * @author: houtrry
 * @date: 2019/9/29 16:33
 * @version: $
 * @description:
 */
public class CirclePoint {

    private static final String TAG = "CirclePoint";
    private PointF point;
    private LatLng latLng;
    private int radius;
    private int stokeWidth;
    private Integer stokeColor = null;
    private Integer solidColor = null;

    private float touchRatio;

    private int centerRadius = 0;
    private Integer centerColor = null;
    private boolean isLineCenterPoint = false;

    public PointF getPoint() {
        return point;
    }

    public void setPoint(PointF point) {
        this.point = point;
    }

    public void setPoint(float x, float y) {
        this.point.x = x;
        this.point.y = y;
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

    public void draw(Canvas canvas, Paint paint) {
        if (canvas == null) {
            return;
        }
        if (stokeColor != null) {
            paint.setColor(stokeColor);
            canvas.drawCircle(point.x, point.y, radius, paint);
        }
        if (solidColor != null) {
            paint.setColor(solidColor);
            canvas.drawCircle(point.x, point.y, radius - stokeWidth, paint);
        }
        if (centerRadius > 0) {
            paint.setColor(centerColor);
            canvas.drawCircle(point.x, point.y, centerRadius, paint);
        }
    }

    public void setStokeColor(Integer stokeColor) {
        this.stokeColor = stokeColor;
    }

    public void setSolidColor(Integer solidColor) {
        this.solidColor = solidColor;
    }

    public void setCenterColor(Integer centerColor) {
        this.centerColor = centerColor;
    }

    public boolean isLineCenterPoint() {
        return isLineCenterPoint;
    }

    public void setLineCenterPoint(boolean lineCenterPoint) {
        isLineCenterPoint = lineCenterPoint;
    }

    @Override
    public String toString() {
        return "CirclePoint{" +
                "point=" + point +
                ", latLng=" + latLng +
                ", radius=" + radius +
                ", stokeWidth=" + stokeWidth +
                ", stokeColor=" + stokeColor +
                ", solidColor=" + solidColor +
                ", touchRatio=" + touchRatio +
                ", centerRadius=" + centerRadius +
                ", centerColor=" + centerColor +
                ", isLineCenterPoint=" + isLineCenterPoint +
                '}';
    }

    public boolean isTargetView(float x, float y) {
        return Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2)) < radius * touchRatio;
    }

    public void fixPosition(float dx, float dy) {
        point.x += dx;
        point.y += dy;
    }

    public void recalculatePointByLatLng(boolean needRecalculateLatLngFirst) {
        if (needRecalculateLatLngFirst) {
            recalculateLatLngByPoint();
        }


    }

    public void recalculateLatLngByPoint() {

    }


}
