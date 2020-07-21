package com.haibin.calendarviewproject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.haibin.calendarview.TrunkBranchAnnals;
import com.haibin.calendarviewproject.base.activity.BaseActivity;
import com.haibin.calendarviewproject.colorful.ColorfulActivity;
import com.haibin.calendarviewproject.custom.CustomActivity;
import com.haibin.calendarviewproject.full.FullActivity;
import com.haibin.calendarviewproject.index.IndexActivity;
import com.haibin.calendarviewproject.meizu.MeiZuActivity;
import com.haibin.calendarviewproject.meizu.MeiZuMonthView;
import com.haibin.calendarviewproject.meizu.MeizuWeekView;
import com.haibin.calendarviewproject.multi.MultiActivity;
import com.haibin.calendarviewproject.pager.ViewPagerActivity;
import com.haibin.calendarviewproject.progress.ProgressActivity;
import com.haibin.calendarviewproject.range.RangeActivity;
import com.haibin.calendarviewproject.simple.SimpleActivity;
import com.haibin.calendarviewproject.single.SingleActivity;
import com.haibin.calendarviewproject.solay.SolarActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnCalendarLongClickListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnYearChangeListener,
        CalendarView.OnWeekChangeListener,
        CalendarView.OnViewChangeListener,
        CalendarView.OnCalendarInterceptListener,
        CalendarView.OnYearViewChangeListener,
        DialogInterface.OnClickListener,
        View.OnClickListener {

    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextLunar;

    TextView mTextCurrentDay;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;

    private AlertDialog mMoreDialog;

    private AlertDialog mFuncDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        setStatusBarDarkMode();
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        //mTextLunar = findViewById(R.id.tv_lunar);

        mRelativeTool = findViewById(R.id.rl_tool);
        mCalendarView = findViewById(R.id.calendarView);
        //mCalendarView.setRange(2018, 7, 1, 2019, 4, 28);
        mTextCurrentDay = findViewById(R.id.tv_current_day);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                //mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        findViewById(R.id.iv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreDialog == null) {
                    mMoreDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.list_dialog_title)
                            .setItems(R.array.list_dialog_items, MainActivity.this)
                            .create();
                }
                mMoreDialog.show();
            }
        });

        final DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                mCalendarLayout.expand();
                                break;
                            case 1:
                                boolean result = mCalendarLayout.shrink();
                                Log.e("shrink", " --  " + result);
                                break;
                            case 2:
                                mCalendarView.scrollToPre(false);
                                break;
                            case 3:
                                mCalendarView.scrollToNext(false);
                                break;
                            case 4:
                                //mCalendarView.scrollToCurrent(true);
                                mCalendarView.scrollToCalendar(2018, 12, 30);
                                break;
                            case 5:
                                mCalendarView.setRange(2018, 7, 1, 2019, 4, 28);
