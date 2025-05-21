package com.example.myapplication1.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication1.R
import com.example.myapplication1.ui.currency.CurrencyConverterActivity
import com.example.myapplication1.ui.home.HomeActivity
import com.example.myapplication1.ui.login.LoginActivity
import com.example.myapplication1.ui.stats.StatsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var categoryListView: ListView
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var categories: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val nav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        nav.selectedItemId = R.id.nav_settings
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
                R.id.nav_currency -> {
                    startActivity(Intent(this, CurrencyConverterActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        // Валюта
        val currencySpinner = findViewById<Spinner>(R.id.currencySpinner)
        val currencies = listOf("KZT ₸", "USD $", "EUR €", "RUB ₽")
        val currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        currencySpinner.adapter = currencyAdapter

        val savedCurrency = sharedPreferences.getString("default_currency", "₸")
        val index = currencies.indexOf(savedCurrency)
        if (index >= 0) currencySpinner.setSelection(index)

        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sharedPreferences.edit().putString("default_currency", currencies[position]).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Категории
        categoryListView = findViewById(R.id.categoriesList)
        val categoriesSet = sharedPreferences.getStringSet("categories", setOf("Еда", "Транспорт", "Зарплата"))!!
        categories = categoriesSet.toMutableList().sorted().toMutableList()
        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        categoryListView.adapter = categoryAdapter

        categoryListView.setOnItemLongClickListener { _, _, position, _ ->
            val categoryToRemove = categories[position]
            AlertDialog.Builder(this)
                .setTitle("Удалить категорию")
                .setMessage("Вы уверены, что хотите удалить '$categoryToRemove'?")
                .setPositiveButton("Удалить") { _, _ ->
                    categories.removeAt(position)
                    saveCategories()
                    categoryAdapter.notifyDataSetChanged()
                }
                .setNegativeButton("Отмена", null)
                .show()
            true
        }

        val addCategoryButton = findViewById<Button>(R.id.addCategoryButton)
        val addCategoryEdit = findViewById<EditText>(R.id.newCategoryEdit)

        addCategoryButton.setOnClickListener {
            val newCategory = addCategoryEdit.text.toString().trim()
            if (newCategory.isNotEmpty() && !categories.contains(newCategory)) {
                categories.add(newCategory)
                categories.sort()
                saveCategories()
                categoryAdapter.notifyDataSetChanged()
                addCategoryEdit.setText("")
            } else {
                Toast.makeText(this, "Категория уже существует или пустая", Toast.LENGTH_SHORT).show()
            }
        }

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun saveCategories() {
        sharedPreferences.edit().putStringSet("categories", categories.toSet()).apply()
    }
}