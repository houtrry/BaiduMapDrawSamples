package com.houtrry.baidumapdrawsamples;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import androidx.core.view.ViewCompat;


/**
 * @author: houtrry
 * @date: 2019/9/29 16:04
 * @version: $
 * @description:
 */
public class MapFloatingLayerView1 extends View {

    private static final String TAG = "MapFloatingLayerView";

    private int mRadius = 15;
    private int mStokeWidth = 6;
    private int mStokeColor = Color.parseColor("#FF0000");
    private int mSolidColor = Color.WHITE;

    private float mTouchRatio = 3;
    private Paint mPaint;
    private boolean enableDrag = true;

    private int mLineColor = Color.parseColor("#00FF00");
    private int mLineWidth = 6;
    private boolean isLineDotted = true;
    private Paint mLinePaint;
    private boolean isClosedLine = false;

    private int mLineCenterPointColor = Color.WHITE;
    private int mLineCenterPointRadius = 9;
    private float mLineCenterPointRatio = mTouchRatio * 2;
    private boolean isMapStatusChanging = false;
    private BaiduMap mBaiduMap;
    private Paint mCirclePointPaint;
    private Paint mPolygonPaint;
    private OnPointDragListener mOnPointDragListener;


    public MapFloatingLayerView1(Context context) {
        this(context, null);
    }

    public MapFloatingLayerView1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapFloatingLayerView1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setPathEffect(!isLineDotted ? null : new DashPathEffect(new float[]{20, 15}, 1));
        mLinePaint.setStrokeWidth(mLineWidth);

        mCirclePointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePointPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mPolygonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPolygonPaint.setStyle(Paint.Style.FILL_AND_STROKE);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPolygons(canvas);
        drawLines(canvas);
        drawCirclePoints(canvas);
    }

    public void setOnPointDragListener(OnPointDragListener onPointDragListener) {
        mOnPointDragListener = onPointDragListener;
    }

    private void drawPolygons(Canvas canvas) {
        for (PolygonOverlay overlay : mPolygonOverlays) {
            overlay.draw(canvas, mPolygonPaint);
        }
    }

    private void drawLines(Canvas canvas) {
        for (LineOverlay overlay : mLineOverlays) {
            overlay.draw(canvas, mLinePaint);
        }
    }

    private void drawCirclePoints(Canvas canvas) {
        for (CirclePointOverlay overlay :
                mCirclePointOverlays) {
            overlay.draw(canvas, mCirclePointPaint);
        }
    }

    private CirclePoint mStartPointView = null;
    private CirclePoint mEndPointView = null;
    private int mCirclePointViewSize = 0;

    //    private void drawLine(Canvas canvas) {
    //        mCirclePointViewSize = mCirclePoints.size();
    //        for (int i = 0; i < mCirclePointViewSize - 1; i++) {
    //            mStartPointView = mCirclePoints.get(i);
    //            mEndPointView = mCirclePoints.get(i + 1);
    //            canvas.drawLine(mStartPointView.getPoint().x, mStartPointView.getPoint().y, mEndPointView.getPoint().x, mEndPointView.getPoint().y, mLinePaint);
    //        }
    //        if (isClosedLine && mCirclePointViewSize > 2) {
    //            mStartPointView = mCirclePoints.get(mCirclePointViewSize - 1);
    //            mEndPointView = mCirclePoints.get(0);
    //            canvas.drawLine(mStartPointView.getPoint().x, mStartPointView.getPoint().y, mEndPointView.getPoint().x, mEndPointView.getPoint().y, mLinePaint);
    //        }
    //    }
    //
    //    private Path mLinePath = new Path();
    //    private PointF mPointF;
    //
    //    private void drawLinePath(Canvas canvas) {
    //        mCirclePointViewSize = mCirclePoints.size();
    //        if (mCirclePointViewSize < 2) {
    //
    //            return;
    //        }
    //        mLinePath.reset();
    //        mPointF = mCirclePoints.get(0).getPoint();
    //        mLinePath.moveTo(mPointF.x, mPointF.y);
    //        for (int i = 1; i < mCirclePointViewSize; i++) {
    //            mPointF = mCirclePoints.get(i).getPoint();
    //            mLinePath.lineTo(mPointF.x, mPointF.y);
    //        }
    //        if (isClosedLine && mCirclePointViewSize > 2) {
    //            mLinePath.close();
    //        }
    //        canvas.drawPath(mLinePath, mLinePaint);
    //    }
    //
    //    private void drawCirclePoint(Canvas canvas) {
    //        for (CirclePoint circlePoint : mCirclePoints) {
    //            circlePoint.draw(canvas, mPaint);
    //        }
    //    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        showLog("===>>>onTouchEvent, event: " + event);
        if (MotionEvent.ACTION_DOWN == event.getAction()
                && findTargetCirclePointViewPosition(event.getX(), event.getY()) >= 0) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void mapStatusChangeStart(BaiduMap map) {
        isMapStatusChanging = true;
        Projection projection = map.getProjection();
        if (projection == null) {
            Log.e(TAG, "===>>>mapStatusChange, projection is null");
            return;
        }
        Point point = new Point();
        for (CirclePointOverlay circlePoint : mCirclePointOverlays) {
            PointF pointF = circlePoint.getPoint();
            point.x = (int) (pointF.x + 0.5f);
            point.y = (int) (pointF.y + 0.5f);
            LatLng latLng = projection.fromScreenLocation(point);
            circlePoint.setLatLng(latLng);
        }
    }

    public void mapStatusChange(BaiduMap map) {
        if (!isMapStatusChanging) {
            return;
        }
        Projection projection = map.getProjection();
        if (projection == null) {
            Log.e(TAG, "===>>>mapStatusChange, projection is null");
            return;
        }
        Point point = new Point();
        LatLng latLng = null;
        for (CirclePointOverlay circlePoint : mCirclePointOverlays) {
            latLng = circlePoint.getLatLng();
            if (latLng == null) {
                PointF pointF = circlePoint.getPoint();
                point.x = (int) (pointF.x + 0.5f);
                point.y = (int) (pointF.y + 0.5f);
                latLng = projection.fromScreenLocation(point);
                circlePoint.setLatLng(latLng);
            }
            Point newPoint = projection.toScreenLocation(latLng);
            circlePoint.setPoint(newPoint.x, newPoint.y);
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void mapStatusChangeEnd(BaiduMap map) {
        isMapStatusChanging = false;
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }

    private long mTouchDownTime = 0;
    private boolean hasTouchDrag = false;
    private float mDragStartX = 0;
    private float mDragStartY = 0;
    private float mCurrentX = 0;
    private float mCurrentY = 0;

    private int mTargetCirclePointViewPosition = -1;

    public boolean handleTouchEvent(MotionEvent event) {
        showLog("===>>>handleTouchEvent, event: " + event);
        boolean needIntercept = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownTime = System.currentTimeMillis();
                hasTouchDrag = false;
                mDragStartX = event.getX();
                mDragStartY = event.getY();
                mTargetCirclePointViewPosition = findTargetCirclePointViewPosition(mDragStartX, mDragStartY);
                showLog("===>>>preformPointDrag,,, targetCirclePointViewPosition: " + mTargetCirclePointViewPosition);
                if (mTargetCirclePointViewPosition >= 0) {
                    needIntercept = true;
                }
                isFirstMove = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (enableDrag && mCirclePointOverlays.size() > 0) {
                    preformDrag(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                long dt = System.currentTimeMillis() - mTouchDownTime;
                showLog("===>>>hasTouchDrag: dt: " + dt);
                if (dt < ViewConfiguration.getTapTimeout()
                        && Math.sqrt(Math.pow(event.getX() - mDragStartX, 2) + Math.pow(event.getY() - mDragStartY, 2)) < ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    preformClick(event);
                }
                if (hasTouchDrag && mOnPointDragListener != null) {
                    //                    int targetCirclePointViewPosition1 = findTargetCirclePointViewPosition(event.getX(), event.getY());
                    if (mTargetCirclePointViewPosition >= 0) {
                        mOnPointDragListener.onPointDragEnd(mCirclePointOverlays.get(mTargetCirclePointViewPosition), mTargetCirclePointViewPosition);
                    }
                }
                mTargetCirclePointViewPosition = -1;
                break;
            default:
                break;
        }
        return needIntercept;
    }

    private void preformDrag(MotionEvent event) {
        mCurrentX = event.getX();
        mCurrentY = event.getY();
        //        int targetCirclePointViewPosition = findTargetCirclePointViewPosition(mCurrentX, mCurrentY);
        showLog("===>>>preformDrag, targetCirclePointViewPosition: " + mTargetCirclePointViewPosition);
        if (mTargetCirclePointViewPosition < 0) {
            showLog("===>>>preformDrag, no targetCirclePointView in list");
            //            preformOuterDrag();
            return;
        }
        preformPointDrag(mTargetCirclePointViewPosition);
    }

    private void preformOuterDrag() {
        showLog("===>>>preformOuterDrag");
        //        float dx = mCurrentX - mDragStartX;
        //        float dy = mCurrentY - mDragStartY;
        //        if (mOnLayerClickListener != null && mOnLayerClickListener.onLayerDragClick(dx, dy)) {
        //            return;
        //        }
        //        for (CirclePointOverlay view : mCirclePointOverlays) {
        //            view.fixPosition(dx, dy);
        //        }
        //        ViewCompat.postInvalidateOnAnimation(this);
        //        mDragStartX = mCurrentX;
        //        mDragStartY = mCurrentY;
    }


    private boolean isFirstMove = false;

    private LatLng fromScreenLocation(int x, int y) {
        Projection projection = mBaiduMap.getProjection();
        if (projection == null) {
            return null;
        }
        return projection.fromScreenLocation(new Point(x, y));
    }

    private void preformPointDrag(int targetCirclePointViewPosition) {
        showLog("===>>>preformPointDrag: " + targetCirclePointViewPosition);
        if (mBaiduMap == null) {
            return;
        }
        CirclePointOverlay circlePoint = mCirclePointOverlays.get(targetCirclePointViewPosition);
        if (!circlePoint.isEnableDrag()) {
            return;
        }
        Projection projection = mBaiduMap.getProjection();
        if (projection == null) {
            return;
        }
        final int x = (int) (mCurrentX + 0.5f);
        final int y = (int) (mCurrentY + 0.5f);

        circlePoint.setLatLng(projection.fromScreenLocation(new Point(x, y)));
        circlePoint.setPoint(x, y);

        showLog("===>>>preformPointDrag, isFirstMove: " + isFirstMove + ", " + circlePoint.isLineCenterPoint() + ", " + targetCirclePointViewPosition + "/" + mCirclePointOverlays.size());
        //        if (isFirstMove && circlePoint.isLineCenterPoint()) {
        //            CirclePointOverlay mainCirclePoint = getMainCirclePoint(circlePoint.getPoint().x, circlePoint.getPoint().y);
        //            mainCirclePoint.setLatLng(circlePoint.getLatLng());
        //            mCirclePointOverlays.set(targetCirclePointViewPosition, mainCirclePoint);
        //            mCirclePointOverlays.add(targetCirclePointViewPosition + 1, getCenterCirclePoint(mCirclePointOverlays.get(targetCirclePointViewPosition + 1), x, y));
        //            mCirclePointOverlays.add(targetCirclePointViewPosition, getCenterCirclePoint(mCirclePointOverlays.get(targetCirclePointViewPosition - 1), x, y));
        //            showLog("===>>>preformPointDrag, add 2 center point");
        //        } else {
        //            showLog("===>>>preformPointDrag, update last and next point");
        //            updateLastAndNextPointPosition(targetCirclePointViewPosition, circlePoint);
        //        }
        //        isFirstMove = false;
        //        ViewCompat.postInvalidateOnAnimation(this);
        hasTouchDrag = true;
        if (mOnPointDragListener != null) {
            if (isFirstMove) {
                mOnPointDragListener.onPointDragStart(circlePoint, targetCirclePointViewPosition);
            } else {
                mOnPointDragListener.onPointDragging(circlePoint, targetCirclePointViewPosition);
            }
        }
        isFirstMove = false;
    }

    private void updateLastAndNextPointPosition(int targetCirclePointViewPosition, CirclePointOverlay circlePoint) {
        if (targetCirclePointViewPosition > 1) {
            CirclePointOverlay lastMainPoint = mCirclePointOverlays.get(targetCirclePointViewPosition - 2);
            CirclePointOverlay lastCenterPoint = mCirclePointOverlays.get(targetCirclePointViewPosition - 1);
            lastCenterPoint.setPoint((lastMainPoint.getPoint().x + circlePoint.getPoint().x) * 0.5f, (lastMainPoint.getPoint().y + circlePoint.getPoint().y) * 0.5f);
        }

        if (targetCirclePointViewPosition < mCirclePointOverlays.size() - 2) {
            CirclePointOverlay nextMainPoint = mCirclePointOverlays.get(targetCirclePointViewPosition + 2);
            CirclePointOverlay nextCenterPoint = mCirclePointOverlays.get(targetCirclePointViewPosition + 1);
            nextCenterPoint.setPoint((nextMainPoint.getPoint().x + circlePoint.getPoint().x) * 0.5f, (nextMainPoint.getPoint().y + circlePoint.getPoint().y) * 0.5f);
        }
    }

    private CirclePointOverlay getCenterCirclePoint(CirclePointOverlay pointView, float x, float y) {
        PointF point = new PointF((x + pointView.getPoint().x) * 0.5f, (y + pointView.getPoint().y) * 0.5f);
        CirclePointOverlay centerPoint = new CirclePointOverlay();
        centerPoint.setPoint(point);
        centerPoint.setRadius(mLineCenterPointRadius);
        centerPoint.setLineCenterPoint(true);
        centerPoint.setSolidColor(mLineCenterPointColor);
        centerPoint.setTouchRatio(mLineCenterPointRatio);
        return centerPoint;
    }

    private CirclePointOverlay getMainCirclePoint(float x, float y) {
        CirclePointOverlay circlePoint = new CirclePointOverlay();
        circlePoint.setPoint(new PointF(x, y));
        circlePoint.setRadius(mRadius);
        circlePoint.setLineCenterPoint(false);
        circlePoint.setSolidColor(mSolidColor);
        circlePoint.setTouchRatio(mTouchRatio);
        circlePoint.setStokeColor(mStokeColor);
        circlePoint.setStokeWidth(mStokeWidth);
        return circlePoint;
    }

    //    private List<CirclePoint> mCirclePoints = new ArrayList<>();

    private void preformClick(MotionEvent event) {
        showLog("===>>>preformClick");

        float eventX = event.getX();
        float eventY = event.getY();
        //        int targetCirclePointViewPosition = findTargetCirclePointViewPosition(eventX, eventY);
        if (mTargetCirclePointViewPosition >= 0) {
            if (mOnLayerClickListener != null) {
                mOnLayerClickListener.onPointClick(mCirclePointOverlays.get(mTargetCirclePointViewPosition), mTargetCirclePointViewPosition);
            }
            return;
        }
        if (mOnLayerClickListener != null) {
            mOnLayerClickListener.onLayerClick(new PointF(eventX, eventY), fromScreenLocation((int) (eventX + 0.5f), (int) (eventY + 0.5f)));
        }

        //        if (mCirclePoints.size() > 0) {
        //            mCirclePoints.add(getCenterCirclePoint(mCirclePoints.get(mCirclePoints.size() - 1), eventX, eventY));
        //        }
        //        mCirclePoints.add(getMainCirclePoint(eventX, eventY));
        //        ViewCompat.postInvalidateOnAnimation(this);
    }

    private int findTargetCirclePointViewPosition(float x, float y) {
        int position = -1;
        for (int i = 0; i < mCirclePointOverlays.size(); i++) {
            if (mCirclePointOverlays.get(i).isTargetView(x, y)) {
                position = i;
                break;
            }
        }
        showLog("===>>>findTargetCirclePointViewPosition, " + position);
        return position;
    }

    private OnLayerClickListener mOnLayerClickListener;

    public void setOnLayerClickListener(OnLayerClickListener onLayerClickListener) {
        mOnLayerClickListener = onLayerClickListener;
    }

    public void setMap(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;
    }

    private List<LineOverlay> mLineOverlays = new ArrayList<>();
    private List<PolygonOverlay> mPolygonOverlays = new ArrayList<>();

    public LineOverlay drawLine(List<LatLng> list, List<PointF> pointList, int lineColor, int lineWidth, boolean isClosedLine) {
        LineOverlay lineOverlay = new LineOverlay();
        lineOverlay.setList(list);
        lineOverlay.setPointList(pointList);
        lineOverlay.setLineColor(lineColor);
        lineOverlay.setLineWidth(lineWidth);
        lineOverlay.setClosedLine(isClosedLine);
        mLineOverlays.add(lineOverlay);
        return lineOverlay;
    }

    public PolygonOverlay drawPolygon(List<LatLng> list, List<PointF> pointList, int color) {
        PolygonOverlay polygonOverlay = new PolygonOverlay();
        polygonOverlay.setList(list);
        polygonOverlay.setPointList(pointList);
        polygonOverlay.setColor(color);
        mPolygonOverlays.add(polygonOverlay);
        return polygonOverlay;
    }

    private List<CirclePointOverlay> mCirclePointOverlays = new ArrayList<>();

    public CirclePointOverlay drawCirclePoint(LatLng latLng, PointF point, float radius, float stokeWidth, Integer stokeColor,
                                              Integer solidColor, float touchRatio, float centerRadius,
                                              Integer centerColor, boolean isLineCenterPoint, boolean enableDrag) {
        if (latLng == null && point == null) {
            throw new IllegalArgumentException("latLng is null, and point is null");
        }
        if (latLng == null) {
            latLng = fromScreenLocation((int) (point.x + 0.5f), (int) (point.y + 0.5f));
        }

        CirclePointOverlay circlePointOverlay = new CirclePointOverlay();
        circlePointOverlay.setLatLng(latLng);
        circlePointOverlay.setPoint(point);
        circlePointOverlay.setRadius(radius);
        circlePointOverlay.setStokeWidth(stokeWidth);
        circlePointOverlay.setStokeColor(stokeColor);
        circlePointOverlay.setSolidColor(solidColor);
        circlePointOverlay.setTouchRatio(touchRatio);
        circlePointOverlay.setCenterRadius(centerRadius);
        circlePointOverlay.setCenterColor(centerColor);
        circlePointOverlay.setLineCenterPoint(isLineCenterPoint);
        circlePointOverlay.setEnableDrag(enableDrag);
        mCirclePointOverlays.add(circlePointOverlay);
        return circlePointOverlay;
    }

    public void clear() {
        mLineOverlays.clear();
        mPolygonOverlays.clear();
        mCirclePointOverlays.clear();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void forceRefresh() {
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void forceRefreshTargetPosition() {
        mTargetCirclePointViewPosition = findTargetCirclePointViewPosition(mCurrentX, mCurrentY);
    }
}
