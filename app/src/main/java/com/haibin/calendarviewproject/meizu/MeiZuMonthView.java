package com.haibin.calendarviewproject.meizu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;



public class MeiZuMonthView extends MonthView {

    /**
     *
     * Tùy chỉnh bàn chải văn bản được đánh dấu bởi Meizu
     */
    private Paint mTextPaint = new Paint();

    /**
     *
     * Tùy chỉnh nền tròn của logo Meizu
     */
    private Paint mSchemeBasicPaint = new Paint();
    private float mRadio;
    private int mPadding;
    private float mSchemeBaseLine;

    public MeiZuMonthView(Context context) {
        super(context);

        mTextPaint.setTextSize(dipToPx(context, 8));
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);

        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
        mSchemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeBasicPaint.setFakeBoldText(true);
        mRadio = dipToPx(getContext(), 7);
        mPadding = dipToPx(getContext(), 4);
        Paint.FontMetrics metrics = mSchemeBasicPaint.getFontMetrics();
        mSchemeBaseLine = mRadio - metrics.descent + (metrics.bottom - metrics.top) / 2 + dipToPx(getContext(), 1);

//        //兼容硬件加速无效的代码
        //Tương thích với mã tăng tốc phần cứng không hợp lệ
//        setLayerType(View.LAYER_TYPE_SOFTWARE, mSchemeBasicPaint);
//        //4.0以上硬件加速会导致无效
        // Tăng tốc phần cứng trên 4.0 sẽ gây ra không hợp lệ
//        mSchemeBasicPaint.setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.SOLID));
    }

    /**
     * Vẽ ngày đã chọn
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar Lịch dương lịch
     * @param x         日历Card x起点坐标        Thẻ lịch x tọa độ điểm bắt đầu
     * @param y         日历Card y起点坐标        Thẻ lịch y tọa độ điểm bắt đầu
     * @param hasScheme hasScheme 非标记的日期    Ngày không đánh dấu
     * @return true 则绘制onDrawScheme，因为这里背景色不是是互斥的
     *Vẽ trên DrawScheme, vì màu nền ở đây không loại trừ lẫn nhau
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        mSelectedPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x + mPadding, y + mPadding, x + mItemWidth - mPadding, y + mItemHeight - mPadding, mSelectedPaint);
        return true;
    }

    /**
     * Vẽ ngày đánh dấu sự kiện
     *
     * @param canvas   canvas
     * @param calendar 日历calendar
     * @param x        日历Card x起点坐标  Thẻ lịch x tọa độ điểm bắt đầu
     * @param y        日历Card y起点坐标  Thẻ lịch y tọa độ điểm bắt đầu
     */
    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        mSchemeBasicPaint.setColor(calendar.getSchemeColor());

        canvas.drawCircle(x + mItemWidth - mPadding - mRadio / 2, y + mPadding + mRadio, mRadio, mSchemeBasicPaint);

        canvas.drawText(calendar.getScheme(),
                x + mItemWidth - mPadding - mRadio / 2 - getTextWidth(calendar.getScheme()) / 2,
                y + mPadding + mSchemeBaseLine, mTextPaint);
    }

    private float getTextWidth(String text) {
        return mTextPaint.measureText(text);
    }

    /**
     * 绘制文本
     *
     * @param canvas     canvas
     * @param calendar   日历calendar
     * @param x          日历Card x起点坐标 Thẻ lịch x tọa độ điểm bắt đầu
     * @param y          日历Card y起点坐标 Thẻ lịch y tọa độ điểm bắt đầu
     * @param hasScheme  是否是标记的日期   Có phải là một ngày được đánh dấu
     * @param isSelected 是否选中          Cho dù được chọn
     */
    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int top = y - mItemHeight / 6;

        boolean isInRange = isInRange(calendar);

        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    mSelectTextPaint);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10, mSelectedLunarTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentMonth() && isInRange ? mSchemeTextPaint : mOtherMonthTextPaint);

            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10, mCurMonthLunarTextPaint);
        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() && isInRange ? mCurMonthTextPaint : mOtherMonthTextPaint);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10,
                    calendar.isCurrentDay() && isInRange ? mCurDayLunarTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthLunarTextPaint : mOtherMonthLunarTextPaint);
        }
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
