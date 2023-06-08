package com.example.myapplication2

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.*

data class State(var value: Int)




class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        val editText_username = findViewById<EditText>(R.id.username)
        val editText_password = findViewById<EditText>(R.id.password)
        val button_login = findViewById<Button>(R.id.login)
        var usernameState = State(1)
        var passwrodState = State(1)
        var loginState = State(-2)



        checkText(editText_username, usernameState, ::usernameValid, button_login, loginState)
        checkText(editText_password, passwrodState, ::passwordValid, button_login, loginState)

        button_login.setOnClickListener {
            val username = editText_username.text.toString()
            val password = editText_password.text.toString()
            if (username == "123456" && password == "123456") {
                Toast.makeText(this, "登陆成功", Toast.LENGTH_LONG)
                finish()
            } else {
                Toast.makeText(this, "登陆失败", Toast.LENGTH_LONG)
            }
        }


        val button_register = findViewById<Button>(R.id.register)
        button_register.setOnClickListener{
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        val button_language = findViewById<Button>(R.id.button_language)
        button_language.setOnClickListener {
            var locale: Locale = if (button_language.text != "ENG") { Locale("zh") }
            else { Locale("en") }
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    private fun usernameValid(username : String) : Int {
        if (username.length !in 5..15) return -1
        return 1
    }
    private fun passwordValid(password : String) : Int {
        if (password.length !in 5..15) return -1
        return 1
    }

    private fun checkText(editText: EditText, state : State, onChangeFun : (String)->Int, button: Button, loginState: State) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (onChangeFun(s.toString()) == 1 && state.value == 1) {
                    loginState.value += 1
                    state.value = 0
                }
                if (loginState.value >= 0) button.isEnabled = true
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
}