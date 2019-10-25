package com.houtrry.baidumapdrawsamples;

import com.baidu.mapapi.model.LatLng;

/**
 * @author: houtrry
 * @date: 2019/10/24 17:54
 * @version: $
 * @description:
 */
public interface OnMarkerDragListener {
    void onMarkerDragStart(LatLng latLng, int position);
    void onMarkerDragging(LatLng latLng, int position);
    void onMarkerDragEnd(LatLng latLng, int position);
}
