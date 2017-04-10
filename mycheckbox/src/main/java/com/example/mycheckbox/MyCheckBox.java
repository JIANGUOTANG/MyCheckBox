package com.example.mycheckbox;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;

/**
 * Created by jian on 2017/4/6.
 */
public class MyCheckBox extends View implements Checkable {
    private boolean mChecked;
    private Context mContext;

    public MyCheckBox(Context context) {
        this(context, null);
    }

    private int textSize = 16;//文字大小
    private Paint textPaint;
    private int mStrokeWidth = 5;
    private String mText = "A";//需要绘制的文字：ABCD
    private int mCheckedColor = COLOR_CHECKED, mUnCheckedColor= COLOR_UNCHECKED;//选中的颜色和没选中的颜色
    public MyCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
        paint.setColor(mUnCheckedColor);
        paint.setStrokeWidth(mStrokeWidth);
        textPaint= new Paint();
        textPaint.setColor(mUnCheckedColor);
       // paint.setStrokeWidth(mStrokeWidth);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
                if (isChecked()) {
                    paint.setColor(mCheckedColor);
                    textPaint.setColor(mCheckedColor);
                    postInvalidate();
                } else {
                    paint.setColor(mUnCheckedColor);
                    textPaint.setColor(mUnCheckedColor);
                    postInvalidate();
                }
            }
        });

    }

    private static final int COLOR_CHECKED = Color.parseColor("#FF4081");
    private static final int COLOR_UNCHECKED = Color.parseColor("#607D8B");

    private void init(AttributeSet attrs) {
        TypedArray appearance = getContext().obtainStyledAttributes(attrs, R.styleable.MyCheckBox);
        if (appearance != null) {
            int n = appearance.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = appearance.getIndex(i);
                if (attr == R.styleable.MyCheckBox_textSize) {
                    textSize = appearance.getDimensionPixelSize(attr, textSize);

                } else if (attr == R.styleable.MyCheckBox_colorChecked) {
                    mCheckedColor = appearance.getColor(attr, COLOR_CHECKED);
                } else if (attr == R.styleable.MyCheckBox_colorUnchecked) {
                    mUnCheckedColor = appearance.getColor(attr, COLOR_UNCHECKED);
                } else if (attr == R.styleable.MyCheckBox_text) {
                    mText = appearance.getString(attr);
                } else if (attr == R.styleable.MyCheckBox_strokeWidth) {
                    mStrokeWidth = appearance.getDimensionPixelSize(attr, mStrokeWidth);
                }
            }

            appearance.recycle();
        }
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return CheckBox.class.getName();
    }

    private int width;
    private int height;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        rec.set(0, 0, width, height);
        rectInside.set(mStroke, mStroke, width - mStroke, height - mStroke);
    }

    private int mStroke = 10;

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        this.setChecked(!isChecked());
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        invalidate();
//        if (mListener != null) {
//            mListener.onCheckedChanged(SmoothCheckBox.this, mChecked);
//        }
    }


    static class SavedState extends BaseSavedState {
        boolean checked;

        /**
         * Constructor called from {@link CompoundButton#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            checked = (Boolean) in.readValue(null);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(checked);
        }

        @Override
        public String toString() {
            return "CompoundButton.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked + "}";
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
    Rect rec = new Rect();
    Rect rectInside = new Rect();
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.checked = isChecked();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);

        requestLayout();
    }

    Paint paint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rec, paint);
        drawText(canvas);
    }
    private void drawText(Canvas canvas){
        textPaint.setTextSize(textSize);
        float textWidth = textPaint.measureText(mText);
        float x = width / 2 - textWidth / 2;
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        //metrics.ascent为负数
        float dy = -(metrics.descent + metrics.ascent) / 2;
        float y = height / 2 + dy;
        canvas.drawText(mText, x, y, textPaint);
    }

    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked);
    }
}

