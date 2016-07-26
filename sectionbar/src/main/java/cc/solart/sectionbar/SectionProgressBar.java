/*
 * Copyright (C) 2016 solartisan/imilk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.solart.sectionbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 分段进度条
 */
public class SectionProgressBar extends View {
    private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#ececec");
    private static final int DEFAULT_FOREGROUND_COLOR = Color.parseColor("#b93a2c");
    private static final int DEFAULT_SPACE_COLOR = Color.WHITE;
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#dd000000");
    private static final int DEFAULT_LIGHT_TEXT_COLOR = Color.parseColor("#89000000");
    private static final float DEFAULT_INITIAL_RATIO = 0.05f;//just pinned zero position
    private static final long DEFAULT_TRANSITION_TIME = 1000;
    private int mBackgroundColor;
    private int mForegroundColor;
    private int mSpaceColor;
    private int mTextColor;
    private int mLightTextColor;
    private float mTextSize;
    private float mLightTextSize;
    private float mBarHeight;
    private Drawable mCursorDrawable;
    private Paint mBackgroundPaint;
    private Paint mForegroundPaint;
    private Paint mTextPaint;
    private Paint mLightTextPaint;
    private Paint mSectionPaint;
    private String[] mLevels;
    private int[] mLevelValues;
    private ValueAnimator mRatioAnimator;
    private long mTransitionDuration = DEFAULT_TRANSITION_TIME;
    private float mRatio = DEFAULT_INITIAL_RATIO;
    private RatioPolicy mPolicy = new RatioPolicy() {
        @Override
        public float computeProgressRatio(int current) {
            float ratio = 0.05f;
            float span = 0.9f / (mLevelValues.length - 1);
            int index = 0;
            int sum = 0;
            for (int i = 1; i <= mLevelValues.length - 1; i++) {
                if (current >= mLevelValues[i]) {
                    ratio += span;
                    index = i;
                    sum = mLevelValues[i];
                }
            }
            if (index < (mLevelValues.length - 1)) {
                int value_span = mLevelValues[index + 1] - mLevelValues[index];
                float diff = current - sum;
                ratio = ratio + (diff / value_span) * span;
            } else {
                ratio = 0.96f;
            }
            return ratio;
        }
    };

    public SectionProgressBar(Context context) {
        this(context, null);
    }

