package com.example.myapplication1.ui.currency

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication1.R
import com.example.myapplication1.api.ApiClient
import com.example.myapplication1.api.ExchangeRateApi
import com.example.myapplication1.api.ExchangeRateResponse
import com.example.myapplication1.ui.home.HomeActivity
import com.example.myapplication1.ui.settings.SettingsActivity
import com.example.myapplication1.ui.stats.StatsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyConverterActivity : AppCompatActivity() {

    private val accessKey = "21bdeb82afcfeea1c389dfdcbeab2d0c"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_converter)
        val nav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        nav.selectedItemId = R.id.nav_currency
        nav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_stats -> {
                    startActivity(Intent(this, StatsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_currency -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        val amountEditText = findViewById<EditText>(R.id.amountEditText)
        val fromSpinner = findViewById<Spinner>(R.id.fromCurrencySpinner)
        val toSpinner = findViewById<Spinner>(R.id.toCurrencySpinner)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)
        val convertButton = findViewById<Button>(R.id.convertButton)

        val currencies = listOf("KZT", "USD", "EUR",  "RUB")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        fromSpinner.adapter = adapter
        toSpinner.adapter = adapter

        convertButton.setOnClickListener {
            val amount = amountEditText.text.toString().toDoubleOrNull()
            val from = fromSpinner.selectedItem.toString()
            val to = toSpinner.selectedItem.toString()

            if (amount == null || amount <= 0.0) {
                Toast.makeText(this, "Введите корректную сумму", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val api = ApiClient.retrofit.create(ExchangeRateApi::class.java)
            val call = api.convertCurrency(from, to, amount, accessKey)

            call.enqueue(object : Callback<ExchangeRateResponse> {
                override fun onResponse(
                    call: Call<ExchangeRateResponse>,
                    response: Response<ExchangeRateResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("API_DEBUG", "Ответ: $body")
                        if (body?.success == true) {
                            resultTextView.text = "Результат: ${body.result}"
                        } else {
                            resultTextView.text = "Ошибка: неверный ключ или параметры"
                        }
                    } else {
                        resultTextView.text = "Ошибка: ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<ExchangeRateResponse>, t: Throwable) {
                    resultTextView.text = "Ошибка: ${t.localizedMessage}"
                }
            })
        }
    }
}


