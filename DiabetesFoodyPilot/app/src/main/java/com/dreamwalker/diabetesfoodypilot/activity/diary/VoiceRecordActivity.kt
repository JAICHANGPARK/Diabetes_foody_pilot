package com.dreamwalker.diabetesfoodypilot.activity.diary

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.dreamwalker.diabetesfoodypilot.R
import com.dreamwalker.diabetesfoodypilot.activity.HomeActivity
import com.dreamwalker.spacebottomnav.SpaceItem
import com.dreamwalker.spacebottomnav.SpaceNavigationView
import com.dreamwalker.spacebottomnav.SpaceOnClickListener
import com.dreamwalker.spacebottomnav.SpaceOnLongClickListener
import es.dmoral.toasty.Toasty
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_voice_record.*
import net.gotev.speech.Speech
import org.jetbrains.anko.toast

class VoiceRecordActivity : AppCompatActivity() {

    companion object {
        const val RECORD_AUDIO_REQUEST_CODE = 101
    }

    var realm: Realm? = null


    lateinit var spaceNavigationView: SpaceNavigationView
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


    private fun setSpaceNavigationView(sis: Bundle) {
        spaceNavigationView.initWithSaveInstanceState(sis)
        spaceNavigationView.setCentreButtonIcon(R.drawable.ic_add_white_24dp)
        spaceNavigationView.addSpaceItem(SpaceItem("HOME", R.drawable.ic_home_white_24dp))
        spaceNavigationView.addSpaceItem(SpaceItem("SEARCH", R.drawable.ic_search_white_24dp))
        spaceNavigationView.addSpaceItem(SpaceItem("CHART", R.drawable.ic_bubble_chart_white_24dp))
        spaceNavigationView.addSpaceItem(SpaceItem("PROFILE", R.drawable.ic_person_outline_white_24dp))
        spaceNavigationView.shouldShowFullBadgeText(true)
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(false)
        spaceNavigationView.showIconOnly()

        spaceNavigationView.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {
//                Log.d("onCentreButtonClick ", "onCentreButtonClick")
//                spaceNavigationView.shouldShowFullBadgeText(true)

            }

            override fun onItemClick(itemIndex: Int, itemName: String) {

                Log.d("onItemClick ", "$itemIndex $itemName")
                when (itemIndex) {
                    0 -> {
                        startActivity(Intent(this@VoiceRecordActivity, HomeActivity::class.java))
                        finish()
                    }
                    1 -> Toasty.warning(this@VoiceRecordActivity, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show()
                    2 -> Toasty.warning(this@VoiceRecordActivity, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show()
                    3 -> {
                    }
                }

            }

            override fun onItemReselected(itemIndex: Int, itemName: String) {
                Log.d("onItemReselected ", "$itemIndex $itemName")
                when (itemIndex) {
                    0 -> {
                        startActivity(Intent(this@VoiceRecordActivity, HomeActivity::class.java))
                        finish()
                    }
                    1 -> Toasty.warning(this@VoiceRecordActivity, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show()
                    2 -> Toasty.warning(this@VoiceRecordActivity, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show()
                    3 -> {
                    }
                }
            }
        })

        spaceNavigationView.setSpaceOnLongClickListener(object : SpaceOnLongClickListener {
            override fun onCentreButtonLongClick() {

            }

            override fun onItemLongClick(itemIndex: Int, itemName: String) {
                Toast.makeText(this@VoiceRecordActivity, itemIndex.toString() + " " + itemName, Toast.LENGTH_SHORT).show()
            }
        })

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
