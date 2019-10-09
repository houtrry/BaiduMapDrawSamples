package com.houtrry.baidumapdrawsamples;

/**
 * @author: houtrry
 * @date: 2019/10/9 14:42
 * @version: $
 * @description:
 */
public interface OnPointDragListener {
    void onPointDragStart(CirclePointOverlay circlePointOverlay, int index);
    void onPointDragging(CirclePointOverlay circlePointOverlay, int index);
    void onPointDragEnd(CirclePointOverlay circlePointOverlay, int index);
}
