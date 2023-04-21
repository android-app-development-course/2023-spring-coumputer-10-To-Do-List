package com.example.myapplication2

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
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
        // TODO: get all result and save as json


        // clear editText focus
        val editText = findViewById<EditText>(R.id.editText)
        val back = findViewById<View>(R.id.back)

        back.setOnClickListener {
            clearEditTextFocus(editText)
        }


        // radioButton animation
        val radioButton = findViewById<RadioButton>(R.id.radioButton)
        val radioButton2 = findViewById<RadioButton>(R.id.radioButton2)
        val radioButton3 = findViewById<RadioButton>(R.id.radioButton3)
        val radioButton4 = findViewById<RadioButton>(R.id.radioButton4)
        val checkedColor = ContextCompat.getColor(
            this, R.color.purple_500)
        val uncheckedColor = ContextCompat.getColor(this, R.color.purple_200)
        val checkedSize = 1.15f // 单位为dp
        val uncheckedSize = 1f // 单位为dp
        animateRadioButton(radioButton, checkedColor, uncheckedColor, checkedSize, uncheckedSize)
        animateRadioButton(radioButton2, checkedColor, uncheckedColor, checkedSize, uncheckedSize)
        animateRadioButton(radioButton3, checkedColor, uncheckedColor, checkedSize, uncheckedSize)
        animateRadioButton(radioButton4, checkedColor, uncheckedColor, checkedSize, uncheckedSize)

        // time picker dialog and text&color animation
        val button3 = findViewById<Button>(R.id.button3)
        val button4 = findViewById<Button>(R.id.button4)
        animateTimePickerButton(button3, "请选择事务开始时间")
        animateTimePickerButton(button4, "请选择事务结束时间")




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


    private fun animateRadioButton(radioButton: RadioButton,
                                   checkedColor: Int, uncheckedColor: Int,
                                   checkedSize: Float, uncheckedSize: Float) {
        radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            val editText = findViewById<EditText>(R.id.editText)
            clearEditTextFocus(editText)
            val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(),
                if (isChecked) uncheckedColor else checkedColor,
                if (isChecked) checkedColor else uncheckedColor)
            colorAnimator.duration = 300
            colorAnimator.addUpdateListener {
                buttonView.setTextColor(it.animatedValue as Int)
                buttonView.compoundDrawables[1].colorFilter =
                    PorterDuffColorFilter(it.animatedValue as Int, PorterDuff.Mode.SRC_IN)
            }
            colorAnimator.start()
            val scaleAnimation = ScaleAnimation(if (isChecked) uncheckedSize else checkedSize,
                if (isChecked) checkedSize else uncheckedSize,
                if (isChecked) uncheckedSize else checkedSize,
                if (isChecked) checkedSize else uncheckedSize,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            scaleAnimation.duration = 200
            scaleAnimation.fillAfter = true
            buttonView.startAnimation(scaleAnimation)
        }
    }

    private fun animateTimePickerButton(button : Button, title : String) {
        button.setOnClickListener {
            val editText = findViewById<EditText>(R.id.editText)
            clearEditTextFocus(editText)
            CardDatePickerDialog.builder(this)
                .setTitle(title)
                .setThemeColor(Color.parseColor("#6200EE"))
                .setDisplayType(DateTimeConfig.MONTH, DateTimeConfig.DAY, DateTimeConfig.HOUR, DateTimeConfig.MIN)
                .showBackNow(false)
                .showFocusDateInfo(false)
                .setOnChoose {millisecond->

                    val sdf = SimpleDateFormat("MM-dd\nHH:mm", Locale.getDefault())
                    val dateString: String = sdf.format(Date(millisecond))

                    val oldValue = button.text.toString()
                    val textAnimator = ValueAnimator.ofFloat(0f, 1f)
                    textAnimator.duration = 200
                    textAnimator.addUpdateListener { valueAnimator ->
                        val fraction = valueAnimator.animatedFraction
                        val text = if (fraction == 1f) dateString else interpolate(
                            oldValue,
                            dateString,
                            fraction
                        )
                        button.text = text
                    }

                    val originalColor = button.currentTextColor
                    val colorAnimator = ObjectAnimator.ofInt(button, "textColor", originalColor, getRandomColor())
                    colorAnimator.duration = 200
                    colorAnimator.repeatCount = 1
                    colorAnimator.repeatMode = ValueAnimator.REVERSE
                    colorAnimator.setEvaluator(ArgbEvaluator())
                    colorAnimator.doOnEnd {
                        button.setTextColor(originalColor)
                    }

                    val animatorSet = AnimatorSet()
                    animatorSet.playTogether(colorAnimator, textAnimator)
                    animatorSet.start()


                }.build().show()
        }
    }

    private fun clearEditTextFocus(editText: EditText) {
        editText.clearFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun interpolate(oldValue: String, newValue: String, fraction: Float): String {
        val oldLength = oldValue.length
        val newLength = newValue.length
        val length = if (oldLength > newLength) oldLength else newLength
        val builder = StringBuilder(length)
        for (i in 0 until length) {
            val oldChar = if (i < oldLength) oldValue[i] else ' '
            val newChar = if (i < newLength) newValue[i] else ' '
            builder.append(interpolateChar(oldChar, newChar, fraction))
        }
        return builder.toString()
    }

    private fun interpolateChar(oldChar: Char, newChar: Char, fraction: Float): Char {
        val oldInt = oldChar.toInt()
        val newInt = newChar.toInt()
        val interpolatedInt = (oldInt + fraction * (newInt - oldInt)).toInt()
        return interpolatedInt.toChar()
    }

    private fun getRandomColor(): Int {
        val random = Random()
        // 生成 0 到 255 之间的随机数
        val r = random.nextInt(256)
        val g = random.nextInt(256)
        val b = random.nextInt(256)
        // 将随机生成的颜色值转换为 ARGB 格式
        return Color.argb(255, r, g, b)
    }


}