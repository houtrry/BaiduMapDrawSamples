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

/**
 * @author: houtrry
 * @date: 2019/9/29 15:55
 * @version: $
 * @description:
 */
public class LayerFrameView extends FrameLayout implements MapFloatingLayerView.OnLayerClickListener {

    private static final String TAG = "LayerFrameView";
    private MapFloatingLayerView mMapFloatingLayerView;

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
        if (mMapFloatingLayerView != null ) {
            mMapFloatingLayerView.handleTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void init(Context context, AttributeSet attrs) {
        initMapView(context, attrs);
        initMapFloatingLayerView(context, attrs);
    }

    private void initMapView(Context context, AttributeSet attrs) {
        MapView mapView = new MapView(context);
        addView(mapView);
        final BaiduMap map = mapView.getMap();
        map.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                showLog("===>>>onMapClick "+latLng);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        map.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
//                map.getProjection().toScreenLocation()
            }
        });
    }

    private void initMapFloatingLayerView(Context context, AttributeSet attrs) {
        mMapFloatingLayerView = new MapFloatingLayerView(context, attrs);
        addView(mMapFloatingLayerView);
        mMapFloatingLayerView.setOnLayerClickListener(this);
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void onLayerClick(PointF point) {

    }

    @Override
    public boolean onLayerDragClick(float dx, float dy) {
//        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) mMapFloatingLayerView.getLayoutParams();
//        marginLayoutParams.leftMargin = (int) dx;
//        marginLayoutParams.topMargin = (int) dy;
        mMapFloatingLayerView.setTranslationX(dx);
        mMapFloatingLayerView.setTranslationY(dy);
//        mMapFloatingLayerView.setLayoutParams(marginLayoutParams);

//        setPadding((int)dx, (int) dy, 0, 0);
        return true;
    }
}
