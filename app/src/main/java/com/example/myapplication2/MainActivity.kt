package com.example.myapplication2

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.typeOf


object AppData {

    data class Time(var data : Long)

    data class AllToDo(
        @SerializedName("top") var top : Array<Todo>,
        @SerializedName("rep") var rep : Array<Todo>,
        @SerializedName("other") var other : Array<Todo>) {
        fun push(todo : AppData.Todo) {
            if (todo.isTop) this.top.plus(todo)
            else if (todo.isRep) this.rep.plus(todo)
            else this.other.plus(todo)
        }

        fun del(todo: AppData.Todo) {
            if (todo.isTop) {
                for (i in this.top) {
                    if (i == todo) {
                        this.top = this.top.filterIndexed { index, _ -> index != this.top.indexOf(i) }.toTypedArray()
                    }
                }
            } else if (todo.isRep) {
                for (i in this.rep) {
                    if (i == todo) {
                        this.rep = this.rep.filterIndexed { index, _ -> index != this.rep.indexOf(i) }.toTypedArray()
                    }
                }
            } else {
                for (i in this.other) {
                    if (i == todo) {
                        this.other = this.other.filterIndexed { index, _ -> index != this.other.indexOf(i) }.toTypedArray()
                    }
                }
            }
        }
    }


    data class Todo(
        @SerializedName("name") var name: String,
        @SerializedName("type") var type : Int,
        @SerializedName("startTime") var startTime: Long,
        @SerializedName("endTime") var endTime: Long,
        @SerializedName("isRep") var isTop : Boolean,
        @SerializedName("isTop") var isRep : Boolean) : Parcelable {
        @RequiresApi(Build.VERSION_CODES.Q)
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readBoolean(),
            parcel.readBoolean()
        )

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeInt(type)
            parcel.writeLong(startTime)
            parcel.writeLong(endTime)
            parcel.writeBoolean(isTop)
            parcel.writeBoolean(isRep)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Todo> {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun createFromParcel(parcel: Parcel): Todo {
                return Todo(parcel)
            }

            override fun newArray(size: Int): Array<Todo?> {
                return arrayOfNulls(size)
            }
        }
    }

    lateinit var global: AllToDo
}


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //read data
        val gson = Gson()

