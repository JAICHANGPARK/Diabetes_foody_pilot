package com.dreamwalker.diabetesfoodypilot.activity.appinfo

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.consts.IntentConst
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.dreamwalker.diabetesfoodypilot.R
import com.dreamwalker.diabetesfoodypilot.activity.WebActivity
import kotlinx.android.synthetic.main.activity_developer.*

class DeveloperActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        setSupportActionBar(toolbar)

        GithubButton.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra(IntentConst.WEB_URL, "https://github.com/JAICHANGPARK")
            startActivity(intent)
        }
        qiitaButton.setOnClickListener {

            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra(IntentConst.WEB_URL, "https://qiita.com/Dreamwalker")
            startActivity(intent)
        }
    }
}
