package com.example.myapplication2

import android.animation.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.loper7.date_time_picker.DateTimeConfig
import com.loper7.date_time_picker.dialog.CardDatePickerDialog
import java.io.File
import java.io.FileInputStream
import java.security.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val startTime = AppData.Time(-1)
        val endTime = AppData.Time(-1)


        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)


        val linearLayout3 = findViewById<LinearLayout>(R.id.linearLayout3)
        val linearLayout4 = findViewById<LinearLayout>(R.id.linearLayout4)
        val textView2 = findViewById<TextView>(R.id.textView2)
        val textView3 = findViewById<TextView>(R.id.textView3)
        val switch = findViewById<com.suke.widget.SwitchButton>(R.id.switch1)
        val switch2 = findViewById<com.suke.widget.SwitchButton>(R.id.switch2)
        animateSwitch(switch, linearLayout3, textView2)
        animateSwitch(switch2, linearLayout4, textView3)


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
        animateRadioButton(radioButton, checkedColor, uncheckedColor)
        animateRadioButton(radioButton2, checkedColor, uncheckedColor)
        animateRadioButton(radioButton3, checkedColor, uncheckedColor)
        animateRadioButton(radioButton4, checkedColor, uncheckedColor)

        // time picker dialog and text&color animation
        val button3 = findViewById<Button>(R.id.button3)
        val button4 = findViewById<Button>(R.id.button4)
        animateTimePickerButton(button3, "请选择事务开始时间", startTime)
        animateTimePickerButton(button4, "请选择事务结束时间", endTime)



        //
        val data = intent.getStringExtra("extra_data")
        if (data != null) {
            Log.d("SecondActivity", data)
        }


        val button2 = findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            var type = radioGroup.indexOfChild(findViewById<RadioButton>(radioGroup.checkedRadioButtonId))
            val name = editText.text.toString()
            val isTop = switch.isChecked
            val isRep = switch2.isChecked
            if (type > 5) type = 1
            val todo = AppData.Todo(name, type, startTime.data, endTime.data, isTop, isRep)
            if (checkTodo(todo)) {
                Log.d("2222222222", todo.toString())
                val intent = Intent()
                intent.putExtra("todo", todo)
                setResult(RESULT_OK, intent)
                finish()
            }
        }


    }

    // add animation to RadioButton
    private fun animateRadioButton(radioButton: RadioButton,
                                   checkedColor: Int, uncheckedColor: Int,
                                   checkedSize: Float = 1.15f, uncheckedSize: Float = 1f) {
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


    // add animation to TimePickerButton
    private fun animateTimePickerButton(button : Button, title : String, time: AppData.Time) {
        button.setOnClickListener {
            val editText = findViewById<EditText>(R.id.editText)
            clearEditTextFocus(editText)

            val scaleAnimation = ScaleAnimation(
                1f, 1.1f, // 水平方向的起始和结束缩放比例
                1f, 1.1f, // 垂直方向的起始和结束缩放比例
                Animation.RELATIVE_TO_SELF, 0.5f, // 缩放中心点的 x 坐标
                Animation.RELATIVE_TO_SELF, 0.5f // 缩放中心点的 y 坐标
            ).apply {
                duration = 200 // 动画持续时间，单位为毫秒
                interpolator = AccelerateDecelerateInterpolator() // 加速减速插值器
                fillAfter = true // 动画结束后保持最终状态
            }
            button.startAnimation(scaleAnimation)

            CardDatePickerDialog.builder(this)
                .setTitle(title)
                .setThemeColor(Color.parseColor("#6200EE"))
                .setDisplayType(DateTimeConfig.MONTH, DateTimeConfig.DAY, DateTimeConfig.HOUR, DateTimeConfig.MIN)
                .showBackNow(false)
                .showFocusDateInfo(false)
                .setOnChoose {millisecond->

                    time.data = millisecond
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

                    val sizeAnimation = ScaleAnimation(
                        1f, 0.92f, // 水平方向的起始和结束缩放比例
                        1f, 0.92f, // 垂直方向的起始和结束缩放比例
                        Animation.RELATIVE_TO_SELF, 0.5f, // 缩放中心点的 x 坐标
                        Animation.RELATIVE_TO_SELF, 0.5f // 缩放中心点的 y 坐标
                    ).apply {
                        duration = 200 // 动画持续时间，单位为毫秒
                        interpolator = AccelerateDecelerateInterpolator() // 加速减速插值器
                        fillAfter = true // 动画结束后保持最终状态
                    }
                    button.startAnimation(sizeAnimation)

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
        val oldInt = oldChar.code
        val newInt = newChar.code
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

    private fun checkTodo(todo: AppData.Todo):Boolean {
        var f = true
        if (todo.name.isEmpty()) {
            Toast.makeText(applicationContext, "请输入事务名称", Toast.LENGTH_SHORT).show()
            f = false
        }
        if (todo.startTime == -1L) {
            Toast.makeText(applicationContext, "请选择开始时间", Toast.LENGTH_SHORT).show()
            f = false
        }
        if (todo.endTime == -1L) {
            Toast.makeText(applicationContext, "请选择结束时间", Toast.LENGTH_SHORT).show()
            f = false
        }
        return f
    }

    // add animation to Switch
    private fun animateSwitch(switch : com.suke.widget.SwitchButton,
                              linearLayout : LinearLayout, textView: TextView) {

        val originalBackground = linearLayout.background
        switch.setOnCheckedChangeListener { _, isChecked ->
            val startColor = Color.WHITE // 起始颜色
            val endColor = 0xFF6200EE.toInt() // 结束颜色
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT,
                intArrayOf(startColor, startColor)
            ) // 创建渐变背景
            gradientDrawable.cornerRadius = 25f // 设置圆角

            val layerDrawable = LayerDrawable(
                arrayOf(gradientDrawable, originalBackground)
            ) // 创建层叠背景

            if (isChecked) {
                val backgroundAnimator = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    startColor,
                    endColor
                ).apply {
                    duration = 300 // 动画持续时间，单位为毫秒
                    addUpdateListener { animator ->
                        val color = animator.animatedValue as Int
                        gradientDrawable.colors = intArrayOf(startColor, color)
                        linearLayout.background = layerDrawable
                    }
                }
                backgroundAnimator.start()

                val textAnimator = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    Color.BLACK,
                    Color.WHITE
                ).apply {
                    duration = 200 // 动画持续时间，单位为毫秒
                    addUpdateListener { animator ->
                        val color = animator.animatedValue as Int
                        textView.setTextColor(color)
                    }
                }
                textAnimator.start()
            } else {
                val backgroundAnimator = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    endColor,
                    startColor
                ).apply {
                    duration = 300 // 动画持续时间，单位为毫秒
                    addUpdateListener { animator ->
                        val color = animator.animatedValue as Int
                        gradientDrawable.colors = intArrayOf(startColor, color)
                        linearLayout.background = layerDrawable
                    }
                }
                backgroundAnimator.start()

                val textAnimator = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    Color.WHITE,
                    Color.BLACK
                ).apply {
                    duration = 200 // 动画持续时间，单位为毫秒
                    addUpdateListener { animator ->
                        val color = animator.animatedValue as Int
                        textView.setTextColor(color)
                    }
                }
                textAnimator.start()
            }
        }




    }

}