package com.example.myapplication2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

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