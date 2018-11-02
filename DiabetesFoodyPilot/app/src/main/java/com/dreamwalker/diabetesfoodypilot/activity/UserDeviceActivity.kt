package com.dreamwalker.diabetesfoodypilot.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.dreamwalker.diabetesfoodypilot.R
import com.dreamwalker.diabetesfoodypilot.adapter.accessory.userdevice.UserDeviceAdapter
import com.dreamwalker.diabetesfoodypilot.model.accessory.Device
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_user_device.*

class UserDeviceActivity : AppCompatActivity() , IActivityBaseSetting {


    var userDeviceAdapter:UserDeviceAdapter? = null
    lateinit var userDeviceList: ArrayList<Device>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_device)
        setSupportActionBar(toolbar)

        initSetting()

        userDeviceList = ArrayList()
        userDeviceList = Paper.book("user").read("device")

        userDeviceAdapter = UserDeviceAdapter(this, userDeviceList)

        with(recycler_view){
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = userDeviceAdapter
        }



    }

    override fun initSetting() {
       initPaper()
    }

    override fun bindView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun initPaper(){
        Paper.init(this)
    }


}
