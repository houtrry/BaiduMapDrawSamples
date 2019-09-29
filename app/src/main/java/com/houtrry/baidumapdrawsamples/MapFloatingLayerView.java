package com.houtrry.baidumapdrawsamples;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    private int mFixX;
    private int mFixY;

    private float mTouchRatio = 3;
    private Paint mPaint;
    private boolean enableDrag = true;


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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.argb(80, 123, 255, 156));
        for (CirclePointView circlePointView: mCirclePointViews) {
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

    public void handleTouchEvent(MotionEvent event) {
        showLog("===>>>handleTouchEvent, event: " + event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownTime = System.currentTimeMillis();
                hasTouchDrag = false;
                mDragStartX = event.getX();
                mDragStartY = event.getY();
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
                showLog("===>>>hasTouchDrag: "+hasTouchDrag+", dt: "+dt);
                mLatestDragX = mCurrentX - mDragStartX;
                mLatestDragY = mCurrentY - mDragStartY;
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
        showLog("===>>>preformDrag, targetCirclePointViewPosition: "+targetCirclePointViewPosition);
        if (targetCirclePointViewPosition < 0) {
            showLog("===>>>preformDrag, no targetCirclePointView in list");
            preformOuterDrag();
            return;
        }
        preformPointDrag();
    }

    private float mLatestDragX = 0;
    private float mLatestDragY = 0;
    private void preformOuterDrag() {
        mLatestDragX = 0;
        mLatestDragY = 0;
        showLog("===>>>preformOuterDrag");
        float dx = mLatestDragX + mCurrentX - mDragStartX;
        float dy = mLatestDragY + mCurrentY - mDragStartY;
        if (mOnLayerClickListener != null && mOnLayerClickListener.onLayerDragClick(dx, dy)) {
            return;
        }
        for (CirclePointView view : mCirclePointViews) {
            view.fixPosition(dx, dy);
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void preformPointDrag() {
        showLog("===>>>preformPointDrag");

    }

    private List<CirclePointView> mCirclePointViews = new ArrayList<>();

    private void preformClick(MotionEvent event) {
        showLog("===>>>preformClick");
        if (mOnLayerClickListener != null) {
            mOnLayerClickListener.onLayerClick(new PointF(event.getX(), event.getY()));
        }
        PointF point = new PointF(event.getX(), event.getY());
        CirclePointView circlePointView = new CirclePointView();
        circlePointView.setPoint(point);
        circlePointView.setRadius(mRadius);
        circlePointView.setFixX(0);
        circlePointView.setFixY(0);
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
    }
}
