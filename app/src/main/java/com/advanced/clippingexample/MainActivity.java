package com.advanced.clippingexample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ClippedView(this));
    }

    private static class ClippedView extends View {

        private final Paint mPaint = new Paint();
        private final Path mPath = new Path();

        // SOS: getDimension returns dimension in pixels, eg 1.7 pixels
        private final int mClipRectRight = (int) getResources().getDimension(R.dimen.clipRectRight);
        private final int mClipRectBottom = (int) getResources().getDimension(R.dimen.clipRectBottom);
        private final int mRectInset = (int) getResources().getDimension(R.dimen.rectInset);
        private final int mSmallRectOffset = (int) getResources().getDimension(R.dimen.smallRectOffset);

        private final int mCircleRadius = (int) getResources().getDimension(R.dimen.circleRadius);

        private final int mTextOffsetY = (int) getResources().getDimension(R.dimen.textOffsetY);
        private final int mTextSize = (int) getResources().getDimension(R.dimen.textSize);

        // row & column variables for convenience
        private final int mColumnOne = mRectInset;
        private final int mColumnnTwo = mColumnOne + mClipRectRight + mRectInset;

        private final int mRowOne = mRectInset;
        private final int mRowTwo = mRowOne + mClipRectBottom + mRectInset;
        private final int mRowThree = mRowTwo + mClipRectBottom + mRectInset;
        private final int mRowFour = mRowThree + mClipRectBottom + mRectInset;
        private final int mTextRow = mRowFour + (int) (1.5 * mClipRectBottom);

        private final RectF mRectF;

        ClippedView(Context context) {
            this(context, null);
        }

        ClippedView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            setFocusable(true);
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth((int) getResources().getDimension(R.dimen.strokeWidth));
            mPaint.setTextSize((int) getResources().getDimension(R.dimen.textSize));

            mRectF = new RectF(mRectInset, mRectInset,
                    mClipRectRight - mRectInset, mClipRectBottom - mRectInset);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.BLACK);

            // ROW 1, COL 1
            canvas.save();
            canvas.translate(mColumnOne, mRowOne);
            drawClippedRectangle(canvas);
            canvas.restore();

            // ROW 1, COL 2
            // SOS: Region.Op.DIFFERENCE means draw everywhere except in this rect (normally clipRect
            // means draw only inside the rect). This restriction combines w the restriction in
            // drawClippedRectangle
            canvas.save();
            canvas.translate(mColumnnTwo, mRowOne);
            canvas.clipRect(2 * mRectInset, 2 * mRectInset,
                    mClipRectRight - 2 * mRectInset, mClipRectBottom - 2 * mRectInset);
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                canvas.clipRect(4 * mRectInset, 4 * mRectInset,
                        mClipRectRight - 4 * mRectInset, mClipRectBottom - 4 * mRectInset,
                        Region.Op.DIFFERENCE);
            } else {
                canvas.clipOutRect(4 * mRectInset, 4 * mRectInset,
                        mClipRectRight - 4 * mRectInset, mClipRectBottom - 4 * mRectInset);
            }
            drawClippedRectangle(canvas);
            canvas.restore();

            // ROW 2, COL 1
            canvas.save();
            canvas.translate(mColumnOne, mRowTwo);
            // SOS: rewind can be faster than reset if my Path always uses the same number of elements
            // (lines, circles etc), whereas it'll be slower if not.
            mPath.rewind();
            mPath.addCircle(mCircleRadius, mClipRectBottom - mCircleRadius, mCircleRadius, Path.Direction.CCW);
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                canvas.clipPath(mPath, Region.Op.DIFFERENCE);
            } else {
                canvas.clipOutPath(mPath);
            }
            drawClippedRectangle(canvas);
            canvas.restore();

            // ROW 2, COL 2
            canvas.save();
            canvas.translate(mColumnnTwo, mRowTwo);
            canvas.clipRect(0, 0,
                    mClipRectRight - mSmallRectOffset, mClipRectBottom - mSmallRectOffset);
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                canvas.clipRect(mSmallRectOffset, mSmallRectOffset,
                        mClipRectRight, mClipRectBottom,
                        Region.Op.INTERSECT);
            } else {
                canvas.clipRect(mSmallRectOffset, mSmallRectOffset,
                        mClipRectRight, mClipRectBottom);
            }
            drawClippedRectangle(canvas);
            canvas.restore();

            // ROW 3, COL 1
            canvas.save();
            canvas.translate(mColumnOne, mRowThree);
            mPath.rewind();
            mPath.addCircle(mRectInset + mCircleRadius, mCircleRadius + mRectInset,
                    mCircleRadius, Path.Direction.CCW);
            mPath.addRect(mClipRectRight / 2f - mCircleRadius, mCircleRadius + mRectInset,
                    mClipRectRight / 2f + mCircleRadius, mClipRectBottom - mRectInset,
                    Path.Direction.CCW);
            canvas.clipPath(mPath);
            drawClippedRectangle(canvas);
            canvas.restore();

            // ROW 3, COL 2
            canvas.save();
            canvas.translate(mColumnnTwo, mRowThree);
            mPath.rewind();
            mPath.addRoundRect(mRectF, (float) mClipRectRight / 4, (float) mClipRectRight / 4,
                    Path.Direction.CCW);
            canvas.clipPath(mPath);
            drawClippedRectangle(canvas);
            canvas.restore();

            // ROW 4, COL 1
            canvas.save();
            canvas.translate(mColumnOne, mRowFour);
            canvas.clipRect(2 * mRectInset, 2 * mRectInset,
                    mClipRectRight - 2 * mRectInset, mClipRectBottom - 2 * mRectInset);
            drawClippedRectangle(canvas);
            canvas.restore();

            // ROW 5, COL 2 (we align the end of the text w this col, not COL 1!)
            canvas.save();
            canvas.translate(mColumnnTwo, mTextRow);
            mPaint.setTextSize(mTextSize);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            mPaint.setColor(Color.CYAN);
            canvas.skew(0.2f, 0.3f);
            canvas.drawText("Skewed and", 0, 0, mPaint);
            canvas.restore();

            // ROW 5, COL 2
            canvas.save();
            canvas.translate(mColumnnTwo, mTextRow);
            mPaint.setColor(Color.CYAN);
            mPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Translated Text", 0, 0, mPaint);
            canvas.restore();
        }

        private void drawClippedRectangle(@NonNull Canvas canvas) {
            canvas.clipRect(0, 0, mClipRectRight, mClipRectBottom);

            canvas.drawColor(Color.WHITE);

            mPaint.setColor(Color.RED);
            canvas.drawLine(0, 0, mClipRectRight, mClipRectBottom, mPaint);

            mPaint.setColor(Color.GREEN);
            canvas.drawCircle(mCircleRadius, mClipRectBottom - mCircleRadius, mCircleRadius, mPaint);

            mPaint.setColor(Color.BLUE);
            // SOS: RIGHT side of text will be at the coordinates passed in drawText
            mPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Clipping", mClipRectRight, mTextOffsetY, mPaint);
        }
    }
}
