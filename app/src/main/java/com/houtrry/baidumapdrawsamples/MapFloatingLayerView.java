package com.houtrry.baidumapdrawsamples;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

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
        return super.onTouchEvent(event);
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

    public void handleTouchEvent(MotionEvent event) {
        showLog("===>>>handleTouchEvent, event: " + event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownTime = System.currentTimeMillis();
                hasTouchDrag = false;
                mDragStartX = event.getX();
                mDragStartY = event.getY();
                if (mFixX == null) {
                    mFixX = mDragStartX - event.getRawX();
                    mFixY = mDragStartY - event.getRawY();
                }
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
    }

    private void preformDrag(MotionEvent event) {
        mCurrentX = event.getX();
        mCurrentY = event.getY();
        int targetCirclePointViewPosition = findTargetCirclePointViewPosition(mCurrentX, mCurrentY);
        showLog("===>>>preformDrag, targetCirclePointViewPosition: " + targetCirclePointViewPosition);
        if (targetCirclePointViewPosition < 0) {
            showLog("===>>>preformDrag, no targetCirclePointView in list");
            preformOuterDrag();
            return;
        }
        preformPointDrag();
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

    private void preformPointDrag() {
        showLog("===>>>preformPointDrag");
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
            CirclePointView lastPoint = mCirclePointViews.get(mCirclePointViews.size() - 1);

            PointF point = new PointF((eventX + lastPoint.getPoint().x) * 0.5f, (eventY + lastPoint.getPoint().y) * 0.5f);
            CirclePointView centerPoint = new CirclePointView();
            centerPoint.setPoint(point);
            centerPoint.setRadius(mLineCenterPointRadius);
            centerPoint.setFixX(mFixX);
            centerPoint.setFixY(mFixY);
            centerPoint.setSolidColor(mLineCenterPointColor);
            centerPoint.setTouchRatio(mLineCenterPointRatio);
            mCirclePointViews.add(centerPoint);
        }

        PointF point = new PointF(eventX, eventY);
        CirclePointView circlePointView = new CirclePointView();
        circlePointView.setPoint(point);
        circlePointView.setRadius(mRadius);
        circlePointView.setFixX(mFixX);
        circlePointView.setFixY(mFixY);
        circlePointView.setSolidColor(mSolidColor);
        circlePointView.setTouchRatio(mTouchRatio);
        circlePointView.setStokeColor(mStokeColor);
        circlePointView.setStokeWidth(mStokeWidth);
        mCirclePointViews.add(circlePointView);
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

    public interface OnLayerClickListener {
        void onLayerClick(PointF point);

        boolean onLayerDragClick(float dx, float dy);

        void onPointClick(CirclePointView circlePointView);
    }
}
