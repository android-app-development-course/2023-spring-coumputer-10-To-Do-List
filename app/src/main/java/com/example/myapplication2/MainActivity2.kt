package com.example.myapplication2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.loper7.date_time_picker.DateTimeConfig
import com.loper7.date_time_picker.dialog.CardDatePickerDialog
import java.text.SimpleDateFormat
import java.util.*

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        var type: Int
        var name: String
        var startTimeStamp: Long
        var EndTimeStamp: Long


        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            CardDatePickerDialog.builder(this)
                .setTitle("选择事务开始时间")
                .setThemeColor(Color.parseColor("#6200EE"))
                .setDisplayType(DateTimeConfig.MONTH, DateTimeConfig.DAY, DateTimeConfig.HOUR, DateTimeConfig.MIN)
                .showBackNow(false)
                .showFocusDateInfo(false)
                .setOnChoose {millisecond->
                    val sdf = SimpleDateFormat("MM-dd\nHH:mm", Locale.getDefault())
                    val dateString: String = sdf.format(Date(millisecond))
                    button3.text = dateString
                }.build().show()
        }


        val button4 = findViewById<Button>(R.id.button4)
        button4.setOnClickListener {
            CardDatePickerDialog.builder(this)
                .setTitle("选择事务结束时间")
                .setThemeColor(Color.parseColor("#6200EE"))
                .setDisplayType(DateTimeConfig.MONTH, DateTimeConfig.DAY, DateTimeConfig.HOUR, DateTimeConfig.MIN)
                .showBackNow(false)
                .showFocusDateInfo(false)
                .setOnChoose {millisecond->
                    val sdf = SimpleDateFormat("MM-dd\nHH:mm", Locale.getDefault())
                    val dateString: String = sdf.format(Date(millisecond))
                    button4.text = dateString
                }.build().show()
        }




        //
        var data = intent.getStringExtra("extra_data")
        if (data != null) {
            Log.d("SecondActivity", data)
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            finish()
        }

        val intent = Intent()
        intent.putExtra("data_return", "Hello FirstActivity")
        setResult(RESULT_OK, intent)
    }
}