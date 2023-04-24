package com.example.myapplication2

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName


object AppData {

    data class Time(var data : Long)

    data class AllToDo(
        @SerializedName("top") var top : Array<Todo>,
        @SerializedName("rep") var rep : Array<Todo>,
        @SerializedName("other") var other : Array<Todo>)

    data class Todo(
        @SerializedName("name") var name: String,
        @SerializedName("type") var type : Int,
        @SerializedName("startTime") var startTime: Long,
        @SerializedName("endTime") var endTime: Long,
        @SerializedName("isRep") var isRep : Boolean,
        @SerializedName("isTop") var isTop : Boolean) : Parcelable {
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
            parcel.writeBoolean(isRep)
            parcel.writeBoolean(isTop)
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

        var allTodo = AppData.AllToDo(top, rep, other)


        //
//        val linearTop = findViewById<LinearLayout>(R.id.linearTop)
//        val linearRep = findViewById<LinearLayout>(R.id.linearRep)
//        val linearOther = findViewById<LinearLayout>(R.id.linearOther)
//        val viewTop = findViewById<View>(R.id.viewTop)
//        val viewRep = findViewById<View>(R.id.viewRep)
//        val viewOther = findViewById<View>(R.id.viewOther)
//        val linearTopEntry = findViewById<LinearLayout>(R.id.linearTopEntry)
//        val linearRepEntry = findViewById<LinearLayout>(R.id.linearRepEntry)
//        val linearOtherEntry = findViewById<LinearLayout>(R.id.linearOtherEntry)



        // 数据传递
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            val data = "Hello SecondActivity"

            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra("extra_data", data)
            startActivityForResult(intent, 1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val todo = data?.getParcelableExtra<AppData.Todo>("todo")

            Log.d("111111111", todo.toString())
        }
    }
}