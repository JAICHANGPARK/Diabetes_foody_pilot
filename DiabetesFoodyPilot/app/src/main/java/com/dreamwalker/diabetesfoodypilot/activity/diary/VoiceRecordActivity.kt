package com.dreamwalker.diabetesfoodypilot.activity.diary

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.dreamwalker.diabetesfoodypilot.R
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_voice_record.*
import net.gotev.speech.Speech
import net.gotev.speech.SpeechDelegate
import org.jetbrains.anko.toast
import java.util.*

class VoiceRecordActivity : AppCompatActivity() {

    companion object {
        const val RECORD_AUDIO_REQUEST_CODE = 101
    }

    var realm: Realm? = null
    var listeningFlag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_record)
        setSupportActionBar(toolbar)

//        spaceNavigationView = findViewById(R.id.space)
//        if (savedInstanceState != null) {
//            setSpaceNavigationView(savedInstanceState)
//        }


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
                listeningFlag = true;
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


    }


    private fun initSpeech(colors: IntArray, heights: IntArray) {
        Speech.init(this, packageName)
        recognition_view.setColors(colors)
        recognition_view.setBarMaxHeightsInDp(heights)
    }

    fun initRealm() {
        val realmConfig = RealmConfiguration.Builder().name("voice_diary.realm").build()
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
                toast(now.toString() + "\n" + nowDate.toString())



            }
        }

        return super.onOptionsItemSelected(item)
    }


//    private fun setSpaceNavigationView(sis: Bundle) {
//        //TODO Anko 사랑해
//        with(space) {
//
//            initWithSaveInstanceState(sis)
//            setCentreButtonIcon(com.dreamwalker.diabetesfoodypilot.R.drawable.ic_settings_voice_white_24dp)
//            addSpaceItem(SpaceItem("HOME", R.drawable.ic_home_white_24dp))
//            addSpaceItem(SpaceItem("PROFILE", R.drawable.ic_person_outline_white_24dp));
//
//            shouldShowFullBadgeText(true)
//            setCentreButtonIconColorFilterEnabled(false)
//            showIconOnly()
//            setSpaceOnClickListener(object : SpaceOnClickListener {
//                override fun onItemClick(itemIndex: Int, itemName: String?) {
//                    when(itemIndex){
//                        0 -> finish()
//                    }
//                }
//
//                override fun onItemReselected(itemIndex: Int, itemName: String?) {
//                    when(itemIndex){
//                        0 -> finish()
//                    }
//                }
//
//                override fun onCentreButtonClick() {
//                    Log.d("onCentreButtonClick ", "onCentreButtonClick")
//                    shouldShowFullBadgeText(true)
//                }
//            })
//
//            setSpaceOnLongClickListener(
//                    object : SpaceOnLongClickListener {
//                        override fun onCentreButtonLongClick() {
//
//                        }
//
//                        override fun onItemLongClick(itemIndex: Int, itemName: String) {
//                            Toast.makeText(this@VoiceRecordActivity, itemIndex.toString() + " " + itemName, Toast.LENGTH_SHORT).show()
//                        }
//                    })
//        }
//
//    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        spaceNavigationView.onSaveInstanceState(outState)
    }


    override fun onDestroy() {
        super.onDestroy()
        Speech.getInstance().shutdown()


    }


}
