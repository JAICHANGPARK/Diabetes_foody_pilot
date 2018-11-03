package com.dreamwalker.diabetesfoodypilot.activity.diary

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dreamwalker.diabetesfoodypilot.R
import com.dreamwalker.diabetesfoodypilot.database.diary.VoiceMemo
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.model.CalendarEvent
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import io.realm.Realm
import io.realm.RealmConfiguration
import java.text.SimpleDateFormat
import java.util.*

class VoiceDiaryActivity : AppCompatActivity() {

    lateinit var simpleDateFormat: SimpleDateFormat
    var realm: Realm? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_diary)

        val defaultSelectedDate = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val todayDate = defaultSelectedDate.time
        val todayString = simpleDateFormat.format(todayDate)


        Realm.init(applicationContext)
        val realmConfig = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().name("voice_diary.realm").build()
        realm = Realm.getInstance(realmConfig)

        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -1)
        val endDate = Calendar.getInstance()  /* ends after 1 month from now */
        endDate.add(Calendar.MONTH, 1)



        val predict = CalendarEventsPredicate { date ->
            val events = ArrayList<CalendarEvent>()
            val eventDate = simpleDateFormat.format(date!!.time)
            val tmp = realm!!.where(VoiceMemo::class.java).equalTo("rawDate", eventDate).findAll()

            for (i in tmp.indices) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    events.add(CalendarEvent(getColor(R.color.colorAccentProfile), "count"))
                } else {
                    events.add(CalendarEvent(R.color.colorAccentProfile, "count"))
                }
            }

            events
        }

        val horizontalCalendar = HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .addEvents(predict)
                .build()

        val listener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar?, position: Int) {
                val selectDate = date?.time
                val selectDateFormattString = simpleDateFormat.format(selectDate)
//                glucoseArrayList.clear()
                val result = realm!!.where(VoiceMemo::class.java).equalTo("rawDate", selectDateFormattString).findAll().sort("datetime")
//                glucoseArrayList.addAll(result)

//                checkLayoutDisplay(glucoseArrayList)


//                homeAdapter!!.notifyDataSetChanged()
            }
        }

        horizontalCalendar.calendarListener = listener


    }
}
