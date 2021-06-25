package com.sztorm.notecalendar

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kizitonwose.calendarview.CalendarView
import com.sztorm.notecalendar.calendarview.ThemedDayBinder
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedAbbreviatedString
import kotlinx.android.synthetic.main.fragment_month.view.*
import com.sztorm.notecalendar.helpers.DateHelper.Companion.toLocalizedString
import kotlinx.android.synthetic.main.calendar_week_day_bar.view.*
import kotlinx.android.synthetic.main.fragment_month.view.layoutDayOfWeekBar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [MonthFragment.createInstance] factory method to create an instance of this fragment.
 */
class MonthFragment : Fragment() {
    private lateinit var mView: View
    private lateinit var mainActivity: MainActivity
    private lateinit var startMonth: YearMonth
    private lateinit var endMonth: YearMonth

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    private fun setDayOfWeekBarText(firstDayOfWeek: DayOfWeek) {
        val dayOfWeekBar: View = mView.layoutDayOfWeekBar
        val context: Context = dayOfWeekBar.context
        var dayOfWeekIterator: DayOfWeek = firstDayOfWeek

        dayOfWeekBar.lblFirstDay.text = dayOfWeekIterator.toLocalizedAbbreviatedString(context)
        dayOfWeekIterator += 1
        dayOfWeekBar.lblSecondDay.text = dayOfWeekIterator.toLocalizedAbbreviatedString(context)
        dayOfWeekIterator += 1
        dayOfWeekBar.lblThirdDay.text = dayOfWeekIterator.toLocalizedAbbreviatedString(context)
        dayOfWeekIterator += 1
        dayOfWeekBar.lblFourthDay.text = dayOfWeekIterator.toLocalizedAbbreviatedString(context)
        dayOfWeekIterator += 1
        dayOfWeekBar.lblFifthDay.text = dayOfWeekIterator.toLocalizedAbbreviatedString(context)
        dayOfWeekIterator += 1
        dayOfWeekBar.lblSixthDay.text = dayOfWeekIterator.toLocalizedAbbreviatedString(context)
        dayOfWeekIterator += 1
        dayOfWeekBar.lblSeventhDay.text = dayOfWeekIterator.toLocalizedAbbreviatedString(context)
    }

    private fun isNearEdgeOfMonthRange(currentMonth: YearMonth)
        = startMonth > currentMonth.minusMonths(2) ||
            endMonth < currentMonth.plusMonths(2)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_month, container, false)
        val themePainter: ThemePainter = mainActivity.themePainter
        val calendarView: CalendarView = mView.calendarView
        val viewedDate: LocalDate = mainActivity.viewedDate
        val currentSelectedMonth: YearMonth = YearMonth.of(viewedDate.year, viewedDate.month)
        startMonth = currentSelectedMonth.minusMonths(HALF_CACHED_MONTH_COUNT)
        endMonth = currentSelectedMonth.plusMonths(HALF_CACHED_MONTH_COUNT)
        val firstDayOfWeek: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val dayBinder = ThemedDayBinder(mainActivity)

        setDayOfWeekBarText(firstDayOfWeek)
        themePainter.paintCaledarDayOfWeekBar(mView.layoutDayOfWeekBar)

        calendarView.dayBinder = dayBinder
        calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentSelectedMonth)
        calendarView.monthScrollListener = {
            val currentMonth = it.yearMonth
            val text = "${currentMonth.month.toLocalizedString(mView.context)} ${currentMonth.year}"
            mView.lblMonthAndYear.text = text

            if (isNearEdgeOfMonthRange(currentMonth)) {
                startMonth = currentMonth.minusMonths(HALF_CACHED_MONTH_COUNT)
                endMonth = currentMonth.plusMonths(HALF_CACHED_MONTH_COUNT)
                mView.calendarView.updateMonthRangeAsync(startMonth, endMonth)
            }
        }
        return mView
    }

    companion object : MainFragmentCreator<MonthFragment> {
        private const val HALF_CACHED_MONTH_COUNT: Long = 10

        @JvmStatic
        override fun createInstance(): MonthFragment = MonthFragment()

        @JvmStatic
        override val fragmentType: MainActivity.MainFragmentType
                = MainActivity.MainFragmentType.MONTH_FRAGMENT
    }
}