package com.houtrry.baidumapdrawsamples;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: houtrry
 * @date: 2019/9/29 15:55
 * @version: $
 * @description:
 */
public class LayerFrameView extends FrameLayout {

    private static final String TAG = "LayerFrameView";
    private BaiduMap mBaiduMap;
    private MapView mMapView;

    private List<LatLng> mLatLngs = new ArrayList<>();
    private int mTargetAnnulusOverlay = -1;

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

    private void init(Context context, AttributeSet attrs) {
        initMapView(context, attrs);

    }

    private void initMapView(Context context, AttributeSet attrs) {
        mMapView = new MapView(context);
        addView(mMapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                showLog("===>>>onMapClick, latLng: " + latLng);
                createNewPoint(latLng);
                if (mOnMapClickListener != null) {
                    mOnMapClickListener.onMapClick(latLng);
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                showLog("===>>>onMapPoiClick, latLng: " + mapPoi.getPosition());
                createNewPoint(mapPoi.getPosition());
                if (mOnMapClickListener != null) {
                    mOnMapClickListener.onMapClick(mapPoi.getPosition());
                }
                return true;
            }
        });
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }

    public MapView getMapView() {
        return mMapView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        showLog("===>>>onInterceptTouchEvent, ev: " + ev);
        if (MotionEvent.ACTION_DOWN == ev.getAction()) {
            int targetAnnulusOverlay = findTargetAnnulusOverlay((int) ev.getX(), (int) ev.getY());
            showLog("===>>>onInterceptTouchEvent, targetAnnulusOverlay: " + targetAnnulusOverlay);
            if (targetAnnulusOverlay >= 0) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    private long mTouchDownTime = 0;
    private int mDragStartX;
    private int mDragStartY;
    private int mCurrentDragX;
    private int mCurrentDragY;
    private boolean isCanDrag = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        showLog("===>>>onTouchEvent, event: " + event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        mCurrentDragX = x;
        mCurrentDragY = y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTargetAnnulusOverlay = findTargetAnnulusOverlay(x, y);
                mTouchDownTime = System.currentTimeMillis();
                mDragStartX = x;
                mDragStartY = y;
                isCanDrag = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isCanDrag) {
                    break;
                }
                showLog("===>>>ACTION_MOVE, "+event.getActionIndex()+", "+event.findPointerIndex(event.getActionIndex()));
                if (mTargetAnnulusOverlay >= 0 && mTargetAnnulusOverlay < mAnnulusOverlays.size()) {
                    LatLng latLng = fromScreenLocation(x, y);
                    if (latLng != null) {
                        mAnnulusOverlays.get(mTargetAnnulusOverlay).updatePosition(latLng);
                        preformDrag(x, y, latLng);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!isCanDrag) {
                    break;
                }
                if (event.findPointerIndex(event.getActionIndex()) != 0) {
                    showLog("===>>>ACTION_UP, 0");
                    break;
                }
                isCanDrag = false;
                if ((System.currentTimeMillis() - mTouchDownTime) < ViewConfiguration.getTapTimeout()
                        && Math.sqrt(Math.pow(x - mDragStartX, 2) + Math.pow(y - mDragStartY, 2)) < ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    LatLng latLng = fromScreenLocation(x, y);
                    if (latLng != null) {
                        if (mOnMarkerClickListener != null) {
                            mOnMarkerClickListener.onMarkerClick(latLng, mTargetAnnulusOverlay);
                        }
                    }
                }
                if (hasTouchDrag && mTargetAnnulusOverlay >= 0) {
                    LatLng latLng = fromScreenLocation(x, y);
                    if (latLng != null) {
                        if (mOnMarkerDragListener != null) {
                            mOnMarkerDragListener.onMarkerDragEnd(latLng, mTargetAnnulusOverlay);
                        }
                    }
                }
                isFirstPreformMove = true;
                hasTouchDrag = false;
                break;
            default:
                break;
        }
        return true;
    }

    private LatLng fromScreenLocation(int x, int y) {
        if (mBaiduMap == null) {
            return null;
        }
        Projection projection = mBaiduMap.getProjection();
        if (projection == null) {
            return null;
        }
        return projection.fromScreenLocation(new Point(x, y));
    }

    private boolean isFirstPreformMove = true;
    private boolean hasTouchDrag = false;

    private void preformDrag(int x, int y, LatLng latLng) {
        if (isFirstPreformMove) {
            if (mOnMarkerDragListener != null) {
                mOnMarkerDragListener.onMarkerDragStart(latLng, mTargetAnnulusOverlay);
            }
            isFirstPreformMove = false;
            return;
        }

        if (mOnMarkerDragListener != null) {
            mOnMarkerDragListener.onMarkerDragging(latLng, mTargetAnnulusOverlay);
        }
        hasTouchDrag = true;
    }

    private void createNewPoint(LatLng latLng) {
        mLatLngs.add(latLng);
    }

    public Overlay addOverlay(OverlayOptions options) {
        return mBaiduMap.addOverlay(options);
    }

    private List<AnnulusOverlay> mAnnulusOverlays = new ArrayList<>();
    private static final int Z_INDEX_FOR_BASE_LINE_AND_NODE = 5;

    public AnnulusOverlay addAnnulusOverlay(AnnulusOverlay.Builder builder) {
        AnnulusOverlay annulusOverlay = builder.baiduMap(mBaiduMap).zIndex(Z_INDEX_FOR_BASE_LINE_AND_NODE)
                .build();
        mAnnulusOverlays.add(annulusOverlay);
        return annulusOverlay;
    }

    public AnnulusOverlay addAnnulusOverlay(AnnulusOverlay.Builder builder, int position) {
        AnnulusOverlay annulusOverlay = builder.baiduMap(mBaiduMap).zIndex(Z_INDEX_FOR_BASE_LINE_AND_NODE)
                .build();
        mAnnulusOverlays.add(position, annulusOverlay);
        return annulusOverlay;
    }



    public Polyline addLineOverlay(List<LatLng> latLngs, int stokeColor, int stokeWidth, boolean isDotted) {
        final PolylineOptions polylineOptions = new PolylineOptions().color(stokeColor)
                .width(stokeWidth)
                .dottedLine(isDotted)
                .points(latLngs);
        return (Polyline) mBaiduMap.addOverlay(polylineOptions);
    }

    /**
     * 在地图上添加多边形Option，用于显示
     *
     * @param latLngs
     * @param stokeColor
     * @param stokeWidth
     * @param centerColor
     * @return
     */
    public Overlay addAreaOverlay(List<LatLng> latLngs, int stokeColor, int stokeWidth, int centerColor) {
        OverlayOptions polygonOption = new PolygonOptions()
                .points(latLngs)
                .stroke(new Stroke(stokeWidth, stokeColor))
                .fillColor(centerColor);

        return mBaiduMap.addOverlay(polygonOption);
    }

    public void clearMap() {
        mAnnulusOverlays.clear();
        mBaiduMap.clear();
    }

    private int findTargetAnnulusOverlay(int x, int y) {
        for (int i = 0; i < mAnnulusOverlays.size(); i++) {
            if (mAnnulusOverlays.get(i).isTarget(x, y)) {
                return i;
            }
        }
        return -1;
    }

    private OnMapClickListener mOnMapClickListener;
    private OnMarkerClickListener mOnMarkerClickListener;
    private OnMarkerDragListener mOnMarkerDragListener;

    public void setOnMapClickListener(OnMapClickListener onMapClickListener) {
        mOnMapClickListener = onMapClickListener;
    }

    public void setOnMarkerClickListener(OnMarkerClickListener onMarkerClickListener) {
        mOnMarkerClickListener = onMarkerClickListener;
    }

    public void setOnMarkerDragListener(OnMarkerDragListener onMarkerDragListener) {
        mOnMarkerDragListener = onMarkerDragListener;
    }

    public void forceRefreshPosition() {
        showLog("===>>>forceRefreshPosition, start, "+mTargetAnnulusOverlay+", "+mCurrentDragX+", "+mCurrentDragY+", "+mAnnulusOverlays.size()+", "+mAnnulusOverlays);
        mTargetAnnulusOverlay = findTargetAnnulusOverlay(mCurrentDragX, mCurrentDragY);
        showLog("===>>>forceRefreshPosition, end,   "+mTargetAnnulusOverlay+", "+mCurrentDragX+", "+mCurrentDragY+", "+mAnnulusOverlays.size()+", "+mAnnulusOverlays);
    }
}
