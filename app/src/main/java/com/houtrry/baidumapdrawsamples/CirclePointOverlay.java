package com.houtrry.baidumapdrawsamples;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;

/**
 * @author: houtrry
 * @date: 2019/10/9 13:46
 * @version: $
 * @description:
 */
public class CirclePointOverlay {

    private static final String TAG = "CirclePointOverlay";

    private PointF point;
    private LatLng latLng;
    private float radius;
    private float stokeWidth;
    private Integer stokeColor = null;
    private Integer solidColor = null;

    private float touchRatio;

    private float centerRadius = 0;
    private Integer centerColor = null;
    private boolean isLineCenterPoint = false;

    private boolean enableDrag = false;

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

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getStokeWidth() {
        return stokeWidth;
    }

    public void setStokeWidth(float stokeWidth) {
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

    public float getCenterRadius() {
        return centerRadius;
    }

    public void setCenterRadius(float centerRadius) {
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

    public boolean isEnableDrag() {
        return enableDrag;
    }

    public void setEnableDrag(boolean enableDrag) {
        this.enableDrag = enableDrag;
    }

    @Override
    public String toString() {
        return "CirclePointOverlay{" +
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
                ", enableDrag=" + enableDrag +
                '}';
    }

    public boolean isTargetView(float x, float y) {
        double sqrt = Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2));
        float v = radius * touchRatio;
        Log.d(TAG, "===>>>isTargetView, sqrt: " + sqrt + "/" + v + ", (" + x + "," + y + "), " + "(" + point.x + "," + point.y + ")");
        return sqrt < v;
    }

}
