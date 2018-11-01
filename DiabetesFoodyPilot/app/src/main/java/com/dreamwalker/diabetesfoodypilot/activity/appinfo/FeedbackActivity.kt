package com.dreamwalker.diabetesfoodypilot.activity.appinfo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.dreamwalker.diabetesfoodypilot.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        setSupportActionBar(toolbar)
        Toasty.Config.getInstance().apply()

        buttonContact0.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "plain/text"
            val address = arrayOf("itsmejeffrey.dev@gmail.com")    //이메일 주소 입력
            emailIntent.putExtra(Intent.EXTRA_EMAIL, address)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[개선사항건의]애플리케이션 개선사항 건의")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "개선사항을 건의합니다. 하단에 문제의 내용을 추가해주세요 ")
            startActivity(emailIntent)
        }

        buttonContact1.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "plain/text"
            val address = arrayOf("itsmejeffrey.dev@gmail.com")    //이메일 주소 입력
            emailIntent.putExtra(Intent.EXTRA_EMAIL, address)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[이용불편건의]애플리케이션 이용관련 불편 건의")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "이용불편건의 합니다. 하단에 문제의 내용을 추가해주세요 ")
            startActivity(emailIntent)
        }

        buttonContact2.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "plain/text"
            val address = arrayOf("itsmejeffrey.dev@gmail.com")    //이메일 주소 입력
            emailIntent.putExtra(Intent.EXTRA_EMAIL, address)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[버그 및 이상 신고]애플리케이션 관련 개선사항 건의")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "버그 및 이상 신고 건의합니다. 하단에 문제의 내용을 추가해주세요 ")
            startActivity(emailIntent)
        }

        buttonContact3.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "plain/text"
            val address = arrayOf("itsmejeffrey.dev@gmail.com")    //이메일 주소 입력
            emailIntent.putExtra(Intent.EXTRA_EMAIL, address)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[기타문의]애플리케이션 관련 개선사항 건의")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "기타문의합니다.. 하단에 문제의 내용을 추가해주세요 ")
            startActivity(emailIntent)
        }
        home.setOnClickListener {
            finish()
        }

        buttonService0.setOnClickListener {
            Toasty.error(this, "준비중..", Toast.LENGTH_SHORT).show()
        }

        buttonService1.setOnClickListener {
            Toasty.error(this, "준비중..", Toast.LENGTH_SHORT).show()
        }
    }
}
