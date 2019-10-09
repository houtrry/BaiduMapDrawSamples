package com.houtrry.baidumapdrawsamples;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements OnLayerClickListener, OnPointDragListener {


    private static final String TAG = "LayerActivity";

    private boolean isDrawBaseLand = true;
    private LayerFrameView mLayerFrameView;
    private List<PointF> mPointFList = new ArrayList<>();
    private List<LatLng> mLatLngs = new ArrayList<>();
    private List<CirclePointOverlay> mCirclePointOverlays = new ArrayList<>();
    private boolean needClose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayerFrameView = findViewById(R.id.lfv);
        mLayerFrameView.setOnLayerClickListener(this);
        mLayerFrameView.setOnPointDragListener(this);
    }

    @Override
    public void onLayerClick(PointF point, LatLng latLng) {
        mPointFList.add(point);
        mLatLngs.add(latLng);
        refreshLineAndNode(true);
    }

    @Override
    public boolean onLayerDragClick(float dx, float dy) {
        return false;
    }

    @Override
    public void onPointClick(CirclePointOverlay circlePoint, int position) {
        if (position != 0 || mLatLngs.size() < 3) {
            return;
        }
        mLayerFrameView.clear();
        mLayerFrameView.drawLine(mLatLngs, mPointFList, Color.parseColor("#FF0000"), 4, true);
        needClose = true;
        refreshNode(true);
    }

    @Override
    public void onPointDragStart(CirclePointOverlay circlePointOverlay, int index) {
        if (circlePointOverlay.isLineCenterPoint()) {
            index = (index + 1) / 2;
            mPointFList.add(index, circlePointOverlay.getPoint());
            mLatLngs.add(index, circlePointOverlay.getLatLng());
        }
        refreshLineAndNode(false);
        mLayerFrameView.forceRefreshTargetPosition();
        mLayerFrameView.forceRefresh();
        showLog("===>>>onPointDragStart, " + index + ", " + circlePointOverlay);
    }

    @Override
    public void onPointDragging(CirclePointOverlay circlePointOverlay, int index) {
        showLog("===>>>onPointDragging, " + index + ", " + circlePointOverlay);
        index = index / 2;
        mPointFList.set(index, circlePointOverlay.getPoint());
        mLatLngs.set(index, circlePointOverlay.getLatLng());
        refreshLineAndNode(true);
    }

    @Override
    public void onPointDragEnd(CirclePointOverlay circlePointOverlay, int index) {
        showLog("===>>>onPointDragEnd, " + index + ", " + circlePointOverlay);
    }

    private void refreshNode(boolean needForceRefresh) {
        mCirclePointOverlays.clear();
        int size = mLatLngs.size();
        for (int i = 0; i < size; i++) {
            mCirclePointOverlays.add(mLayerFrameView.drawCirclePoint(mLatLngs.get(i), mPointFList.get(i), 15, 4, Color.parseColor("#FF0000"), Color.RED,
                    2, 10, Color.RED, false, true));
            if (i < size - 1) {
                mCirclePointOverlays.add(mLayerFrameView.drawCirclePoint(null, getCenterPointF(mPointFList.get(i), mPointFList.get(i + 1)), 15, 4, Color.parseColor("#FF0000"), Color.WHITE,
                        2, 11, Color.WHITE, true, true));
            }
        }
        if (needClose) {
            mCirclePointOverlays.add(mLayerFrameView.drawCirclePoint(null, getCenterPointF(mPointFList.get(0), mPointFList.get(size - 1)), 15, 4, Color.parseColor("#FF0000"), Color.WHITE,
                    2, 11, Color.WHITE, true, true));
        }
        if (needForceRefresh) {
            mLayerFrameView.forceRefresh();
        }
    }

    private PointF getCenterPointF(PointF point1, PointF point2) {
        return new PointF((point1.x + point2.x) * 0.5f, (point1.y + point2.y) * 0.5f);
    }


    private void showLog(String message) {
        Log.d(TAG, message);
    }

    private void refreshLineAndNode(boolean needForceRefresh) {
        if (mLatLngs.size() > 1) {
            mLayerFrameView.clear();
            mLayerFrameView.drawLine(mLatLngs, mPointFList, Color.parseColor("#FF0000"), 4, needClose);
        }
        refreshNode(needForceRefresh);
    }
}
