package com.example.myapplication1.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication1.R
import com.example.myapplication1.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        val currencySpinner = findViewById<Spinner>(R.id.currencySpinner)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        val currencies = listOf("₸", "$", "€", "₽")
        val currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        currencySpinner.adapter = currencyAdapter

        // Загружаем сохранённую валюту
        val savedCurrency = sharedPreferences.getString("default_currency", "₸")
        val index = currencies.indexOf(savedCurrency)
        if (index >= 0) {
            currencySpinner.setSelection(index)
        }

        // Сохраняем валюту при изменении
        currencySpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedCurrency = currencies[position]
                sharedPreferences.edit().putString("default_currency", selectedCurrency).apply()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                // Ничего не делаем
            }
        })


        // Выход из аккаунта
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
