package com.example.myapplication1.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication1.R
import com.example.myapplication1.data.repository.TransactionStore
import com.example.myapplication1.ui.add.AddTransactionActivity
import com.example.myapplication1.ui.login.LoginActivity
import com.example.myapplication1.ui.stats.StatsActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.addTransactionButton).setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
            findViewById<Button>(R.id.statsButton).setOnClickListener {
                startActivity(Intent(this, StatsActivity::class.java))
            }

        }

        val recyclerView = findViewById<RecyclerView>(R.id.transactionRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val userTransactions = TransactionStore.transactions.filter { it.userId == uid }
        transactionAdapter = TransactionAdapter(userTransactions)
        recyclerView.adapter = transactionAdapter
    }

    override fun onResume() {
        super.onResume()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val userTransactions = TransactionStore.transactions.filter { it.userId == uid }
        transactionAdapter = TransactionAdapter(userTransactions)
        findViewById<RecyclerView>(R.id.transactionRecyclerView).adapter = transactionAdapter
    }

}
