package com.example.myapplication1.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication1.R
import com.example.myapplication1.data.repository.TransactionStore
import com.example.myapplication1.ui.add.AddTransactionActivity
import com.example.myapplication1.ui.currency.CurrencyConverterActivity
import com.example.myapplication1.ui.login.LoginActivity
import com.example.myapplication1.ui.settings.SettingsActivity
import com.example.myapplication1.ui.stats.StatsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val nav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        nav.selectedItemId = R.id.nav_home
        nav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> true
                R.id.nav_stats -> {
                    startActivity(Intent(this, StatsActivity::class.java))
                    true
                }
                R.id.nav_currency -> {
                    startActivity(Intent(this, CurrencyConverterActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        findViewById<Button>(R.id.addTransactionButton).setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }




        val recyclerView = findViewById<RecyclerView>(R.id.transactionRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val userTransactions = TransactionStore.transactions.filter { it.userId == uid }

        transactionAdapter = TransactionAdapter(userTransactions) { transaction ->
            val intent = Intent(this, AddTransactionActivity::class.java)
            intent.putExtra("transaction_id", transaction.id)
            startActivity(intent)
        }
        recyclerView.adapter = transactionAdapter

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val userTransactions = TransactionStore.transactions.filter { it.userId == uid }

                val transactionToRemove = userTransactions[position]
                TransactionStore.transactions.removeIf { it.id == transactionToRemove.id }

                onResume()
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val userTransactions = TransactionStore.transactions.filter { it.userId == uid }
        transactionAdapter = TransactionAdapter(userTransactions) { transaction ->
            val intent = Intent(this, AddTransactionActivity::class.java)
            intent.putExtra("transaction_id", transaction.id)
            startActivity(intent)
        }
        findViewById<RecyclerView>(R.id.transactionRecyclerView).adapter = transactionAdapter

        // ====== Новый правильный расчёт баланса ======
        val balanceText = findViewById<TextView>(R.id.balanceText)
        val income = userTransactions.filter { it.type.name == "INCOME" }.sumOf { it.amount }
        val expense = userTransactions.filter { it.type.name == "EXPENSE" }.sumOf { it.amount }
        val balance = income - expense

        // Получаем валюту из настроек
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val defaultCurrency = sharedPreferences.getString("default_currency", "₸") ?: "₸"

        // Выводим баланс с валютой
        balanceText.text = "Баланс: ${String.format("%,.2f", balance)} $defaultCurrency"
    }

}
