package com.dreamwalker.diabetesfoodypilot.activity.diary

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dreamwalker.diabetesfoodypilot.R
import com.dreamwalker.diabetesfoodypilot.activity.HomeActivity
import com.dreamwalker.diabetesfoodypilot.activity.ProfileHomeActivity
import com.dreamwalker.diabetesfoodypilot.adapter.diary.VoiceDiaryAdapter
import com.dreamwalker.diabetesfoodypilot.database.diary.VoiceMemo
import com.dreamwalker.spacebottomnav.SpaceItem
import com.dreamwalker.spacebottomnav.SpaceOnClickListener
import com.dreamwalker.spacebottomnav.SpaceOnLongClickListener
import com.yanzhenjie.recyclerview.swipe.*
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.model.CalendarEvent
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import es.dmoral.toasty.Toasty
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_voice_diary.*
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

open class VoiceDiaryActivity : AppCompatActivity(), SwipeItemClickListener {


    lateinit var simpleDateFormat: SimpleDateFormat
    var realm: Realm? = null

    lateinit var horizontalCalendar: HorizontalCalendar


    lateinit var voiceMemoAdapter: VoiceDiaryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_diary)
        setSpaceNavigationView(savedInstanceState)

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

        horizontalCalendar = HorizontalCalendar.Builder(this, R.id.calendarView)
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

        with(recycler_view) {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            addItemDecoration(DefaultItemDecoration(ContextCompat.getColor(applicationContext, R.color.divider_color)))
            setSwipeItemClickListener(this@VoiceDiaryActivity)
            setSwipeMenuCreator(swipeMenuCreator)
            setSwipeMenuItemClickListener(mMenuItemClickListener)
        }

        val result = realm!!.where(VoiceMemo::class.java).equalTo("rawDate", todayString).findAll()
        var leadList1: ArrayList<VoiceMemo?> = ArrayList()
        leadList1.addAll(result.filterNotNull())
        voiceMemoAdapter = VoiceDiaryAdapter(this@VoiceDiaryActivity, leadList1)

    }

    private val swipeMenuCreator = object : SwipeMenuCreator {
        override fun onCreateMenu(swipeLeftMenu: SwipeMenu, swipeRightMenu: SwipeMenu, viewType: Int) {
            val width = 74
            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            val height = ViewGroup.LayoutParams.MATCH_PARENT

            // 添加左侧的，如果不添加，则左侧不会出现菜单。

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            run {


                val addItem = SwipeMenuItem(this@VoiceDiaryActivity)
                        .setBackground(R.drawable.selector_green)
                        .setText("添加")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height)

                swipeRightMenu.addMenuItem(addItem) // 添加菜单到右侧。

                val deleteItem = SwipeMenuItem(this@VoiceDiaryActivity)
                        .setBackground(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_delete)
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height)

                swipeRightMenu.addMenuItem(deleteItem)// 添加菜单到右侧。

            }
        }
    }

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private val mMenuItemClickListener = SwipeMenuItemClickListener { menuBridge ->
        menuBridge.closeMenu()

        val direction = menuBridge.direction // 左侧还是右侧菜单。
        val adapterPosition = menuBridge.adapterPosition // RecyclerView的Item的position。
        val menuPosition = menuBridge.position // 菜单在RecyclerView的Item中的Position。

        if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
            Toast.makeText(this@VoiceDiaryActivity, "list第$adapterPosition; 右侧菜单第$menuPosition", Toast.LENGTH_SHORT).show()
        } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
            Toast.makeText(this@VoiceDiaryActivity, "list第$adapterPosition; 左侧菜单第$menuPosition", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(itemView: View?, position: Int) {
        toast("第" + position + "个")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        space.onSaveInstanceState(outState)
    }

    private fun setSpaceNavigationView(sis: Bundle?) {
        space.initWithSaveInstanceState(sis)
        space.setCentreButtonIcon(R.drawable.ic_add_white_24dp)
        space.addSpaceItem(SpaceItem("HOME", R.drawable.ic_home_white_24dp))
        space.addSpaceItem(SpaceItem("SEARCH", R.drawable.ic_search_white_24dp))
        space.addSpaceItem(SpaceItem("CHART", R.drawable.ic_bubble_chart_white_24dp))
        space.addSpaceItem(SpaceItem("PROFILE", R.drawable.ic_person_outline_white_24dp))
        space.shouldShowFullBadgeText(true)
        space.setCentreButtonIconColorFilterEnabled(false)
        space.showIconOnly()

        space.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {
                Log.d("onCentreButtonClick ", "onCentreButtonClick")
                space.shouldShowFullBadgeText(true)
                startActivity(Intent(this@VoiceDiaryActivity, VoiceRecordActivity::class.java))
            }

            override fun onItemClick(itemIndex: Int, itemName: String) {

                Log.d("onItemClick ", "$itemIndex $itemName")
                when (itemIndex) {
                    0 -> {
                        val intent = Intent(this@VoiceDiaryActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    1 -> Toasty.warning(this@VoiceDiaryActivity, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show()
                    2 -> {
                        val now = Calendar.getInstance()
                        horizontalCalendar.selectDate(now, true) // set immediate to false to ignore animation.
                    }
                    3 -> {
                        val intent = Intent(this@VoiceDiaryActivity, ProfileHomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }


            }

            override fun onItemReselected(itemIndex: Int, itemName: String) {
                Log.d("onItemReselected ", "$itemIndex $itemName")
                when (itemIndex) {
                    0 -> {
                        val intent = Intent(this@VoiceDiaryActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    1 -> Toasty.warning(this@VoiceDiaryActivity, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show()
                    2 -> {
                        val now = Calendar.getInstance()
                        horizontalCalendar.selectDate(now, true) // set immediate to false to ignore animation.
//                        Toasty.warning(this@VoiceDiaryActivity, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show()
                    }
                    3 -> {
                        val intent = Intent(this@VoiceDiaryActivity, ProfileHomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                    }
                }
            }
        })

        space.setSpaceOnLongClickListener(object : SpaceOnLongClickListener {
            override fun onCentreButtonLongClick() {

            }

            override fun onItemLongClick(itemIndex: Int, itemName: String) {
                Toast.makeText(this@VoiceDiaryActivity, itemIndex.toString() + " " + itemName, Toast.LENGTH_SHORT).show()
            }
        })

    }

}
