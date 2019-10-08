package com.houtrry.baidumapdrawsamples;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
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
public class MapFloatingLayerView extends View {

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

    public MapFloatingLayerView(Context context) {
        this(context, null);
    }

    public MapFloatingLayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapFloatingLayerView(Context context, AttributeSet attrs, int defStyle) {
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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.argb(80, 123, 255, 156));
        //        drawLine(canvas);
        drawLinePath(canvas);
        drawCirclePoint(canvas);
    }

    private CirclePointView mStartPointView = null;
    private CirclePointView mEndPointView = null;
    private int mCirclePointViewSize = 0;

    private void drawLine(Canvas canvas) {
        mCirclePointViewSize = mCirclePointViews.size();
        for (int i = 0; i < mCirclePointViewSize - 1; i++) {
            mStartPointView = mCirclePointViews.get(i);
            mEndPointView = mCirclePointViews.get(i + 1);
            canvas.drawLine(mStartPointView.getPoint().x, mStartPointView.getPoint().y, mEndPointView.getPoint().x, mEndPointView.getPoint().y, mLinePaint);
        }
        if (isClosedLine && mCirclePointViewSize > 2) {
            mStartPointView = mCirclePointViews.get(mCirclePointViewSize - 1);
            mEndPointView = mCirclePointViews.get(0);
            canvas.drawLine(mStartPointView.getPoint().x, mStartPointView.getPoint().y, mEndPointView.getPoint().x, mEndPointView.getPoint().y, mLinePaint);
        }
    }

    private Path mLinePath = new Path();
    private PointF mPointF;

    private void drawLinePath(Canvas canvas) {
        mCirclePointViewSize = mCirclePointViews.size();
        if (mCirclePointViewSize < 2) {

            return;
        }
        mLinePath.reset();
        mPointF = mCirclePointViews.get(0).getPoint();
        mLinePath.moveTo(mPointF.x, mPointF.y);
        for (int i = 1; i < mCirclePointViewSize; i++) {
            mPointF = mCirclePointViews.get(i).getPoint();
            mLinePath.lineTo(mPointF.x, mPointF.y);
        }
        if (isClosedLine && mCirclePointViewSize > 2) {
            mLinePath.close();
        }
        canvas.drawPath(mLinePath, mLinePaint);
    }

    private void drawCirclePoint(Canvas canvas) {
        for (CirclePointView circlePointView : mCirclePointViews) {
            circlePointView.draw(canvas, mPaint);
        }
    }

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
        for (CirclePointView circlePointView : mCirclePointViews) {
            PointF pointF = circlePointView.getPoint();
            point.x = (int) (pointF.x + 0.5f);
            point.y = (int) (pointF.y + 0.5f);
            LatLng latLng = projection.fromScreenLocation(point);
            circlePointView.setLatLng(latLng);
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
        for (CirclePointView circlePointView : mCirclePointViews) {
            latLng = circlePointView.getLatLng();
            if (latLng == null) {
                PointF pointF = circlePointView.getPoint();
                point.x = (int) (pointF.x + 0.5f);
                point.y = (int) (pointF.y + 0.5f);
                latLng = projection.fromScreenLocation(point);
                circlePointView.setLatLng(latLng);
            }
            Point newPoint = projection.toScreenLocation(latLng);
            circlePointView.setPoint(newPoint.x, newPoint.y);
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

    private Float mFixX = null;
    private Float mFixY = null;

    public boolean handleTouchEvent(MotionEvent event) {
        showLog("===>>>handleTouchEvent, event: " + event);
        boolean needIntercept = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownTime = System.currentTimeMillis();
                hasTouchDrag = false;
                mDragStartX = event.getX();
                mDragStartY = event.getY();
                if (mFixX == null) {
                    mFixX = mDragStartX - event.getX();
                    mFixY = mDragStartY - event.getY();
                }
                int targetCirclePointViewPosition = findTargetCirclePointViewPosition(mCurrentX, mCurrentY);
                showLog("===>>>preformPointDrag,,, targetCirclePointViewPosition: "+targetCirclePointViewPosition);
                if (targetCirclePointViewPosition >= 0) {
                    needIntercept =  true;
                }
                isFirstMove = true;
                break;
            case MotionEvent.ACTION_MOVE:
                hasTouchDrag = true;
                if (enableDrag && mCirclePointViews.size() > 0) {
                    preformDrag(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                long dt = System.currentTimeMillis() - mTouchDownTime;
                showLog("===>>>hasTouchDrag: " + hasTouchDrag + ", dt: " + dt);
                if (!hasTouchDrag && dt < ViewConfiguration.getPressedStateDuration() * 4) {
                    preformClick(event);
                }
                break;
            default:
                break;

        }
        return needIntercept;
    }

    private void preformDrag(MotionEvent event) {
        mCurrentX = event.getX();
        mCurrentY = event.getY();
        int targetCirclePointViewPosition = findTargetCirclePointViewPosition(mCurrentX, mCurrentY);
        showLog("===>>>preformDrag, targetCirclePointViewPosition: " + targetCirclePointViewPosition);
        if (targetCirclePointViewPosition < 0) {
            showLog("===>>>preformDrag, no targetCirclePointView in list");
//            preformOuterDrag();
            return;
        }
        preformPointDrag(targetCirclePointViewPosition);
    }

    private void preformOuterDrag() {
        showLog("===>>>preformOuterDrag");
        float dx = mCurrentX - mDragStartX;
        float dy = mCurrentY - mDragStartY;
        if (mOnLayerClickListener != null && mOnLayerClickListener.onLayerDragClick(dx, dy)) {
            return;
        }
        for (CirclePointView view : mCirclePointViews) {
            view.fixPosition(dx, dy);
        }
        ViewCompat.postInvalidateOnAnimation(this);
        mDragStartX = mCurrentX;
        mDragStartY = mCurrentY;
    }


    private boolean isFirstMove = false;

    private void preformPointDrag(int targetCirclePointViewPosition) {
        showLog("===>>>preformPointDrag: "+targetCirclePointViewPosition);

        // TODO: 2019/10/8 根据位置映射坐标经纬度
        if (mBaiduMap == null) {
            return;
        }
        Projection projection = mBaiduMap.getProjection();
        if (projection == null) {
            return;
        }
        final int x = (int) (mCurrentX + 0.5f);
        final int y = (int) (mCurrentY + 0.5f);
        CirclePointView circlePointView = mCirclePointViews.get(targetCirclePointViewPosition);
        circlePointView.setLatLng(projection.fromScreenLocation(new Point(x, y)));
        circlePointView.setPoint(x, y);

        showLog("===>>>preformPointDrag, isFirstMove: "+isFirstMove+", "+circlePointView.isLineCenterPoint());
        if (isFirstMove) {
            if (circlePointView.isLineCenterPoint()) {
                circlePointView.setLineCenterPoint(true);
                mCirclePointViews.set(targetCirclePointViewPosition, circlePointView);
                mCirclePointViews.add(targetCirclePointViewPosition + 1, getCenterCirclePoint(mCirclePointViews.get(targetCirclePointViewPosition + 1), x, y));
                mCirclePointViews.add(targetCirclePointViewPosition, getCenterCirclePoint(mCirclePointViews.get(targetCirclePointViewPosition - 1), x, y));
            } else {

            }


            isFirstMove = false;
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private CirclePointView getCenterCirclePoint(CirclePointView pointView, float x, float y) {
        PointF point = new PointF((x + pointView.getPoint().x) * 0.5f, (y + pointView.getPoint().y) * 0.5f);
        CirclePointView centerPoint = new CirclePointView();
        centerPoint.setPoint(point);
        centerPoint.setRadius(mLineCenterPointRadius);
        centerPoint.setFixX(mFixX);
        centerPoint.setFixY(mFixY);
        centerPoint.setLineCenterPoint(true);
        centerPoint.setSolidColor(mLineCenterPointColor);
        centerPoint.setTouchRatio(mLineCenterPointRatio);
        return centerPoint;
    }

    private CirclePointView getMainCirclePoint(float x, float y) {
        CirclePointView circlePointView = new CirclePointView();
        circlePointView.setPoint(new PointF(x, y));
        circlePointView.setRadius(mRadius);
        circlePointView.setFixX(mFixX);
        circlePointView.setFixY(mFixY);
        circlePointView.setLineCenterPoint(false);
        circlePointView.setSolidColor(mSolidColor);
        circlePointView.setTouchRatio(mTouchRatio);
        circlePointView.setStokeColor(mStokeColor);
        circlePointView.setStokeWidth(mStokeWidth);
        return circlePointView;
    }

    private List<CirclePointView> mCirclePointViews = new ArrayList<>();

    private void preformClick(MotionEvent event) {
        showLog("===>>>preformClick");

        float eventX = event.getX();
        float eventY = event.getY();
        int targetCirclePointViewPosition = findTargetCirclePointViewPosition(eventX, eventY);
        if (targetCirclePointViewPosition >= 0) {
            if (mOnLayerClickListener != null) {
                mOnLayerClickListener.onPointClick(mCirclePointViews.get(targetCirclePointViewPosition));
            }
            return;
        }
        if (mOnLayerClickListener != null) {
            mOnLayerClickListener.onLayerClick(new PointF(eventX, eventY));
        }

        if (mCirclePointViews.size() > 0) {
            mCirclePointViews.add(getCenterCirclePoint(mCirclePointViews.get(mCirclePointViews.size() - 1), eventX, eventY));
        }
        mCirclePointViews.add(getMainCirclePoint(eventX, eventY));
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private int findTargetCirclePointViewPosition(float x, float y) {
        int position = -1;
        for (int i = 0; i < mCirclePointViews.size(); i++) {
            if (mCirclePointViews.get(i).isTargetView(x, y)) {
                position = i;
                break;
            }
        }
        return position;
    }

    private OnLayerClickListener mOnLayerClickListener;

    public void setOnLayerClickListener(OnLayerClickListener onLayerClickListener) {
        mOnLayerClickListener = onLayerClickListener;
    }

    public void setMap(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;
    }

    public interface OnLayerClickListener {
        void onLayerClick(PointF point);

        boolean onLayerDragClick(float dx, float dy);

        void onPointClick(CirclePointView circlePointView);
    }
}
