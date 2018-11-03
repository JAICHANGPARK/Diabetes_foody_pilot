package com.dreamwalker.diabetesfoodypilot.activity.diary

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.dreamwalker.diabetesfoodypilot.R
import com.dreamwalker.diabetesfoodypilot.database.diary.VoiceMemo
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_voice_record.*
import net.gotev.speech.Speech
import net.gotev.speech.SpeechDelegate
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class VoiceRecordActivity : AppCompatActivity() {

    companion object {
        const val RECORD_AUDIO_REQUEST_CODE = 101

        const val MORNING_DAY = R.id.button1
        const val LUNCH_DAY = R.id.button2
        const val DINNER_DAY = R.id.button3
        const val SNACK_DAY = R.id.button4
        const val ETC_DAY = R.id.button5
    }


    var realm: Realm? = null
    var listeningFlag: Boolean = false
    var intakeTimeType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_record)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { _ ->
            val builder = AlertDialog.Builder(this@VoiceRecordActivity)
            builder.setTitle("알림")
            builder.setMessage("음성 메모를 종료하시겠어요?")
            builder.setPositiveButton(android.R.string.yes) { _, _ -> finish() }
            builder.setNegativeButton(android.R.string.no) { dialog, _ -> dialog.dismiss() }
            builder.show()
        }


        //TODO 권한 값을 확인한다.
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }

        initRealm()

        val colors = intArrayOf(
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color2),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color4),
                ContextCompat.getColor(this, R.color.color5))
        val heights = intArrayOf(60, 76, 58, 80, 55)

        initSpeech(colors, heights)
        val delegate = object : SpeechDelegate {
            override fun onStartOfSpeech() {
                toast("말해주세요")
                listeningFlag = true
                processFloatingActionButton(true)

            }

            override fun onSpeechPartialResults(results: MutableList<String>?) {
                val str = StringBuilder()
                if (results != null) {
                    for (res in results) {
                        str.append(res).append(" ")
                    }
                    Log.i("speech", "partial result: " + str.toString().trim { it <= ' ' })
                }
            }

            override fun onSpeechRmsChanged(value: Float) {
            }

            override fun onSpeechResult(result: String?) {
                listeningFlag = false
                processFloatingActionButton(false)
                if (result != null && result.isNotEmpty()) {
                    runOnUiThread {
                        memo_edit_text.append(result)
                    }
                } else {
                    toast("공백은 입력할 수 없습니다.")
                }
            }
        }

        Speech.getInstance().startListening(recognition_view, delegate)

        floating_action_button.setOnClickListener {
            if (listeningFlag) {
                toast("듣고 있습니다. 말해주세요")
            } else {
                Speech.getInstance().startListening(recognition_view, delegate)
            }
        }

        with(segmented3) {
            setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    MORNING_DAY -> {
                        intakeTimeType = "아침"
//                        toast("아침 선택")
                    }
                    LUNCH_DAY -> {
                        intakeTimeType = "점심"
//                        toast("일주일간 데이터를 가져올게요")
                    }
                    DINNER_DAY -> {
                        intakeTimeType = "저녁"
//                        toast("한달간 데이터를 가져올게요")
                    }

                    SNACK_DAY -> {
                        intakeTimeType = "간식"
//                        toast("한달간 데이터를 가져올게요")
                    }

                    ETC_DAY -> {
                        intakeTimeType = "기타"
//                        toast("한달간 데이터를 가져올게요")
                    }
                }
            }
//            check(MORNING_DAY)
        }


    }
    private fun processVoiceView(trigger:Boolean){
        if(trigger){
            linearLayout.visibility = View.VISIBLE
        }else{
            linearLayout.visibility = View.GONE
        }

    }

    private fun processFloatingActionButton(trigger: Boolean) {
        if (trigger) {
            processVoiceView(trigger)
            floating_action_button.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_clear_black_24dp))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                floating_action_button.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryProfile, null))
            }
        } else {
            processVoiceView(trigger)
            floating_action_button.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_settings_voice_white_24dp))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                floating_action_button.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccentProfile, null))
            }
        }


    }


    private fun initSpeech(colors: IntArray, heights: IntArray) {
        Speech.init(this, packageName)
        recognition_view.setColors(colors)
        recognition_view.setBarMaxHeightsInDp(heights)
    }

    fun initRealm() {
        val realmConfig = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().name("voice_diary.realm").build()
        realm = Realm.getInstance(realmConfig)
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_AUDIO_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    toast("음성 메모를 위한 권한을 허가하셨습니다.")
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    toast("음성메모를 위해 권한 허가가 필요합니다.")
                    finish()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_voice_memo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.done -> {
                val now = Calendar.getInstance()
                val nowDate = Date()
//                toast(now.toString() + "\n" + nowDate.toString())
                if (intakeTimeType.isNullOrEmpty()) {
                    toast("아침, 점심, 저녁등 유형을 선택해주세요.")
                } else {
//                    toast(nowDate.toString() + intakeTimeType)
                    val userMemo = memo_edit_text.text.toString()
                    if (userMemo.isNotEmpty()) {

                        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
                        val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
                        val outputDateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
                        val formattedDate = outputFormat.format(nowDate)
                        val formattedTime = outputTimeFormat.format(nowDate)
                        val formattedDateTime = outputDateTimeFormat.format(nowDate)

                        realm?.executeTransaction {
                            val voiceMemo = realm!!.createObject(VoiceMemo::class.java)
                            voiceMemo.type = intakeTimeType
                            voiceMemo.memo = userMemo
                            voiceMemo.rawDate = formattedDate
                            voiceMemo.rawTime = formattedTime
                            voiceMemo.datetime = formattedDateTime
                            voiceMemo.date = nowDate
                        }

                        toast("저장 완료")
                        finish()
                    }else{
                        toast("메모를 입력해주세요")
                    }


                }
            }
        }

        return super.onOptionsItemSelected(item)
    }


    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        spaceNavigationView.onSaveInstanceState(outState)
    }


    override fun onDestroy() {
        super.onDestroy()
        Speech.getInstance().shutdown()


    }


}