//                                mCalendarView.setRange(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), 6,
//                                        mCalendarView.getCurYear(), mCalendarView.getCurMonth(), 23);
                                break;
                            case 6:
                                Log.e("scheme", "  " + mCalendarView.getSelectedCalendar().getScheme() + "  --  "
                                        + mCalendarView.getSelectedCalendar().isCurrentDay());
                                List<Calendar> weekCalendars = mCalendarView.getCurrentWeekCalendars();
                                for (Calendar calendar : weekCalendars) {
                                    Log.e("onWeekChange", calendar.toString() + "  --  " + calendar.getScheme());
                                }
                                new AlertDialog.Builder(MainActivity.this)
                                        .setMessage(String.format("Calendar Range: \n%s —— %s",
                                                mCalendarView.getMinRangeCalendar(),
                                                mCalendarView.getMaxRangeCalendar()))
                                        .show();
                                break;
                        }
                    }
                };

        findViewById(R.id.iv_func).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFuncDialog == null) {
                    mFuncDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.func_dialog_title)
                            .setItems(R.array.func_dialog_items, listener)
                            .create();
                }
                mFuncDialog.show();
            }
        });

        mCalendarLayout = findViewById(R.id.calendarLayout);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnCalendarLongClickListener(this, true);
        mCalendarView.setOnWeekChangeListener(this);
        mCalendarView.setOnYearViewChangeListener(this);

        //设置日期拦截事件，仅适用单选模式，当前无效
        //Đặt ngày để chặn sự kiện, chỉ áp dụng cho chế độ chọn một lần, hiện không hợp lệ
        mCalendarView.setOnCalendarInterceptListener(this);

        mCalendarView.setOnViewChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "month" + mCalendarView.getCurDay() + "day");
        //mTextLunar.setText("today");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
    }

    @SuppressWarnings("unused")
    @Override
    protected void initData() {

        final int year = mCalendarView.getCurYear();
        final int month = mCalendarView.getCurMonth();

        Map<String, Calendar> map = new HashMap<>();
        for (int y = 1997; y < 2082; y++) {
            for (int m = 1; m <= 12; m++) {
                map.put(getSchemeCalendar(y, m, 1, 0xFF40db25, "one").toString(),
                        getSchemeCalendar(y, m, 1, 0xFF40db25, "one"));
                map.put(getSchemeCalendar(y, m, 2, 0xFFe69138, "two").toString(),
                        getSchemeCalendar(y, m, 2, 0xFFe69138, "two"));
                map.put(getSchemeCalendar(y, m, 3, 0xFFdf1356, "three").toString(),
                        getSchemeCalendar(y, m, 3, 0xFFdf1356, "three"));
                map.put(getSchemeCalendar(y, m, 4, 0xFFaacc44, "four").toString(),
                        getSchemeCalendar(y, m, 4, 0xFFaacc44, "four"));
                map.put(getSchemeCalendar(y, m, 5, 0xFFbc13f0, "five").toString(),
                        getSchemeCalendar(y, m, 5, 0xFFbc13f0, "five"));
                map.put(getSchemeCalendar(y, m, 6, 0xFF542261, "six").toString(),
                        getSchemeCalendar(y, m, 6, 0xFF542261, "six"));
                map.put(getSchemeCalendar(y, m, 7, 0xFF4a4bd2, "seven").toString(),
                        getSchemeCalendar(y, m, 7, 0xFF4a4bd2, "seven"));
                map.put(getSchemeCalendar(y, m, 8, 0xFFe69138, "eight").toString(),
                        getSchemeCalendar(y, m, 8, 0xFFe69138, "eight"));
                map.put(getSchemeCalendar(y, m, 9, 0xFF542261, "nine").toString(),
                        getSchemeCalendar(y, m, 9, 0xFF542261, "nine"));
                map.put(getSchemeCalendar(y, m, 10, 0xFF87af5a, "ten").toString(),
                        getSchemeCalendar(y, m, 10, 0xFF87af5a, "ten"));
                map.put(getSchemeCalendar(y, m, 11, 0xFF40db25, "eleven").toString(),
                        getSchemeCalendar(y, m, 11, 0xFF40db25, "eleven"));
                map.put(getSchemeCalendar(y, m, 12, 0xFFcda1af, "twelve").toString(),
                        getSchemeCalendar(y, m, 12, 0xFFcda1af, "twelve"));
                map.put(getSchemeCalendar(y, m, 13, 0xFF95af1a, "thirteen").toString(),
                        getSchemeCalendar(y, m, 13, 0xFF95af1a, "thirteen"));
                map.put(getSchemeCalendar(y, m, 14, 0xFF33aadd, "fourteen").toString(),
                        getSchemeCalendar(y, m, 14, 0xFF33aadd, "fourteen"));
                map.put(getSchemeCalendar(y, m, 15, 0xFF1aff1a, "fifteen").toString(),
                        getSchemeCalendar(y, m, 15, 0xFF1aff1a, "fifteen"));
                map.put(getSchemeCalendar(y, m, 16, 0xFF22acaf, "sixteen").toString(),
                        getSchemeCalendar(y, m, 16, 0xFF22acaf, "sixteen"));
                map.put(getSchemeCalendar(y, m, 17, 0xFF99a6fa, "seventeen").toString(),
                        getSchemeCalendar(y, m, 17, 0xFF99a6fa, "seventeen"));
                map.put(getSchemeCalendar(y, m, 18, 0xFFe69138, "eighteen").toString(),
                        getSchemeCalendar(y, m, 18, 0xFFe69138, "eighteen"));
                map.put(getSchemeCalendar(y, m, 19, 0xFF40db25, "nineteen").toString(),
                        getSchemeCalendar(y, m, 19, 0xFF40db25, "nineteen"));
                map.put(getSchemeCalendar(y, m, 20, 0xFFe69138, "twenty").toString(),
                        getSchemeCalendar(y, m, 20, 0xFFe69138, "twenty"));
                map.put(getSchemeCalendar(y, m, 21, 0xFF40db25, "twenty-one").toString(),
                        getSchemeCalendar(y, m, 21, 0xFF40db25, "twenty-one"));
                map.put(getSchemeCalendar(y, m, 22, 0xFF99a6fa, "twenty-two").toString(),
                        getSchemeCalendar(y, m, 22, 0xFF99a6fa, "twenty-two"));
                map.put(getSchemeCalendar(y, m, 23, 0xFF33aadd, "twenty-three").toString(),
                        getSchemeCalendar(y, m, 23, 0xFF33aadd, "twenty-three"));
                map.put(getSchemeCalendar(y, m, 24, 0xFF40db25, "twenty-four").toString(),
                        getSchemeCalendar(y, m, 24, 0xFF40db25, "twenty-four"));
                map.put(getSchemeCalendar(y, m, 25, 0xFF1aff1a, "twenty-five").toString(),
                        getSchemeCalendar(y, m, 25, 0xFF1aff1a, "twenty-five"));
                map.put(getSchemeCalendar(y, m, 26, 0xFF40db25, "twenty-six").toString(),
                        getSchemeCalendar(y, m, 26, 0xFF40db25, "twenty-six"));
                map.put(getSchemeCalendar(y, m, 27, 0xFF95af1a, "twenty-seven").toString(),
                        getSchemeCalendar(y, m, 27, 0xFF95af1a, "twenty-seven"));
                map.put(getSchemeCalendar(y, m, 28, 0xFF40db25, "twenty-eight").toString(),
                        getSchemeCalendar(y, m, 28, 0xFF40db25, "twenty-eight"));
            }
        }

        //28560 数据量增长不会影响UI响应速度，请使用这个API替换
        //28560 Việc tăng âm lượng dữ liệu sẽ không ảnh hưởng đến tốc độ phản hồi của UI, thay vào đó, hãy sử dụng API này
        //Ở đây là cài sự kiện hiển thị bên trên góc phải và dấu chấm bên dưới.
        mCalendarView.setSchemeDate(map);

        //可自行测试性能差距
        //Có thể tự kiểm tra khoảng trống hiệu suất
        //mCalendarView.setSchemeDate(schemes);

        findViewById(R.id.ll_flyme).setOnClickListener(this);
        findViewById(R.id.ll_simple).setOnClickListener(this);
        findViewById(R.id.ll_range).setOnClickListener(this);
        findViewById(R.id.ll_colorful).setOnClickListener(this);
        findViewById(R.id.ll_index).setOnClickListener(this);
        findViewById(R.id.ll_tab).setOnClickListener(this);
        findViewById(R.id.ll_single).setOnClickListener(this);
        findViewById(R.id.ll_multi).setOnClickListener(this);
        findViewById(R.id.ll_solar_system).setOnClickListener(this);
        findViewById(R.id.ll_progress).setOnClickListener(this);
        findViewById(R.id.ll_custom).setOnClickListener(this);
        findViewById(R.id.ll_full).setOnClickListener(this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case 0:
                mCalendarView.setWeekStarWithSun();
                break;
            case 1:
                mCalendarView.setWeekStarWithMon();
                break;
            case 2:
                mCalendarView.setWeekStarWithSat();
                break;
            case 3:
                if (mCalendarView.isSingleSelectMode()) {
                    mCalendarView.setSelectDefaultMode();
                } else {
                    mCalendarView.setSelectSingleMode();
                }
                break;
            case 4:
                mCalendarView.setWeekView(MeizuWeekView.class);
                mCalendarView.setMonthView(MeiZuMonthView.class);
                mCalendarView.setWeekBar(EnglishWeekBar.class);
                break;
            case 5:
                mCalendarView.setAllMode();
                break;
            case 6:
                mCalendarView.setOnlyCurrentMode();
                break;
            case 7:
                mCalendarView.setFixMode();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_flyme:
                MeiZuActivity.show(this);
                //CalendarActivity.show(this);

                break;
            case R.id.ll_custom:
                CustomActivity.show(this);
                break;
            case R.id.ll_full:
                FullActivity.show(this);
                break;
            case R.id.ll_range:
                RangeActivity.show(this);
                break;
            case R.id.ll_simple:
                SimpleActivity.show(this);
                break;
            case R.id.ll_colorful:
                ColorfulActivity.show(this);
                break;
            case R.id.ll_index:
                IndexActivity.show(this);
                break;
            case R.id.ll_tab:
                ViewPagerActivity.show(this);
                break;
            case R.id.ll_single:
                SingleActivity.show(this);
                break;
            case R.id.ll_multi:
                MultiActivity.show(this);
                break;
            case R.id.ll_solar_system:
                SolarActivity.show(this);
                break;
            case R.id.ll_progress:
                ProgressActivity.show(this);
                break;

        }
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        // Nếu màu được đánh dấu riêng, màu này sẽ được sử dụng
        calendar.setScheme(text);
        return calendar;
    }


    @Override
    public void onCalendarOutOfRange(Calendar calendar) {
        Toast.makeText(this, String.format("%s : OutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        //Todo ............................................................................................
        Log.e("HDT0309", "(2)onCalendarSelect: calendar " + calendar + " - isClick " + isClick);
        Toast.makeText(this, getCalendarText(calendar), Toast.LENGTH_SHORT).show();
        //calendar format: yyyyMMdd


        //Log.e("onDateSelected", "  -- " + calendar.getYear() + "  --  " + calendar.getMonth() + "  -- " + calendar.getDay());
        //mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);

        //Thay đổi text dòng trên hiển thị ngày tháng năm
        mTextMonthDay.setText("day " + calendar.getDay() + " month" + calendar.getMonth() + " year " + calendar.getYear());


        mTextYear.setText(String.valueOf(calendar.getYear()));
        //mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
        /*if (isClick) {
            Toast.makeText(this, getCalendarText(calendar), Toast.LENGTH_SHORT).show();
        }*/
//        Log.e("lunar "," --  " + calendar.getLunarCalendar().toString() + "\n" +
//        "  --  " + calendar.getLunarCalendar().getYear());
        Log.e("onDateSelected", "  -- " + calendar.getYear() +
                "  --  " + calendar.getMonth() +
                "  -- " + calendar.getDay() +
                "  --  " + isClick + "  --   " + calendar.getScheme());
        Log.e("onDateSelected", "  " + mCalendarView.getSelectedCalendar().getScheme() +
                "  --  " + mCalendarView.getSelectedCalendar().isCurrentDay());
        Log.e("干支年纪 ： ", " -- " + TrunkBranchAnnals.getTrunkBranchYear(calendar.getLunarCalendar().getYear()));
        //Todo ............................................................................................
    }

    @Override
    public void onCalendarLongClickOutOfRange(Calendar calendar) {
        Toast.makeText(this, String.format("%s : LongClickOutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalendarLongClick(Calendar calendar) {
        Toast.makeText(this, "\n" + "Nhấn và giữ để không chọn ngày\n" + getCalendarText(calendar), Toast.LENGTH_SHORT).show();
    }

    private static String getCalendarText(Calendar calendar) {
        Log.e("HDT0309", "(3)getCalendarText: ");
        return String.format("Lịch mới%s \n Âm Lịch%s \n Ngày lễ của người Gregorian\n：%s \n Tết âm lịch：%s \n Thuật ngữ mặt trời：%s \n Tháng nhuận：%s",
                calendar.getMonth() + "tháng" + calendar.getDay() + "ngày",
                calendar.getLunarCalendar().getMonth() + "tháng" + calendar.getLunarCalendar().getDay() + "ngày",
                TextUtils.isEmpty(calendar.getGregorianFestival()) ? "Không" : calendar.getGregorianFestival(),
                TextUtils.isEmpty(calendar.getTraditionFestival()) ? "Không" : calendar.getTraditionFestival(),
                TextUtils.isEmpty(calendar.getSolarTerm()) ? "Không" : calendar.getSolarTerm(),
                calendar.getLeapMonth() == 0 ? "Không" : String.format("bước nhảy vọt%stháng", calendar.getLeapMonth()));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMonthChange(int year, int month) {
        //Todo ............................................................................................
        Log.e("HDT0309", "onMonthChange: " + year + "  --  " + month);
        Calendar calendar = mCalendarView.getSelectedCalendar();
        //mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "tháng" + calendar.getDay() + "ngày");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        //mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
        //Todo ............................................................................................
    }

    @Override
    public void onViewChange(boolean isMonthView) {
        //Todo ............................................................................................
        Log.e("HDT0309", "(1) onViewChange: isMonthView : " + (isMonthView ? "Xem tháng" : "Xem tuần"));
    }


    @Override
    public void onWeekChange(List<Calendar> weekCalendars) {
        //Todo ............................................................................................
        for (Calendar calendar : weekCalendars) {
            Log.e("HDT0309", "(3)onWeekChange:" + calendar.toString());
        }
    }

    @Override
    public void onYearViewChange(boolean isClose) {
        Log.e("HDT0309", "onYearViewChange: Chế độ xem năm -- " + (isClose ? "tắt" : "bật"));
    }

    /**
     * 屏蔽某些不可点击的日期，可根据自己的业务自行修改
     * Chặn một số ngày không thể nhấp nhất định, có thể được sửa đổi theo doanh nghiệp của riêng bạn
     *
     * @param calendar calendar
     * @return 是否屏蔽某些不可点击的日期，MonthView和WeekView有类似的API可调用
     * Cho dù chặn một số ngày không thể xem được nhất định, MonthView và WeekView có các API tương tự có thể được gọi
     */
    @Override
    public boolean onCalendarIntercept(Calendar calendar) {
        Log.e("onCalendarIntercept", calendar.toString());
        int day = calendar.getDay();
        return day == 1 || day == 3 || day == 6 || day == 11 || day == 12 || day == 15 || day == 20 || day == 26;
    }

    @Override
    public void onCalendarInterceptClick(Calendar calendar, boolean isClick) {
        Toast.makeText(this, calendar.toString() + "Chặn không thể nhấp", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
        Log.e("HDT0309", "onYearChange: Năm thay đổi " + year);
    }

}