//        val file = File(this.filesDir, "data.json")
//        var jsonString = file.readText()
//        if (!file.exists()) {
//            jsonString = assets.open("data.json").bufferedReader().use { it.readText() }
//            file.writeText(jsonString)
//        }

        val jsonString = assets.open("data.json").bufferedReader().use { it.readText() }
        val json = gson.fromJson(jsonString, JsonObject::class.java)
        var top = gson.fromJson(json["top"], Array<AppData.Todo>::class.java)
        Log.d("top", json["top"].toString())
        var rep = gson.fromJson(json["rep"], Array<AppData.Todo>::class.java)
        var other = gson.fromJson(json["other"], Array<AppData.Todo>::class.java)
        top.sortBy { it.startTime }
        rep.sortBy { it.startTime }
        other.sortBy { it.startTime }
        AppData.global = AppData.AllToDo(top, rep, other)
        for (i in AppData.global.top) viewTodo(i)
        for (i in AppData.global.rep) viewTodo(i)
        for (i in AppData.global.other) viewTodo(i)


        val button5 = findViewById<Button>(R.id.button5)
        button5.setOnClickListener {
            val tmp = AppData.Todo("吃饭", 1, 123, 456, true, true)
            viewTodo(tmp)
            Log.d("json", gson.toJson(tmp))
        }



        // 数据传递
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            val data = "Hello SecondActivity"
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra("extra_data", data)
            startActivityForResult(intent, 1)
            Log.d("11111111", "OK")
        }

    }

    override fun onStop() {
        super.onStop()
        val gson = Gson()
        Log.d("json", gson.toJson(AppData.global.top))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val todo = data?.getParcelableExtra<AppData.Todo>("todo")
            if (todo != null) {
                AppData.global.push(todo)
                viewTodo(todo)
            }
            Log.d("111111111", todo.toString())
        }
    }

    fun viewTodo(todo: AppData.Todo) {
        if (todo.isTop) pushTodo(findViewById(R.id.linearTop), findViewById(R.id.linearTopEntry), todo)
        else if (todo.isRep) pushTodo(findViewById(R.id.linearRep), findViewById(R.id.linearRepEntry), todo)
        else pushTodo(findViewById(R.id.linearOther), findViewById(R.id.linearOtherEntry), todo)
    }

    private fun pushTodo(outer: LinearLayout, inter: LinearLayout, todo: AppData.Todo) {
        val entry = getEntry(outer, inter, todo)
        inter.addView(entry)


        val anim = ValueAnimator.ofInt(outer.height, outer.height + 140)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val outerParams = outer.layoutParams
            outerParams.height = value
            outer.layoutParams = outerParams
        }
        anim.duration = 300
        anim.start()
    }

    private fun getEntry(outer: LinearLayout, inter: LinearLayout, todo: AppData.Todo) : LinearLayout {
        val res = LinearLayout(this)

        val textView = TextView(this)
        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        val startTimeString: String = sdf.format(Date(todo.startTime))
        val endTimeString: String = sdf.format(Date(todo.startTime))
        textView.setBackgroundResource(R.drawable.sperate_view)
        textView.text = startTimeString + "\n" + endTimeString
        textView.textSize = 18f
        textView.gravity = Gravity.CENTER
        textView.setTextColor(Color.parseColor("#000000"))
        val timeParams = LinearLayout.LayoutParams(
            300,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        timeParams.marginStart = 0
        timeParams.topMargin = 5
        timeParams.bottomMargin = 5
        textView.layoutParams = timeParams
        res.addView(textView)


        val typeStr = arrayOf("日\n常", "学\n习", "运\n动", "娱\n乐")
        val textView3 = TextView(this)
        textView3.text = typeStr[todo.type]
        textView3.textSize = 14f
        textView3.gravity = Gravity.CENTER
        textView3.setTextColor(Color.parseColor("#ffffff"))
        textView3.setBackgroundResource(R.drawable.tpye_view)
        val timeParams3 = LinearLayout.LayoutParams(
            80,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        textView3.layoutParams = timeParams3
        res.addView(textView3)


        val textView2 = TextView(this)
        textView2.text = todo.name
        textView2.textSize = 22f
        textView2.gravity = Gravity.CENTER
        textView2.setTextColor(Color.parseColor("#ffffff"))
        textView2.setBackgroundResource(R.drawable.name_view)
        val timeParams2 = LinearLayout.LayoutParams(
            350,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        timeParams2.marginStart = -3
        textView2.layoutParams = timeParams2
        res.addView(textView2)

        val button = Button(this)
        button.setBackgroundResource(R.drawable.start_btn)
        val btnParams = LinearLayout.LayoutParams(
            80,
            80
        )
        btnParams.marginStart = 20
        btnParams.topMargin = 22
        btnParams.bottomMargin = 22
        button.layoutParams = btnParams

        var handler = Handler(Looper.getMainLooper())
        var seconds: Int = 0
        var isRunning: Boolean = false
        button.setOnClickListener {
            if (!isRunning) {
                handler.post(object : Runnable {
                    override fun run() {
                        val hours = seconds / 3600
                        val minutes = (seconds % 3600) / 60
                        val secs = seconds % 60

                        val time = String.format("%02d:%02d:%02d", hours, minutes, secs)
                        textView.text = time

                        if (isRunning) {
                            seconds++
                            handler.postDelayed(this, 1000)
                        }
                    }
                })
                isRunning = true
            } else {
                handler.removeCallbacksAndMessages(null)
                seconds = 0
                textView.text = startTimeString + "\n" + endTimeString
                isRunning = false
            }
        }
        res.addView(button)


        val button2 = Button(this)
        button2.setBackgroundResource(R.drawable.del_btn)
        button2.layoutParams = btnParams
        button2.setOnClickListener {
            inter.removeView(res)
            AppData.global.del(todo)

            val anim = ValueAnimator.ofInt(outer.height, outer.height - 140)
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                val outerParams = outer.layoutParams
                outerParams.height = value
                outer.layoutParams = outerParams
            }
            anim.duration = 300
            anim.start()
        }
        res.addView(button2)

        val resParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        resParams.marginStart = 0
        resParams.bottomMargin = 20
        res.gravity = Gravity.LEFT and Gravity.CENTER_VERTICAL
        res.layoutParams = resParams
        res.setBackgroundResource(R.drawable.entry_view)
        return res
    }
}