package com.example.myapplication1.ui.home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication1.R
import com.example.myapplication1.data.model.Transaction
import com.example.myapplication1.data.model.TransactionType

class TransactionAdapter(
    private val items: List<Transaction>,
    private val onTransactionClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amountText: TextView = view.findViewById(R.id.amountText)
        val categoryText: TextView = view.findViewById(R.id.categoryText)
        val noteText: TextView = view.findViewById(R.id.noteText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = items[position]

        val context = holder.itemView.context
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val defaultCurrency = sharedPreferences.getString("default_currency", "₸") ?: "₸"

        val sign = if (transaction.type == TransactionType.INCOME) "+" else "-"
        holder.amountText.text = "$sign${String.format("%,.2f", transaction.amount)} $defaultCurrency"

        val color = if (transaction.type == TransactionType.INCOME) {
            Color.parseColor("#2E7D32") // Зелёный для дохода
        } else {
            Color.parseColor("#C62828") // Красный для расхода
        }
        holder.amountText.setTextColor(color)

        holder.categoryText.text = transaction.category
        holder.noteText.text = transaction.note

        holder.itemView.setOnClickListener {
            onTransactionClick(transaction)
        }
    }

    override fun getItemCount(): Int = items.size
}
