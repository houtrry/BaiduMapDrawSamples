package com.houtrry.baidumapdrawsamples;

import android.graphics.PointF;

import com.baidu.mapapi.model.LatLng;

/**
 * @author: houtrry
 * @date: 2019/10/9 13:54
 * @version: $
 * @description:
 */
public interface OnLayerClickListener {

    void onLayerClick(PointF point, LatLng latLng);

    boolean onLayerDragClick(float dx, float dy);

    void onPointClick(CirclePointOverlay circlePoint, int position);
}
