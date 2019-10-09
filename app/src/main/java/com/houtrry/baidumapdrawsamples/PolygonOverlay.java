package com.houtrry.baidumapdrawsamples;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * @author: houtrry
 * @date: 2019/10/9 13:42
 * @version: $
 * @description:
 */
public class PolygonOverlay {

    private List<LatLng> list;
    private List<PointF> pointList;
    private int color;

    public List<LatLng> getList() {
        return list;
    }

    public void setList(List<LatLng> list) {
        this.list = list;
    }

    public List<PointF> getPointList() {
        return pointList;
    }

    public void setPointList(List<PointF> pointList) {
        this.pointList = pointList;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    private Path mPath = new Path();

    public void draw(Canvas canvas, Paint polygonPaint) {
        if (!generatePath()) {
            return;
        }
        polygonPaint.setColor(color);
        canvas.drawPath(mPath, polygonPaint);
    }

    private PointF mTempPoint = null;

    private boolean generatePath() {
        if (pointList == null || pointList.size() < 3) {
            return false;
        }
        mPath.reset();
        mTempPoint = pointList.get(0);
        mPath.moveTo(mTempPoint.x, mTempPoint.y);
        for (int i = 1; i < pointList.size() - 1; i++) {
            mTempPoint = pointList.get(i);
            mPath.lineTo(mTempPoint.x, mTempPoint.y);
        }
        mPath.close();
        return true;
    }
}
