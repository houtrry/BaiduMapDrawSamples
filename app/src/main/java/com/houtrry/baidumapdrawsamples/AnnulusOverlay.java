package com.houtrry.baidumapdrawsamples;

import android.graphics.Point;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;

/**
 * @author: houtrry
 * @date: 2019/10/22 15:41
 * @version: $
 * @description:
 */
public class AnnulusOverlay {

    private Builder mBuilder;
    private Marker mMarker;

    private AnnulusOverlay() {
    }

    private AnnulusOverlay(Builder builder) {
        this.mBuilder = builder;
        drawAnnulusOverlay(builder);
    }

    private void drawAnnulusOverlay(Builder builder) {
        final BitmapDescriptor bitmapDescriptor = BitmapDescriptorManager.getInstance().getBitmapDescriptor(builder.outerRadius, builder.borderWidth, builder.borderColor, builder.solidColor, builder.centerRadius, builder.centerColor);
        final OverlayOptions option = new MarkerOptions()
                .position(builder.latLng)
                .icon(bitmapDescriptor)
                .draggable(false)
                .zIndex(builder.zIndex)
                .anchor(0.5f, 0.5f)
                //设置平贴地图，在地图中双指下拉查看效果
                .flat(true);

        //在地图上添加Marker，并显示
        mMarker = (Marker) builder.baiduMap.addOverlay(option);
    }

    public boolean isTarget(int x, int y) {
        if (mBuilder.baiduMap == null || !mBuilder.enableDrag || mBuilder.touchRatio <= 0 || mBuilder.latLng == null) {
            return false;
        }
        Projection projection = mBuilder.baiduMap.getProjection();
        if (projection == null) {
            return false;
        }
        final Point point = projection.toScreenLocation(mBuilder.latLng);
        return Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2)) < mBuilder.outerRadius * mBuilder.touchRatio;
    }

    public void remove() {
        if (mMarker != null) {
            mMarker.remove();
            mMarker = null;
        }
    }

    public void updatePosition(LatLng latLng) {
        if (latLng == null || mBuilder == null) {
            return;
        }
        mBuilder.latLng(latLng);
        if (mMarker != null) {
            mMarker.setPosition(latLng);
        }
    }

    public void updateProperty(int borderWidth, Integer borderColor, int outerRadius, Integer solidColor,
                               Integer centerColor, int centerRadius, float touchRatio, boolean enableDrag) {
        if (mBuilder == null) {
            return;
        }
        mBuilder.borderWidth(borderWidth)
                .borderColor(borderColor)
                .outerRadius(outerRadius)
                .solidColor(solidColor)
                .centerColor(centerColor)
                .centerRadius(centerRadius)
                .touchRatio(touchRatio)
                .enableDrag(enableDrag);

        mMarker.setIcon(BitmapDescriptorManager.getInstance().getBitmapDescriptor(mBuilder.outerRadius, mBuilder.borderWidth, mBuilder.borderColor, mBuilder.solidColor, mBuilder.centerRadius, mBuilder.centerColor));
    }

    public static class Builder {
        private int borderWidth;
        private Integer borderColor;
        private int outerRadius;

        private Integer solidColor;
        private Integer centerColor;
        private int centerRadius;
        private BaiduMap baiduMap;
        private LatLng latLng;
        private int zIndex;
        private float touchRatio;
        private boolean enableDrag;

        public Builder borderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
            return this;
        }

        public Builder borderColor(Integer borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        public Builder outerRadius(int outerRadius) {
            this.outerRadius = outerRadius;
            return this;
        }

        public Builder solidColor(Integer solidColor) {
            this.solidColor = solidColor;
            return this;
        }

        public Builder centerColor(Integer centerColor) {
            this.centerColor = centerColor;
            return this;
        }

        public Builder centerRadius(int centerRadius) {
            this.centerRadius = centerRadius;
            return this;
        }

        public Builder latLng(LatLng latLng) {
            this.latLng = latLng;
            return this;
        }

        public Builder zIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        public Builder touchRatio(float touchRatio) {
            this.touchRatio = touchRatio;
            return this;
        }

        public Builder enableDrag(boolean enableDrag) {
            this.enableDrag = enableDrag;
            return this;
        }

        public Builder baiduMap(BaiduMap baiduMap) {
            this.baiduMap = baiduMap;
            return this;
        }

        public AnnulusOverlay build() {
            return new AnnulusOverlay(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "borderWidth=" + borderWidth +
                    ", borderColor=" + borderColor +
                    ", outerRadius=" + outerRadius +
                    ", solidColor=" + solidColor +
                    ", centerColor=" + centerColor +
                    ", centerRadius=" + centerRadius +
                    ", baiduMap=" + baiduMap +
                    ", latLng=" + latLng +
                    ", zIndex=" + zIndex +
                    ", touchRatio=" + touchRatio +
                    ", enableDrag=" + enableDrag +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AnnulusOverlay{" +
                "mBuilder=" + mBuilder +
                '}';
    }
}