    public SectionProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLevels = context.getResources().getStringArray(R.array.SectionLevels);
        mLevelValues = context.getResources().getIntArray(R.array.SectionLevelValues);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SectionProgressBar, defStyleAttr, 0);
        mBackgroundColor = ta.getColor(R.styleable.SectionProgressBar_section_background, DEFAULT_BACKGROUND_COLOR);
        mForegroundColor = ta.getColor(R.styleable.SectionProgressBar_section_foreground, DEFAULT_FOREGROUND_COLOR);
        mSpaceColor = ta.getColor(R.styleable.SectionProgressBar_section_space_color, DEFAULT_SPACE_COLOR);
        mTextColor = ta.getColor(R.styleable.SectionProgressBar_section_text_color, DEFAULT_TEXT_COLOR);
        mLightTextColor = ta.getColor(R.styleable.SectionProgressBar_section_light_text_color, DEFAULT_LIGHT_TEXT_COLOR);
        mTextSize = ta.getDimension(R.styleable.SectionProgressBar_section_text_size, sp2px(12));
        mLightTextSize = ta.getDimension(R.styleable.SectionProgressBar_section_text_size, sp2px(12));
        mBarHeight = ta.getDimension(R.styleable.SectionProgressBar_section_bar_height, dp2px(5));
        mCursorDrawable = ta.getDrawable(R.styleable.SectionProgressBar_section_bar_cursor);
        ta.recycle();

        initializePaints();
    }

    private void initializePaints() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setColor(mBackgroundColor);
        mForegroundPaint = new Paint();
        mForegroundPaint.setAntiAlias(true);
        mForegroundPaint.setStyle(Paint.Style.FILL);
        mForegroundPaint.setColor(mForegroundColor);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        mLightTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLightTextPaint.setColor(mLightTextColor);
        mLightTextPaint.setTextAlign(Paint.Align.CENTER);
        mLightTextPaint.setTextSize(mLightTextSize);
        mSectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSectionPaint.setColor(mSpaceColor);
    }

    private float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    private float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawForeground(canvas);
        drawSectionAndSubscript(canvas);
    }

    private void drawBackground(Canvas canvas) {
        RectF rectF = new RectF();
        rectF.left = getPaddingLeft();
        rectF.top = getHeight() / 3.f - mBarHeight / 2.f;
        rectF.right = getWidth() - getPaddingRight();
        rectF.bottom = getHeight() / 3.f + mBarHeight / 2.f;
        canvas.drawRoundRect(rectF, mBarHeight / 2.f, mBarHeight / 2.f, mBackgroundPaint);
    }


    private void drawForeground(Canvas canvas) {
        RectF rectF = new RectF();
        rectF.left = getPaddingLeft();
        rectF.top = getHeight() / 3.f - mBarHeight / 2.f;
        rectF.right = getWidth() * mRatio - getPaddingRight(); //the dynamic position
        rectF.bottom = getHeight() / 3.f + mBarHeight / 2.f;
        canvas.drawRoundRect(rectF, mBarHeight / 2.f, mBarHeight / 2.f, mForegroundPaint);
        //draw cursor
        Rect cursorRect = new Rect();
        cursorRect.left = (int) (rectF.right - mCursorDrawable.getIntrinsicWidth() / 2);
        cursorRect.right = (int) (rectF.right + mCursorDrawable.getIntrinsicWidth() / 2);
        cursorRect.bottom = (int) (rectF.top - dp2px(5));
        cursorRect.top = cursorRect.bottom - mCursorDrawable.getIntrinsicHeight();
        mCursorDrawable.setBounds(cursorRect);
        mCursorDrawable.draw(canvas);
    }

    private void drawSectionAndSubscript(Canvas canvas) {
        float section_width = dp2px(1.5f);
        float effective_width = getWidth() - getPaddingLeft() - getPaddingRight();
        float section_span = (effective_width - effective_width * 0.1f) / (mLevelValues.length - 1);
        //初始化分段符位置
        RectF rectF = new RectF();
        rectF.left = getPaddingLeft() + getWidth() * 0.05f - section_width / 2.f;
        rectF.top = getHeight() / 3.f - mBarHeight / 2.f;
        rectF.right = rectF.left + section_width;
        rectF.bottom = getHeight() / 3.f + mBarHeight / 2.f;
        //初始化Level位置
        float mLevelX = rectF.centerX();
        float mLevelY = rectF.bottom + dp2px(20f);
        float mLevelValueY = mLevelY + dp2px(15f);
        //绘制分段间隔及下标
        for (int i = 0; i < mLevelValues.length; i++) {
            if (i > 0) {
                rectF.left += section_span;
                rectF.right = rectF.left + section_width;
                mLevelX += section_span;
            }
            canvas.drawRect(rectF, mSectionPaint);
            canvas.drawText(mLevels[i], mLevelX, mLevelY, mTextPaint);
            canvas.drawText(String.valueOf(mLevelValues[i]), mLevelX, mLevelValueY, mLightTextPaint);
        }
    }

    public void setCurrent(int current) {
        if (current < 0) {
            throw new IllegalArgumentException("current value not allowed for negative numbers");
        }
        if (mRatioAnimator == null) {
            mRatioAnimator = new ValueAnimator();
            mRatioAnimator.setDuration(mTransitionDuration);
        }
        mRatioAnimator.cancel();
        mRatioAnimator.setFloatValues(DEFAULT_INITIAL_RATIO, mPolicy.computeProgressRatio(current));
        mRatioAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRatio = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mRatioAnimator.start();
    }

    public void setTransitionDuration(long duration) {
        mTransitionDuration = duration;
    }

    public void setRatioPolicy(RatioPolicy policy) {
        if (policy == null) throw new IllegalArgumentException("The policy must be not null!");
        mPolicy = policy;
    }

    public void setSectionBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
    }

    public void setSectionForegroundColor(int mForegroundColor) {
        this.mForegroundColor = mForegroundColor;
    }

    public void setSectionSpaceColor(int mSectionColor) {
        this.mSpaceColor = mSectionColor;
    }

    public void setLevelTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public void setLightTextColor(int lightTextColor) {
        this.mLightTextColor = lightTextColor;
    }

    public void setLevelTextSize(float textSize) {
        this.mTextSize = textSize;
    }

    public void setLightTextSize(float lightTextSize) {
        this.mLightTextSize = lightTextSize;
    }

    public void setBarHeight(float barHeight) {
        this.mBarHeight = barHeight;
    }

    public void setCursorDrawable(Drawable cursorDrawable) {
        this.mCursorDrawable = cursorDrawable;
    }

    public void setLevels(String[] mLevels) {
        this.mLevels = mLevels;
    }

    public void setLevelValues(int[] mLevelValues) {
        this.mLevelValues = mLevelValues;
    }

    public interface RatioPolicy {
        /**
         * Calculated ratio based on current value
         *
         * @param current The value of current state
         * @return
         */
        float computeProgressRatio(int current);
    }
}
