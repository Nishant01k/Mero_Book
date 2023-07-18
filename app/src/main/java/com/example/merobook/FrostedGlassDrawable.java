package com.example.merobook;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FrostedGlassDrawable extends Drawable {

    private Paint paint;
    private Bitmap sourceBitmap;
    private Canvas sourceCanvas;
    private Rect bounds;

    public FrostedGlassDrawable(View view) {
        paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        sourceBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        sourceCanvas = new Canvas(sourceBitmap);
        bounds = new Rect(0, 0, view.getWidth(), view.getHeight());

        // Draw the content view onto the bitmap
        view.draw(sourceCanvas);

        // Apply a blur filter to create the frosted glass effect
        paint.setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.NORMAL));
        paint.setColor(Color.TRANSPARENT);
        sourceCanvas.drawRect(bounds, paint);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawBitmap(sourceBitmap, null, bounds, null);
    }

    @Override
    public void setAlpha(int alpha) {}

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {}

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}