package com.houtrry.baidumapdrawsamples;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Layer2Activity";

    private LayerFrameView mLayerFrameView;

    private List<LatLng> mLatLngs = new ArrayList<>();
    private Polyline mPolyline;
    private boolean needClosedLine = false;
    private List<AnnulusOverlay> mAnnulusOverlays = new ArrayList<>();

    private static final int BORDER_WIDTH_MAIN = 5;
    private static final int BORDER_WIDTH_CENTER = 4;
    private static final int BORDER_COLOR_MAIN = Color.RED;
    private static final int BORDER_COLOR_CENTER = Color.BLACK;
    private static final int OUTER_RADIUS_MAIN = 25;
    private static final int OUTER_RADIUS_CENTER = 20;
    private static final int SOLID_COLOR_MAIN = Color.WHITE;
    private static final int SOLID_COLOR_CENTER = Color.WHITE;
    private static final int CENTER_COLOR_MAIN = Color.BLUE;
    private static final int CENTER_COLOR_CENTER = Color.RED;
    private static final int CENTER_RADIUS_MAIN = 15;
    private static final int CENTER_RADIUS_CENTER = 12;
    private static final int TOUCH_RATIO_MAIN = 3;
    private static final int TOUCH_RATIO_CENTER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayerFrameView = findViewById(R.id.lfv);

        mLayerFrameView.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mLatLngs.size() > 0) {
                    addNode(getCenterLatLng(mLatLngs.get(mLatLngs.size() - 1), latLng), false);
                }
                mLatLngs.add(latLng);
                addNode(latLng, true);
                refreshLine();
            }
        });
        mLayerFrameView.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public void onMarkerClick(LatLng latLng, int position) {
                showLog("===>>>onMarkerClick, position: " + position + ", latLng: " + latLng);
                if (position == 0 && mLatLngs != null && mLatLngs.size() > 2 && !needClosedLine) {
                    needClosedLine = true;
                    addNode(getCenterLatLng(mLatLngs.get(mLatLngs.size() - 1), latLng), false);
                    refreshLine();
                }
            }
        });
        mLayerFrameView.setOnMarkerDragListener(new OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(LatLng latLng, int position) {
                showLog("===>>>onMarkerDragStart, position: " + position + ", latLng: " + latLng);

                if (isMainNode(latLng, position)) {

                } else {
                    AnnulusOverlay annulusOverlay = mAnnulusOverlays.get(position);
                    annulusOverlay.updateProperty(BORDER_WIDTH_MAIN, BORDER_COLOR_MAIN, OUTER_RADIUS_MAIN, SOLID_COLOR_MAIN, CENTER_COLOR_MAIN, CENTER_RADIUS_MAIN, TOUCH_RATIO_MAIN, true);

                    int lastMainPosition = position / 2;
                    showLog("===>>>onMarkerDragStart, lastMainPosition: " + lastMainPosition);
                    addNode(getCenterLatLng(latLng, mLatLngs.get(lastMainPosition)), false, position);
                    if (lastMainPosition < mLatLngs.size()-1) {
                        addNode(getCenterLatLng(latLng, mLatLngs.get(lastMainPosition + 1)), false, position + 2);
                    } else {
                        addNode(getCenterLatLng(latLng, mLatLngs.get(0)), false, position + 2);
                    }
                    mLatLngs.add(lastMainPosition + 1, latLng);
                    mLayerFrameView.forceRefreshPosition();
                }
            }

            @Override
            public void onMarkerDragging(LatLng latLng, int position) {
                showLog("===>>>onMarkerDragging, position: " + position + ", latLng: " + latLng);
                if (isMainNode(latLng, position)) {
                    mLatLngs.set(position / 2, latLng);
                    if (position > 0) {
                        mAnnulusOverlays.get(position - 1).updatePosition(getCenterLatLng(mLatLngs.get(position / 2 - 1), latLng));
                    } else if (position == 0 && needClosedLine) {
                        mAnnulusOverlays.get(mAnnulusOverlays.size() - 1).updatePosition(getCenterLatLng(mLatLngs.get(mLatLngs.size() - 1), latLng));
                    }
                    if (position / 2 < mLatLngs.size() - 1) {
                        mAnnulusOverlays.get(position + 1).updatePosition(getCenterLatLng(mLatLngs.get(position / 2 + 1), latLng));
                    } else if (position / 2 == mLatLngs.size() - 1 && mAnnulusOverlays.size() > position + 1) {
                        mAnnulusOverlays.get(position + 1).updatePosition(getCenterLatLng(mLatLngs.get(0), latLng));
                    }
                }
                refreshLine();
            }

            @Override
            public void onMarkerDragEnd(LatLng latLng, int position) {
                if (isMainNode(latLng, position)) {
                    mLatLngs.set(position / 2, latLng);
                }
                refreshLine();
                showLog("===>>>onMarkerDragEnd, position: " + position + ", latLng: " + latLng);
            }
        });
    }

    private LatLng getCenterLatLng(LatLng latLng, LatLng latLng1) {
        return new LatLng((latLng.latitude + latLng1.latitude) * 0.5d, (latLng.longitude + latLng1.longitude) * 0.5d);
    }

    private void refreshLine() {
        if (mLatLngs == null || mLatLngs.size() < 2) {
            return;
        }
        List<LatLng> list = new ArrayList<>(mLatLngs);
        if (needClosedLine) {
            if (list.get(0).longitude != list.get(list.size() - 1).longitude || list.get(0).longitude != list.get(list.size() - 1).longitude) {
                list.add(list.get(0));
            }
        }
        showLog("===>>>refreshLine, list: " + list);
        if (mPolyline == null) {
            mPolyline = mLayerFrameView.addLineOverlay(list, Color.GREEN, 6, !needClosedLine);
        } else {
            mPolyline.setPoints(list);
            mPolyline.setDottedLine(!needClosedLine);
        }
    }


    private void showLog(String message) {
        Log.d(TAG, message);
    }

    private void addNode(LatLng latLng, boolean isMainNode) {
        AnnulusOverlay.Builder builder = new AnnulusOverlay.Builder().outerRadius(isMainNode ? OUTER_RADIUS_MAIN : OUTER_RADIUS_CENTER).borderColor(isMainNode ? BORDER_COLOR_MAIN : BORDER_COLOR_CENTER).borderWidth(isMainNode ? BORDER_WIDTH_MAIN : BORDER_WIDTH_CENTER).solidColor(isMainNode?SOLID_COLOR_MAIN:SOLID_COLOR_CENTER)
                .centerColor(isMainNode ? CENTER_COLOR_MAIN : CENTER_COLOR_CENTER).centerRadius(isMainNode ? CENTER_RADIUS_MAIN : CENTER_RADIUS_CENTER).latLng(latLng).enableDrag(true).touchRatio(isMainNode? TOUCH_RATIO_MAIN:TOUCH_RATIO_CENTER);
        mAnnulusOverlays.add(mLayerFrameView.addAnnulusOverlay(builder));
    }

    private void addNode(LatLng latLng, boolean isMainNode, int position) {
        AnnulusOverlay.Builder builder = new AnnulusOverlay.Builder().outerRadius(isMainNode ? OUTER_RADIUS_MAIN : OUTER_RADIUS_CENTER).borderColor(isMainNode ? BORDER_COLOR_MAIN : BORDER_COLOR_CENTER).borderWidth(isMainNode ? BORDER_WIDTH_MAIN : BORDER_WIDTH_CENTER).solidColor(isMainNode?SOLID_COLOR_MAIN:SOLID_COLOR_CENTER)
                .centerColor(isMainNode ? CENTER_COLOR_MAIN : CENTER_COLOR_CENTER).centerRadius(isMainNode ? CENTER_RADIUS_MAIN : CENTER_RADIUS_CENTER).latLng(latLng).enableDrag(true).touchRatio(isMainNode? TOUCH_RATIO_MAIN:TOUCH_RATIO_CENTER);
        mAnnulusOverlays.add(position, mLayerFrameView.addAnnulusOverlay(builder, position));
    }

    private boolean isMainNode(LatLng latLng, int position) {
        return position % 2 == 0;
    }
}
