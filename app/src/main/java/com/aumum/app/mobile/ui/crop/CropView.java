package com.aumum.app.mobile.ui.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 26/11/2014.
 */
public class CropView extends View {

    public static final int BORDERDISTANCE = 50;

    private Paint mPaint;

    public CropView(Context context) {
        this(context, null);
    }

    public CropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        int borderlength = width - BORDERDISTANCE *2;

        mPaint.setColor(0xaa000000);

        // top
        canvas.drawRect(0, 0, width, (height - borderlength) / 2, mPaint);
        // bottom
        canvas.drawRect(0, (height + borderlength) / 2, width, height, mPaint);
        // left
        canvas.drawRect(0, (height - borderlength) / 2, BORDERDISTANCE,
                (height + borderlength) / 2, mPaint);
        // right
        canvas.drawRect(borderlength + BORDERDISTANCE, (height - borderlength) / 2, width,
                (height + borderlength) / 2, mPaint);

        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(2.0f);
        // top
        canvas.drawLine(BORDERDISTANCE, (height - borderlength) / 2, width - BORDERDISTANCE, (height - borderlength) / 2, mPaint);
        // bottom
        canvas.drawLine(BORDERDISTANCE, (height + borderlength) / 2, width - BORDERDISTANCE, (height + borderlength) / 2, mPaint);
        // left
        canvas.drawLine(BORDERDISTANCE, (height - borderlength) / 2, BORDERDISTANCE, (height + borderlength) / 2, mPaint);
        // right
        canvas.drawLine(width - BORDERDISTANCE, (height - borderlength) / 2, width - BORDERDISTANCE, (height + borderlength) / 2, mPaint);
    }
}
