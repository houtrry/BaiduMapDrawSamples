package com.houtrry.baidumapdrawsamples;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * @author: houtrry
 * @date: 2019/10/9 11:38
 * @version: $
 * @description:
 */
public class LineOverlay {

    private List<LatLng> list;
    private List<PointF> pointList;
    private int lineColor;
    private int lineWidth;
    private boolean isClosedLine;
    private boolean isLineDotted;

    public List<LatLng> getList() {
        return list;
    }

    public void setList(List<LatLng> list) {
        this.list = list;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public boolean isClosedLine() {
        return isClosedLine;
    }

    public void setClosedLine(boolean closedLine) {
        isClosedLine = closedLine;
    }

    public boolean isLineDotted() {
        return isLineDotted;
    }

    public void setLineDotted(boolean lineDotted) {
        isLineDotted = lineDotted;
    }

    public List<PointF> getPointList() {
        return pointList;
    }

    public void setPointList(List<PointF> pointList) {
        this.pointList = pointList;
    }

    @Override
    public String toString() {
        return "LineOverlay{" +
                "list=" + list +
                ", pointList=" + pointList +
                ", lineColor=" + lineColor +
                ", lineWidth=" + lineWidth +
                ", isClosedLine=" + isClosedLine +
                ", isLineDotted=" + isLineDotted +
                '}';
    }

    private Path mPath = new Path();
    private PointF mTempPoint = null;
    public void draw(Canvas canvas, Paint linePaint) {
        if (!generatePath()) {
            return;
        }
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setPathEffect(!isLineDotted ? null : new DashPathEffect(new float[]{20, 15}, 1));
        canvas.drawPath(mPath, linePaint);
    }
    private boolean generatePath() {
        if (pointList == null || pointList.size() < 2) {
            return false;
        }
        mPath.reset();
        mTempPoint = pointList.get(0);
        mPath.moveTo(mTempPoint.x, mTempPoint.y);
        for (int i = 1; i < pointList.size(); i++) {
            mTempPoint = pointList.get(i);
            mPath.lineTo(mTempPoint.x, mTempPoint.y);
        }
        if (isClosedLine()) {
            mPath.close();
        }
        return true;
    }

}
