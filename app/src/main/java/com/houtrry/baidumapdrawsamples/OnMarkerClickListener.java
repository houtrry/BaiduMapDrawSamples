package com.houtrry.baidumapdrawsamples;

import com.baidu.mapapi.model.LatLng;

/**
 * @author: houtrry
 * @date: 2019/10/24 17:43
 * @version: $
 * @description:
 */
public interface OnMarkerClickListener {
    void onMarkerClick(LatLng latLng, int position);
}
