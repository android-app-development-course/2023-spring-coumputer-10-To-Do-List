package com.example.myapplication2

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
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
import java.text.SimpleDateFormat
import java.util.*


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
}




class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        //read data
        val gson = Gson()
        val jsonString = assets.open("data.json").bufferedReader().use { it.readText() }
        val json = gson.fromJson(jsonString, JsonObject::class.java)
        var top = gson.fromJson(json["top"], Array<AppData.Todo>::class.java)
        var rep = gson.fromJson(json["rep"], Array<AppData.Todo>::class.java)
        var other = gson.fromJson(json["other"], Array<AppData.Todo>::class.java)
        top.sortBy { it.startTime }
        rep.sortBy { it.startTime }
        other.sortBy { it.startTime }
        var allTodo = AppData.AllToDo(top, rep, other)


        //
        val linearTop = findViewById<LinearLayout>(R.id.linearTop)
        val linearRep = findViewById<LinearLayout>(R.id.linearRep)
        val linearOther = findViewById<LinearLayout>(R.id.linearOther)
        val viewTop = findViewById<View>(R.id.viewTop)
        val viewRep = findViewById<View>(R.id.viewRep)
        val viewOther = findViewById<View>(R.id.viewOther)
        val linearTopEntry = findViewById<LinearLayout>(R.id.linearTopEntry)
        val linearRepEntry = findViewById<LinearLayout>(R.id.linearRepEntry)
        val linearOtherEntry = findViewById<LinearLayout>(R.id.linearOtherEntry)


        val button5 = findViewById<Button>(R.id.button5)

        button5.setOnClickListener {
            pushTodo(linearTop, linearTopEntry, AppData.Todo("吃饭", 1, 123, 456, true, true))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val todo = data?.getParcelableExtra<AppData.Todo>("todo")
            if (todo != null) {
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
        val entry = getEntry(todo)
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

    private fun getEntry(todo: AppData.Todo) : LinearLayout {
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

        val textView2 = TextView(this)
        textView2.text = todo.name
        textView2.textSize = 22f
        textView2.gravity = Gravity.CENTER
        textView2.setTextColor(Color.parseColor("#000000"))
        val timeParams2 = LinearLayout.LayoutParams(
            450,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        textView2.layoutParams = timeParams2
        res.addView(textView2)

        val button = Button(this)
        res.addView(button)


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