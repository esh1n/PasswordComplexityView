package com.esh1n.passwordcomplexity.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.esh1n.passwordcomplexity.R;


public class PasswordComplexityView extends View {

    private static final float DEFAULT_TEXT_SIZE = 32;
    private static final int DEFAULT_TEXT_AND_CIRCLES_SPACE = 32;
    private static final int DEFAULT_CIRCLES_COUNT = 3;
    private static final int DEFAULT_CIRCLES_RADIUS = 16;
    private static final int DEFAULT_SPACE_CIRCLES = 16;
    String mText = "This is some text.";
    TextPaint mTextPaint;
    Paint circlesPaint;
    StaticLayout mStaticLayout;
    float mTextSize = DEFAULT_TEXT_SIZE;
    int mSpaceBetweenCirclesAndText = DEFAULT_TEXT_AND_CIRCLES_SPACE;
    int mSpaceBetweenCircles = DEFAULT_CIRCLES_COUNT;
    int mCirclesCount = DEFAULT_CIRCLES_COUNT;
    int mCirclesRadius = DEFAULT_CIRCLES_RADIUS;
    PasswordComplexity mState = PasswordComplexity.STRONG;

    // use PasswordComplexity constructor if creating MyView programmatically
    public PasswordComplexityView(Context context) {
        super(context);
        setUpPaints();
    }

    // this constructor is used when created from xml
    public PasswordComplexityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readDimensions(context, attrs);
        initColorsAndSize();
    }

    private void readDimensions(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.PasswordComplexityView, 0, 0);
        try {
            mTextSize = a.getDimension(R.styleable.PasswordComplexityView_titleTextSize, DEFAULT_TEXT_SIZE);
            mSpaceBetweenCirclesAndText = a.getDimensionPixelSize
                    (R.styleable.PasswordComplexityView_spaceBetweenCirclesAndText,
                            DEFAULT_TEXT_AND_CIRCLES_SPACE);
            mCirclesCount = a.getInteger
                    (R.styleable.PasswordComplexityView_circlesCount, DEFAULT_CIRCLES_COUNT);
            mCirclesRadius = a.getDimensionPixelSize
                    (R.styleable.PasswordComplexityView_circleRadiusSize, DEFAULT_CIRCLES_RADIUS);
            mSpaceBetweenCircles = a.getDimensionPixelSize
                    (R.styleable.PasswordComplexityView_spaceBetweenCircles, DEFAULT_SPACE_CIRCLES);
            String startState = a.getString(R.styleable.PasswordComplexityView_startState);
            mState = PasswordComplexity.valueOf(startState);

        } finally {
            a.recycle();
        }
    }


    private void initColorsAndSize() {
        setUpPaints();
        refreshPaintsByState(mState);
    }

    private void setUpPaints() {
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);

        circlesPaint = new Paint();
        circlesPaint.setStyle(Paint.Style.FILL);
    }

    private void refreshPaintsByState(PasswordComplexity passwordComplexity) {

        int textColor = ContextCompat.getColor(getContext(), passwordComplexity.getColorResourceId());
        int circlesColor = ContextCompat.getColor(getContext(), passwordComplexity.getColorResourceId());

        mTextPaint.setColor(textColor);
        circlesPaint.setColor(circlesColor);

        mText = getContext().getString(passwordComplexity.getDescriptionResourceId());

        if (!isEmpty()) {
            circlesPaint.setColor(circlesColor);
            mTextPaint.setColor(textColor);
            resizeTextLayout();
        }
    }

    private void resizeTextLayout() {
        int width = (int) mTextPaint.measureText(mText);
        mStaticLayout = new StaticLayout(mText, mTextPaint, width,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Tell the parent layout how big this view would like to be
        // but still respect any requirements (measure specs) that are passed down.

        // determine the width
        int width;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthRequirement = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthRequirement;
        } else {
            int textWidth = mStaticLayout.getWidth() + getPaddingLeft() + getPaddingRight();
            int circleWidth = mCirclesCount * 2 * mCirclesRadius;
            int spaceWidth = mSpaceBetweenCircles * (mCirclesCount - 1);
            width = textWidth + mSpaceBetweenCirclesAndText + circleWidth + spaceWidth;
            if (widthMode == MeasureSpec.AT_MOST) {
                if (width > widthRequirement) {
                    width = widthRequirement;
                    // too long for a single line so relayout as multiline
                    mStaticLayout = new StaticLayout(mText, mTextPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
                }
            }
        }

        // determine the height
        int height;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightRequirement = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightRequirement;
        } else {
            int maxHeight = Math.max(mStaticLayout.getHeight(), 2 * mCirclesRadius);
            height = maxHeight + getPaddingTop() + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightRequirement);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // do as little as possible inside onDraw to improve performance

        // draw the text on the canvas after adjusting for padding
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        mStaticLayout.draw(canvas);
        int centerY = mStaticLayout.getHeight() / 2;
        int startWidth = mStaticLayout.getWidth() + mCirclesRadius + mSpaceBetweenCirclesAndText;
        int index = 0;
        while (index < mCirclesCount) {
            canvas.drawCircle(startWidth, centerY, mCirclesRadius, circlesPaint);
            startWidth += mSpaceBetweenCircles + 2 * mCirclesRadius;
            index++;
        }

        canvas.restore();
    }

    public boolean isEmpty() {
        return mText == null || mText.length() == 0;
    }

    public void setComplexity(PasswordComplexity passwordComplexity) {
        refreshPaintsByState(passwordComplexity);
        requestLayout();
    }

    public enum PasswordComplexity {
        WEAK,
        STRONG,
        MEDIUM;

        public int getDescriptionResourceId() {
            switch (this) {
                case MEDIUM:
                    return R.string.text_medium_password;
                case STRONG:
                    return R.string.text_strong_password;
                case WEAK:
                    return R.string.text_weak_password;

            }
            return R.string.text_weak_password;
        }

        public int getColorResourceId() {
            switch (this) {
                case MEDIUM:
                    return R.color.color_medium_password;
                case STRONG:
                    return R.color.color_strong_password;
                case WEAK:
                    return R.color.color_weak_password;

            }
            return R.color.color_weak_password;
        }
    }
}