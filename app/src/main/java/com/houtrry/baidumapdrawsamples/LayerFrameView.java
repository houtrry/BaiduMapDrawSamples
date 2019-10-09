package com.houtrry.baidumapdrawsamples;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * @author: houtrry
 * @date: 2019/9/29 15:55
 * @version: $
 * @description:
 */
public class LayerFrameView extends FrameLayout implements OnLayerClickListener {

    private static final String TAG = "LayerFrameView";
    private MapFloatingLayerView1 mMapFloatingLayerView;
    private BaiduMap mBaiduMap;

    public LayerFrameView(Context context) {
        this(context, null);
    }

    public LayerFrameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LayerFrameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        showLog("===>>>dispatchTouchEvent: " + ev);
        boolean result = false;
        if (mMapFloatingLayerView != null) {
            result = mMapFloatingLayerView.handleTouchEvent(ev);
        }
        return result || super.dispatchTouchEvent(ev);
    }

    private void init(Context context, AttributeSet attrs) {
        initMapView(context, attrs);
        initMapFloatingLayerView(context, attrs);
    }

    private void initMapView(Context context, AttributeSet attrs) {
        MapView mapView = new MapView(context);
        addView(mapView);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                showLog("===>>>onMapClick " + latLng);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                showLog("===>>>onMapStatusChangeStart, " + mapStatus.zoom);
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int reason) {
                showLog("===>>>onMapStatusChangeStart, " + mapStatus.zoom + ", " + reason + BaiduMap.OnMapStatusChangeListener.REASON_GESTURE);
                if (BaiduMap.OnMapStatusChangeListener.REASON_GESTURE == reason) {
                    mMapFloatingLayerView.mapStatusChangeStart(mBaiduMap);
                }
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                showLog("===>>>onMapStatusChange, " + mapStatus.zoom);
                mMapFloatingLayerView.mapStatusChange(mBaiduMap);
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                //                map.getProjection().toScreenLocation()
                showLog("===>>>onMapStatusChangeFinish, " + mapStatus.zoom);
                mMapFloatingLayerView.mapStatusChangeEnd(mBaiduMap);
            }
        });
    }

    private void initMapFloatingLayerView(Context context, AttributeSet attrs) {
        mMapFloatingLayerView = new MapFloatingLayerView1(context, attrs);
        addView(mMapFloatingLayerView);
        mMapFloatingLayerView.setOnLayerClickListener(this);
        mMapFloatingLayerView.setMap(mBaiduMap);
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void onLayerClick(PointF point, LatLng latLng) {
        showLog("===>>>onLayerClick,,, point: " + point + ", " + latLng);
    }

    @Override
    public boolean onLayerDragClick(float dx, float dy) {
        //        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) mMapFloatingLayerView.getLayoutParams();
        //        marginLayoutParams.leftMargin = (int) dx;
        //        marginLayoutParams.topMargin = (int) dy;
        //        mMapFloatingLayerView.setTranslationX(dx);
        //        mMapFloatingLayerView.setTranslationY(dy);
        //        mMapFloatingLayerView.setLayoutParams(marginLayoutParams);

        //        setPadding((int)dx, (int) dy, 0, 0);
        return false;
    }

    @Override
    public void onPointClick(CirclePointOverlay circlePoint, int position) {
        showLog("===>>>onPointClick, position: " + position + ", " + circlePoint);
    }

    public void setOnPointDragListener(OnPointDragListener onPointDragListener) {
        if (mMapFloatingLayerView != null) {
            mMapFloatingLayerView.setOnPointDragListener(onPointDragListener);
        }
    }

    public void setOnLayerClickListener(OnLayerClickListener onLayerClickListener) {
        if (mMapFloatingLayerView != null) {
            mMapFloatingLayerView.setOnLayerClickListener(onLayerClickListener);
        }
    }

    public LineOverlay drawLine(List<LatLng> list, List<PointF> pointList, int lineColor, int lineWidth, boolean isClosedLine) {
        return mMapFloatingLayerView == null ? null : mMapFloatingLayerView.drawLine(list, pointList, lineColor, lineWidth, isClosedLine);
    }

    public PolygonOverlay drawPolygon(List<LatLng> list, List<PointF> pointList, int color) {
        return mMapFloatingLayerView == null ? null : mMapFloatingLayerView.drawPolygon(list, pointList, color);
    }

    public CirclePointOverlay drawCirclePoint(LatLng latLng, PointF point, float radius, float stokeWidth, Integer stokeColor,
                                              Integer solidColor, float touchRatio, float centerRadius,
                                              Integer centerColor, boolean isLineCenterPoint, boolean enableDrag) {
        return mMapFloatingLayerView == null ? null : mMapFloatingLayerView.drawCirclePoint(latLng, point, radius, stokeWidth, stokeColor, solidColor, touchRatio, centerRadius, centerColor, isLineCenterPoint, enableDrag);
    }

    public void clear() {
        if (mMapFloatingLayerView != null) {
            mMapFloatingLayerView.clear();
        }
    }

    public void forceRefresh() {
        if (mMapFloatingLayerView != null) {
            mMapFloatingLayerView.forceRefresh();
        }
    }

    public void forceRefreshTargetPosition() {
        if (mMapFloatingLayerView != null) {
            mMapFloatingLayerView.forceRefreshTargetPosition();
        }
    }
}
