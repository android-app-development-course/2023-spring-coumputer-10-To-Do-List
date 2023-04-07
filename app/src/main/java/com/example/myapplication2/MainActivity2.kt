package com.example.myapplication2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.loper7.date_time_picker.controller.DateTimeController
import com.loper7.date_time_picker.dialog.CardDatePickerDialog

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        CardDatePickerDialog.builder(this)
            .setTitle("SET MAX DATE")
            .setOnChoose {millisecond->

            }.build().show()






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