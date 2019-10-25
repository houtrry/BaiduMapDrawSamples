package com.houtrry.baidumapdrawsamples;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.SparseArray;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;

import java.util.Objects;

/**
 * @author: houtrry
 * @date: 2019/10/14 16:43
 * @version: $
 * @description:
 */
public class BitmapDescriptorManager {

    private SparseArray<BitmapDescriptor> mBitmapDescriptorSparseArray = new SparseArray<>();

    private BitmapDescriptorManager() {
    }

    public BitmapDescriptor getBitmapDescriptor(int radius, int stokeWidth, Integer stokeColor, Integer solidColor, float centerRadius, Integer centerColor) {
        int hash = hash(radius, stokeWidth, stokeColor, solidColor, centerRadius, centerColor);
        BitmapDescriptor bitmapDescriptor = mBitmapDescriptorSparseArray.get(hash);
        if (bitmapDescriptor != null) {
            return bitmapDescriptor;
        }
        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(createBitmap(radius, stokeWidth, stokeColor, solidColor, centerRadius, centerColor));
        mBitmapDescriptorSparseArray.put(hash, bitmapDescriptor);
        return bitmapDescriptor;
    }

    private static class SingleInstance {
        private static final BitmapDescriptorManager INSTANCE = new BitmapDescriptorManager();
    }

    public static BitmapDescriptorManager getInstance() {
        return SingleInstance.INSTANCE;
    }

    private int hash(int radius, int stokeWidth, Integer stokeColor, Integer solidColor, float centerRadius, Integer centerColor) {
        return Objects.hash(radius, stokeWidth, stokeColor, solidColor, centerRadius, centerColor);
    }

    private Bitmap createBitmap(int radius, int stokeWidth, Integer stokeColor, Integer solidColor, float centerRadius, Integer centerColor) {
        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        if (stokeColor != null) {
            paint.setColor(stokeColor);
            canvas.drawCircle(radius, radius, radius, paint);
        }
        if (solidColor != null) {
            paint.setColor(solidColor);
            canvas.drawCircle(radius, radius, radius - stokeWidth, paint);
        }
        if (centerRadius > 0 && centerColor != null) {
            paint.setColor(centerColor);
            canvas.drawCircle(radius, radius, centerRadius, paint);
        }
        return bitmap;
    }
}
